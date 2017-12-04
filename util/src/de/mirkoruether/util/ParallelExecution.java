package de.mirkoruether.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class ParallelExecution<T, R>
{
    private final Function<T, R> func;
    private final ExecutorService executor;

    public ParallelExecution(Function<T, R> func, ExecutorService executor)
    {
        this.func = func;
        this.executor = executor;
    }

    public R[] getArr(T[] in, Class<R> clazz)
    {
        return getArr(in, clazz, 0);
    }

    public R[] getArr(T[] in, Class<R> clazz, long timeout)
    {
        return get(new LinqList<>(in), timeout).toArray(clazz);
    }

    public LinqList<R> get(List<T> in)
    {
        return get(in, 0);
    }

    public LinqList<R> get(List<T> in, long timeout)
    {
        return new Exec<T, R>().get(in, timeout, executor, func);
    }

    public static <T> T inExecutorF(Function<ExecutorService, T> func, int n)
    {
        ExecutorService executer = n <= 0 ? Executors.newCachedThreadPool() : Executors.newFixedThreadPool(n);
        T result = func.apply(executer);
        executer.shutdown();
        return result;
    }

    public static void inExecutor(Consumer<ExecutorService> func, int n)
    {
        inExecutorF((ex) ->
        {
            func.accept(ex);
            return 0;
        }, n);
    }

    protected static class Exec<T, R>
    {
        private final Object notifier = new Object();

        private int finished = 0;

        protected LinqList<R> get(List<T> in, long timeout, ExecutorService executor, Function<T, R> func)
        {
            try
            {
                LinqList<R> result = new LinqList<>(in.size());

                for(int i = 0; i < in.size(); i++)
                {
                    result.add(null);
                    final int index = i;
                    executor.execute(() ->
                    {
                        result.set(index, func.apply(in.get(index)));
                        synchronized(notifier)
                        {
                            if(++finished == in.size())
                            {
                                notifier.notifyAll();
                            }
                        }
                    });
                }

                synchronized(notifier)
                {
                    if(finished == in.size())
                    {
                        return result;
                    }
                    notifier.wait(timeout);
                }

                return result;
            }
            catch(InterruptedException ex)
            {
                throw new RuntimeException("Interrupted!", ex);
            }
        }
    }
}

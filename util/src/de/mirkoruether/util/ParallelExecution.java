package de.mirkoruether.util;

import java.lang.reflect.Array;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class ParallelExecution<T, R>
{
    private final Function<T, R> func;
    private final Function<Integer, R[]> arraySupplier;
    private final ExecutorService executor;

    public ParallelExecution(Function<T, R> func, Function<Integer, R[]> arraySupplier, ExecutorService executor)
    {
        this.func = func;
        this.arraySupplier = arraySupplier;
        this.executor = executor;
    }

    public ParallelExecution(Function<T, R> func, Class<R> clazz, ExecutorService executor)
    {
        this(func, (i) -> newArray(clazz, i), executor);
    }

    public ParallelExecution(Function<T, R> func, ExecutorService executor)
    {
        this(func, (i) ->
     {
         throw new UnsupportedOperationException("No array supplier given!");
     }, executor);
    }

    public R[] get(T[] in)
    {
        return get(in, 0);
    }

    public R[] get(T[] in, long timeout)
    {
        return get(in, arraySupplier.apply(in.length), timeout);
    }

    public R[] get(T[] in, R[] dest)
    {
        return get(in, dest, 0);
    }

    public R[] get(T[] in, R[] dest, long timeout)
    {
        return new Exec<T, R>().get(in, dest, timeout, executor, func);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(Class<T> clazz, int length)
    {
        return (T[])Array.newInstance(clazz, length);
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

        protected R[] get(T[] in, R[] dest, long timeout, ExecutorService executor, Function<T, R> func)
        {
            if(dest.length < in.length)
            {
                throw new ArrayIndexOutOfBoundsException("Destination array is to small");
            }

            try
            {
                for(int i = 0; i < in.length; i++)
                {
                    final int index = i;
                    executor.execute(() ->
                    {
                        dest[index] = func.apply(in[index]);
                        synchronized(notifier)
                        {
                            if(++finished == in.length)
                            {
                                notifier.notifyAll();
                            }
                        }
                    });
                }

                synchronized(notifier)
                {
                    if(finished == in.length)
                    {
                        return dest;
                    }
                    notifier.wait(timeout);
                }

                return dest;
            }
            catch(InterruptedException ex)
            {
                throw new RuntimeException("Interrupted!", ex);
            }
        }
    }
}

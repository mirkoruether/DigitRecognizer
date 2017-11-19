package de.mirkoruether.util;

import java.lang.reflect.Array;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class ParallelExecution<T, R>
{
    private final Function<T, R> func;
    private final Class<R> clazz;
    private final ExecutorService executor;

    private int finished = 0;
    private R[] results;

    private final Object notifier = new Object();

    public ParallelExecution(Function<T, R> func, Class<R> clazz, ExecutorService executor)
    {
        this.func = func;
        this.clazz = clazz;
        this.executor = executor;
    }

    public R[] get(T[] in)
    {
        return get(in, 0);
    }

    public R[] get(T[] in, long timeout)
    {
        try
        {
            newResults(in.length);
            finished = 0;
            for(int i = 0; i < in.length; i++)
            {
                final int index = i;
                executor.execute(() ->
                {
                    results[index] = func.apply(in[index]);
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
                    return results;
                }
                notifier.wait(timeout);
            }

            R[] rs = results;
            results = null;
            finished = 0;
            return rs;
        }
        catch(InterruptedException ex)
        {
            throw new RuntimeException("Interrupted!", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void newResults(int length)
    {
        results = (R[])Array.newInstance(clazz, length);
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
}

package de.mirkoruether.util;

import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Stopwatch
{
    public static void timeExecution(Runnable task, Consumer<Long> timeConsumer)
    {
        timeExecution(() ->
        {
            task.run();
            return 0;
        }, timeConsumer);
    }

    public static <T> T timeExecution(Supplier<T> task, Consumer<Long> timeConsumer)
    {
        long time = System.currentTimeMillis();
        T result = task.get();
        timeConsumer.accept(System.currentTimeMillis() - time);
        return result;
    }

    public static void timeExecutionToStream(Runnable task, String name, PrintStream stream)
    {
        stream.println(name + " started.");
        timeExecution(task, (dur) -> stream.println(name + " finished. " + ((double)dur / 1000) + "s elapsed"));
    }

    public static <T> T timeExecutionToStream(Supplier<T> task, String name, PrintStream stream)
    {
        stream.println(name + " started.");
        return timeExecution(task, (dur) -> stream.println(name + " finished. " + ((double)dur / 1000) + "s elapsed"));
    }

    private Stopwatch()
    {
    }
}

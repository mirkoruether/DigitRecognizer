package de.mirkoruether.util;

import java.util.Arrays;
import java.util.Random;

public class Randomizer
{
    private Randomizer()
    {
    }

    public static <T> LinqList<T> shuffle(LinqList<T> in)
    {
        return shuffleInPlace(new LinqList<>(in));
    }

    public static <T> LinqList<T> shuffleInPlace(LinqList<T> in)
    {
        Random r = new Random();
        LinqList<ShuffleBox<T>> randList = new LinqList<>(in.size());

        for(T obj : in)
        {
            randList.add(new ShuffleBox<>(obj, r));
        }
        randList.sort(null);

        for(int i = 0; i < in.size(); i++)
        {
            in.set(i, randList.get(i).getContent());
        }
        return in;
    }

    public static <T> T[] shuffleArr(T[] in)
    {
        return shuffleArrInPlace(Arrays.copyOf(in, in.length));
    }

    public static <T> T[] shuffleArrInPlace(T[] in)
    {
        Random r = new Random();
        LinqList<ShuffleBox<T>> randList = new LinqList<>(in.length);

        for(T obj : in)
        {
            randList.add(new ShuffleBox<>(obj, r));
        }
        randList.sort(null);

        for(int i = 0; i < in.length; i++)
        {
            in[i] = randList.get(i).getContent();
        }
        return in;
    }

    private static class ShuffleBox<T> implements Comparable<ShuffleBox<?>>
    {
        private final int weight;
        private final T content;

        private ShuffleBox(T content, Random rand)
        {
            this.content = content;
            this.weight = rand.nextInt();
        }

        @Override
        public int compareTo(ShuffleBox<?> o)
        {
            return getWeight() > o.getWeight() ? 1 : -1;
        }

        public T getContent()
        {
            return content;
        }

        public int getWeight()
        {
            return weight;
        }
    }
}

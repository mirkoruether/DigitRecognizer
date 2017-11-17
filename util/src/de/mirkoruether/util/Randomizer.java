package de.mirkoruether.util;

import java.util.Random;

public class Randomizer
{
    private Randomizer()
    {
    }

    public static <T> T[] shuffle(T[] in, Class<T> clazz)
    {
        Random rand = new Random();
        LinqList<ShuffleBox<T>> boxes = new LinqList<>(in)
                .select((t) -> new ShuffleBox<>(t, rand));

        boxes.sort(null);

        return boxes.select((b) -> b.getContent()).toArray(clazz);
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

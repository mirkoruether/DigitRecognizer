package de.mirkoruether.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RollingList<T> extends LinqList<T>
{
    private static final long serialVersionUID = -1919488078890614105L;

    private int capacity;

    public RollingList(int capacity)
    {
        super(capacity);
        this.capacity = capacity;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
        while(super.size() > capacity)
        {
            super.remove(0);
        }
    }

    @Override
    public boolean add(T e)
    {
        while(super.size() >= capacity)
        {
            super.remove(0);
        }

        return super.add(e);
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        for(T obj : c)
        {
            add(obj);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super T> c)
    {
        throw new UnsupportedOperationException();
    }
}

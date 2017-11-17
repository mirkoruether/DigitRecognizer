package de.mirkoruether.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Predicate;

public class LinqList<T> extends ArrayList<T>
{
    private static final long serialVersionUID = -8485556470489154672L;

    public LinqList()
    {
    }

    public LinqList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public LinqList(Collection<? extends T> c)
    {
        super(c);
    }

    @SafeVarargs
    public LinqList(T... arr)
    {
        super(Arrays.asList(arr));
    }

    public LinqList(Enumeration<? extends T> e)
    {
        super(Collections.list(e));
    }

    @Override
    public Object clone()
    {
        return super.clone();
    }

    public T first()
    {
        return get(0);
    }

    public T last()
    {
        return get(size() - 1);
    }

    public LinqList<T> where(Predicate<T> func)
    {
        LinqList<T> result = new LinqList<>();
        for(T obj : this)
        {
            if(func.test(obj))
            {
                result.add(obj);
            }
        }
        return result;
    }

    public <R> LinqList<R> select(Function<T, R> func)
    {
        LinqList<R> result = new LinqList<>(size());
        for(T obj : this)
        {
            result.add(func.apply(obj));
        }
        return result;
    }

    public boolean one(Predicate<T> func)
    {
        for(T obj : this)
        {
            if(func.test(obj))
                return true;
        }
        return false;
    }

    public boolean none(Predicate<T> func)
    {
        return !one(func);
    }

    public boolean all(Predicate<T> func)
    {
        for(T obj : this)
        {
            if(!func.test(obj))
                return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> clazz)
    {
        return toArray((T[])Array.newInstance(clazz, size()));
    }
}

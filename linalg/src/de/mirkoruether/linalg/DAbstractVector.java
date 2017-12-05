package de.mirkoruether.linalg;

import java.util.Arrays;
import java.util.Comparator;

public abstract class DAbstractVector extends DMatrix
{
    private static final long serialVersionUID = 1866209478182118726L;

    protected DAbstractVector(DMatrix vectorMatrix)
    {
        super(vectorMatrix.data, vectorMatrix.columns);
    }

    protected DAbstractVector(double[] data, int columns)
    {
        super(data, columns);
    }

    protected DAbstractVector(int rows, int columns)
    {
        super(rows, columns);
    }

    public double get(int index)
    {
        return data[index];
    }

    public void put(int index, double value)
    {
        data[index] = value;
    }

    public int getLength()
    {
        return data.length;
    }

    public double[] toArray()
    {
        return Arrays.copyOf(data, data.length);
    }

    public double innerProduct(DAbstractVector other)
    {
        if(getLength() != other.getLength())
        {
            throw new SizeException("Vector lengths differ");
        }

        double result = 0.0;
        for(int i = 0; i < getLength(); i++)
        {
            result += get(i) * other.get(i);
        }
        return result;
    }

    public double norm()
    {
        return Math.sqrt(innerProduct(this));
    }

    public int indexOfMaxium()
    {
        return indexOfHighestRank((a, b) -> a > b ? 1 : -1);
    }

    public int indexOfMinimum()
    {
        return indexOfHighestRank((a, b) -> a < b ? 1 : -1);
    }

    public int indexOfHighestRank(Comparator<Double> com)
    {
        int index = 0;
        double max = get(0);
        for(int i = 1; i < getLength(); i++)
        {
            double cur = get(i);
            if(com.compare(cur, max) > 0)
            {
                index = i;
                max = cur;
            }
        }
        return index;
    }

    @Override
    public DMatrix addInPlace(DMatrix other)
    {
        if(other instanceof DAbstractVector)
        {
            return super.uncheckedAddInPlace(other);
        }
        return super.addInPlace(other);
    }

    @Override
    public DMatrix subInPlace(DMatrix other)
    {
        if(other instanceof DAbstractVector)
        {
            return super.uncheckedSubInPlace(other);
        }
        return super.subInPlace(other);
    }

    @Override
    public DMatrix elementWiseMulInPlace(DMatrix other)
    {
        if(other instanceof DAbstractVector)
        {
            return super.uncheckedElementWiseMulInPlace(other);
        }
        return super.elementWiseMulInPlace(other);
    }

    @Override
    public DMatrix elementWiseDivInPlace(DMatrix other)
    {
        if(other instanceof DAbstractVector)
        {
            return super.uncheckedElementWiseDivInPlace(other);
        }
        return super.elementWiseDivInPlace(other);
    }
}

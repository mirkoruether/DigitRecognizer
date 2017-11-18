package de.mirkoruether.linalg;

import java.util.Comparator;
import org.jblas.DoubleMatrix;

public class DVector extends DMatrix
{
    private static final long serialVersionUID = 4463788698739255742L;

    /**
     * Use 'new DMatrix(mat) toVectorDuplicate()' instead.
     * @param rowVectorMatrix
     */
    protected DVector(DoubleMatrix rowVectorMatrix)
    {
        super(rowVectorMatrix);
        assertRowVector();
    }

    public DVector(double[] content)
    {
        super(new double[][]
        {
            content
        });
    }

    public DVector(int length)
    {
        super(1, length);
    }

    public double get(int index)
    {
        return super.get(0, index);
    }

    public void put(int index, double value)
    {
        super.put(0, index, value);
    }

    public double[] toArray()
    {
        return inner.toArray();
    }

    public int getLength()
    {
        return getColumnCount();
    }

    @Override
    public DVector applyFunctionElementWise(DFunction func)
    {
        return getDuplicate().applyFunctionElementWiseInPlace(func);
    }

    @Override
    public DVector applyFunctionElementWiseInPlace(DFunction func)
    {
        return (DVector)super.applyFunctionElementWiseInPlace(func);
    }

    @Override
    public DVector add(DMatrix other)
    {
        return getDuplicate().addInPlace(other);
    }

    @Override
    public DVector addInPlace(DMatrix other)
    {
        return (DVector)super.addInPlace(other);
    }

    @Override
    public DVector sub(DMatrix other)
    {
        return getDuplicate().subInPlace(other);
    }

    @Override
    public DVector subInPlace(DMatrix other)
    {
        return (DVector)super.subInPlace(other);
    }

    @Override
    public DVector elementWiseMul(DMatrix other)
    {
        return getDuplicate().elementWiseMulInPlace(other);
    }

    @Override
    public DVector elementWiseMulInPlace(DMatrix other)
    {
        return (DVector)super.elementWiseMulInPlace(other);
    }

    @Override
    public DVector elementWiseDiv(DMatrix other)
    {
        return getDuplicate().elementWiseDivInPlace(other);
    }

    @Override
    public DVector elementWiseDivInPlace(DMatrix other)
    {
        return (DVector)super.elementWiseDivInPlace(other);
    }

    @Override
    public DVector scalarMul(double r)
    {
        return getDuplicate().scalarMulInPlace(r);
    }

    @Override
    public DVector scalarMulInPlace(double r)
    {
        return (DVector)super.scalarMulInPlace(r);
    }

    public DMatrix columnVectorDuplicate()
    {
        return this.transpose();
    }

    public DMatrix rowVectorDuplicate()
    {
        return getDuplicate();
    }

    @Override
    public DVector getDuplicate()
    {
        return new DVector(inner.dup());
    }

    public double innerProduct(DVector other)
    {
        return matrixMul(other.transpose()).toScalar();
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

    public static DVector ones(int length)
    {
        return DMatrix.ones(1, length).toVectorDuplicate();
    }
}

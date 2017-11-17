package de.mirkoruether.ann;

import org.jblas.DoubleMatrix;

public class DVector extends DMatrix
{
    /**
     * Use 'new DMatrix(mat).toVectorReference()' instead.
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
        return super.get(1, index);
    }

    public void put(int index, double value)
    {
        super.put(1, index, value);
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
        return super.add(other).toVectorReference();
    }

    @Override
    public DVector addInPlace(DMatrix other)
    {
        return super.addInPlace(other).toVectorReference();
    }

    @Override
    public DVector sub(DMatrix other)
    {
        return super.sub(other).toVectorReference();
    }

    @Override
    public DVector subInPlace(DMatrix other)
    {
        return super.subInPlace(other).toVectorReference();
    }

    @Override
    public DVector elementWiseMul(DMatrix other)
    {
        return super.elementWiseMul(other).toVectorReference();
    }

    @Override
    public DVector elementWiseMulInPlace(DMatrix other)
    {
        return super.elementWiseMulInPlace(other).toVectorReference();
    }

    @Override
    public DVector scalarMul(double r)
    {
        return super.scalarMul(r).toVectorReference();
    }

    @Override
    public DVector scalarMulInPlace(double r)
    {
        return super.scalarMulInPlace(r).toVectorReference();
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
        return super.getDuplicate().toVectorReference();
    }

    public double innerProduct(DVector other)
    {
        return matrixMul(other.transpose()).toScalar();
    }

    public double norm()
    {
        return Math.sqrt(innerProduct(this));
    }
}

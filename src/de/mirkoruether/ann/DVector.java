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

    public DVector addVector(DVector other)
    {
        return getDuplicate().addVectorInPlace(other);
    }

    public DVector addVectorInPlace(DVector other)
    {
        jBlasExec(this, other, (a, b) -> a.assertSameSize(b));
        DMatrix.jBlasExec(this, other, (a, b) -> a.addi(b));
        return this;
    }

    public DVector subVector(DVector other)
    {
        return getDuplicate().addVectorInPlace(other);
    }

    public DVector subVectorInPlace(DVector other)
    {
        jBlasExec(this, other, (a, b) -> a.assertSameSize(b));
        DMatrix.jBlasExec(this, other, (a, b) -> a.subi(b));
        return this;
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

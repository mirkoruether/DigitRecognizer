package de.mirkoruether.linalg;

import java.util.Arrays;

public class DRowVector extends DAbstractVector
{
    private static final long serialVersionUID = 4463788698739255742L;

    /**
     * Use 'new DMatrix(mat) toRowVectorDuplicate()' instead.
     * @param rowVectorMatrix
     */
    protected DRowVector(DMatrix rowVectorMatrix)
    {
        super(rowVectorMatrix);
        assertRowVector();
    }

    public DRowVector(double... content)
    {
        super(Arrays.copyOf(content, content.length), content.length);
    }

    public DRowVector(int length)
    {
        super(1, length);
    }

    @Override
    public DRowVector getDuplicate()
    {
        return super.getDuplicate().asRowVector();
    }

    @Override
    public DColumnVector transpose()
    {
        return super.transpose().asColumnVector();
    }

    // <editor-fold defaultstate="collapsed" desc="Calculations">
    @Override
    public DRowVector applyFunctionElementWise(DFunction func)
    {
        return getDuplicate().applyFunctionElementWiseInPlace(func);
    }

    @Override
    public DRowVector applyFunctionElementWiseInPlace(DFunction func)
    {
        super.applyFunctionElementWiseInPlace(func);
        return this;
    }

    @Override
    public DRowVector scalarMul(double r)
    {
        return getDuplicate().scalarMulInPlace(r);
    }

    @Override
    public DRowVector scalarMulInPlace(double r)
    {
        super.scalarMulInPlace(r);
        return this;
    }

    @Override
    public DRowVector scalarDiv(double r)
    {
        return getDuplicate().scalarDivInPlace(r);
    }

    @Override
    public DRowVector scalarDivInPlace(double r)
    {
        super.scalarDivInPlace(r);
        return this;
    }

    @Override
    public DRowVector add(DMatrix other)
    {
        return getDuplicate().addInPlace(other);
    }

    @Override
    public DRowVector addInPlace(DMatrix other)
    {
        return (DRowVector)super.addInPlace(other);
    }

    @Override
    public DRowVector sub(DMatrix other)
    {
        return getDuplicate().subInPlace(other);
    }

    @Override
    public DRowVector subInPlace(DMatrix other)
    {
        super.subInPlace(other);
        return this;
    }

    @Override
    public DRowVector elementWiseMul(DMatrix other)
    {
        return getDuplicate().elementWiseMulInPlace(other);
    }

    @Override
    public DRowVector elementWiseMulInPlace(DMatrix other)
    {
        super.elementWiseMulInPlace(other);
        return this;
    }

    @Override
    public DRowVector elementWiseDiv(DMatrix other)
    {
        return getDuplicate().elementWiseDivInPlace(other);
    }

    @Override
    public DRowVector elementWiseDivInPlace(DMatrix other)
    {
        super.elementWiseDivInPlace(other);
        return this;
    }
    // </editor-fold>

    public static DRowVector ones(int length)
    {
        return DMatrix.ones(1, length).asRowVector();
    }
}

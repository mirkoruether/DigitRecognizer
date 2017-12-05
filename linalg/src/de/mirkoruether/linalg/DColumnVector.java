package de.mirkoruether.linalg;

import java.util.Arrays;

public class DColumnVector extends DAbstractVector
{
    private static final long serialVersionUID = 3265330448851923460L;

    /**
     * Use 'new DMatrix(mat) toRowVectorDuplicate()' instead.
     * @param columnVectorMatrix
     */
    protected DColumnVector(DMatrix columnVectorMatrix)
    {
        super(columnVectorMatrix);
        assertColumnVector();
    }

    public DColumnVector(double... content)
    {
        super(Arrays.copyOf(content, content.length), 1);
    }

    public DColumnVector(int length)
    {
        super(length, 1);
    }

    @Override
    public DColumnVector getDuplicate()
    {
        return super.getDuplicate().asColumnVector();
    }

    @Override
    public DRowVector transpose()
    {
        return super.transpose().asRowVector();
    }

    // <editor-fold defaultstate="collapsed" desc="Calculations">
    @Override
    public DColumnVector applyFunctionElementWise(DFunction func)
    {
        return getDuplicate().applyFunctionElementWiseInPlace(func);
    }

    @Override
    public DColumnVector applyFunctionElementWiseInPlace(DFunction func)
    {
        super.applyFunctionElementWiseInPlace(func);
        return this;
    }

    @Override
    public DColumnVector scalarMul(double r)
    {
        return getDuplicate().scalarMulInPlace(r);
    }

    @Override
    public DColumnVector scalarMulInPlace(double r)
    {
        super.scalarMulInPlace(r);
        return this;
    }

    @Override
    public DColumnVector scalarDiv(double r)
    {
        return getDuplicate().scalarDivInPlace(r);
    }

    @Override
    public DColumnVector scalarDivInPlace(double r)
    {
        super.scalarDivInPlace(r);
        return this;
    }

    @Override
    public DColumnVector add(DMatrix other)
    {
        return getDuplicate().addInPlace(other);
    }

    @Override
    public DColumnVector addInPlace(DMatrix other)
    {
        super.addInPlace(other);
        return this;
    }

    @Override
    public DColumnVector sub(DMatrix other)
    {
        return getDuplicate().subInPlace(other);
    }

    @Override
    public DColumnVector subInPlace(DMatrix other)
    {
        super.subInPlace(other);
        return this;
    }

    @Override
    public DColumnVector elementWiseMul(DMatrix other)
    {
        return getDuplicate().elementWiseMulInPlace(other);
    }

    @Override
    public DColumnVector elementWiseMulInPlace(DMatrix other)
    {
        super.elementWiseMulInPlace(other);
        return this;
    }

    @Override
    public DColumnVector elementWiseDiv(DMatrix other)
    {
        return getDuplicate().elementWiseDivInPlace(other);
    }

    @Override
    public DColumnVector elementWiseDivInPlace(DMatrix other)
    {
        super.elementWiseDivInPlace(other);
        return this;
    }
    // </editor-fold>

    public static DColumnVector ones(int length)
    {
        return DMatrix.ones(length, 1).asColumnVector();
    }
}

package de.mirkoruether.linalg;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jblas.DoubleMatrix;
import org.jblas.exceptions.SizeException;

public class DMatrix implements Serializable
{
    private static final long serialVersionUID = 4110775618079402269L;

    protected final DoubleMatrix inner;

    protected DMatrix(DoubleMatrix matrix)
    {
        inner = matrix;
    }

    public DMatrix(double[][] content)
    {
        inner = new DoubleMatrix(content);
    }

    public DMatrix(int rows, int cols)
    {
        inner = new DoubleMatrix(rows, cols);
    }

    public DMatrix(Size size)
    {
        this(size.getRows(), size.getColumns());
    }

    public DoubleMatrix getInnerReference()
    {
        return inner;
    }

    public DoubleMatrix getInnerDuplicate()
    {
        return inner.dup();
    }

    public DMatrix getDuplicate()
    {
        return new DMatrix(inner.dup());
    }

    public int getRowCount()
    {
        return inner.rows;
    }

    public int getColumnCount()
    {
        return inner.columns;
    }

    public Size getSize()
    {
        return new Size(getRowCount(), getColumnCount());
    }

    public double get(int row, int column)
    {
        return inner.get(row, column);
    }

    public void put(int row, int column, double value)
    {
        inner.put(row, column, value);
    }

    public DMatrix applyFunctionElementWise(DFunction func)
    {
        return getDuplicate().applyFunctionElementWiseInPlace(func);
    }

    public DMatrix applyFunctionElementWiseInPlace(DFunction func)
    {
        mjBlasFunc(this, (a) ->
           {
               for(int i = 0; i < a.length; i++)
               {
                   a.put(i, func.apply(a.get(i)));
               }
               return a;
           });
        return this;
    }

    public DMatrix add(DMatrix other)
    {
        return getDuplicate().addInPlace(other);
    }

    public DMatrix addInPlace(DMatrix other)
    {
        jBlasExec(this, other, (a, b) -> a.assertSameSize(b));
        mjBlasFunc(this, other, (a, b) -> a.addi(b));
        return this;
    }

    public DMatrix sub(DMatrix other)
    {
        return getDuplicate().subInPlace(other);
    }

    public DMatrix subInPlace(DMatrix other)
    {
        jBlasExec(this, other, (a, b) -> a.assertSameSize(b));
        mjBlasFunc(this, other, (a, b) -> a.subi(b));
        return this;
    }

    public DMatrix scalarMul(double r)
    {
        return getDuplicate().scalarMulInPlace(r);
    }

    public DMatrix scalarMulInPlace(double r)
    {
        mjBlasFunc(this, (a) -> a.muli(r));
        return this;
    }

    public DMatrix elementWiseMul(DMatrix other)
    {
        return getDuplicate().elementWiseMulInPlace(other);
    }

    public DMatrix elementWiseMulInPlace(DMatrix other)
    {
        mjBlasFunc(this, other, (a, b) -> a.muli(b));
        return this;
    }

    public DMatrix elementWiseDiv(DMatrix other)
    {
        return getDuplicate().elementWiseDivInPlace(other);
    }

    public DMatrix elementWiseDivInPlace(DMatrix other)
    {
        mjBlasFunc(this, other, (a, b) -> a.divi(b));
        return this;
    }

    public DMatrix matrixMul(DMatrix other)
    {
        return mjBlasFunc(this, other, (a, b) -> a.mmul(b));
    }

    public DMatrix transpose()
    {
        return mjBlasFunc(this, (a) -> a.transpose());
    }

    public boolean isVector()
    {
        return isColumnVector() || isRowVector();
    }

    public boolean isColumnVector()
    {
        return getColumnCount() == 1;
    }

    public boolean isRowVector()
    {
        return getRowCount() == 1;
    }

    public void assertVector()
    {
        if(!isVector())
        {
            throw new SizeException("Matrix is no colum or row vector");
        }
    }

    public void assertColumnVector()
    {
        if(!isColumnVector())
        {
            throw new SizeException("Matrix is no colum vector");
        }
    }

    public void assertRowVector()
    {
        if(!isRowVector())
        {
            throw new SizeException("Matrix is no row vector");
        }
    }

    public DVector toVectorDuplicate()
    {
        if(isRowVector())
        {
            return new DVector(getInnerDuplicate());
        }
        else if(isColumnVector())
        {
            return new DVector(inner.transpose());
        }
        else
        {
            throw new SizeException("Matrix is no colum or row vector");
        }
    }

    public DVector getRowAsVector(int row)
    {
        return subMatrix(row, 1, 0, getColumnCount()).toVectorDuplicate();
    }

    public DVector getColumnAsVector(int column)
    {
        return subMatrix(0, getRowCount(), column, 1).toVectorDuplicate();
    }

    public boolean isScalar()
    {
        return inner.isScalar();
    }

    public void assertScalar()
    {
        if(!isScalar())
        {
            throw new SizeException("This matrix is bigger than 1x1");
        }
    }

    public double toScalar()
    {
        assertScalar();
        return get(0, 0);
    }

    public double[][] getContentCopy()
    {
        return inner.toArray2();
    }

    protected void validateIndices(int row, int col)
    {
        if(row < 0 || row >= getRowCount())
        {
            throw new IndexOutOfBoundsException("Row-Index " + row + " is out of bound for Matrix with " + getRowCount() + " rows");
        }

        if(col < 0 || col >= getColumnCount())
        {
            throw new IndexOutOfBoundsException("Column-Index " + row + " is out of bound for Matrix with " + getColumnCount() + " columns");
        }
    }

    public void foreachRowCol(BiConsumer<Integer, Integer> action)
    {
        for(int i = 0; i < getRowCount(); i++)
        {
            for(int j = 0; j < getColumnCount(); j++)
            {
                action.accept(i, j);
            }
        }
    }

    public boolean isSquare()
    {
        return inner.isSquare();
    }

    public double determinant()
    {
        inner.assertSquare();

        if(getColumnCount() == 1)
            return this.toScalar();

        double result = 0;
        for(int i = 1; i < getRowCount(); i++)
        {
            double summand = get(0, i) * crossOutColumnAndFirstRow(i).determinant();
            if(i % 2 == 0)
                result += summand;
            else
                result -= summand;
        }
        return result;
    }

    private DMatrix crossOutColumnAndFirstRow(int columnNumber)
    {
        DMatrix result;
        int newRC = getRowCount() - 1;
        int newCC = getColumnCount() - 1;
        if(columnNumber == 0)
        {
            result = subMatrix(1, newRC, 1, newCC);
        }
        else if(columnNumber == getColumnCount() - 1)
        {
            result = subMatrix(1, newRC, 0, newCC);
        }
        else
        {
            DMatrix matrixLeft = subMatrix(1, newRC, 0, columnNumber);
            DMatrix matrixRight = subMatrix(1, newRC, columnNumber + 1, newCC - columnNumber);
            result = matrixLeft.append(matrixRight, Side.Right);
        }
        return result;
    }

    public DMatrix subMatrix(int fromRow, int height, int fromColumn, int width)
    {
        DMatrix result = new DMatrix(height, width);
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                result.put(i, j, get(i + fromRow, j + fromColumn));
            }
            return result;
        }
        return result;
    }

    public DMatrix append(DMatrix matrix, Side side)
    {
        switch(side)
        {
            case Right:
                return appendRight(matrix);
            case Bottom:
                return appendBottom(matrix);
            case Left:
                return matrix.appendRight(this);
            case Top:
                return matrix.appendBottom(this);
            default:
                throw new IllegalArgumentException("Unknown side");
        }
    }

    protected DMatrix appendRight(DMatrix matrix)
    {
        if(matrix.getRowCount() != getRowCount())
            throw new SizeException("Could not append matrix, sizes do not fit.");

        DMatrix result = new DMatrix(getRowCount(), getColumnCount() + matrix.getColumnCount());
        for(int i = 0; i < getRowCount(); i++)
        {
            for(int j = 0; j < getColumnCount(); j++)
            {
                result.put(i, j, get(i, j));
            }
            for(int j = getColumnCount(); j < getColumnCount() + matrix.getColumnCount(); j++)
            {
                result.put(i, j, matrix.get(i, j - getColumnCount()));
            }
        }
        return result;
    }

    protected DMatrix appendBottom(DMatrix table)
    {
        if(table.getColumnCount() != getColumnCount())
            throw new SizeException("Could not append matrix, sizes do not fit.");

        DMatrix result = new DMatrix(getRowCount() + table.getRowCount(), getColumnCount());
        for(int j = 0; j < getColumnCount(); j++)
        {
            for(int i = 0; i < getRowCount(); i++)
            {
                result.put(i, j, get(i, j));
            }
            for(int i = getRowCount(); i < getRowCount() + table.getRowCount(); i++)
            {
                result.put(i, j, table.get(i - getRowCount(), j));
            }
        }
        return result;
    }

    public static enum Side
    {
        Right,
        Bottom,
        Left,
        Top
    }

    @Override
    public int hashCode()
    {
        return inner.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof DMatrix)
        {
            return inner.equals(innerOrNull((DMatrix)obj));
        }
        return false;
    }

    public static <T> T jBlasFunc(DMatrix a, Function<DoubleMatrix, T> func)
    {
        return func.apply(innerOrNull(a));
    }

    public static <T> T jBlasFunc(DMatrix a, DMatrix b, BiFunction<DoubleMatrix, DoubleMatrix, T> func)
    {
        return func.apply(innerOrNull(a), innerOrNull(b));
    }

    public static DMatrix mjBlasFunc(DMatrix a, Function<DoubleMatrix, DoubleMatrix> func)
    {
        return new DMatrix(func.apply(innerOrNull(a)));
    }

    public static DMatrix mjBlasFunc(DMatrix a, DMatrix b, BiFunction<DoubleMatrix, DoubleMatrix, DoubleMatrix> func)
    {
        return new DMatrix(func.apply(innerOrNull(a), innerOrNull(b)));
    }

    public static void jBlasExec(DMatrix a, Consumer<DoubleMatrix> con)
    {
        con.accept(innerOrNull(a));
    }

    public static void jBlasExec(DMatrix a, DMatrix b, BiConsumer<DoubleMatrix, DoubleMatrix> con)
    {
        con.accept(innerOrNull(a), innerOrNull(b));
    }

    private static DoubleMatrix innerOrNull(DMatrix m)
    {
        return m == null ? null : m.getInnerReference();
    }

    public static DMatrix ones(int rows, int columns)
    {
        DMatrix mat = new DMatrix(rows, columns);
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                mat.put(row, column, 1);
            }
        }
        return mat;
    }

    public static DMatrix identity(int size)
    {
        DMatrix mat = new DMatrix(size, size);
        for(int i = 0; i < size; i++)
        {
            mat.put(i, i, 1);
        }
        return mat;
    }

    public static class Size
    {
        private int rows;
        private int columns;

        public Size(int rows, int columns)
        {
            this.rows = rows;
            this.columns = columns;
        }

        public int getRows()
        {
            return rows;
        }

        public void setRows(int rows)
        {
            this.rows = rows;
        }

        public int getColumns()
        {
            return columns;
        }

        public void setColumns(int columns)
        {
            this.columns = columns;
        }
    }
}

package de.mirkoruether.linalg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class DMatrix implements Serializable
{
    private static final long serialVersionUID = 4110775618079402269L;

    protected final double[] data;
    protected final int columns;
    protected final int rows;

    protected DMatrix(double[] data, int columns)
    {
        if(data.length % columns != 0)
        {
            throw new IllegalArgumentException("Unsupported data length");
        }
        this.data = data;
        this.columns = columns;
        this.rows = data.length / columns;
    }

    public DMatrix(int rows, int columns)
    {
        this.data = new double[rows * columns];
        this.columns = columns;
        this.rows = rows;
    }

    public DMatrix(double[][] content)
    {
        this(content.length, content[0].length);

        for(int i = 0; i < content.length; i++)
        {
            double[] row = content[i];
            if(row.length != columns)
            {
                throw new IllegalArgumentException("Wrong row length at index " + i);
            }
            System.arraycopy(row, 0, data, columns * i, columns);
        }
    }

    public DMatrix(Size size)
    {
        this(size.getRows(), size.getColumns());
    }

    public DMatrix getDuplicate()
    {
        return new DMatrix(Arrays.copyOf(data, data.length), columns);
    }

    public int getRowCount()
    {
        return rows;
    }

    public int getColumnCount()
    {
        return columns;
    }

    public Size getSize()
    {
        return new Size(getRowCount(), getColumnCount());
    }

    public boolean sameSize(DMatrix other)
    {
        return getSize().equals(other.getSize());
    }

    public void assertSameSize(DMatrix other)
    {
        if(!sameSize(other))
        {
            throw new SizeException("Sizes differ");
        }
    }

    private int index(int row, int column)
    {
        return columns * row + column;
    }

    public double get(int row, int column)
    {
        validateIndices(row, column);
        return data[index(row, column)];
    }

    public void put(int row, int column, double value)
    {
        validateIndices(row, column);
        data[index(row, column)] = value;
    }

    public DMatrix applyFunctionElementWise(DFunction func)
    {
        return getDuplicate().applyFunctionElementWiseInPlace(func);
    }

    public DMatrix applyFunctionElementWiseInPlace(DFunction func)
    {
        for(int i = 0; i < data.length; i++)
        {
            data[i] = func.apply(data[i]);
        }
        return this;
    }

    public DMatrix add(DMatrix other)
    {
        return getDuplicate().addInPlace(other);
    }

    public DMatrix addInPlace(DMatrix other)
    {
        assertSameSize(other);
        for(int i = 0; i < data.length; i++)
        {
            data[i] += other.data[i];
        }
        return this;
    }

    public DMatrix sub(DMatrix other)
    {
        return getDuplicate().subInPlace(other);
    }

    public DMatrix subInPlace(DMatrix other)
    {
        assertSameSize(other);
        for(int i = 0; i < data.length; i++)
        {
            data[i] -= other.data[i];
        }
        return this;
    }

    public DMatrix scalarMul(double r)
    {
        return getDuplicate().scalarMulInPlace(r);
    }

    public DMatrix scalarMulInPlace(double r)
    {
        for(int i = 0; i < data.length; i++)
        {
            data[i] *= r;
        }
        return this;
    }

    public DMatrix elementWiseMul(DMatrix other)
    {
        return getDuplicate().elementWiseMulInPlace(other);
    }

    public DMatrix elementWiseMulInPlace(DMatrix other)
    {
        assertSameSize(other);
        for(int i = 0; i < data.length; i++)
        {
            data[i] *= other.data[i];
        }
        return this;
    }

    public DMatrix elementWiseDiv(DMatrix other)
    {
        return getDuplicate().elementWiseDivInPlace(other);
    }

    public DMatrix elementWiseDivInPlace(DMatrix other)
    {
        assertSameSize(other);
        for(int i = 0; i < data.length; i++)
        {
            data[i] /= other.data[i];
        }
        return this;
    }

    public DMatrix matrixMul(DMatrix other)
    {
        int n = getRowCount();
        int bn = other.getRowCount();
        int bm = other.getColumnCount();

        DMatrix C = new DMatrix(n, bm);

        for(int i = 0; i < n; i++)
        {
            for(int k = 0; k < bn; k++)
            {
                for(int j = 0; j < bm; j++)
                {
                    C.put(i, j, C.get(i, j) + get(i, k) * other.get(k, j));
                }
            }
        }
        return C;
    }

    public DMatrix transpose()
    {
        DMatrix other = new DMatrix(columns, rows);
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                other.put(j, i, get(i, j));
            }
        }
        return other;
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

    public DVector asRowVector()
    {
        assertRowVector();
        return new DVector(this);
    }

    public DVector toVectorDuplicate()
    {
        if(isRowVector())
        {
            return new DVector(getDuplicate());
        }
        else if(isColumnVector())
        {
            return new DVector(transpose());
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
        return data.length == 1;
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
        double[][] result = new double[rows][columns];
        for(int i = 0; i < rows; i++)
        {
            System.arraycopy(data, i * columns, result[i], 0, columns);
        }
        return result;
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
        return rows == columns;
    }

    public void assertSquare()
    {
        if(!isSquare())
        {
            throw new SizeException("Matrix is no square matrix");
        }
    }

    public double determinant()
    {
        assertSquare();

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
    public boolean equals(Object obj)
    {
        if(obj instanceof DMatrix)
        {
            DMatrix other = (DMatrix)obj;

            if(!sameSize(other))
            {
                return false;
            }
            else
            {
                return Arrays.equals(data, other.data);
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + this.rows;
        hash = 83 * hash + this.columns;
        hash = 83 * hash + Arrays.hashCode(this.data);
        return hash;
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

        @Override
        public boolean equals(Object obj)
        {
            if(obj instanceof Size)
            {
                Size b = (Size)obj;
                return b.getColumns() == columns && b.getRows() == rows;
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 83 * hash + this.rows;
            hash = 83 * hash + this.columns;
            return hash;
        }
    }
}

package de.mirkoruether.digitrecognizer;

import de.mirkoruether.linalg.DMatrix;
import java.util.Random;

public class Test
{
    private static long timestamp = System.currentTimeMillis();

    public static void main(String[] args)
    {
        DMatrix m1 = new DMatrix(new double[][]
        {
            {
                1, 2
            },
            {
                3, 4
            }
        });

        DMatrix m2 = new DMatrix(new double[][]
        {
            {
                5, 6
            },
            {
                7, 8
            }
        });

        printMatrix(m1);
        printMatrix(m2);
        printMatrix(m1.matrixMul(m2));

        time();

        double[][] m3c = new double[2000][2000];
        double[][] m4c = new double[m3c.length][m3c[0].length];
        Random ran = new Random();
        for(int i = 0; i < m3c.length; i++)
        {
            for(int j = 0; j < m3c[0].length; j++)
            {
                m3c[i][j] = ran.nextDouble();
                m4c[i][j] = ran.nextDouble();
            }
        }

        DMatrix m3 = new DMatrix(m3c);
        DMatrix m4 = new DMatrix(m4c);

        timeMessage("MatrixCreation");

        m3.matrixMul(m4);

        timeMessage("JblasMatrixMultiplication");
    }

    private static void timeMessage(String name)
    {
        System.out.println(name + ": " + time() + "ms elapsed");
    }

    private static long time()
    {
        long curr = System.currentTimeMillis();
        long result = curr - timestamp;
        timestamp = curr;
        return result;
    }

    private static void printMatrix(DMatrix m)
    {
        System.out.println("--Begin-Matrix--");
        for(int i = 0; i < m.getRowCount(); i++)
        {
            for(int j = 0; j < m.getColumnCount(); j++)
            {
                char[] space = emptyCharArray(20);
                char[] val = String.valueOf(m.get(i, j)).toCharArray();
                System.arraycopy(val, 0, space, 0, Math.min(space.length, val.length));
                System.out.print(new String(space));
            }
            System.out.println();
        }
        System.out.println("--End-Matrix--");
    }

    private static char[] emptyCharArray(int length)
    {
        char[] arr = new char[length];
        for(int i = 0; i < length; i++)
        {
            arr[i] = ' ';
        }
        return arr;
    }
}

package de.mirkoruether.digitrecognizer;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.util.LinqList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PerformanceTest
{
    public static void main(String[] args) throws InterruptedException
    {
        long t;
        int n = 50;
        int[] size = new int[n];
        long[] timeBlas = new long[n];
        long[] timeIt = new long[n];

        System.out.println("WARMUP");
        for(int i = 1; i < 200; i++)
        {
            DMatrix m1 = createMatrix(i);
            DMatrix m2 = createMatrix(i);
            m1.matrixMul(m2);
        }

        Thread.sleep(2000);
        System.out.println("WARMUP");
        for(int i = 1; i < 200; i++)
        {
            DMatrix m1 = createMatrix(i);
            DMatrix m2 = createMatrix(i);
            m1.matrixMul(m2);
        }

        for(int j = 0; j < 100000; j++)
        {
            if(j % 10000 == 0)
            {
                System.out.println("Iteration " + j);
            }

            for(int i = 0; i < n; i++)
            {
                int dim = (int)Math.pow(2.0, i / 10.0);

                size[i] = dim;

                DMatrix m1 = createMatrix(dim);
                DMatrix m2 = createMatrix(dim);
                t = System.nanoTime();
                m1.matrixMul(m2);
                timeBlas[i] += System.nanoTime() - t;

                t = System.nanoTime();
                m1.matrixMulIt(m2);
                timeIt[i] += System.nanoTime() - t;
            }
        }

        LinqList<String> lines = new LinqList<>();
        lines.add("n,timeBlas,timeIt");
        for(int i = 0; i < n; i++)
        {
            lines.add(size[i] + "," + timeBlas[i] + "," + timeIt[i]);
        }

        write("performance.csv", lines.toArray(String.class));
    }

    public static void write(String filename, String[] x)
    {
        try(FileWriter fw = new FileWriter(filename);
            BufferedWriter outputWriter = new BufferedWriter(fw);)
        {
            for(String x1 : x)
            {
                outputWriter.write(x1);
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputWriter.close();
        }
        catch(IOException ex)
        {
        }
    }

    private static DMatrix createMatrix(int dim)
    {
        Random r = new Random();
        DMatrix result = new DMatrix(dim, dim);
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                result.put(i, j, r.nextGaussian());
            }
        }
        return result;
    }
}

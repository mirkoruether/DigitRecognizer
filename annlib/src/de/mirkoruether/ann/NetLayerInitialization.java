package de.mirkoruether.ann;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import java.util.Random;

public interface NetLayerInitialization
{
    public DVector initBiases(int size);

    public DMatrix initWeights(int size, int inputSize);

    public static class Gaussian implements NetLayerInitialization
    {
        @Override
        public DVector initBiases(int size)
        {
            Random rand = new Random();
            double[] values = new double[size];
            for(int i = 0; i < values.length; i++)
            {
                values[i] = rand.nextGaussian();
            }
            return new DVector(values);
        }

        @Override
        public DMatrix initWeights(int inputSize, int outputSize)
        {
            Random rand = new Random();
            double[][] values = new double[outputSize][inputSize];
            for(double[] row : values)
            {
                for(int j = 0; j < inputSize; j++)
                {
                    row[j] = rand.nextGaussian();
                }
            }
            return new DMatrix(values);
        }
    }
}

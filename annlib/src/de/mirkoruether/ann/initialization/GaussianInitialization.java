package de.mirkoruether.ann.initialization;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import java.util.Random;

public class GaussianInitialization implements NetLayerInitialization
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
    public DMatrix initWeights(int outputSize, int inputSize)
    {
        Random rand = new Random();
        double[][] values = new double[inputSize][outputSize];
        for(double[] row : values)
        {
            for(int j = 0; j < outputSize; j++)
            {
                row[j] = rand.nextGaussian();
            }
        }
        return new DMatrix(values);
    }
}

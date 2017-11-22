package de.mirkoruether.ann.initialization;

import de.mirkoruether.linalg.DMatrix;

public class NormalizedGaussianInitialization extends GaussianInitialization
{
    @Override
    public DMatrix initWeights(int size, int inputSize)
    {
        double normalizationFactor = 1.0 / Math.sqrt(inputSize);
        return super.initWeights(size, inputSize).scalarMulInPlace(normalizationFactor);
    }
}

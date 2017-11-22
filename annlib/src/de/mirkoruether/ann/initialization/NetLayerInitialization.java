package de.mirkoruether.ann.initialization;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;

public interface NetLayerInitialization
{
    public DVector initBiases(int size);

    public DMatrix initWeights(int outputSize, int inputSize);
}

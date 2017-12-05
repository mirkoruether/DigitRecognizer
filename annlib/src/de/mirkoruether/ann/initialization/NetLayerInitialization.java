package de.mirkoruether.ann.initialization;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DRowVector;

public interface NetLayerInitialization
{
    public DRowVector initBiases(int size);

    public DMatrix initWeights(int outputSize, int inputSize);
}

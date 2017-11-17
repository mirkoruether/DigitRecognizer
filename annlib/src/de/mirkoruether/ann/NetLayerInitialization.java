package de.mirkoruether.ann;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;

public interface NetLayerInitialization
{
    public DVector initBiases(int size);

    public DMatrix initWeights(int size, int inputSize);
}

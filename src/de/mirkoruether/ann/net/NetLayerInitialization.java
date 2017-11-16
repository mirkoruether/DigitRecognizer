package de.mirkoruether.ann.net;

import de.mirkoruether.ann.DMatrix;
import de.mirkoruether.ann.DVector;

public interface NetLayerInitialization
{
    public DVector initBiases(int size);

    public DMatrix initWeights(int size, int inputSize);
}

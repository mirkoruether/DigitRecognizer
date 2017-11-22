package de.mirkoruether.ann.training.regularization;

import de.mirkoruether.linalg.DMatrix;

public interface CostFunctionRegularization
{
    public DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize);
}

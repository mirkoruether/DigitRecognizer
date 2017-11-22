package de.mirkoruether.ann.training.regularization;

import de.mirkoruether.linalg.DMatrix;

public class L2Regularization extends AbstractCostFunctionRegularization
{
    public L2Regularization(double regularizationParameter)
    {
        super(regularizationParameter);
    }

    @Override
    public DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize, double regularizationParameter)
    {
        return weigths.scalarMul(learningRate * regularizationParameter / trainingSetSize);
    }
}

package de.mirkoruether.ann.training.regularization;

import de.mirkoruether.linalg.DMatrix;

public abstract class AbstractCostFunctionRegularization implements CostFunctionRegularization
{
    private double regularizationParameter;

    protected AbstractCostFunctionRegularization(double regularizationParameter)
    {
        this.regularizationParameter = regularizationParameter;
    }

    public double getRegularizationParameter()
    {
        return regularizationParameter;
    }

    public void setRegularizationParameter(double regularizationParameter)
    {
        this.regularizationParameter = regularizationParameter;
    }

    @Override
    public DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize)
    {
        return calculateWeightDecay(weigths, learningRate, trainingSetSize, regularizationParameter);
    }

    public abstract DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize, double regularizationParameter);
}

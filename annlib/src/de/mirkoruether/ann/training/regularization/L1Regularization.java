package de.mirkoruether.ann.training.regularization;

import de.mirkoruether.linalg.DFunction;
import de.mirkoruether.linalg.DMatrix;

public class L1Regularization extends AbstractCostFunctionRegularization
{
    public L1Regularization(double regularizationParameter)
    {
        super(regularizationParameter);
    }

    @Override
    public DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize, double regularizationParameter)
    {
        final DFunction sgn = (x) -> x > 0 ? 1.0 : -1.0;
        return weigths.applyFunctionElementWise(sgn).scalarMulInPlace(learningRate * regularizationParameter / trainingSetSize);
    }
}

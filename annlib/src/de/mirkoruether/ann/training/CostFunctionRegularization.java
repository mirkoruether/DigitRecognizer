package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DFunction;
import de.mirkoruether.linalg.DMatrix;

public interface CostFunctionRegularization
{
    public DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize);

    public static abstract class Abstract implements CostFunctionRegularization
    {
        private double regularizationParameter;

        protected Abstract(double regularizationParameter)
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

    public static class L1 extends Abstract
    {
        public L1(double regularizationParameter)
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

    public static class L2 extends Abstract
    {
        public L2(double regularizationParameter)
        {
            super(regularizationParameter);
        }

        @Override
        public DMatrix calculateWeightDecay(DMatrix weigths, double learningRate, int trainingSetSize, double regularizationParameter)
        {
            return weigths.scalarMul(learningRate * regularizationParameter / trainingSetSize);
        }
    }
}

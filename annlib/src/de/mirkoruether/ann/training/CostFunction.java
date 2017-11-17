package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DVector;
import org.jblas.exceptions.SizeException;

public interface CostFunction
{
    public DVector calculateErrorOfLastLayer(DVector netOutput, DVector solution, DVector lastLayerDerivativeActivation);

    public DVector calculateGradient(DVector netOutput, DVector solution);

    public double calculateCosts(DVector netOutput, DVector solution);

    public static class Quadratic implements CostFunction
    {
        @Override
        public double calculateCosts(DVector netOutput, DVector solution)
        {
            if(netOutput.getLength() != solution.getLength())
            {
                throw new SizeException("net output and solution differ in length");
            }

            double result = 0;
            for(int i = 0; i < netOutput.getLength(); i++)
            {
                result += Math.pow(solution.get(i) - netOutput.get(i), 2);
            }

            return result * 0.5;
        }

        @Override
        public DVector calculateErrorOfLastLayer(DVector netOutput, DVector solution, DVector lastLayerDerivativeActivation)
        {
            return calculateGradient(netOutput, solution).elementWiseMulInPlace(lastLayerDerivativeActivation);
        }

        @Override
        public DVector calculateGradient(DVector netOutput, DVector solution)
        {
            return netOutput.sub(solution);
        }
    }

    public static class CrossEntropy implements CostFunction
    {
        @Override
        public double calculateCosts(DVector netOutput, DVector solution)
        {
            if(netOutput.getLength() != solution.getLength())
            {
                throw new SizeException("net output and solution differ in length");
            }

            double result = 0;
            for(int i = 0; i < netOutput.getLength(); i++)
            {
                double a = netOutput.get(i);
                double y = solution.get(i);
                result += y * Math.log(a) + (1 - y) * Math.log(1 - a);
            }

            return -result;
        }

        @Override
        public DVector calculateErrorOfLastLayer(DVector netOutput, DVector solution, DVector lastLayerDerivativeActivation)
        {
            return netOutput.sub(solution);
        }

        @Override
        public DVector calculateGradient(DVector netOutput, DVector solution)
        {
            int le = netOutput.getLength();
            DVector a = netOutput;
            DVector y = solution;
            // y/a - (1-y)/(1-a) element wise
            DVector grad = y.elementWiseDiv(a).subInPlace(DVector.ones(le).subInPlace(y).elementWiseDiv(DVector.ones(le).subInPlace(a)));
            return grad;
        }
    }
}

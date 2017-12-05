package de.mirkoruether.ann.training.costs;

import de.mirkoruether.linalg.DRowVector;
import de.mirkoruether.linalg.SizeException;

public class CrossEntropyCosts implements CostFunction
{
    @Override
    public double calculateCosts(DRowVector netOutput, DRowVector solution)
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
    public DRowVector calculateGradient(DRowVector netOutput, DRowVector solution)
    {
        int le = netOutput.getLength();
        DRowVector a = netOutput;
        DRowVector y = solution;
        // y/a - (1-y)/(1-a) element wise
        DRowVector grad = y.elementWiseDiv(a).subInPlace(DRowVector.ones(le).subInPlace(y).elementWiseDiv(DRowVector.ones(le).subInPlace(a)));
        return grad;
    }

    @Override
    public DRowVector calculateErrorOfLastLayer(DRowVector netOutput, DRowVector solution, DRowVector lastLayerDerivativeActivation)
    {
        return netOutput.sub(solution);
    }
}

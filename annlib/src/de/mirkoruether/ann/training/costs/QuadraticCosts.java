package de.mirkoruether.ann.training.costs;

import de.mirkoruether.linalg.DRowVector;
import de.mirkoruether.linalg.SizeException;

public class QuadraticCosts implements CostFunction
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
            result += Math.pow(solution.get(i) - netOutput.get(i), 2);
        }
        return result * 0.5;
    }

    @Override
    public DRowVector calculateGradient(DRowVector netOutput, DRowVector solution)
    {
        return netOutput.sub(solution);
    }
}

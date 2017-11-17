package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DVector;
import org.jblas.exceptions.SizeException;

public class QuadraticCostFunction implements CostFunction
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
    public DVector calculateGradient(DVector netOutput, DVector solution)
    {
        return netOutput.sub(solution);
    }
}

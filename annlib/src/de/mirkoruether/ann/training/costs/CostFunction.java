package de.mirkoruether.ann.training.costs;

import de.mirkoruether.linalg.DRowVector;

public interface CostFunction
{
    public double calculateCosts(DRowVector netOutput, DRowVector solution);

    public DRowVector calculateGradient(DRowVector netOutput, DRowVector solution);

    public default DRowVector calculateErrorOfLastLayer(DRowVector netOutput, DRowVector solution, DRowVector lastLayerDerivativeActivation)
    {
        return calculateGradient(netOutput, solution).elementWiseMulInPlace(lastLayerDerivativeActivation);
    }
}

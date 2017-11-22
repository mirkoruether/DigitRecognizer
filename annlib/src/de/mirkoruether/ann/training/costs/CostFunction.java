package de.mirkoruether.ann.training.costs;

import de.mirkoruether.linalg.DVector;

public interface CostFunction
{
    public double calculateCosts(DVector netOutput, DVector solution);

    public DVector calculateGradient(DVector netOutput, DVector solution);

    public default DVector calculateErrorOfLastLayer(DVector netOutput, DVector solution, DVector lastLayerDerivativeActivation)
    {
        return calculateGradient(netOutput, solution).elementWiseMulInPlace(lastLayerDerivativeActivation);
    }
}

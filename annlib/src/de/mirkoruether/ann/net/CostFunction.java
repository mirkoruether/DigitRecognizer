package de.mirkoruether.ann.net;

import de.mirkoruether.linalg.DVector;

public interface CostFunction
{
    public DVector calculateGradient(DVector netOutput, DVector solution);

    public double calculateCosts(DVector netOutput, DVector solution);
}

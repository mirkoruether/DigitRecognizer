package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DVector;

public interface NetOutputTest
{
    public boolean test(DVector netOutput, DVector solution);
}

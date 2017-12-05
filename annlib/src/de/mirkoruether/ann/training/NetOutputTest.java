package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DRowVector;

public interface NetOutputTest
{
    public boolean test(DRowVector netOutput, DRowVector solution);
}

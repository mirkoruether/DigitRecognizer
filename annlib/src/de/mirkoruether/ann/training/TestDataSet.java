package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DRowVector;

public class TestDataSet
{
    private final TrainingData[] data;
    private final NetOutputTest test;

    public TestDataSet(TrainingData[] data, NetOutputTest test)
    {
        this.data = data;
        this.test = test;
    }

    public boolean test(DRowVector netOutput, DRowVector solution)
    {
        return test.test(netOutput, solution);
    }

    public TrainingData[] getData()
    {
        return data;
    }

    public NetOutputTest getTest()
    {
        return test;
    }

    public int getLength()
    {
        return data.length;
    }
}

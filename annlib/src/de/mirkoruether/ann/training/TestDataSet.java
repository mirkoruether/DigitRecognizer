package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DVector;

public class TestDataSet
{
    private final TrainingData[] data;
    private final Test test;

    public TestDataSet(TrainingData[] data, Test test)
    {
        this.data = data;
        this.test = test;
    }

    public boolean test(DVector netOutput, DVector solution)
    {
        return test.test(netOutput, solution);
    }

    public TrainingData[] getData()
    {
        return data;
    }

    public Test getTest()
    {
        return test;
    }

    public int getLength()
    {
        return data.length;
    }

    public static interface Test
    {
        public boolean test(DVector netOutput, DVector solution);
    }
}

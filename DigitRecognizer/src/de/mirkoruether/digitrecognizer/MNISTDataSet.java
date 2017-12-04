package de.mirkoruether.digitrecognizer;

import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TrainingData;

public class MNISTDataSet
{
    private final TrainingData[] trainingData;
    private final TestDataSet validationData;
    private final TestDataSet testData;

    public MNISTDataSet(TrainingData[] trainingData, TestDataSet validationData, TestDataSet testData)
    {
        this.trainingData = trainingData;
        this.validationData = validationData;
        this.testData = testData;
    }

    public TrainingData[] getTrainingData()
    {
        return trainingData;
    }

    public TestDataSet getValidationData()
    {
        return validationData;
    }

    public TestDataSet getTestData()
    {
        return testData;
    }
}

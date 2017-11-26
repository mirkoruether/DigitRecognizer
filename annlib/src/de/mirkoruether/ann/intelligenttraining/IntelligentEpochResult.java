package de.mirkoruether.ann.intelligenttraining;

import de.mirkoruether.ann.training.TestResult;

public class IntelligentEpochResult
{
    private int epochNumber;
    private double learningRate;
    private TestResult validationDataTestResult;
    private TestResult testDataTestResult;

    public IntelligentEpochResult(IntelligentEpochResult r)
    {
        this(r.epochNumber, r.learningRate,
             new TestResult(r.validationDataTestResult), new TestResult(r.testDataTestResult));
    }

    public IntelligentEpochResult(int epochNumber, double learningRate,
                                  TestResult validationDataTestResult, TestResult testDataTestResult)
    {
        this.epochNumber = epochNumber;
        this.validationDataTestResult = validationDataTestResult;
        this.testDataTestResult = testDataTestResult;
    }

    public int getEpochNumber()
    {
        return epochNumber;
    }

    public void setEpochNumber(int epochNumber)
    {
        this.epochNumber = epochNumber;
    }

    public double getLearningRate()
    {
        return learningRate;
    }

    public void setLearningRate(double learningRate)
    {
        this.learningRate = learningRate;
    }

    public TestResult getValidationDataTestResult()
    {
        return validationDataTestResult;
    }

    public void setValidationDataTestResult(TestResult validationDataTestResult)
    {
        this.validationDataTestResult = validationDataTestResult;
    }

    public TestResult getTestDataTestResult()
    {
        return testDataTestResult;
    }

    public void setTestDataTestResult(TestResult testDataTestResult)
    {
        this.testDataTestResult = testDataTestResult;
    }
}

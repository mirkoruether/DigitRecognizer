package de.mirkoruether.ann.intelligenttraining;

import de.mirkoruether.ann.training.TestResult;

public class IntelligentEpochResult
{
    private int epochNumber;
    private TestResult validationDataTestResult;
    private TestResult testDataTestResult;

    public IntelligentEpochResult(int epochNumber, TestResult validationDataTestResult, TestResult testDataTestResult)
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

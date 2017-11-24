package de.mirkoruether.ann.intelligenttraining;

import de.mirkoruether.ann.training.StochasticGradientDescentTrainer;
import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TestResult;
import de.mirkoruether.ann.training.TrainingData;
import java.util.function.Consumer;

public class IntelligentTrainer
{
    private StochasticGradientDescentTrainer trainer;
    private TrainingData[] trainingData;
    private TestDataSet validationData;
    private TestDataSet testData;

    public IntelligentTrainer(StochasticGradientDescentTrainer trainer, TrainingData[] trainingData, TestDataSet validationData, TestDataSet testData)
    {
        this.trainer = trainer;
        this.trainingData = trainingData;
        this.validationData = validationData;
        this.testData = testData;
    }

    public void train(double startLearningRate, IntelligentAbortCondition con, Consumer<IntelligentEpochResult> log)
    {
        IntelligentEpochResult result = generateAndLogResult(0, log);

        double currLearningRate = startLearningRate;
        while(!con.abort(result))
        {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    protected IntelligentEpochResult generateAndLogResult(int epoch, Consumer<IntelligentEpochResult> log)
    {
        TestResult vali = trainer.test(validationData);
        TestResult test = trainer.test(testData);
        IntelligentEpochResult r = new IntelligentEpochResult(epoch, vali, test);
        if(log != null)
        {
            log.accept(r);
        }
        return r;
    }

    public StochasticGradientDescentTrainer getTrainer()
    {
        return trainer;
    }

    public void setTrainer(StochasticGradientDescentTrainer trainer)
    {
        this.trainer = trainer;
    }

    public TrainingData[] getTrainingData()
    {
        return trainingData;
    }

    public void setTrainingData(TrainingData[] trainingData)
    {
        this.trainingData = trainingData;
    }

    public TestDataSet getValidationData()
    {
        return validationData;
    }

    public void setValidationData(TestDataSet validationData)
    {
        this.validationData = validationData;
    }

    public TestDataSet getTestData()
    {
        return testData;
    }

    public void setTestData(TestDataSet testData)
    {
        this.testData = testData;
    }
}

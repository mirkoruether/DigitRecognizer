package de.mirkoruether.ann.intelligenttraining;

import de.mirkoruether.ann.training.StochasticGradientDescentTrainer;
import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TestResult;
import de.mirkoruether.ann.training.TrainingData;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public void train(IntelligentAbortCondition abortCondition, Consumer<IntelligentEpochResult> log, double startLearningRate, Supplier<IntelligentAbortCondition> learningRateLowering, double learningRateDivisor)
    {
        IntelligentEpochResult result = generateAndLogResult(0, Double.POSITIVE_INFINITY, log);

        int epoch = 0;
        int epochWithLearningRate = 0;
        double currLearningRate = startLearningRate;
        IntelligentAbortCondition lrLowering = learningRateLowering.get();
        while(!abortCondition.abort(result))
        {
            IntelligentEpochResult lrResult = new IntelligentEpochResult(result);
            lrResult.setEpochNumber(epochWithLearningRate);

            if(lrLowering.abort(lrResult))
            {
                currLearningRate /= learningRateDivisor;
                lrLowering = learningRateLowering.get();

                epochWithLearningRate = 0;
                continue;
            }

            trainer.train(trainingData, currLearningRate, 1);

            epoch++;
            epochWithLearningRate++;
            result = generateAndLogResult(epoch, currLearningRate, log);
        }
    }

    protected IntelligentEpochResult generateAndLogResult(int epoch, double learningRate, Consumer<IntelligentEpochResult> log)
    {
        TestResult vali = trainer.test(validationData);
        TestResult test = trainer.test(testData);
        IntelligentEpochResult r = new IntelligentEpochResult(epoch, learningRate, vali, test);
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

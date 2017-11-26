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

    public void train(Consumer<IntelligentEpochResult> log, double startLearningRate)
    {
        final IntelligentAbortCondition abortCondition = new IntelligentAbortConditionSet()
                .addNoImprovementTestAccuracy(6);

        train(abortCondition, log, startLearningRate);
    }

    public void train(IntelligentAbortCondition abortCondition, Consumer<IntelligentEpochResult> log, double startLearningRate)
    {
        final double learningRateDivisor = 2.0;
        final Supplier<IntelligentAbortCondition> learningRateLowering = ()
                -> new IntelligentAbortConditionSet()
                        .addNoImprovementTestAccuracy(2);

        train(abortCondition, log, startLearningRate, learningRateLowering, learningRateDivisor);
    }

    public void train(IntelligentAbortCondition abortCondition, Consumer<IntelligentEpochResult> log, double startLearningRate, Supplier<IntelligentAbortCondition> learningRateLowering, double learningRateDivisor)
    {
        int epoch = 0;
        long startTime = System.currentTimeMillis();

        int epochWithLearningRate = 0;
        long lrStartTime = System.currentTimeMillis();

        double currLearningRate = startLearningRate;
        IntelligentAbortCondition lrLowering = learningRateLowering.get();
        IntelligentEpochResult result = generateAndLogResult(0, Double.POSITIVE_INFINITY, startTime, 0, log);
        while(!abortCondition.abort(result))
        {
            IntelligentEpochResult lrResult = generateLrResult(result, epochWithLearningRate, lrStartTime);

            if(lrLowering.abort(lrResult))
            {
                currLearningRate /= learningRateDivisor;
                lrLowering = learningRateLowering.get();

                epochWithLearningRate = 0;
                lrStartTime = System.currentTimeMillis();
                continue;
            }

            trainer.train(trainingData, currLearningRate, 1);

            epoch++;
            epochWithLearningRate++;
            result = generateAndLogResult(epoch, currLearningRate, startTime, result.getTotalTime(), log);
        }
    }

    private IntelligentEpochResult generateAndLogResult(int epoch, double learningRate, long startTime, long lastTotalDuration, Consumer<IntelligentEpochResult> log)
    {
        TestResult vali = trainer.test(validationData);
        TestResult test = trainer.test(testData);
        long currTime = System.currentTimeMillis();
        long totalTime = currTime - startTime;
        long epochTime = totalTime - lastTotalDuration;
        IntelligentEpochResult r = new IntelligentEpochResult(epoch, learningRate, totalTime, epochTime, vali, test);
        if(log != null)
        {
            log.accept(r);
        }
        return r;
    }

    private IntelligentEpochResult generateLrResult(IntelligentEpochResult r, int lrEpoch, long lrStartTime)
    {
        IntelligentEpochResult lrResult = new IntelligentEpochResult(r);
        lrResult.setEpochNumber(lrEpoch);

        long currTime = System.currentTimeMillis();
        long totalTime = currTime - lrStartTime;
        long epochTime = lrEpoch == 0 ? totalTime : r.getEpochTime();
        lrResult.setTotalTime(totalTime);
        lrResult.setEpochTime(epochTime);

        return lrResult;
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

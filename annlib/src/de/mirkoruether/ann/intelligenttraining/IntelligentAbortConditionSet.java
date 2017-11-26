package de.mirkoruether.ann.intelligenttraining;

import de.mirkoruether.util.LinqList;
import de.mirkoruether.util.RollingList;
import java.util.Collection;
import java.util.function.Function;

public class IntelligentAbortConditionSet implements IntelligentAbortCondition
{
    private final LinqList<IntelligentAbortCondition> conditions;
    private final LinqList<IntelligentEpochResult> results = new LinqList<>();
    private long startTime = -1;

    public IntelligentAbortConditionSet()
    {
        conditions = new LinqList<>();
    }

    public IntelligentAbortConditionSet(Collection<? extends IntelligentAbortCondition> conditions)
    {
        this.conditions = new LinqList<>(conditions);
    }

    @Override
    public boolean abort(IntelligentEpochResult result)
    {
        results.add(result);

        if(result.getEpochNumber() == 0)
        {
            startTime = System.currentTimeMillis();
        }

        return conditions.one(x -> x.abort(result));
    }

    public LinqList<IntelligentAbortCondition> getConditions()
    {
        return conditions;
    }

    public IntelligentAbortConditionSet add(IntelligentAbortCondition con)
    {
        conditions.add(con);
        return this;
    }

    public IntelligentAbortConditionSet addTimeLimit(long timeLimit)
    {
        return add(x -> (System.currentTimeMillis() - startTime) > (timeLimit * 1000));
    }

    public IntelligentAbortConditionSet addEpochLimit(int epochs)
    {
        return add(x -> x.getEpochNumber() >= epochs);
    }

    public IntelligentAbortConditionSet addTestAccuracyLimit(double accuracy)
    {
        return add(x -> x.getTestDataTestResult().getAccuracy() >= accuracy);
    }

    public IntelligentAbortConditionSet addValidationAccuracyLimit(double accuracy)
    {
        return add(x -> x.getValidationDataTestResult().getAccuracy() >= accuracy);
    }

    public IntelligentAbortConditionSet addTestCostsLimit(double costs)
    {
        return add(x -> x.getTestDataTestResult().getAverageCosts() <= costs);
    }

    public IntelligentAbortConditionSet addValidationCostsLimit(double costs)
    {
        return add(x -> x.getValidationDataTestResult().getAverageCosts() <= costs);
    }

    public IntelligentAbortConditionSet addTestAccuracyStagnation(int epochs, double deltaAccuracy)
    {
        return add(new RollingResultsCondition(epochs)
        {
            @Override
            public boolean abort(RollingList<IntelligentEpochResult> results)
            {
                double max = results.maxValue(x -> x.getTestDataTestResult().getAccuracy());
                double min = results.minValue(x -> x.getTestDataTestResult().getAccuracy());

                return max - min < deltaAccuracy;
            }
        });
    }

    public IntelligentAbortConditionSet addValidationAccuracyStagnation(int epochs, double deltaAccuracy)
    {
        return add(new RollingResultsCondition(epochs)
        {
            @Override
            public boolean abort(RollingList<IntelligentEpochResult> results)
            {
                double max = results.maxValue(x -> x.getValidationDataTestResult().getAccuracy());
                double min = results.minValue(x -> x.getValidationDataTestResult().getAccuracy());

                return max - min < deltaAccuracy;
            }
        });
    }

    public IntelligentAbortConditionSet addAccuracyDeltaLimit(int epochs, double limit)
    {
        return add(new RollingResultsCondition(epochs)
        {
            @Override
            public boolean abort(RollingList<IntelligentEpochResult> results)
            {
                double maxTest = results.maxValue(x -> x.getTestDataTestResult().getAccuracy());
                double minValidation = results.minValue(x -> x.getValidationDataTestResult().getAccuracy());

                return minValidation - maxTest >= limit;
            }
        });
    }

    public IntelligentAbortConditionSet addAccuracyFactorLimit(int epochs, double limit)
    {
        return add(new RollingResultsCondition(epochs)
        {
            @Override
            public boolean abort(RollingList<IntelligentEpochResult> results)
            {
                double maxTest = results.maxValue(x -> x.getTestDataTestResult().getAccuracy());
                double minValidation = results.minValue(x -> x.getValidationDataTestResult().getAccuracy());

                return minValidation / maxTest <= limit;
            }
        });
    }

    public IntelligentAbortConditionSet addNoImprovement(int epochs, Function<IntelligentEpochResult, Double> indicator)
    {
        return add(x -> results.size() - results.maxIndex(indicator) > epochs);
    }

    public IntelligentAbortConditionSet addNoImprovementTestAccuracy(int epochs)
    {
        return addNoImprovement(epochs, x -> x.getTestDataTestResult().getAccuracy());
    }

    public IntelligentAbortConditionSet addNoImprovementValidationAccuracy(int epochs)
    {
        return addNoImprovement(epochs, x -> x.getValidationDataTestResult().getAccuracy());
    }

    public IntelligentAbortConditionSet addNoImprovementTestCosts(int epochs)
    {
        return addNoImprovement(epochs, x -> -1.0 * x.getTestDataTestResult().getAverageCosts());
    }

    public IntelligentAbortConditionSet addNoImprovementValidationCosts(int epochs)
    {
        return addNoImprovement(epochs, x -> -1.0 * x.getValidationDataTestResult().getAverageCosts());
    }

    public static abstract class RollingResultsCondition implements IntelligentAbortCondition
    {
        private final RollingList<IntelligentEpochResult> results;

        public RollingResultsCondition(int capacity)
        {
            this.results = new RollingList<>(capacity);
        }

        @Override
        public boolean abort(IntelligentEpochResult result)
        {
            if(results.size() < results.getCapacity())
            {
                return false;
            }

            results.add(result);
            return abort(results);
        }

        public abstract boolean abort(RollingList<IntelligentEpochResult> results);
    }
}

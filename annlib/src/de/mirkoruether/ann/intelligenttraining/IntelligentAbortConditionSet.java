package de.mirkoruether.ann.intelligenttraining;

import de.mirkoruether.util.LinqList;
import java.util.Collection;
import java.util.function.Function;

public class IntelligentAbortConditionSet implements IntelligentAbortCondition
{
    private final LinqList<IntelligentAbortCondition> conditions;
    private final LinqList<IntelligentEpochResult> results = new LinqList<>();

    public IntelligentAbortConditionSet()
    {
        conditions = new LinqList<>();
    }

    public IntelligentAbortConditionSet(Collection<? extends IntelligentAbortCondition> conditions)
    {
        this.conditions = new LinqList<>(conditions);
    }

    public void reset()
    {
        results.clear();
    }

    @Override
    public boolean abort(IntelligentEpochResult result)
    {
        results.add(result);

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

    public IntelligentAbortConditionSet addRolling(int count, RollingResultsCondition con)
    {
        return add((x) ->
        {
            if(results.size() < count)
            {
                return false;
            }

            return con.abort(results.subList(results.size() - count, results.size()));
        });
    }

    public IntelligentAbortConditionSet addTimeLimit(long timeLimit)
    {
        return add(x -> x.getTotalTime() > (timeLimit * 1000));
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
        return addRolling(epochs, (r) ->
                  {
                      double max = r.maxValue(x -> x.getTestDataTestResult().getAccuracy());
                      double min = r.minValue(x -> x.getTestDataTestResult().getAccuracy());

                      return max - min < deltaAccuracy;
                  });
    }

    public IntelligentAbortConditionSet addValidationAccuracyStagnation(int epochs, double deltaAccuracy)
    {
        return addRolling(epochs, (r) ->
                  {
                      double max = r.maxValue(x -> x.getValidationDataTestResult().getAccuracy());
                      double min = r.minValue(x -> x.getValidationDataTestResult().getAccuracy());

                      return max - min < deltaAccuracy;
                  });
    }

    public IntelligentAbortConditionSet addAccuracyDeltaLimit(int epochs, double limit)
    {
        return addRolling(epochs, (r) ->
                  {
                      double maxTest = r.maxValue(x -> x.getTestDataTestResult().getAccuracy());
                      double minValidation = r.minValue(x -> x.getValidationDataTestResult().getAccuracy());

                      return minValidation - maxTest >= limit;
                  });
    }

    public IntelligentAbortConditionSet addAccuracyFactorLimit(int epochs, double limit)
    {
        return addRolling(epochs, (r) ->
                  {
                      double maxTest = r.maxValue(x -> x.getTestDataTestResult().getAccuracy());
                      double minValidation = r.minValue(x -> x.getValidationDataTestResult().getAccuracy());

                      return minValidation / maxTest <= limit;
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

    public static interface RollingResultsCondition
    {
        public boolean abort(LinqList<IntelligentEpochResult> results);
    }
}

package de.mirkoruether.ann.intelligenttraining;

public interface IntelligentAbortCondition
{
    public boolean abort(IntelligentEpochResult result);
}

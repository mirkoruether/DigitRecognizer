package de.mirkoruether.ann;

import de.mirkoruether.linalg.DRowVector;

public class DetailedResult
{
    private DRowVector[] weightedInputs;
    private DRowVector[] activationsInclInput;

    public DetailedResult(DRowVector[] weightedInputs, DRowVector[] activationsInclInput)
    {
        this.weightedInputs = weightedInputs;
        this.activationsInclInput = activationsInclInput;
    }

    public DRowVector[] getWeightedInputs()
    {
        return weightedInputs;
    }

    public DRowVector getWeightedInput(int layer)
    {
        return weightedInputs[layer];
    }

    public void setWeightedInputs(DRowVector[] weightedInputs)
    {
        this.weightedInputs = weightedInputs;
    }

    public DRowVector[] getActivationsInclInput()
    {
        return activationsInclInput;
    }

    public DRowVector getActivation(int layerPlusOne)
    {
        return activationsInclInput[layerPlusOne];
    }

    public DRowVector getNetOutput()
    {
        return activationsInclInput[activationsInclInput.length - 1];
    }

    public void setActivationsInclInput(DRowVector[] activationsInclInput)
    {
        this.activationsInclInput = activationsInclInput;
    }
}

package de.mirkoruether.ann;

import de.mirkoruether.linalg.DVector;

public class DetailedResult
{
    private DVector[] weightedInputs;
    private DVector[] activationsInclInput;

    public DetailedResult(DVector[] weightedInputs, DVector[] activationsInclInput)
    {
        this.weightedInputs = weightedInputs;
        this.activationsInclInput = activationsInclInput;
    }

    public DVector[] getWeightedInputs()
    {
        return weightedInputs;
    }

    public DVector getWeightedInput(int layer)
    {
        return weightedInputs[layer];
    }

    public void setWeightedInputs(DVector[] weightedInputs)
    {
        this.weightedInputs = weightedInputs;
    }

    public DVector[] getActivationsInclInput()
    {
        return activationsInclInput;
    }

    public DVector getActivation(int layerPlusOne)
    {
        return activationsInclInput[layerPlusOne];
    }

    public DVector getNetOutput()
    {
        return activationsInclInput[activationsInclInput.length - 1];
    }

    public void setActivationsInclInput(DVector[] activationsInclInput)
    {
        this.activationsInclInput = activationsInclInput;
    }
}

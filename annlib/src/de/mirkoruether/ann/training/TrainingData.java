package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DVector;

public class TrainingData
{
    private DVector input;
    private DVector solution;

    public TrainingData(DVector input, DVector solutions)
    {
        this.input = input;
        this.solution = solutions;
    }

    public DVector getInput()
    {
        return input;
    }

    public void setInput(DVector input)
    {
        this.input = input;
    }

    public DVector getSolution()
    {
        return solution;
    }

    public void setSolution(DVector solution)
    {
        this.solution = solution;
    }
}

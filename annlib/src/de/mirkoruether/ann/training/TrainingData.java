package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DRowVector;

public class TrainingData
{
    private DRowVector input;
    private DRowVector solution;

    public TrainingData(DRowVector input, DRowVector solutions)
    {
        this.input = input;
        this.solution = solutions;
    }

    public DRowVector getInput()
    {
        return input;
    }

    public void setInput(DRowVector input)
    {
        this.input = input;
    }

    public DRowVector getSolution()
    {
        return solution;
    }

    public void setSolution(DRowVector solution)
    {
        this.solution = solution;
    }
}

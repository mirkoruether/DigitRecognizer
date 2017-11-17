package de.mirkoruether.ann.training;

import de.mirkoruether.linalg.DVector;

public class TrainingData
{
    private DVector input;
    private DVector solutions;

    public TrainingData(DVector input, DVector solutions)
    {
        this.input = input;
        this.solutions = solutions;
    }

    public DVector getInput()
    {
        return input;
    }

    public void setInput(DVector input)
    {
        this.input = input;
    }

    public DVector getSolutions()
    {
        return solutions;
    }

    public void setSolutions(DVector solutions)
    {
        this.solutions = solutions;
    }
}

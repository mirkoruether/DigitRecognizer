package de.mirkoruether.ann;

import de.mirkoruether.linalg.DFunction;

public class ActivationFunction
{
    public final DFunction f;

    public final DFunction f_derivative;

    public ActivationFunction(DFunction f, DFunction f_derivative)
    {
        this.f = f;
        this.f_derivative = f_derivative;
    }
}

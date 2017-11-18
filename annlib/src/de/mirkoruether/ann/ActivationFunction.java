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

    public static ActivationFunction logistic(double T)
    {
        return new ActivationFunction((x) -> 1 / (1 + Math.exp(-x / T)),
                                      (x) -> Math.exp(x / T) / (T * Math.pow(Math.exp(x / T) + 1.0, 2.0)));
    }

    public static ActivationFunction logistic()
    {
        return logistic(1.0);
    }
}

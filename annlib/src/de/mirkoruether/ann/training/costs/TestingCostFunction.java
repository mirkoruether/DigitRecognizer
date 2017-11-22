package de.mirkoruether.ann.training.costs;

import de.mirkoruether.ann.training.NetOutputTest;
import de.mirkoruether.linalg.DVector;

public class TestingCostFunction implements CostFunction
{
    private CostFunction baseFunc;
    private NetOutputTest test;
    private double factorIfRight;
    private double factorIfWrong;

    public TestingCostFunction(CostFunction baseFunc, NetOutputTest test, double factorIfWrong)
    {
        this(baseFunc, test, 1.0, factorIfWrong);
    }

    public TestingCostFunction(CostFunction baseFunc, NetOutputTest test, double factorIfRight, double factorIfWrong)
    {
        this.baseFunc = baseFunc;
        this.test = test;
        this.factorIfRight = factorIfRight;
        this.factorIfWrong = factorIfWrong;
    }

    @Override
    public double calculateCosts(DVector netOutput, DVector solution)
    {
        return baseFunc.calculateCosts(netOutput, solution) * (test.test(netOutput, solution) ? factorIfRight : factorIfWrong);
    }

    @Override
    public DVector calculateGradient(DVector netOutput, DVector solution)
    {
        return baseFunc.calculateGradient(netOutput, solution).scalarMulInPlace(test.test(netOutput, solution) ? factorIfRight : factorIfWrong);
    }

    @Override
    public DVector calculateErrorOfLastLayer(DVector netOutput, DVector solution, DVector lastLayerDerivativeActivation)
    {
        return baseFunc.calculateErrorOfLastLayer(netOutput, solution, lastLayerDerivativeActivation).scalarMulInPlace(test.test(netOutput, solution) ? factorIfRight : factorIfWrong);
    }

    public CostFunction getBaseFunc()
    {
        return baseFunc;
    }

    public void setBaseFunc(CostFunction baseFunc)
    {
        this.baseFunc = baseFunc;
    }

    public NetOutputTest getTest()
    {
        return test;
    }

    public void setTest(NetOutputTest test)
    {
        this.test = test;
    }

    public double getFactorIfRight()
    {
        return factorIfRight;
    }

    public void setFactorIfRight(double factorIfRight)
    {
        this.factorIfRight = factorIfRight;
    }

    public double getFactorIfWrong()
    {
        return factorIfWrong;
    }

    public void setFactorIfWrong(double factorIfWrong)
    {
        this.factorIfWrong = factorIfWrong;
    }
}

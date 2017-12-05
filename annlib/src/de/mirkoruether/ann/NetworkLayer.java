package de.mirkoruether.ann;

import de.mirkoruether.ann.initialization.NetLayerInitialization;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DRowVector;
import de.mirkoruether.linalg.SizeException;

public class NetworkLayer
{
    private final DMatrix weights;
    private final DRowVector biases;
    private final ActivationFunction activationFunction;

    public NetworkLayer(int outputSize, int inputSize, NetLayerInitialization init, ActivationFunction activationFunction)
    {
        this(init.initWeights(outputSize, inputSize), init.initBiases(outputSize), activationFunction);
    }

    public NetworkLayer(DMatrix weights, DRowVector biases, ActivationFunction activationFunction)
    {
        if(biases.getLength() != weights.getColumnCount())
        {
            throw new SizeException("row count of weights and length of biases differ");
        }

        this.weights = weights;
        this.biases = biases;
        this.activationFunction = activationFunction;
    }

    public DRowVector feedForward(DRowVector in)
    {
        return calculateWeightedInput(in).applyFunctionElementWiseInPlace(activationFunction.f);
    }

    public DRowVector calculateWeightedInput(DRowVector in)
    {
        return in.matrixMul(weights)
                .toRowVectorDuplicate()
                .addInPlace(biases);
    }

    public int getInputSize()
    {
        return weights.getRowCount();
    }

    public int getOutputSize()
    {
        return weights.getColumnCount();
    }

    public DMatrix getWeights()
    {
        return weights;
    }

    public DRowVector getBiases()
    {
        return biases;
    }

    public ActivationFunction getActivationFunction()
    {
        return activationFunction;
    }
}

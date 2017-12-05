package de.mirkoruether.ann;

import de.mirkoruether.ann.initialization.NetLayerInitialization;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.linalg.SizeException;

public class NetworkLayer
{
    private final DMatrix weights;
    private final DVector biases;
    private final ActivationFunction activationFunction;

    public NetworkLayer(int outputSize, int inputSize, NetLayerInitialization init, ActivationFunction activationFunction)
    {
        this(init.initWeights(outputSize, inputSize), init.initBiases(outputSize), activationFunction);
    }

    public NetworkLayer(DMatrix weights, DVector biases, ActivationFunction activationFunction)
    {
        if(biases.getLength() != weights.getColumnCount())
        {
            throw new SizeException("row count of weights and length of biases differ");
        }

        this.weights = weights;
        this.biases = biases;
        this.activationFunction = activationFunction;
    }

    public DVector feedForward(DVector in)
    {
        return calculateWeightedInput(in).applyFunctionElementWiseInPlace(activationFunction.f);
    }

    public DVector calculateWeightedInput(DVector in)
    {
        return in.matrixMul(weights)
                .toVectorDuplicate()
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

    public DVector getBiases()
    {
        return biases;
    }

    public ActivationFunction getActivationFunction()
    {
        return activationFunction;
    }
}

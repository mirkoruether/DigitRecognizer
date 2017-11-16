package de.mirkoruether.ann.net;

import de.mirkoruether.ann.DMatrix;
import de.mirkoruether.ann.DVector;
import org.jblas.exceptions.SizeException;

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
        if(biases.getLength() != weights.getRowCount())
        {
            throw new SizeException("row count of weights and length of biases differ");
        }

        this.weights = weights;
        this.biases = biases;
        this.activationFunction = activationFunction;
    }

    public DVector feedForward(DVector in)
    {
        return in.matrixMul(weights)
                .toVectorReference()
                .addVectorInPlace(biases)
                .applyFunctionElementWiseInPlace(activationFunction.f);
    }

    public int getInputSize()
    {
        return weights.getColumnCount();
    }

    public int getOutputSize()
    {
        return weights.getRowCount();
    }
}

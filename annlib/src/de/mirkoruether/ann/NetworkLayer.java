package de.mirkoruether.ann;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import org.jblas.exceptions.SizeException;

public class NetworkLayer
{
    private final DMatrix weights;
    private final DVector biases;
    private final ActivationFunction activationFunction;

    private boolean learningMode = false;
    private DVector lastActivation = null;
    private DVector lastWeigthedInput = null;

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
        DVector weigthedInput = in.matrixMul(weights)
                .toVectorDuplicate()
                .addInPlace(biases);

        if(learningMode)
        {
            lastWeigthedInput = weigthedInput;
            lastActivation = weigthedInput.applyFunctionElementWise(activationFunction.f);
            return lastActivation.getDuplicate();
        }
        else
        {
            return weigthedInput.applyFunctionElementWiseInPlace(activationFunction.f);
        }
    }

    public int getInputSize()
    {
        return weights.getRowCount();
    }

    public int getOutputSize()
    {
        return weights.getColumnCount();
    }

    public boolean isLearningMode()
    {
        return learningMode;
    }

    public void setLearningMode(boolean learningMode)
    {
        this.learningMode = learningMode;
        if(!learningMode)
        {
            lastActivation = null;
            lastWeigthedInput = null;
        }
    }

    public DMatrix getWeights()
    {
        return weights;
    }

    public DVector getBiases()
    {
        return biases;
    }

    public DVector getLastActivation()
    {
        return lastActivation;
    }

    public DVector getLastWeigthedInput()
    {
        return lastWeigthedInput;
    }

    public ActivationFunction getActivationFunction()
    {
        return activationFunction;
    }
}

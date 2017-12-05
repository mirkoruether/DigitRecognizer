package de.mirkoruether.ann;

import de.mirkoruether.ann.initialization.NetLayerInitialization;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DRowVector;
import de.mirkoruether.linalg.SizeException;

public class NeuralNetwork
{
    private final NetworkLayer[] layers;

    public NeuralNetwork(NetworkLayer[] layers)
    {
        this.layers = layers;
    }

    public NeuralNetwork(int[] sizes, NetLayerInitialization init, ActivationFunction activationFunction)
    {
        this.layers = new NetworkLayer[sizes.length - 1];

        for(int i = 0; i < layers.length; i++)
        {
            layers[i] = new NetworkLayer(sizes[i + 1], sizes[i], init, activationFunction);
        }
    }

    public DRowVector feedForward(DRowVector in)
    {
        if(in.getLength() != getInputSize())
        {
            throw new SizeException("Wrong input size!");
        }

        DRowVector result = in;
        for(NetworkLayer layer : layers)
        {
            result = layer.feedForward(result);
        }
        return result;
    }

    public DetailedResult feedForwardDetailed(DRowVector in)
    {
        if(in.getLength() != getInputSize())
        {
            throw new SizeException("Wrong input size!");
        }

        DRowVector[] act = new DRowVector[layers.length + 1];
        DRowVector[] wIs = new DRowVector[layers.length];

        act[0] = in;
        for(int i = 0; i < layers.length; i++)
        {
            wIs[i] = layers[i].calculateWeightedInput(act[i]);
            act[i + 1] = wIs[i].applyFunctionElementWise(layers[i].getActivationFunction().f);
        }

        return new DetailedResult(wIs, act);
    }

    public int getInputSize()
    {
        return layers[0].getInputSize();
    }

    public int getOutputSize()
    {
        return layers[layers.length - 1].getOutputSize();
    }

    public NetworkLayer[] getLayers()
    {
        return layers;
    }

    public int getLayerCount()
    {
        return layers.length;
    }

    public NetworkLayer getLayer(int index)
    {
        return layers[index];
    }

    public NetworkLayer getOutputLayer()
    {
        return layers[layers.length - 1];
    }

    public DRowVector[] getBiases()
    {
        DRowVector[] result = new DRowVector[layers.length];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = layers[i].getBiases();
        }
        return result;
    }

    public DMatrix[] getWeights()
    {
        DMatrix[] result = new DMatrix[layers.length];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = layers[i].getWeights();
        }
        return result;
    }
}

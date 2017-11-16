package de.mirkoruether.ann.net;

import de.mirkoruether.ann.DVector;
import org.jblas.exceptions.SizeException;

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

    public DVector feedForward(DVector in)
    {
        if(in.getLength() != getInputSize())
        {
            throw new SizeException("Wrong input size!");
        }

        DVector result = in;
        for(NetworkLayer layer : layers)
        {
            result = layer.feedForward(result);
        }
        return result;
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
}

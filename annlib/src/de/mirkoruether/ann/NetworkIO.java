package de.mirkoruether.ann;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.util.Serializer;
import java.io.File;
import java.io.Serializable;

public class NetworkIO
{
    public static final String BIASES_PREFIX = "biases/layer";
    public static final String WEIGHTS_PREFIX = "weights/layer";

    public static void saveNetworkData(NeuralNetwork net, File f)
    {
        saveNetworkData(net.getWeights(), net.getBiases(), f);
    }

    public static void saveNetworkData(DMatrix[] weights, DVector[] biases, File f)
    {
        if(biases.length != weights.length)
        {
            throw new IllegalArgumentException("Weights and biases differ in length");
        }

        Serializable[] objs = new Serializable[biases.length + weights.length];
        System.arraycopy(weights, 0, objs, 0, weights.length);
        System.arraycopy(biases, 0, objs, weights.length, biases.length);

        Serializer.serializeToFile(f, objs);
    }

    public static NeuralNetwork loadNetwork(File f, ActivationFunction func)
    {
        Serializable[] arr = Serializer.deserializeFile(f, Serializable[].class);
        int lCount = arr.length / 2;
        NetworkLayer[] layers = new NetworkLayer[lCount];

        for(int i = 0; i < lCount; i++)
        {
            layers[i] = new NetworkLayer((DMatrix)arr[i], (DVector)arr[lCount + i], func);
        }
        return new NeuralNetwork(layers);
    }

    private NetworkIO()
    {
    }
}

package de.mirkoruether.ann;

import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.util.LinqList;
import de.mirkoruether.util.Serializer;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

public class NetworkIO
{
    public static final String BIASES_PREFIX = "biases/layer";
    public static final String WEIGHTS_PREFIX = "weights/layer";

    public static void saveNetworkData(NeuralNetwork net, File zip)
    {
        saveNetworkData(net.getBiases(), net.getWeights(), zip);
    }

    public static void saveNetworkData(DVector[] biases, DMatrix[] weights, File zip)
    {
        Serializable[] objs = new Serializable[biases.length + weights.length];
        System.arraycopy(biases, 0, objs, 0, biases.length);
        System.arraycopy(weights, 0, objs, biases.length, weights.length);

        String[] names = new String[objs.length];
        for(int i = 0; i < biases.length; i++)
        {
            names[i] = BIASES_PREFIX + i;
        }
        for(int i = 0; i < weights.length; i++)
        {
            names[i + biases.length] = WEIGHTS_PREFIX + (i);
        }

        Serializer.serializeToZip(zip, objs, names);
    }

    public static NeuralNetwork loadNetwork(File zip, ActivationFunction func)
    {
        Map<String, Object> map = Serializer.deserializeZip(zip);
        LinqList<NetworkLayer> layers = new LinqList<>();

        for(int i = 0; map.containsKey(BIASES_PREFIX + i)
                       && map.containsKey(WEIGHTS_PREFIX + i); i++)
        {
            DVector biases = (DVector)map.get(BIASES_PREFIX + i);
            DMatrix weights = (DMatrix)map.get(WEIGHTS_PREFIX + i);
            layers.add(new NetworkLayer(weights, biases, func));
        }

        return new NeuralNetwork(layers.toArray(NetworkLayer.class));
    }

    private NetworkIO()
    {
    }
}

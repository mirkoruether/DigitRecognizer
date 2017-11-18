package de.mirkoruether.digitrecognizer;

import de.mirkoruether.ann.ActivationFunction;
import de.mirkoruether.ann.NetLayerInitialization;
import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.ann.training.CostFunction;
import de.mirkoruether.ann.training.CostFunctionRegularization;
import de.mirkoruether.ann.training.StochasticGradientDescentTrainer;
import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TrainingData;
import de.mirkoruether.linalg.DMatrix;
import java.util.function.Supplier;

public class Test
{
    private static long timestamp = System.currentTimeMillis();

    public static void main(String[] args)
    {
        int[] sizes = new int[]
        {
            784, 30, 10
        };

        NeuralNetwork net = timeFunc("Net creation", () -> new NeuralNetwork(sizes, new NetLayerInitialization.Gaussian(), ActivationFunction.logistic(1.0)));

        StochasticGradientDescentTrainer sgdt = new StochasticGradientDescentTrainer(net, new CostFunction.CrossEntropy(), new CostFunctionRegularization.L2(5));

        TrainingData[] training = timeFunc("Training data loading", () -> MNISTLoader.importTrainingData("data/train-labels-idx1-ubyte.gz", "data/train-images-idx3-ubyte.gz"));
        TrainingData[] testData = timeFunc("Test data loading", () -> MNISTLoader.importTrainingData("data/t10k-labels-idx1-ubyte.gz", "data/t10k-images-idx3-ubyte.gz"));
        TestDataSet test = new TestDataSet(testData, (o, s) -> s.get(o.indexOfMaxium()) == 1.0);

        System.out.println(timeFunc("Epoch 0: Testing", () -> sgdt.test(test)).toString());

        for(int i = 1; i <= 5; i++)
        {
            timeFunc("Epoch " + i + ": Training with learning rate 1", () -> sgdt.trainEpoch(training, 1, 10));
            System.out.println(timeFunc("Epoch " + i + ": Testing", () -> sgdt.test(test)).toString());
        }

        for(int i = 6; i <= 10; i++)
        {
            timeFunc("Epoch " + i + ": Training with learning rate 0.1", () -> sgdt.trainEpoch(training, 0.1, 10));
            System.out.println(timeFunc("Epoch " + i + ": Testing", () -> sgdt.test(test)).toString());
        }
    }

    private static void timeFunc(String name, Runnable func)
    {
        timeFunc(name, () ->
         {
             func.run();
             return new Object();
         });
    }

    private static <T> T timeFunc(String name, Supplier<T> func)
    {
        System.out.println(name + " started.");
        time();
        T result = func.get();
        System.out.println(name + " finished. " + (double)time() / 1000 + "s elapsed");
        return result;
    }

    private static long time()
    {
        long curr = System.currentTimeMillis();
        long result = curr - timestamp;
        timestamp = curr;
        return result;
    }

    private static void printMatrix(DMatrix m)
    {
        System.out.println("--Begin-Matrix--");
        for(int i = 0; i < m.getRowCount(); i++)
        {
            for(int j = 0; j < m.getColumnCount(); j++)
            {
                char[] space = emptyCharArray(20);
                char[] val = String.valueOf(m.get(i, j)).toCharArray();
                System.arraycopy(val, 0, space, 0, Math.min(space.length, val.length));
                System.out.print(new String(space));
            }
            System.out.println();
        }
        System.out.println("--End-Matrix--");
    }

    private static char[] emptyCharArray(int length)
    {
        char[] arr = new char[length];
        for(int i = 0; i < length; i++)
        {
            arr[i] = ' ';
        }
        return arr;
    }
}

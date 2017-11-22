package de.mirkoruether.digitrecognizer;

import de.mirkoruether.ann.ActivationFunction;
import de.mirkoruether.ann.NetLayerInitialization;
import de.mirkoruether.ann.NetworkIO;
import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.ann.training.MomentumSGDTrainer;
import de.mirkoruether.ann.training.StochasticGradientDescentTrainer;
import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TestResult;
import de.mirkoruether.ann.training.TrainingData;
import de.mirkoruether.ann.training.costs.CrossEntropyCosts;
import de.mirkoruether.ann.training.regularization.L2Regularization;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.util.ParallelExecution;
import de.mirkoruether.util.Stopwatch;
import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

public class Test
{
    private static TrainingData[] training;
    private static TestDataSet test;

    public static void main(String[] args)
    {
        loadMNIST();

        int[] sizes = new int[]
        {
            784, 100, 10
        };

        NeuralNetwork net = new NeuralNetwork(sizes, new NetLayerInitialization.NormalizedGaussian(), ActivationFunction.logistic());
        MomentumSGDTrainer trainer = new MomentumSGDTrainer(net, new CrossEntropyCosts(), new L2Regularization(5.0), 0.7);

        trainAndTest(1, (e) -> 0.1, 10, trainer);

        TestResult r = trainer.test(test);
        File f = new File(String.format("net_%.2f_percent_accuracy.zip", r.getAccuracy() * 100.0).replace('.', '-'));
        NetworkIO.saveNetworkData(net, f);
    }

    @SafeVarargs
    private static void compareConfigs(int epochs, Function<Integer, Double> learningRateFunc,
                                       int batchSize, int iterations,
                                       Supplier<StochasticGradientDescentTrainer>... configs)
    {
        double[] results = new double[configs.length];

        for(int i = 0; i < results.length; i++)
        {
            System.out.println();
            System.out.printf("------ CONFIG %d -----%n", i);
            System.out.println();

            results[i] = getAccuracyForConfig(epochs, learningRateFunc, batchSize, configs[i], iterations);

            System.out.println();
            System.out.printf("-- END OF CONFIG %d --%n", i);
            System.out.println();
        }

        String learningRates = "";
        for(int i = 0; i < epochs; i++)
        {
            if(i > 0)
                learningRates += ";";
            learningRates += learningRateFunc.apply(i);
        }

        System.out.println();
        System.out.println("------ RESULTS ------");
        System.out.println();
        System.out.printf("Conditions: epochs:%d, learning rates:{%s}, batchSize:%d, iterations per config:%d%n",
                          epochs, learningRates, batchSize, iterations);
        for(int i = 0; i < results.length; i++)
        {
            System.out.printf("Accuracy of configuration %d: %.4f%%%n", i, results[i] * 100);
        }
        System.out.println();
        System.out.println("--- END OF RESULT ---");
        System.out.println();
    }

    private static double getAccuracyForConfig(int epochs, Function<Integer, Double> learningRateFunc, int batchSize,
                                               Supplier<StochasticGradientDescentTrainer> sgdtSup, int iterations)
    {
        double sum = 0.0;
        for(int i = 0; i < iterations; i++)
        {
            System.out.println("---Start of Iteration " + (i + 1) + "---");
            StochasticGradientDescentTrainer sgdt = sgdtSup.get();

            sum += trainAndTest(epochs, learningRateFunc, batchSize, sgdt);
            System.out.println("---End of Iteration " + (i + 1) + "---");
        }
        return sum / iterations;
    }

    private static double trainAndTest(int epochs, Function<Integer, Double> learningRateFunc, int batchSize, StochasticGradientDescentTrainer sgdt)
    {
        return ParallelExecution.inExecutorF((ex) ->
        {
            for(int i = 0; i < epochs; i++)
            {
                double learningRate = learningRateFunc.apply(i);
                System.out.println(timeFunc("Epoch " + i + ": Testing", () -> sgdt.test(test)).toString());
                timeFunc("Epoch " + (i + 1) + ": Training with learning rate " + learningRate, () -> sgdt.train(training, learningRate, batchSize, 1, ex));
            }
            TestResult r = timeFunc("Final Testing for this iteration", () -> sgdt.test(test));
            System.out.println(r.toString());
            return r.getAccuracy();
        }, 20);
    }

    private static void loadMNIST()
    {
        training = timeFunc("Training data loading", () -> MNISTLoader.importTrainingData("data/train-labels-idx1-ubyte.gz", "data/train-images-idx3-ubyte.gz"));
        TrainingData[] testData = timeFunc("Test data loading", () -> MNISTLoader.importTrainingData("data/t10k-labels-idx1-ubyte.gz", "data/t10k-images-idx3-ubyte.gz"));
        test = new TestDataSet(testData, (o, s) -> s.get(o.indexOfMaxium()) == 1.0);
    }

    private static void timeFunc(String name, Runnable func)
    {
        Stopwatch.timeExecutionToStream(func, name, System.out);
    }

    private static <T> T timeFunc(String name, Supplier<T> func)
    {
        return Stopwatch.timeExecutionToStream(func, name, System.out);
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

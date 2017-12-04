package de.mirkoruether.digitrecognizer;

import de.mirkoruether.ann.ActivationFunction;
import de.mirkoruether.ann.NetworkIO;
import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.ann.initialization.NormalizedGaussianInitialization;
import de.mirkoruether.ann.intelligenttraining.IntelligentAbortConditionSet;
import de.mirkoruether.ann.intelligenttraining.IntelligentEpochResult;
import de.mirkoruether.ann.intelligenttraining.IntelligentTrainer;
import de.mirkoruether.ann.training.MomentumSGDTrainer;
import de.mirkoruether.ann.training.StochasticGradientDescentTrainer;
import de.mirkoruether.ann.training.TestResult;
import de.mirkoruether.ann.training.costs.CostFunction;
import de.mirkoruether.ann.training.costs.CrossEntropyCosts;
import de.mirkoruether.ann.training.costs.TestingCostFunction;
import de.mirkoruether.ann.training.regularization.L2Regularization;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.util.ParallelExecution;
import de.mirkoruether.util.Stopwatch;
import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

public class Test
{
    private final static int VALIDATION_LENGTH = 10000;
    private final static String MNIST_DATA_PATH = "./data";

    private static MNISTDataSet MNIST;

    public static void main(String[] args)
    {
        MNIST = timeFunc("MNIST data loading", () -> MNISTLoader.loadMNIST(MNIST_DATA_PATH, VALIDATION_LENGTH));
        System.out.println();

        int[] sizes = new int[]
        {
            784, 100, 10
        };

        NeuralNetwork net = new NeuralNetwork(sizes, new NormalizedGaussianInitialization(), ActivationFunction.logistic());
        CostFunction costs = new TestingCostFunction(new CrossEntropyCosts(), MNIST.getTestData().getTest(), 1.5);
        MomentumSGDTrainer trainer = new MomentumSGDTrainer(net, 10, costs, new L2Regularization(3.0), 0.6);

        IntelligentAbortConditionSet learningRateDecrease = new IntelligentAbortConditionSet()
                .addNoImprovementTestAccuracy(2);

        IntelligentAbortConditionSet abort = new IntelligentAbortConditionSet()
                .addNoImprovementTestAccuracy(6);

        IntelligentTrainer it = new IntelligentTrainer(trainer, MNIST.getTrainingData(), MNIST.getValidationData(), MNIST.getTestData());
        it.train(abort, x -> logEpochResult(x), 0.1, learningRateDecrease, 2.0);

        TestResult r = trainer.test(MNIST.getTestData());
        if(r.getAccuracy() > 0.98)
        {
            File f = new File(String.format("net_%.2f_percent_accuracy", r.getAccuracy() * 100.0).replace('.', '-'));
            NetworkIO.saveNetworkData(net, f);
        }
    }

    private static void logEpochResult(IntelligentEpochResult r)
    {
        System.out.printf("---------- Epoch " + intToString(r.getEpochNumber(), 3) + " ----------%n");
        logData(31, "Epoch duration", "%.2fs", r.getEpochTime() / 1000.0);
        logData(31, "Total time elapsed", "%.2fs", r.getTotalTime() / 1000.0);
        logData(31, "Learning rate", "%.4f", r.getLearningRate());
        logData(31, "Validation costs", "%.4f", r.getValidationDataTestResult().getAverageCosts());
        logData(31, "Test costs", "%.4f", r.getTestDataTestResult().getAverageCosts());
        logData(31, "Validation accuracy", "%.2f%%", r.getValidationDataTestResult().getAccuracy() * 100);
        logData(31, "Test accuracy", "%.2f%%", r.getTestDataTestResult().getAccuracy() * 100);
        System.out.printf("-------------------------------%n%n");
    }

    private static void logData(int totalMinLength, String name, String valuePattern, Object... objs)
    {
        String n = name + ": ";
        String v = String.format(valuePattern, objs);
        String r = n + v;
        while(r.length() < totalMinLength)
        {
            n += " ";
            r = n + v;
        }
        System.out.println(r);
    }

    private static String intToString(int n, int minLength)
    {
        String r = String.valueOf(n);
        while(r.length() < minLength)
        {
            r = "0" + r;
        }
        return r;
    }

    @SafeVarargs
    private static void compareConfigs(int epochs, Function<Integer, Double> learningRateFunc,
                                       int iterations, Supplier<StochasticGradientDescentTrainer>... configs)
    {
        double[] results = new double[configs.length];

        for(int i = 0; i < results.length; i++)
        {
            System.out.println();
            System.out.printf("------ CONFIG %d -----%n", i);
            System.out.println();

            results[i] = getAccuracyForConfig(epochs, learningRateFunc, configs[i], iterations);

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
        System.out.printf("Conditions: epochs:%d, learning rates:{%s}, iterations per config:%d%n",
                          epochs, learningRates, iterations);
        for(int i = 0; i < results.length; i++)
        {
            System.out.printf("Accuracy of configuration %d: %.4f%%%n", i, results[i] * 100);
        }
        System.out.println();
        System.out.println("--- END OF RESULT ---");
        System.out.println();
    }

    private static double getAccuracyForConfig(int epochs, Function<Integer, Double> learningRateFunc,
                                               Supplier<StochasticGradientDescentTrainer> sgdtSup, int iterations)
    {
        double sum = 0.0;
        for(int i = 0; i < iterations; i++)
        {
            System.out.println("---Start of Iteration " + (i + 1) + "---");
            StochasticGradientDescentTrainer sgdt = sgdtSup.get();

            sum += trainAndTest(epochs, learningRateFunc, sgdt);
            System.out.println("---End of Iteration " + (i + 1) + "---");
        }
        return sum / iterations;
    }

    private static double trainAndTest(int epochs, Function<Integer, Double> learningRateFunc, StochasticGradientDescentTrainer sgdt)
    {
        return ParallelExecution.inExecutorF((ex) ->
        {
            for(int i = 0; i < epochs; i++)
            {
                double learningRate = learningRateFunc.apply(i);
                System.out.println(timeFunc("Epoch " + i + ": Testing", () -> sgdt.test(MNIST.getTestData())).toString());
                timeFunc("Epoch " + (i + 1) + ": Training with learning rate " + learningRate, () -> sgdt.train(MNIST.getTrainingData(), learningRate, 1, ex));
            }
            TestResult r = timeFunc("Final Testing for this iteration", () -> sgdt.test(MNIST.getTestData()));
            System.out.println(r.toString());
            return r.getAccuracy();
        }, 20);
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

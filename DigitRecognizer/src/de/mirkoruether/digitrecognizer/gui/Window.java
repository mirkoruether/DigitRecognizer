package de.mirkoruether.digitrecognizer.gui;

import de.mirkoruether.ann.ActivationFunction;
import de.mirkoruether.ann.NetworkIO;
import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.ann.initialization.NormalizedGaussianInitialization;
import de.mirkoruether.ann.training.MomentumSGDTrainer;
import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TrainingData;
import de.mirkoruether.ann.training.costs.CrossEntropyCosts;
import de.mirkoruether.ann.training.regularization.L2Regularization;
import de.mirkoruether.digitrecognizer.MNISTLoader;
import de.mirkoruether.util.Stopwatch;
import java.awt.EventQueue;
import java.io.File;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class Window extends JFrame
{
    private static final long serialVersionUID = -7431265748893293519L;

    private static final File NET_FILE = new File("net.zip");
    private static final String TITLEPATTEN = "Ziffernerkenner - %s";

    private TrainingData[] training;
    private TestDataSet test;

    private final JTabbedPane tabs;
    private final PredictPanel predictPanel;

    private NeuralNetwork net;

    public Window()
    {
        this.tabs = new JTabbedPane();
        this.predictPanel = new PredictPanel(this);

        tabs.add("Ziffererkennung", predictPanel);
        add(tabs);

        setTitle(String.format(TITLEPATTEN, "Bereit"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public void initNet()
    {
        if(NET_FILE.exists())
        {
            net = NetworkIO.loadNetwork(NET_FILE, ActivationFunction.logistic());
        }
        else
        {
            net = trainNet();
            NetworkIO.saveNetworkData(net, NET_FILE);
        }
    }

    private NeuralNetwork trainNet()
    {
        if(training == null)
        {
            loadMNIST();
        }

        int[] sizes =
        {
            784, 30, 10
        };

        NeuralNetwork newNet = new NeuralNetwork(sizes, new NormalizedGaussianInitialization(), ActivationFunction.logistic());
        MomentumSGDTrainer trainer = new MomentumSGDTrainer(newNet, new CrossEntropyCosts(),
                                                            new L2Regularization(5.0), 0.75);

        timeFunc("Neuronales Netz wird trainiert", () -> trainer.train(training, 0.3, 10, 1));
        System.out.println(timeFunc("Neuronales Netz wird getestet", () -> trainer.test(test)));

        return newNet;
    }

    public NeuralNetwork getNet()
    {
        return net;
    }

    public void setNet(NeuralNetwork net)
    {
        this.net = net;
    }

    private void loadMNIST()
    {
        training = timeFunc("Trainingsdaten werden geladen", () -> MNISTLoader.importTrainingData("data/train-labels-idx1-ubyte.gz", "data/train-images-idx3-ubyte.gz"));
        TrainingData[] testData = timeFunc("Testdaten werden geladen", () -> MNISTLoader.importTrainingData("data/t10k-labels-idx1-ubyte.gz", "data/t10k-images-idx3-ubyte.gz"));
        test = new TestDataSet(testData, (o, s) -> s.get(o.indexOfMaxium()) == 1.0);
    }

    private void timeFunc(String name, Runnable func)
    {
        timeFunc(name, () ->
         {
             func.run();
             return true;
         });
    }

    private <T> T timeFunc(String name, Supplier<T> func)
    {
        setTitle(String.format(TITLEPATTEN, name));
        T result = Stopwatch.timeExecutionToStream(func, name, System.out);
        setTitle(String.format(TITLEPATTEN, "Bereit"));
        return result;
    }

    public TrainingData[] getTraining()
    {
        return training;
    }

    public TestDataSet getTest()
    {
        return test;
    }

    public static void main(String[] args)
    {
        //NET_FILE.delete();
        EventQueue.invokeLater(() ->
        {
            Window w = new Window();
            w.setVisible(true);
            w.initNet();
        });
    }
}

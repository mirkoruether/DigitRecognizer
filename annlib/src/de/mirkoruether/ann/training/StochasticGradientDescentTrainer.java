package de.mirkoruether.ann.training;

import de.mirkoruether.ann.DetailedResult;
import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.ann.training.costs.CostFunction;
import de.mirkoruether.ann.training.regularization.CostFunctionRegularization;
import de.mirkoruether.linalg.DFunction;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.util.ParallelExecution;
import de.mirkoruether.util.Randomizer;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class StochasticGradientDescentTrainer
{
    private final NeuralNetwork net;
    private int batchSize;
    private CostFunction costs;
    private CostFunctionRegularization reg;

    public StochasticGradientDescentTrainer(NeuralNetwork net, int batchSize, CostFunction costs, CostFunctionRegularization reg)
    {
        this.net = Objects.requireNonNull(net);
        this.batchSize = batchSize;
        this.costs = Objects.requireNonNull(costs);
        this.reg = reg;
    }

    public StochasticGradientDescentTrainer(NeuralNetwork net, int batchSize, CostFunction costs)
    {
        this(net, batchSize, costs, null);
    }

    public TestResult[] trainAndTest(TrainingData[] trainingData, TestDataSet testData, double learningRate, int epochs)
    {
        return ParallelExecution.inExecutorF((ex) -> trainAndTest(trainingData, testData, learningRate, epochs, ex), -1);
    }

    public TestResult[] trainAndTest(TrainingData[] trainingData, TestDataSet testData, double learningRate, int epochs, ExecutorService executer)
    {
        TestResult[] results = new TestResult[epochs + 1];
        results[0] = test(testData);

        for(int i = 0; i < epochs; i++)
        {
            trainEpoch(trainingData, learningRate, executer);
            results[i + 1] = test(testData);
        }

        return results;
    }

    public TestResult test(TestDataSet testData)
    {
        double costSum = 0.0;
        int correct = 0;
        for(TrainingData d : testData.getData())
        {
            DVector out = net.feedForward(d.getInput());
            costSum += costs.calculateCosts(out, d.getSolution());
            correct += testData.test(out, d.getSolution()) ? 1 : 0;
        }

        return new TestResult(testData.getLength(), correct, costSum / testData.getLength());
    }

    public void train(TrainingData[] trainingData, double learningRate, int epochs)
    {
        ParallelExecution.inExecutor((ex) -> train(trainingData, learningRate, epochs, ex), -1);
    }

    public void train(TrainingData[] trainingData, double learningRate, int epochs, ExecutorService executer)
    {
        for(int i = 0; i < epochs; i++)
        {
            trainEpoch(trainingData, learningRate, executer);
        }
    }

    protected void trainEpoch(TrainingData[] trainingData, double learningRate, ExecutorService executer)
    {
        TrainingData[] shuffled = Randomizer.shuffle(trainingData, TrainingData.class);

        for(int i = 0; i < trainingData.length; i += batchSize)
        {
            TrainingData[] batch = new TrainingData[batchSize];
            System.arraycopy(shuffled, i, batch, 0, batchSize);
            trainBatch(batch, learningRate, trainingData.length, executer);
        }
    }

    protected void trainBatch(TrainingData[] trainingDataBatch, double learningRate, int trainingDataSize, ExecutorService executer)
    {
        Function<TrainingData, LayerInfos> func = (x)
                ->
        {
            DetailedResult netOutput = net.feedForwardDetailed(x.getInput());
            return new LayerInfos(netOutput.getActivationsInclInput(),
                                  calculateErrorVectors(netOutput, x.getSolution())
            );
        };

        ParallelExecution<TrainingData, LayerInfos> exec = new ParallelExecution<>(func, LayerInfos.class, executer);
        LayerInfos[] layerInfos = exec.get(trainingDataBatch);

        updateWeights(layerInfos, learningRate, trainingDataSize);
        updateBiases(layerInfos, learningRate);
    }

    protected DVector[] calculateErrorVectors(DetailedResult netResult, DVector solution)
    {
        DVector[] error = new DVector[net.getLayerCount()];

        int L = net.getLayerCount() - 1;

        error[L] = costs.calculateErrorOfLastLayer(netResult.getNetOutput(), solution, calculateActivationDerivativeAtLayer(netResult, L));

        for(int la = L - 1; la >= 0; la--)
        {
            error[la] = error[la + 1].matrixMul(net.getLayer(la + 1).getWeights().transpose())
                    .toVectorDuplicate()
                    .elementWiseMulInPlace(calculateActivationDerivativeAtLayer(netResult, la));
        }

        return error;
    }

    protected DVector calculateActivationDerivativeAtLayer(DetailedResult netOutput, int layer)
    {
        DFunction activationFuncDerivative = getNet().getLayer(layer).getActivationFunction().f_derivative;
        return netOutput.getWeightedInput(layer).applyFunctionElementWise(activationFuncDerivative);
    }

    protected void updateWeights(LayerInfos[] layerInfos, double learningRate, int trainingDataSize)
    {
        for(int la = 0; la < net.getLayerCount(); la++)
        {
            // sum(x, a[x,l-1]T * delta[x,l])
            DMatrix sum = layerInfos[0].getAct(la).transpose()
                    .matrixMul(layerInfos[0].getError(la));

            for(int x = 1; x < layerInfos.length; x++)
            {
                // a[x,l-1]T * delta[x,l]
                DMatrix toAdd = layerInfos[x].getAct(la).transpose()
                        .matrixMul(layerInfos[x].getError(la));

                sum.addInPlace(toAdd);
            }

            // eta/m
            double factor = learningRate / layerInfos.length;

            DMatrix decay = sum.scalarMulInPlace(factor);

            if(reg != null)
            {
                decay.addInPlace(reg.calculateWeightDecay(net.getLayer(la).getWeights(), learningRate, trainingDataSize));
            }

            reduceWeigths(la, decay);
        }
    }

    protected void reduceWeigths(int layer, DMatrix decayInclRegularization)
    {
        net.getLayer(layer).getWeights().subInPlace(decayInclRegularization);
    }

    protected void updateBiases(LayerInfos[] layerInfos, double learningRate)
    {
        for(int la = 0; la < net.getLayerCount(); la++)
        {
            // sum(x, delta[x,l])
            DVector sum = layerInfos[0].getError(la).getDuplicate();
            for(int x = 1; x < layerInfos.length; x++)
            {
                sum.addInPlace(layerInfos[x].getError(la));
            }

            // eta/m
            double factor = learningRate / layerInfos.length;

            net.getLayer(la).getBiases()
                    .subInPlace(sum.scalarMulInPlace(factor));
        }
    }

    public NeuralNetwork getNet()
    {
        return net;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public CostFunction getCosts()
    {
        return costs;
    }

    public void setCosts(CostFunction costs)
    {
        this.costs = costs;
    }

    public CostFunctionRegularization getReg()
    {
        return reg;
    }

    public void setReg(CostFunctionRegularization reg)
    {
        this.reg = reg;
    }

    protected static class LayerInfos
    {
        private final DVector[] lastActivations;
        private final DVector[] errors;

        protected LayerInfos(DVector[] lastActivations, DVector[] errors)
        {
            this.lastActivations = lastActivations;
            this.errors = errors;
        }

        public DVector[] getLastActivations()
        {
            return lastActivations;
        }

        public DVector[] getErrors()
        {
            return errors;
        }

        public DVector getError(int layer)
        {
            return errors[layer];
        }

        public DVector getAct(int layerPlusOne)
        {
            return lastActivations[layerPlusOne];
        }
    }
}

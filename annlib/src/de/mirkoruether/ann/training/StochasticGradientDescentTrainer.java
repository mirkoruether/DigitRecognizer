package de.mirkoruether.ann.training;

import de.mirkoruether.ann.NetworkLayer;
import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.linalg.DFunction;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.util.Randomizer;
import java.util.Objects;

public class StochasticGradientDescentTrainer
{
    private final NeuralNetwork net;
    private CostFunction costs;
    private CostFunctionRegularization reg;

    public StochasticGradientDescentTrainer(NeuralNetwork net, CostFunction costs, CostFunctionRegularization reg)
    {
        this.costs = Objects.requireNonNull(costs);
        this.net = Objects.requireNonNull(net);
        this.reg = reg;
    }

    public StochasticGradientDescentTrainer(NeuralNetwork net, CostFunction costs)
    {
        this(net, costs, null);
    }

    public TestResult[] trainAndTest(TrainingData[] trainingData, TestDataSet testData, double learningRate, int batchSize, int epochs)
    {
        TestResult[] results = new TestResult[epochs + 1];
        results[0] = test(testData);

        for(int i = 0; i < epochs; i++)
        {
            trainEpoch(trainingData, learningRate, batchSize);
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

    public void train(TrainingData[] trainingData, double learningRate, int batchSize, int epochs)
    {
        for(int i = 0; i < epochs; i++)
        {
            trainEpoch(trainingData, learningRate, batchSize);
        }
    }

    protected void trainEpoch(TrainingData[] trainingData, double learningRate, int batchSize)
    {
        TrainingData[] shuffled = Randomizer.shuffle(trainingData, TrainingData.class);
        for(int i = 0; i < trainingData.length; i += batchSize)
        {
            TrainingData[] batch = new TrainingData[batchSize];
            System.arraycopy(shuffled, i, batch, 0, batchSize);
            trainBatch(batch, learningRate, trainingData.length);
        }
    }

    protected void trainBatch(TrainingData[] trainingDataBatch, double learningRate, int trainingDataSize)
    {
        try
        {
            net.setLearningMode(true);

            int batchSize = trainingDataBatch.length;

            DVector[][] errors = new DVector[batchSize][];
            DVector[][] activationsInclInput = new DVector[batchSize][];
            for(int x = 0; x < batchSize; x++)
            {
                DVector netOutput = net.feedForward(trainingDataBatch[x].getInput());
                activationsInclInput[x] = getActivationsInclInput(trainingDataBatch[x].getInput());
                errors[x] = calculateErrorVectors(netOutput, trainingDataBatch[x].getSolution());
            }

            updateWeights(errors, activationsInclInput, learningRate, trainingDataSize);
            updateBiases(errors, learningRate);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            net.setLearningMode(false);
        }
    }

    protected DVector[] getActivationsInclInput(DVector input)
    {
        DVector[] result = new DVector[net.getLayerCount() + 1];
        result[0] = input;

        for(int i = 0; i < net.getLayerCount(); i++)
        {
            result[i + 1] = net.getLayer(i).getLastActivation();
        }
        return result;
    }

    protected DVector[] calculateErrorVectors(DVector netOutput, DVector solution)
    {
        DVector[] error = new DVector[net.getLayerCount()];

        int L = net.getLayerCount() - 1;

        error[L] = costs.calculateErrorOfLastLayer(netOutput, solution, calculateActivationDerivativeAtLastWeightedInput(L));

        for(int la = L - 1; la >= 0; la--)
        {
            error[la] = error[la + 1].matrixMul(net.getLayer(la + 1).getWeights().transpose())
                    .toVectorDuplicate()
                    .elementWiseMulInPlace(calculateActivationDerivativeAtLastWeightedInput(la));
        }

        return error;
    }

    protected DVector calculateActivationDerivativeAtLastWeightedInput(int layer)
    {
        NetworkLayer nl = net.getLayer(layer);
        DFunction activationFuncDerivative = nl.getActivationFunction().f_derivative;
        return nl.getLastWeigthedInput().applyFunctionElementWise(activationFuncDerivative);
    }

    protected void updateWeights(DVector[][] errors, DVector[][] activationsInclInput, double learningRate, int trainingDataSize)
    {
        for(int la = 0; la < net.getLayerCount(); la++)
        {
            // sum(x, a[x,l-1]T * delta[x,l])
            DMatrix sum = activationsInclInput[0][la].transpose()
                    .matrixMul(errors[0][la]);

            for(int x = 1; x < errors.length; x++)
            {
                // a[x,l-1]T * delta[x,l]
                DMatrix toAdd = activationsInclInput[x][la].transpose()
                        .matrixMul(errors[x][la]);

                sum.addInPlace(toAdd);
            }

            // eta/m
            double factor = learningRate / errors.length;

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

    protected void updateBiases(DVector[][] errors, double learningRate)
    {
        for(int la = 0; la < net.getLayerCount(); la++)
        {
            // sum(x, delta[x,l])
            DVector sum = errors[0][la].getDuplicate();
            for(int x = 1; x < errors.length; x++)
            {
                sum.addInPlace(errors[x][la]);
            }

            // eta/m
            double factor = learningRate / errors.length;

            net.getLayer(la).getBiases()
                    .subInPlace(sum.scalarMulInPlace(factor));
        }
    }

    public NeuralNetwork getNet()
    {
        return net;
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
}

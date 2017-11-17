package de.mirkoruether.ann.net;

import de.mirkoruether.linalg.DFunction;
import de.mirkoruether.linalg.DMatrix;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.ann.Util;
import java.util.Objects;

public class StochasticGradientDescentTrainer
{
    private final CostFunction costs;
    private final NeuralNetwork net;

    public StochasticGradientDescentTrainer(CostFunction costs, NeuralNetwork net)
    {
        this.costs = Objects.requireNonNull(costs);
        this.net = Objects.requireNonNull(net);
    }

    public void train(TrainingData[] trainingData, double learningRate, int batchSize, int epochs)
    {
        for(int i = 0; i < epochs; i++)
        {
            trainEpoch(trainingData, learningRate, batchSize);
        }
    }

    public void trainEpoch(TrainingData[] trainingData, double learningRate, int batchSize)
    {
        TrainingData[] shuffled = Util.shuffle(trainingData, TrainingData.class);
        for(int i = 0; i < trainingData.length; i += batchSize)
        {
            TrainingData[] batch = new TrainingData[batchSize];
            System.arraycopy(shuffled, i, batch, 0, batchSize);
            trainBatch(batch, learningRate);
        }
    }

    public void trainBatch(TrainingData[] trainingData, double learningRate)
    {
        try
        {
            net.setLearningMode(true);

            int batchSize = trainingData.length;

            DVector[][] errors = new DVector[batchSize][];
            DVector[][] activationsInclInput = new DVector[batchSize][];
            for(int x = 0; x < batchSize; x++)
            {
                DVector netOutput = net.feedForward(trainingData[x].getInput());
                activationsInclInput[x] = getActivationsInclInput(trainingData[x].getInput());
                errors[x] = calculateErrorVectors(netOutput, trainingData[x].getSolutions());
            }

            updateWeights(errors, activationsInclInput, learningRate);
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

    private DVector[] getActivationsInclInput(DVector input)
    {
        DVector[] result = new DVector[net.getLayerCount() + 1];
        result[0] = input;

        for(int i = 0; i < net.getLayerCount(); i++)
        {
            result[i + 1] = net.getLayer(i).getLastActivation();
        }
        return result;
    }

    private DVector[] calculateErrorVectors(DVector netOutput, DVector solutions)
    {
        DVector[] error = new DVector[net.getLayerCount()];

        int L = net.getLayerCount() - 1;

        error[L] = costs.calculateGradient(netOutput, solutions)
                .elementWiseMulInPlace(calculateActivationDerivativeAtLastWeightedInput(L));

        for(int la = L - 1; la >= 0; la++)
        {
            error[la] = error[la + 1].matrixMul(net.getLayer(la + 1).getWeights().transpose())
                    .toVectorReference()
                    .elementWiseMulInPlace(calculateActivationDerivativeAtLastWeightedInput(la));
        }

        return error;
    }

    private DVector calculateActivationDerivativeAtLastWeightedInput(int layer)
    {
        NetworkLayer nl = net.getLayer(layer);
        DFunction activationFuncDerivative = nl.getActivationFunction().f_derivative;
        return nl.getLastWeigthedInput().applyFunctionElementWise(activationFuncDerivative);
    }

    private void updateWeights(DVector[][] errors, DVector[][] activationsInclInput, double learningRate)
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

            net.getLayer(la).getWeights()
                    .subInPlace(sum.scalarMulInPlace(factor));
        }
    }

    private void updateBiases(DVector[][] errors, double learningRate)
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
}

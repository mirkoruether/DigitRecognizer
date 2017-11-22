package de.mirkoruether.ann.training;

import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.ann.training.costs.CostFunction;
import de.mirkoruether.ann.training.regularization.CostFunctionRegularization;
import de.mirkoruether.linalg.DMatrix;

public class MomentumSGDTrainer extends StochasticGradientDescentTrainer
{
    private double momentumCoEffizient;
    private final DMatrix[] velocities;

    public MomentumSGDTrainer(NeuralNetwork net, CostFunction costs, CostFunctionRegularization reg, double momentumCoEffizient)
    {
        super(net, costs, reg);
        this.momentumCoEffizient = momentumCoEffizient;

        velocities = new DMatrix[net.getLayerCount()];
        clearVelocities();
    }

    public MomentumSGDTrainer(NeuralNetwork net, CostFunction costs, double momentumCoEffizient)
    {
        this(net, costs, null, momentumCoEffizient);
    }

    @Override
    public void train(TrainingData[] trainingData, double learningRate, int batchSize, int epochs)
    {
        clearVelocities();
        super.train(trainingData, learningRate, batchSize, epochs);
        clearVelocities();
    }

    @Override
    protected void reduceWeigths(int layer, DMatrix decayInclRegularization)
    {
        velocities[layer].scalarMulInPlace(momentumCoEffizient).subInPlace(decayInclRegularization);
        getNet().getLayer(layer).getWeights().addInPlace(velocities[layer]);
    }

    public void clearVelocities()
    {
        for(int i = 0; i < getNet().getLayerCount(); i++)
        {
            velocities[i] = new DMatrix(getNet().getLayer(i).getWeights().getSize());
        }
    }
}

package de.mirkoruether.ann.training;

public class TestResult
{
    private final int total;
    private final int correct;
    private final double averageCosts;

    public TestResult(int testLength, int correct, double averageCosts)
    {
        this.total = testLength;
        this.correct = correct;
        this.averageCosts = averageCosts;
    }

    public int getTotal()
    {
        return total;
    }

    public int getCorrect()
    {
        return correct;
    }

    public double getAverageCosts()
    {
        return averageCosts;
    }

    public double getAccuracy()
    {
        return ((double)correct) / total;
    }

    @Override
    public String toString()
    {
        return String.format("TestResult: %d of %d correct (%.2f%%). Average costs: %.6f",
                             getCorrect(),
                             getTotal(),
                             getAccuracy() * 100,
                             getAverageCosts());
    }
}

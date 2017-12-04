/*
* Parts of this class are copied from NeuralNetTest by Chriz98 https://github.com/ChriZ982/NeuralNetTest
* Modified by Mirko Ruether
*
*
* NeuralNetTest by ChriZ98 is licensed under a
* Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
* https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package de.mirkoruether.digitrecognizer;

import de.mirkoruether.ann.training.NetOutputTest;
import de.mirkoruether.ann.training.TestDataSet;
import de.mirkoruether.ann.training.TrainingData;
import de.mirkoruether.linalg.DVector;
import de.mirkoruether.util.ParallelExecution;
import de.mirkoruether.util.Randomizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

/**
 * Loads MNIST data sets. Loading works according to specified standards at
 * http://yann.lecun.com/exdb/mnist/.
 *
 * @author ChriZ98
 * @author Mirko Ruether
 */
public class MNISTLoader
{
    public static final String TRAINING_IMAGES = "train-images-idx3-ubyte.gz";
    public static final String TRAINING_LABELS = "train-labels-idx1-ubyte.gz";
    public static final String TEST_IMAGES = "t10k-images-idx3-ubyte.gz";
    public static final String TEST_LABELS = "t10k-labels-idx1-ubyte.gz";

    /**
     * Loads MNIST data from folder using the default file names.
     * @param folder           Folder path where the MNIST data files are found
     * @param validationLength Desired length of validation data
     * @return MNIST data
     */
    public static MNISTDataSet loadMNIST(String folder, int validationLength)
    {
        return loadMNIST(new File(folder), validationLength);
    }

    /**
     * Loads MNIST data from folder using the default file names.
     * @param folder           Folder where the MNIST data files are found
     * @param validationLength Desired length of validation data
     * @return MNIST data
     */
    public static MNISTDataSet loadMNIST(File folder, int validationLength)
    {
        return loadMNIST(new File(folder, TRAINING_IMAGES), new File(folder, TRAINING_LABELS),
                         new File(folder, TEST_IMAGES), new File(folder, TEST_LABELS),
                         validationLength);
    }

    /**
     * Load MNIST data.
     * @param trainingImageFile Training image file path
     * @param trainingLabelFile Training label file path
     * @param testImageFile     Test image file path
     * @param testLabelFile     Test label file path
     * @param validationLength  Desired length of validation data
     * @return MNIST data
     */
    public static MNISTDataSet loadMNIST(String trainingImageFile, String trainingLabelFile,
                                         String testImageFile, String testLabelFile,
                                         int validationLength)
    {
        return loadMNIST(new File(trainingImageFile), new File(trainingLabelFile),
                         new File(testImageFile), new File(testLabelFile),
                         validationLength);
    }

    /**
     * Load MNIST data.
     * @param trainingImageFile Training image file
     * @param trainingLabelFile Training label file
     * @param testImageFile     Test image file
     * @param testLabelFile     Test label file
     * @param validationLength  Desired length of validation data
     * @return MNIST data
     */
    public static MNISTDataSet loadMNIST(File trainingImageFile, File trainingLabelFile,
                                         File testImageFile, File testLabelFile,
                                         int validationLength)
    {
        DVector[][] data = parallelImport(trainingImageFile, trainingLabelFile, testImageFile, testLabelFile);

        TrainingData[] trainData = buildTrainingData(data[0], data[1]);
        TrainingData[] testData = buildTrainingData(data[2], data[3]);

        return buildMNIST(trainData, testData, validationLength);
    }

    /**
     * Build MNIST data.
     * @param trainData        TrainData
     * @param testData         TestData
     * @param validationLength Desired length of validation data
     * @return MNIST data
     */
    private static MNISTDataSet buildMNIST(TrainingData[] trainData, TrainingData[] testData, int validationLength)
    {
        final NetOutputTest testFunc = (o, s) -> s.get(o.indexOfMaxium()) == 1.0;

        trainData = Randomizer.shuffle(trainData, TrainingData.class);

        TrainingData[] training = new TrainingData[trainData.length - validationLength];
        TrainingData[] validationData = new TrainingData[validationLength];

        System.arraycopy(trainData, 0, validationData, 0, validationData.length);
        System.arraycopy(trainData, validationData.length, training, 0, training.length);

        TestDataSet validation = new TestDataSet(validationData, testFunc);
        TestDataSet test = new TestDataSet(testData, testFunc);

        return new MNISTDataSet(training, validation, test);
    }

    /**
     * Loads MNIST training data for handwritten digits.
     * @param labelFile The MNIST label file containing the solutions
     * @param imageFile The MNIST image file containing the input
     * @return
     */
    public static TrainingData[] loadTrainingData(String imageFile, String labelFile)
    {
        return loadTrainingData(new File(imageFile), new File(labelFile));
    }

    /**
     * Loads MNIST training data for handwritten digits.
     * @param labelFile The MNIST label file containing the solutions
     * @param imageFile The MNIST image file containing the input
     * @return
     */
    public static TrainingData[] loadTrainingData(File imageFile, File labelFile)
    {
        DVector[][] data = parallelImport(imageFile, labelFile);
        return buildTrainingData(data[0], data[1]);
    }

    /**
     * Builds TrainingData from labels and images.
     * @param images Images
     * @param labels Labels
     * @return TrainingData array
     */
    private static TrainingData[] buildTrainingData(DVector[] images, DVector[] labels)
    {
        if(images.length != labels.length)
        {
            throw new RuntimeException("Labels and Images differ in length. Please check your files.");
        }

        TrainingData[] result = new TrainingData[images.length];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = new TrainingData(images[i], labels[i]);
        }

        return result;
    }

    private static DVector[][] parallelImport(File... files)
    {
        final Function<File, DVector[]> func = (f) -> importData(f);

        return ParallelExecution.inExecutorF((exec) ->
        {
            ParallelExecution<File, DVector[]> pexec = new ParallelExecution<>(func, exec);

            DVector[][] dest = new DVector[files.length][];
            pexec.get(files, dest);

            return dest;
        }, 0);
    }

    /**
     * Tries to read MNIST data from some file.
     *
     * @param file filename e.g. "train-images-idx3-ubyte.gz"
     * @return imported data
     */
    private static DVector[] importData(File file)
    {
        try
        {
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
            byte[] magic = new byte[4];
            gzip.read(magic);
            int magicNum = bytesToInt(magic);

            switch(magicNum)
            {
                case 2049:
                    return importLabelFile(gzip);
                case 2051:
                    return importImageFile(gzip);
                default:
                    throw new RuntimeException("Illegal magic-number: " + magicNum);
            }
        }
        catch(IOException ex)
        {
            throw new RuntimeException("Error while reading file:\n", ex);
        }
    }

    /**
     * Converts 4 bytes to 32 bit integer.
     *
     * @param bytes byte array
     * @return integer value representation
     */
    private static int bytesToInt(byte[] bytes)
    {
        return ((bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF));
    }

    /**
     * Imports a label file form MNIST database
     *
     * @param gzip file stream
     * @return imported labels
     * @throws IOException error while reading file
     */
    private static DVector[] importLabelFile(GZIPInputStream gzip) throws IOException
    {
        byte[] itemCountBytes = new byte[4];
        gzip.read(itemCountBytes);
        int itemCount = bytesToInt(itemCountBytes);
        DVector[] data = new DVector[itemCount];
        for(int i = 0; i < itemCount; i++)
        {
            double[] vec = new double[10];
            vec[gzip.read()] = 1.0;
            data[i] = new DVector(vec);
        }
        return data;
    }

    /**
     * Imports an image file form MNIST database
     *
     * @param gzip file stream
     * @return imported images
     * @throws IOException error while reading file
     */
    private static DVector[] importImageFile(GZIPInputStream gzip) throws IOException
    {
        byte[] infoBytes = new byte[4];
        gzip.read(infoBytes);
        int itemCount = bytesToInt(infoBytes);
        gzip.read(infoBytes);
        int rowCount = bytesToInt(infoBytes);
        gzip.read(infoBytes);
        int colCount = bytesToInt(infoBytes);

        DVector[] data = new DVector[itemCount];
        int pixelCount = rowCount * colCount;
        for(int i = 0; i < itemCount; i++)
        {
            double[] vec = new double[pixelCount];
            for(int j = 0; j < pixelCount; j++)
            {
                vec[j] = gzip.read() / 255.0;
            }
            data[i] = new DVector(vec);
        }

        return data;
    }

    /**
     * Prevent users from creating an instance.
     */
    private MNISTLoader()
    {
    }
}

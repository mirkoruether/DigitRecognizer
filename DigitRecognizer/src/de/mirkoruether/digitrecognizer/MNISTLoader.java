/*
* This class is copied from NeuralNetTest by Chriz98 https://github.com/ChriZ982/NeuralNetTest
* Modified by Mirko Ruether
*
*
* NeuralNetTest by ChriZ98 is licensed under a
* Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License
* https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package de.mirkoruether.digitrecognizer;

import de.mirkoruether.ann.training.TrainingData;
import de.mirkoruether.linalg.DVector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    /**
     * Loads MNIST training data for handwritten digits.
     * @param labelFile The MNIST label file containing the solutions
     * @param imageFile The MNIST image file containing the input
     * @return
     */
    public static TrainingData[] importTrainingData(String labelFile, String imageFile)
    {
        return importTrainingData(new File(labelFile), new File(imageFile));
    }

    /**
     * Loads MNIST training data for handwritten digits.
     * @param labelFile The MNIST label file containing the solutions
     * @param imageFile The MNIST image file containing the input
     * @return
     */
    public static TrainingData[] importTrainingData(File labelFile, File imageFile)
    {
        DVector[] labels = importData(labelFile);
        DVector[] images = importData(imageFile);

        if(labels.length != images.length)
        {
            throw new RuntimeException("Labels and Images differ in length. Please check your files.");
        }

        TrainingData[] result = new TrainingData[labels.length];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = new TrainingData(images[i], labels[i]);
        }

        return result;
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

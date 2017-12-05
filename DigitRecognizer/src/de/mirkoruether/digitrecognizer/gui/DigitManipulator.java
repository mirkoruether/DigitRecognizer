package de.mirkoruether.digitrecognizer.gui;

import de.mirkoruether.linalg.DRowVector;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.function.Consumer;

public class DigitManipulator
{
    private static final int OFFSET_TO_IMAGE_BORDER = 35;

    public static DRowVector getAnnInput(BufferedImage im, Consumer<BufferedImage> con, int pause)
    {
        BufferedImage im28x28 = scaleDownTo28x28(moveToCenterAndScale(im, con, pause));
        if(im28x28.getWidth() != 28 || im28x28.getHeight() != 28)
        {
            throw new IllegalArgumentException("Wrong image size");
        }

        double[] vec = new double[784];
        int[] rgbs = new int[784];
        rgbs = im28x28.getRGB(0, 0, 28, 28, rgbs, 0, 28);

        for(int i = 0; i < 784; i++)
        {
            int r = (rgbs[i] >> 24) & 0xFF;
            vec[i] = (r / 256.0);
        }
        return new DRowVector(vec);
    }

    private static BufferedImage markCenterOfMassAndBounds(BufferedImage im, Point centerOfPixels, Rectangle bounds)
    {
        Graphics g1 = im.createGraphics();
        g1.setColor(Color.RED);
        g1.drawOval(centerOfPixels.x, centerOfPixels.y, 5, 5);
        g1.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g1.setColor(Color.BLUE);
        g1.drawLine(centerOfPixels.x, centerOfPixels.y, im.getWidth() / 2, im.getHeight() / 2);
        return im;
    }

    public static BufferedImage moveToCenterAndScale(BufferedImage im, Consumer<BufferedImage> con, int pause)
    {
        Graphics g;
        Point centerOfPixels = getCenterOfPixels(im);

        letDraw(im, con, pause);

        int width = im.getWidth();
        int heigth = im.getHeight();
        int dx = (width / 2) - centerOfPixels.x;
        int dy = (heigth / 2) - centerOfPixels.y;

        BufferedImage moved = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_ARGB);
        g = moved.createGraphics();
        g.drawImage(im, dx, dy, null);

        letDraw(moved, con, pause);

        Rectangle movedBounds = getContentBounds(moved);
        int disX0 = width / 2 - movedBounds.x;
        int disY0 = heigth / 2 - movedBounds.y;
        int disX1 = (int)movedBounds.getMaxX() - width / 2;
        int disY1 = (int)movedBounds.getMaxY() - heigth / 2;
        int maxDis = Math.max(Math.max(disX0, disX1), Math.max(disY0, disY1));

        double scaleFactor = (width / 2.0 - OFFSET_TO_IMAGE_BORDER) / maxDis;

        int offset = (int)((-maxDis + width / 2) * scaleFactor) - OFFSET_TO_IMAGE_BORDER;

        BufferedImage scaled = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_ARGB);
        g = scaled.createGraphics();
        g.drawImage(moved, -offset, -offset, (int)(width * scaleFactor), (int)(heigth * scaleFactor), null);

        letDraw(scaled, con, pause);

        return scaled;
    }

    private static void letDraw(BufferedImage im, Consumer<BufferedImage> con, int pause)
    {
        if(con != null)
        {
            BufferedImage i = markCenterOfMassAndBounds(deepCopy(im), getCenterOfPixels(im), getContentBounds(im));
            con.accept(i);
            sleep(pause);
        }
    }

    private static Point getCenterOfPixels(BufferedImage im)
    {
        int iCount = 0, jCount = 0;
        int iVal = 0, jVal = 0;
        for(int i = 0; i < im.getHeight(); i++)
        {
            for(int j = 0; j < im.getWidth(); j++)
            {
                if(im.getRGB(j, i) != 0)
                {
                    iVal += i;
                    jVal += j;
                    iCount++;
                    jCount++;
                }
            }
        }
        return new Point(jCount == 0 ? 0 : jVal / jCount,
                         iCount == 0 ? 0 : iVal / iCount);
    }

    private static Rectangle getContentBounds(BufferedImage im)
    {
        int x0 = 0;
        boolean impact = false;
        for(; x0 < im.getWidth() && !impact; x0++)
        {
            for(int y = 0; y < im.getHeight(); y++)
            {
                if(im.getRGB(x0, y) != 0)
                {
                    impact = true;
                    break;
                }
            }
        }

        int y0 = 0;
        impact = false;
        for(; y0 < im.getHeight() && !impact; y0++)
        {
            for(int x = 0; x < im.getWidth(); x++)
            {
                if(im.getRGB(x, y0) != 0)
                {
                    impact = true;
                    break;
                }
            }
        }

        int x1 = im.getWidth() - 1;
        impact = false;
        for(; x1 > x0 && !impact; x1--)
        {
            for(int y = 0; y < im.getHeight(); y++)
            {
                if(im.getRGB(x1, y) != 0)
                {
                    impact = true;
                    break;
                }
            }
        }

        int y1 = im.getHeight() - 1;
        impact = false;
        for(; y1 > y0 && !impact; y1--)
        {
            for(int x = 0; x < im.getWidth(); x++)
            {
                if(im.getRGB(x, y1) != 0)
                {
                    impact = true;
                    break;
                }
            }
        }

        return new Rectangle(x0, y0, x1 - x0, y1 - y0);
    }

    private static BufferedImage scaleDownTo28x28(BufferedImage im)
    {
        BufferedImage result = im;

        result = scaleSmooth(result, 250, 250);
        result = scaleSmooth(result, 100, 100);
        result = scaleSmooth(result, 50, 50);
        result = scaleSmooth(result, 28, 28);
        return result;
    }

    private static BufferedImage scaleSmooth(BufferedImage im, double newWidth, double newHeigth)
    {
        int w = im.getWidth();
        int h = im.getHeight();
        BufferedImage result = new BufferedImage((int)newWidth, (int)newHeigth, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(newWidth / w, newHeigth / h);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        result = scaleOp.filter(im, result);
        return result;
    }

    public static BufferedImage vectorToImage(DRowVector vec)
    {
        BufferedImage im = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);

        for(int i = 0; i < vec.getLength(); i++)
        {
            int x = i % 28;
            int y = i / 28;

            float red = 1.0f - (float)vec.get(i);
            Color c = new Color(red, red, red);

            im.setRGB(x, y, c.getRGB());
        }

        return im;
    }

    private static void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
        }
    }

    private static BufferedImage deepCopy(BufferedImage bi)
    {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private DigitManipulator()
    {
    }
}

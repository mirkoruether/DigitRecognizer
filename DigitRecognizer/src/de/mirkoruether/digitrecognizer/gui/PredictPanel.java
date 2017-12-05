package de.mirkoruether.digitrecognizer.gui;

import de.mirkoruether.ann.NeuralNetwork;
import de.mirkoruether.linalg.DRowVector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PredictPanel extends JPanel
{
    private static final long serialVersionUID = 6609853888939503449L;

    private final DrawPanel drawPanel;
    private final JLabel[] labels;
    private final Window window;

    private final static String LABEL_FORMAT = "%d: %.2f%%";

    public PredictPanel(Window window)
    {
        this.window = window;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 10;
        c.fill = GridBagConstraints.NONE;

        drawPanel = new DrawPanel((bi) -> startPrediction(bi));
        add(drawPanel, c);

        labels = new JLabel[10];
        for(int i = 0; i < labels.length; i++)
        {
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = i;
            c.weighty = 1;
            c.insets = new Insets(0, 10, 0, 10);

            JLabel l = new JLabel();
            labels[i] = l;
            setPercentage(i, 0.0, false);

            add(l, c);
        }

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1000;
        c.gridheight = 10;
        add(new JLabel(""), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 10;
        c.weighty = 1000;
        c.gridwidth = 3;
        add(new JLabel(""), c);
    }

    public void setValues(double[] values)
    {
        if(values.length != 10)
            throw new IllegalArgumentException("Length of values must be ten");

        int maxIndex = 0;
        double max = 0.0;
        double sum = 0.0;

        for(int i = 0; i < 10; i++)
        {
            double v = values[i];
            sum += v;

            if(v > max)
            {
                max = v;
                maxIndex = i;
            }
        }

        if(sum == 0)
        {
            throw new IllegalArgumentException("Sum of values is 0");
        }

        for(int i = 0; i < 10; i++)
        {
            setPercentage(i, 100.0 * values[i] / sum, i == maxIndex);
        }
    }

    private void setPercentage(int index, double percentage, boolean isMax)
    {
        labels[index].setForeground(isMax ? Color.RED : Color.BLACK);
        labels[index].setText(String.format(LABEL_FORMAT, index, percentage));
    }

    private void startPrediction(BufferedImage im)
    {
        Thread t = new Thread(() ->
        {
            DRowVector in = DigitManipulator.getAnnInput(im, (x) -> execOnEvtQueue(() -> drawPanel.setImage(x)), 500);
            execOnEvtQueue(() -> drawPanel.setImage(DigitManipulator.vectorToImage(in)));

            NeuralNetwork net = window.getNet();
            if(net.getInputSize() != 784)
            {
                JOptionPane.showMessageDialog(window, "Current Neural Network has wrong input size. Expected:784, Actual:"
                                                      + net.getInputSize(), "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(net.getOutputSize() != 10)
            {
                JOptionPane.showMessageDialog(window, "Current Neural Network has wrong output size. Expected:10, Actual:"
                                                      + net.getOutputSize(), "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DRowVector result = net.feedForward(in);
            execOnEvtQueue(() -> setValues(result.toArray()));
        });
        t.start();
    }

    private void execOnEvtQueue(Runnable r)
    {
        try
        {
            EventQueue.invokeAndWait(r);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Exception in EventQueue", ex);
        }
    }

    public static class DrawPanel extends JPanel
    {
        private static final long serialVersionUID = 5630464123815946791L;
        private static final int BRUSH_RADIUS = 20;

        private final Consumer<BufferedImage> predictionStart;
        private BufferedImage image;
        private Thread t;

        private boolean predictionStarted = false;

        public DrawPanel(Consumer<BufferedImage> predictionStart)
        {
            this.predictionStart = predictionStart;

            Dimension dim = new Dimension(500, 500);
            setPreferredSize(dim);
            setMinimumSize(dim);
            setMaximumSize(dim);
            setSize(dim);

            setBackground(Color.WHITE);

            resetImage();

            nextThread();

            MouseAdapter ma = new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    registerMouseClick(e);
                }

                @Override
                public void mouseDragged(MouseEvent e)
                {
                    registerMouseClick(e);
                }
            };

            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        public void nextThread()
        {
            t = new Thread(() ->
            {
                try
                {
                    Thread.sleep(2000);
                    EventQueue.invokeAndWait(() ->
                    {
                        nextThread();
                        predictionStarted = true;
                        predictionStart.accept(deepCopy(image));
                    });
                }
                catch(InterruptedException ex)
                {
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            });
        }

        public void setImage(BufferedImage image)
        {
            if(image.getWidth() != 500 || image.getHeight() != 500)
            {
                this.image = scaleUp(image, 500, 500);
            }
            else
            {
                this.image = image;
            }
            repaint();
        }

        private BufferedImage scaleUp(BufferedImage im, int newWidth, int newHeigth)
        {
            BufferedImage result = new BufferedImage(newWidth, newHeigth, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = result.createGraphics();

            graphics2D.drawImage(im, 0, 0, newWidth, newHeigth, null);
            graphics2D.dispose();

            return result;
        }

        public void resetImage()
        {
            image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        }

        private void registerMouseClick(MouseEvent e)
        {
            if(predictionStarted)
            {
                resetImage();
                predictionStarted = false;
            }

            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLACK);
            g.fillOval(e.getX() - BRUSH_RADIUS, e.getY() - BRUSH_RADIUS, BRUSH_RADIUS * 2, BRUSH_RADIUS * 2);

            t.interrupt();
            nextThread();
            t.start();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }

        private static BufferedImage deepCopy(BufferedImage bi)
        {
            ColorModel cm = bi.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = bi.copyData(null);
            return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }
    }
}

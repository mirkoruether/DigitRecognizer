package de.mirkoruether.digitrecognizer.gui;

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
import javax.swing.JPanel;

public class PredictPanel extends JPanel
{
    private static final long serialVersionUID = 6609853888939503449L;

    private final DrawPanel drawPanel;
    private final JLabel[] labels;

    private final static String LABEL_FORMAT = "%d: %.2f%%";

    public PredictPanel()
    {
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
        labels[index].setForeground(isMax ? Color.GREEN : Color.BLACK);
        labels[index].setText(String.format(LABEL_FORMAT, index, percentage));
    }

    private void startPrediction(BufferedImage im)
    {

    }

    public static class DrawPanel extends JPanel
    {
        private static final long serialVersionUID = 5630464123815946791L;
        private static final int BRUSH_RADIUS = 30;

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
            this.image = image;
            repaint();
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

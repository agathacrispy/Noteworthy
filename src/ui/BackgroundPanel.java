package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class BackgroundPanel extends JPanel {

    private static final float[] SPEEDS = {-1, 2.7f, -1, -1, 1.8f, 1.1f, 0.5f, -1};
    private static final BufferedImage[] RAW_LAYERS = new BufferedImage[8];

    static {
        for (int i = 0; i < 8; i++) {
            if (SPEEDS[i] == -1) continue;
            try {
                RAW_LAYERS[i] = ImageIO.read(new File("res/Starry_night_Layer_" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.out.println("cant load layer " + (i + 1));
            }
        }
    }

    private final BufferedImage[] scaledLayers = new BufferedImage[8];
    private final float[] offsets = new float[8];
    private final int[] scaledWidths = new int[8];
    private int lastH = -1;
    private final Timer timer;

    public BackgroundPanel() {
        setOpaque(true);
        timer = new Timer(16, e -> {
            for (int i = 0; i < 8; i++) {
                if (scaledWidths[i] == 0) continue;
                offsets[i] = (offsets[i] + SPEEDS[i]) % scaledWidths[i];
            }
            repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        timer.stop();
    }

    private void buildScaledLayers(int h) {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        for (int i = 0; i < 8; i++) {
            if (RAW_LAYERS[i] == null) continue;
            int sw = RAW_LAYERS[i].getWidth() * h / RAW_LAYERS[i].getHeight();
            scaledWidths[i] = sw;
            BufferedImage strip = gc.createCompatibleImage(sw * 2, h, Transparency.TRANSLUCENT);
            Graphics2D dg = strip.createGraphics();
            dg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            dg.drawImage(RAW_LAYERS[i], 0,  0, sw, h, null);
            dg.drawImage(RAW_LAYERS[i], sw, 0, sw, h, null);
            dg.dispose();
            scaledLayers[i] = strip;
        }
        lastH = h;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        if (h != lastH) buildScaledLayers(h);

        g2d.setColor(new Color(0x22, 0x20, 0x34));
        g2d.fillRect(0, 0, w, h);

        for (int i = 7; i >= 0; i--) {
            if (scaledLayers[i] == null) continue;
            g2d.drawImage(scaledLayers[i], -Math.round(offsets[i]), 0, null);
        }
    }
}

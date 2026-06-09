package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TitlePanel extends BackgroundPanel {

    private static final float HOVER_SPEED = 0.02f;
    private static final float HOVER_AMPLITUDE = 6f;

    private BufferedImage logo;
    private Font minecraftFont;
    private float hoverAngle = 0f;

    public TitlePanel(Runnable onContinue) {
        try {
            logo = ImageIO.read(new File("res/logo.png"));
        } catch (IOException e) {
            System.out.println("cant load logo");
        }

        try {
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(16f);
        } catch (FontFormatException | IOException e) {
            minecraftFont = new Font("Segoe UI", Font.PLAIN, 16);
        }

        new Timer(16, e -> {
            hoverAngle += HOVER_SPEED;
            repaint();
        }).start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onContinue.run();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int hoverY = Math.round((float) Math.sin(hoverAngle) * HOVER_AMPLITUDE);

        g2d.setFont(minecraftFont);
        FontMetrics fm = g2d.getFontMetrics();

        int logoW = logo != null ? (int) (w * 0.65) : 0;
        int logoH = logo != null ? (int) ((double) logo.getHeight() / logo.getWidth() * logoW) : 0;
        int lineH = fm.getHeight();
        int gap = 14;

        int totalH = logoH + gap + lineH + gap / 2 + lineH;
        int groupTop = h / 2 - totalH / 2 + hoverY;

        if (logo != null) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(logo, (w - logoW) / 2, groupTop, logoW, logoH, null);
        }

        String tagline = "a karaoke game";
        String sub = "click anywhere to continue";
        int taglineY = groupTop + logoH + gap + fm.getAscent();
        int subY = taglineY + lineH / 2 + fm.getAscent();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0xff, 0xff, 0xff, 180));
        g2d.drawString(tagline, w / 2 - fm.stringWidth(tagline) / 2, taglineY);

        g2d.setColor(new Color(0x78, 0x58, 0x6f));
        g2d.drawString(sub, w / 2 - fm.stringWidth(sub) / 2, subY);
    }
}

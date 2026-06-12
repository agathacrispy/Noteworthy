package ui;

import model.PerformanceResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResultsPanel extends BackgroundPanel {

    private final PerformanceResult result;
    private final BufferedImage gradeImg;

    public ResultsPanel(PerformanceResult result, Runnable onBackToMenu) {
        this.result = result;

        BufferedImage img = null;
        try { img = ImageIO.read(new File("res/" + result.getGrade().toLowerCase() + ".png")); }
        catch (IOException ignored) {}
        gradeImg = img;

        Font menuFont;
        try {
            menuFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            menuFont = new Font("Segoe UI", Font.PLAIN, 20);
        }

        setLayout(new BorderLayout());

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(menuFont);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> onBackToMenu.run());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        topBar.add(backBtn, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        if (gradeImg != null) {
            int imgH = 220;
            int imgW = gradeImg.getWidth() * imgH / gradeImg.getHeight();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(gradeImg, cx - imgW / 2, cy - imgH / 2, imgW, imgH, null);
        }

        drawPitchGraph(g2d);
    }

    private void drawPitchGraph(Graphics2D g2d) {
        int gx = getWidth() / 2;
        int gy = 40;
        int gw = getWidth() / 2 - 40;
        int gh = getHeight() / 2 - 60;

        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(gx, gy, gw, gh);
        g2d.setColor(Color.GRAY);
        g2d.drawRect(gx, gy, gw, gh);

        if (result.getUserPitches() == null || result.getSongPitches() == null) return;

        int midiMin = 36;
        int midiMax = 84;

        drawPitchLine(g2d, result.getSongPitches(), gx, gy, gw, gh, midiMin, midiMax, new Color(80, 180, 80, 180));
        drawPitchLine(g2d, result.getUserPitches(), gx, gy, gw, gh, midiMin, midiMax, new Color(220, 60, 60, 180));
    }

    private void drawPitchLine(Graphics2D g2d, java.util.List<model.PitchFrame> frames, int gx, int gy, int gw, int gh, int midiMin, int midiMax, Color color) {
        if (frames == null || frames.isEmpty()) return;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));

        int n = frames.size();
        int prevX = -1, prevY = -1;

        for (int i = 0; i < n; i++) {
            int midi = frames.get(i).getPitch();
            if (midi == -1) {
                prevX = -1;
                continue;
            }
            int px = gx + (int) ((double) i / n * gw);
            int py = gy + gh - (int) ((double) (midi - midiMin) / (midiMax - midiMin) * gh);

            if (prevX != -1) g2d.drawLine(prevX, prevY, px, py);
            prevX = px;
            prevY = py;
        }
    }
}

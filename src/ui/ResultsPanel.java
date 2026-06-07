package ui;

import model.PerformanceResult;

import javax.swing.*;
import java.awt.*;

public class ResultsPanel extends JPanel {

    private final PerformanceResult result;

    public ResultsPanel(PerformanceResult result, Runnable onBackToMenu) {
        this.result = result;
        setLayout(new BorderLayout());

        JButton backBtn = new JButton("Back to Menu");
        backBtn.addActionListener(e -> onBackToMenu.run());

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        if (result == null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 96));
            g2d.setColor(Color.BLACK);
            String placeholder = "sussy";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(placeholder, cx - fm.stringWidth(placeholder) / 2, cy);

            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            String sub = "diddy";
            FontMetrics fm2 = g2d.getFontMetrics();
            g2d.drawString(sub, cx - fm2.stringWidth(sub) / 2, cy + 40);
            return;
        }

        // grade display
        g2d.setFont(new Font("Arial", Font.BOLD, 160));
        g2d.setColor(Color.BLACK);
        g2d.drawString(result.getGrade(), 80, cy + 60);

        // pitch comparison graph
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

    private void drawPitchLine(Graphics2D g2d, java.util.List<model.PitchFrame> frames,
                               int gx, int gy, int gw, int gh,
                               int midiMin, int midiMax, Color color) {
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

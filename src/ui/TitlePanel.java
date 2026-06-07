package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TitlePanel extends JPanel {

    public TitlePanel(Runnable onContinue) {
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

        GradientPaint bg = new GradientPaint(0, 0, new Color(200, 0, 255), 0, h, new Color(255, 255, 255));
        g2d.setPaint(bg);
        g2d.fillRect(0, 0, w, h);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 96));
        FontMetrics fmTitle = g2d.getFontMetrics();
        String title = "Noteworthy";
        g2d.drawString(title, w / 2 - fmTitle.stringWidth(title) / 2, h / 2 - 20);

        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fmSub = g2d.getFontMetrics();
        String sub = "click anywhere to continue";
        g2d.drawString(sub, w / 2 - fmSub.stringWidth(sub) / 2, h / 2 + 50);
    }
}

package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SettingsPanel extends JPanel {

    private int volumeSliderX = getWidth()/4 + (getWidth()/2)/6 - 15;
    private int volumeSliderY = (getHeight()/2) - 5;
    private JSlider volumeSlider = new JSlider();

    public SettingsPanel(){

        setLayout(new BorderLayout());

        volumeSlider = new JSlider(0, 100, 50);

        add(volumeSlider, BorderLayout.WEST);

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        setFocusable(true);
    }

    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;


        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2d.getFontMetrics();
        int x = centerX - fm.stringWidth("Settings") / 2;
        g2d.drawString("Settings", x, centerY/2);


        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("Output Volume", centerX/6 + 10, centerY - 20);
        g2d.drawRect(centerX/6, centerY, getWidth()/4, 10);
        g2d.drawRect(getWidth() - centerX/4 - centerX/6, centerY, getWidth()/4, 10);
    }

}

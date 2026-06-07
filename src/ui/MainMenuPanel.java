package ui;

import loader.SongLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MainMenuPanel extends JPanel {

    private final SongLoader sl = new SongLoader();
    private final ArrayList<String> songs;

    public MainMenuPanel(Consumer<String> onSongSelected, Runnable onSettings) {
        songs = sl.loadSongs();
        setLayout(new BorderLayout());

        add(new TitleArea(), BorderLayout.CENTER);
        add(buildRightPanel(onSongSelected, onSettings), BorderLayout.EAST);
    }

    private JPanel buildRightPanel(Consumer<String> onSongSelected, Runnable onSettings) {
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(320, 0));
        right.setBackground(new Color(245, 240, 255));

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        settingsBtn.addActionListener(e -> onSettings.run());
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topBar.setOpaque(false);
        topBar.add(settingsBtn);
        right.add(topBar, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        for (String s : songs) {
            JButton btn = new JButton(s);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(e -> onSongSelected.accept(s));
            listPanel.add(btn);
            listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        right.add(scroll, BorderLayout.CENTER);

        return right;
    }

    private static class TitleArea extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint bg = new GradientPaint(0, 0, new Color(200, 0, 255), 0, getHeight(), Color.WHITE);
            g2d.setPaint(bg);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Bahnschrift", Font.BOLD, 72));
            FontMetrics fm = g2d.getFontMetrics();
            String title = "Noteworthy";
            g2d.drawString(title, getWidth() / 2 - fm.stringWidth(title) / 2, getHeight() / 2);
        }
    }
}

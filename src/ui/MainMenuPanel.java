package ui;

import loader.SongLoader;
import model.Song;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MainMenuPanel extends BackgroundPanel {

    private static final float HOVER_SPEED = 0.02f;
    private static final float HOVER_AMPLITUDE = 6f;
    private static final int SCROLL_WIDTH = 320;

    private final SongLoader sl = new SongLoader();
    private final ArrayList<Song> songs;
    private BufferedImage start;
    private Font minecraftFont;
    private float hoverAngle = 0f;

    public MainMenuPanel(Consumer<String> onSongSelected, Runnable onSettings) {
        try {
            start = ImageIO.read(new File("res/start.png"));
        } catch (IOException e) {
            System.out.println("cant load start");
        }

        try {
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(14f);
        } catch (FontFormatException | IOException e) {
            minecraftFont = new Font("Segoe UI", Font.PLAIN, 14);
        }

        songs = sl.loadSongs();
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setOpaque(false);
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setForeground(Color.WHITE);
        settingsBtn.setFont(minecraftFont);
        settingsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsBtn.addActionListener(e -> onSettings.run());
        topBar.add(settingsBtn, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        add(new JPanel() {{
            setOpaque(false);
        }}, BorderLayout.CENTER);

        JPanel songList = new JPanel();
        songList.setLayout(new BoxLayout(songList, BoxLayout.Y_AXIS));
        songList.setOpaque(false);
        for (Song song : songs) {
            songList.add(makeSongRow(song, onSongSelected));
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(songList, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setPreferredSize(new Dimension(SCROLL_WIDTH, 0));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.EAST);

        new Timer(16, e -> {
            hoverAngle += HOVER_SPEED;
            repaint();
        }).start();
    }

    private JPanel makeSongRow(Song song, Consumer<String> onSelect) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(song.getTitle());
        titleLabel.setFont(minecraftFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 14, 2, 0));

        JLabel artistLabel = new JLabel(song.getArtist());
        artistLabel.setFont(minecraftFont);
        artistLabel.setForeground(new Color(0xd6, 0x72, 0xcc));
        artistLabel.setBorder(BorderFactory.createEmptyBorder(0, 14, 5, 0));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.add(Box.createVerticalGlue());
        text.add(titleLabel);
        text.add(artistLabel);
        text.add(Box.createVerticalGlue());
        row.add(text, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setOpaque(true);
                row.setBackground(new Color(255, 255, 255, 30));
                row.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setOpaque(false);
                row.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(song.getFolderName());
            }
        });

        return row;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (start == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int hoverY = Math.round((float) Math.sin(hoverAngle) * HOVER_AMPLITUDE);
        int areaW = getWidth() - SCROLL_WIDTH;
        int imgW = (int) (areaW * 0.7);
        int imgH = (int) ((double) start.getHeight() / start.getWidth() * imgW);
        int x = (areaW - imgW) / 2;
        int y = getHeight() / 2 - imgH / 2 + hoverY;

        g2d.drawImage(start, x, y, imgW, imgH, null);
    }
}

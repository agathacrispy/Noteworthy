package ui;

import loader.SongLoader;
import model.Song;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.BasicStroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MainMenuPanel extends BackgroundPanel {

    private static final float HOVER_SPEED = 0.02f;
    private static final float HOVER_AMPLITUDE = 6f;

    private static final int SCROLL_WIDTH = 320;
    private static final int DETAILS_HEIGHT = 240;

    private final SongLoader sl = new SongLoader();
    private final ArrayList<Song> songs;
    private final Map<Song, JPanel> rowMap = new HashMap<>();

    private BufferedImage start;
    private BufferedImage startPressed;
    private Font songFont, artistFont, menuFont;
    private float hoverAngle = 0f;
    private boolean startHovered = false;
    private boolean showSelectMsg = false;

    private Song hoveredSong = null;
    private Song selectedSong = null;

    private JPanel detailsPanel;
    private Song displaySong = null;

    public MainMenuPanel(Consumer<String> onSongSelected, Runnable onSettings) {
        try {
            start = ImageIO.read(new File("res/start.png"));
        } catch (IOException e) {
            System.out.println("cant load start");
        }
        try {
            startPressed = ImageIO.read(new File("res/startPressed.png"));
        } catch (IOException e) {
            System.out.println("cant load startPressed");
        }

        try {
            songFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(14f);
        } catch (FontFormatException | IOException e) {
            songFont = new Font("Segoe UI", Font.PLAIN, 14);
        }

        try {
            artistFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            artistFont = new Font("Segoe UI", Font.PLAIN, 14);
        }

        try {
            menuFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            menuFont = new Font("Segoe UI", Font.PLAIN, 14);
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
        settingsBtn.setFont(menuFont);
        settingsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsBtn.addActionListener(e -> onSettings.run());
        topBar.add(settingsBtn, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        Rectangle[] startBounds = {null};

        JPanel centerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (start == null) return;
                Graphics2D g2d = (Graphics2D) g;
                int hoverY = Math.round((float) Math.sin(hoverAngle) * HOVER_AMPLITUDE);
                int w = getWidth(), h = getHeight();
                int imgW = (int) (w * 0.7);
                int imgH = (int) ((double) start.getHeight() / start.getWidth() * imgW);
                int x = (w - imgW) / 2;
                int y = h / 2 - imgH / 2 + hoverY;
                startBounds[0] = new Rectangle(x, y, imgW, imgH);
                BufferedImage img = (startHovered && startPressed != null) ? startPressed : start;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2d.drawImage(img, x, y, imgW, imgH, null);
                if (showSelectMsg) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(menuFont.deriveFont(13f));
                    FontMetrics fm = g2d.getFontMetrics();
                    String msg = "please select a song";
                    int mx = (w - fm.stringWidth(msg)) / 2;
                    int my = y + imgH + 20;
                    g2d.setColor(new Color(0, 0, 0, 120));
                    g2d.drawString(msg, mx + 1, my + 1);
                    g2d.setColor(new Color(0xd6, 0x72, 0xcc));
                    g2d.drawString(msg, mx, my);
                }
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (startBounds[0] == null || !startBounds[0].contains(e.getPoint())) return;
                if (selectedSong != null) {
                    onSongSelected.accept(selectedSong.getFolderName());
                } else {
                    showSelectMsg = true;
                    centerPanel.repaint();
                    new Timer(1800, ev -> {
                        showSelectMsg = false;
                        centerPanel.repaint();
                        ((Timer) ev.getSource()).stop();
                    }).start();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (startHovered) {
                    startHovered = false;
                    centerPanel.repaint();
                }
                centerPanel.setCursor(Cursor.getDefaultCursor());
            }
        });
        centerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean over = startBounds[0] != null && startBounds[0].contains(e.getPoint());
                if (over != startHovered) {
                    startHovered = over;
                    centerPanel.setCursor(Cursor.getPredefinedCursor(over ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                    centerPanel.repaint();
                }
            }
        });
        add(centerPanel, BorderLayout.CENTER);

        detailsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (displaySong == null) return;
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();

                g2d.setColor(new Color(0x10, 0x0f, 0x1a, 220));
                g2d.fillRect(0, 0, w, getHeight());

                int tH = w * 9 / 16;
                if (displaySong.getThumbnail() != null) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(displaySong.getThumbnail(), 0, 0, w, tH, null);
                    g2d.setColor(new Color(0x3a, 0x35, 0x55));
                    //g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRect(0, 0, w - 1, tH - 1);
                } else {
                    System.out.println("cant load thumbnail");
                }

                g2d.setFont(menuFont.deriveFont(11f));
                FontMetrics fm = g2d.getFontMetrics();
                int tagX = 10, tagY = tH + 12;
                tagX = drawChips(g2d, displaySong.getGenre(), tagX, tagY, new Color(0xd6, 0x72, 0xcc), fm);
                drawChips(g2d, displaySong.getDuration(), tagX, tagY, new Color(0x3a, 0x35, 0x55), fm);
            }
        };
        detailsPanel.setOpaque(false);
        detailsPanel.setPreferredSize(new Dimension(SCROLL_WIDTH, DETAILS_HEIGHT));
        detailsPanel.setVisible(false);

        JPanel songList = new JPanel();
        songList.setLayout(new BoxLayout(songList, BoxLayout.Y_AXIS));
        songList.setOpaque(false);
        for (Song song : songs) {
            JPanel row = makeSongRow(song);
            rowMap.put(song, row);
            songList.add(row);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(songList, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setOpaque(false);
        rightContainer.setPreferredSize(new Dimension(SCROLL_WIDTH, 0));
        rightContainer.add(scroll, BorderLayout.CENTER);
        rightContainer.add(detailsPanel, BorderLayout.SOUTH);
        add(rightContainer, BorderLayout.EAST);
    }

    @Override
    protected void tick() {
        hoverAngle += HOVER_SPEED;
    }

    private void setSelected(Song song) {
        Song prev = selectedSong;
        selectedSong = (song == selectedSong) ? null : song;
        if (prev != null && rowMap.containsKey(prev)) rowMap.get(prev).repaint();
        if (selectedSong != null && rowMap.containsKey(selectedSong)) rowMap.get(selectedSong).repaint();
        updateDetails(selectedSong != null ? selectedSong : hoveredSong);
    }

    private void updateDetails(Song song) {
        displaySong = song;
        detailsPanel.setVisible(song != null);
        detailsPanel.repaint();
    }

    private int drawChips(Graphics2D g2d, String csv, int x, int y, Color bg, FontMetrics fm) {
        if (csv == null || csv.isBlank()) return x;
        int px = 8, py = 3, gap = 5, chipH = fm.getHeight() + py * 2;
        for (String part : csv.split(",")) {
            String label = part.trim();
            if (label.isEmpty()) continue;
            int chipW = fm.stringWidth(label) + px * 2;
            g2d.setColor(bg);
            g2d.fillRoundRect(x, y, chipW, chipH, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.drawString(label, x + px, y + py + fm.getAscent());
            x += chipW + gap;
        }
        return x;
    }

    private JPanel makeSongRow(Song song) {
        boolean[] hovered = {false};

        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (song == selectedSong) {
                    g.setColor(new Color(255, 255, 255, 50));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else if (hovered[0]) {
                    g.setColor(new Color(255, 255, 255, 25));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(song.getTitle());
        titleLabel.setFont(songFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 14, 2, 0));

        JLabel artistLabel = new JLabel(song.getArtist());
        artistLabel.setFont(artistFont);
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
                hovered[0] = true;
                hoveredSong = song;
                row.repaint();
                updateDetails(song);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered[0] = false;
                hoveredSong = null;
                row.repaint();
                updateDetails(selectedSong);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setSelected(song);
            }
        });

        return row;
    }
}

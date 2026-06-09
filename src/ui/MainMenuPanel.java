package ui;

import loader.SongLoader;
import model.Song;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MainMenuPanel extends BackgroundPanel {

    private final SongLoader sl = new SongLoader();
    private final ArrayList<Song> songs;

    public MainMenuPanel(Consumer<String> onSongSelected, Runnable onSettings) {
        songs = sl.loadSongs();
        setLayout(new BorderLayout());

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);

        JLabel title = new JLabel("Noteworthy", SwingConstants.CENTER);
        title.setFont(new Font("Minecraft", Font.PLAIN, 36));
        title.setForeground(Color.WHITE);
        left.add(title, BorderLayout.CENTER);

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.setOpaque(false);
        settingsBtn.setForeground(Color.WHITE);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setContentAreaFilled(false);
        settingsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        left.add(settingsBtn, BorderLayout.SOUTH);
        settingsBtn.addActionListener(e -> onSettings.run());

        add(left, BorderLayout.CENTER);

        JPanel songList = new JPanel();
        songList.setLayout(new BoxLayout(songList, BoxLayout.Y_AXIS));
        songList.setOpaque(false);

        for (Song song : songs) {
            JButton btn = new JButton(song.getTitle() + " - " + song.getArtist());
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setOpaque(false);
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            btn.addActionListener(e -> onSongSelected.accept(song.getFolderName()));
            songList.add(btn);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(songList, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setPreferredSize(new Dimension(300, 0));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.EAST);
    }
}

package ui;

import loader.SongLoader;
import model.Song;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MainMenuPanel extends JPanel {

    private final SongLoader sl = new SongLoader();
    private final ArrayList<Song> songs;

    public MainMenuPanel(Consumer<String> onSongSelected, Runnable onSettings) {
        songs = sl.loadSongs();
        setLayout(new BorderLayout());

        JPanel left = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Noteworthy", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        left.add(title, BorderLayout.CENTER);
        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(e -> onSettings.run());
        left.add(settingsBtn, BorderLayout.SOUTH);
        add(left, BorderLayout.CENTER);

        JPanel songList = new JPanel();
        songList.setLayout(new BoxLayout(songList, BoxLayout.Y_AXIS));

        for (Song song : songs) {
            JButton btn = new JButton(song.getTitle() + " - " + song.getArtist());
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> onSongSelected.accept(song.getFolderName()));
            songList.add(btn);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(songList, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setPreferredSize(new Dimension(300, 0));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.EAST);
    }
}

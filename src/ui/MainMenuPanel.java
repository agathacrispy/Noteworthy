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

    public MainMenuPanel(Consumer<Song> onSongSelected) {
        songs = sl.loadSongs();
        for (Song song : songs) {
            JButton btn = new JButton(song.toString()); // "Title - Artist"
            btn.addActionListener(e -> onSongSelected.accept(song));
            add(btn);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}

package ui;

import loader.SongLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MainMenuPanel extends JPanel {

    private final SongLoader sl = new SongLoader();
    private final ArrayList<String> songs;
    //private final ArrayList<JButton> songButtons = new ArrayList<>();

    public MainMenuPanel(Consumer<String> onSongSelected){
        songs = sl.loadSongs();
        for (String s : songs) {
            JButton sb = new JButton(s);
            sb.addActionListener(e -> onSongSelected.accept(s));
            add(sb);
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
}

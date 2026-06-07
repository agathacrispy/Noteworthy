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

        for (String s : songs) {
            JButton btn = new JButton(s);
            btn.addActionListener(e -> onSongSelected.accept(s));
            add(btn);
        }

        JButton settingsBtn = new JButton("Settings");
        settingsBtn.addActionListener(e -> onSettings.run());
        add(settingsBtn);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}

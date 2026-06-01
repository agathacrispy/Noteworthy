package ui;

import audio.AudioInputManager;
import engine.LyricsSync;
import engine.PlaybackClock;
import loader.SongLoader;
import model.LyricLine;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GameplayPanel extends JPanel {

    private LyricLine currentLine;
    private final PlaybackClock clock = new PlaybackClock();
    private final Timer timer;
    private final SongLoader sl = new SongLoader();
    private Clip backing;
    private Clip vocals;
    private final AudioInputManager AIM = new AudioInputManager();

    public GameplayPanel(String song) {
        sl.loadLyrics(song);
        clock.start();
        timer = new Timer(50, e -> {
            long elapsed = clock.elapsedMs();
            currentLine = LyricsSync.getCurrentLine(sl.lyrics, elapsed);
            repaint();
        });
        loadAudio(song);
        clock.start();
        timer.start();
        if (vocals != null) vocals.start();
        if (backing != null) backing.start();
        AIM.startRecording();
    }

    private void loadAudio(String song) {
        backing = loadClip("songs/" + song + "/backing.wav");
        vocals  = loadClip("songs/" + song + "/vocals.wav");
        if (backing != null) {
            backing.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    SwingUtilities.invokeLater(this::onSongFinished);
                }
            });
        }
    }

    private void onSongFinished() {
        timer.stop();
        clock.reset();
        AIM.stopRecording();
        if (vocals != null) vocals.stop();
    }

    private Clip loadClip(String path) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.out.println("cant load audio");
            return null;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (currentLine != null) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            int x = centerX - fm.stringWidth(currentLine.getLine()) / 2;
            g2d.drawString(currentLine.getLine(), x, centerY);
        }
    }
}

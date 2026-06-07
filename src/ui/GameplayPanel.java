package ui;

import audio.AudioInputManager;
import engine.LyricsSync;
import engine.PlaybackClock;
import loader.SongLoader;
import model.LyricLine;
import model.Song;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameplayPanel extends JPanel {

    private LyricLine currentLine;
    private final PlaybackClock clock = new PlaybackClock();
    private final Timer timer;
    private final SongLoader sl = new SongLoader();
    private Clip backing;
    private Clip vocals;
    private final AudioInputManager AIM = new AudioInputManager();
    private final Song song;

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public GameplayPanel(Song song) {
        this.song = song;

        loadProperties();
        sl.loadLyrics(song.getFolderName());
        sl.loadPitches(song.getFolderName());
        clock.start();
        timer = new Timer(50, e -> {
            long elapsed = clock.elapsedMs();
            currentLine = LyricsSync.getCurrentLine(sl.lyrics, elapsed);
            repaint();
        });
        loadAudio(song.getFolderName());
        clock.start();
        timer.start();
        if (vocals != null) vocals.start();
        if (backing != null) backing.start();
        AIM.startRecording(properties.getProperty("micDevice"));
    }

    private void loadAudio(String folderName) {
        backing = loadClip("songs/" + folderName + "/backing.wav");
        vocals  = loadClip("songs/" + folderName + "/vocals.wav");
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

    private void loadProperties() {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Err loading settings.properties");
            properties.setProperty("volume", "50");
            properties.setProperty("micSensitivity", "50");
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Song name and artist at the top
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fmTop = g2d.getFontMetrics();
        String header = song.getTitle() + " - " + song.getArtist();
        g2d.drawString(header, centerX - fmTop.stringWidth(header) / 2, 30);

        // Current lyric line
        if (currentLine != null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            int x = centerX - fm.stringWidth(currentLine.getLine()) / 2;
            g2d.drawString(currentLine.getLine(), x, centerY);
        }
    }
}

package ui;

import audio.AudioInputManager;
import audio.PitchDetector;
import engine.ScoringEngine;
import loader.SongLoader;
import model.LyricLine;
import model.PerformanceResult;
import model.PitchFrame;

import java.util.*;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class GameplayPanel extends BackgroundPanel {

    private LyricLine currentLine;
    private LyricLine nextLine;
    private LyricLine thirdLine;
    private long clockStart = -1;
    private final Timer timer;
    private final SongLoader sl = new SongLoader();
    private Clip backing;
    private Clip vocals;
    private final AudioInputManager AIM = new AudioInputManager();
    private final Consumer<PerformanceResult> onFinished;

    private int bucketOffset = 0;
    private final List<Double> runningScores = new ArrayList<>();
    private final List<PitchFrame> liveUserPitches = new ArrayList<>();
    private String liveGrade = "-";
    private int currentUserMidi = -1;
    private int currentSongMidi = -1;
    private Font minecraftFont;
    Color customColor = new Color(0xff, 0xff, 0xff, 180);

    String filePath = "settings.properties";
    Properties properties = new Properties();

    public GameplayPanel(String song, Consumer<PerformanceResult> onFinished) {
        try {
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(48f);
        } catch (FontFormatException | IOException e) {
            minecraftFont = new Font("Segoe UI", Font.PLAIN, 26);
        }

        this.onFinished = onFinished;

        JButton skipBtn = new JButton("Skip to Results (TEMP)");
        skipBtn.addActionListener(e -> onSongFinished());
        add(skipBtn);

        loadProperties();
        sl.loadLyrics(song);
        sl.loadPitches(song);

        timer = new Timer(50, e -> {
            long elapsed = clockStart == -1 ? 0 : (System.nanoTime() - clockStart) / 1_000_000;
            currentLine = getCurrentLine(sl.lyrics, elapsed);
            nextLine = getNextLine(sl.lyrics, elapsed);
            thirdLine = getThirdLine(sl.lyrics, elapsed);
            processLivePitch(elapsed);
            repaint();
        });

        loadAudio(song);
        clockStart = System.nanoTime();
        timer.start();
        if (vocals != null) vocals.start();
        if (backing != null) backing.start();
        AIM.startRecording(properties.getProperty("micDevice"));
    }

    private void processLivePitch(long elapsed) {
        byte[] fullBucket = AIM.getBucket();

        while (bucketOffset + PitchDetector.HOP_BYTES <= fullBucket.length) {
            byte[] chunk = Arrays.copyOfRange(fullBucket, bucketOffset, bucketOffset + PitchDetector.HOP_BYTES);
            int userMidi = PitchDetector.detectSingleFrame(chunk);

            long chunkMs = Math.round((double) bucketOffset / 2 / PitchDetector.SAMPLE_RATE * 1000);
            liveUserPitches.add(new PitchFrame(chunkMs, userMidi));

            PitchFrame expected = getCurrentPitch(sl.pitches, chunkMs);
            int songMidi = (expected != null) ? expected.getPitch() : -1;

            currentUserMidi = userMidi;
            currentSongMidi = songMidi;

            if (songMidi != -1) {
                if (userMidi == -1) {
                    runningScores.add(0.0);
                } else {
                    int diff = Math.abs(userMidi - songMidi);
                    runningScores.add(100.0 * Math.exp(-ScoringEngine.getK() * diff * diff));
                }
            }

            bucketOffset += PitchDetector.HOP_BYTES;
        }

        if (!runningScores.isEmpty()) {
            double sum = 0;
            for (double s : runningScores) sum += s;
            liveGrade = ScoringEngine.computeGrade(sum / runningScores.size());
        }
    }

    private void loadAudio(String song) {
        backing = loadClip("songs/" + song + "/backing.wav");
        vocals = loadClip("songs/" + song + "/vocals.wav");
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
        clockStart = -1;
        AIM.stopRecording();
        if (vocals != null) vocals.stop();
        if (backing != null) backing.stop();

        double similarity = 0;
        if (!runningScores.isEmpty()) {
            double sum = 0;
            for (double s : runningScores) sum += s;
            similarity = sum / runningScores.size();
        }
        String grade = ScoringEngine.computeGrade(similarity);
        PerformanceResult result = new PerformanceResult(runningScores, similarity, grade, liveUserPitches, sl.pitches);
        onFinished.accept(result);
    }

    private Clip loadClip(String path) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.out.println("cant load audio: " + path);
            return null;
        }
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("err loading settings.properties, using defaults");
            properties.setProperty("volume", "50");
            properties.setProperty("micSensitivity", "50");
        }
    }

    private static LyricLine getCurrentLine(List<LyricLine> lyrics, long elapsedMs) {
        LyricLine current = null;
        for (LyricLine line : lyrics) {
            if (line.getStartMs() <= elapsedMs) current = line;
            else break;
        }
        return current;
    }

    public static LyricLine getNextLine(List<LyricLine> lyrics, long elapsedMs){
        Iterator<LyricLine> iterator = lyrics.iterator();
        iterator.next();
        LyricLine next = null;
        for (LyricLine line : lyrics){
            if (line.getStartMs() <= elapsedMs){
                next = iterator.next();
            }else break;
        }
        return next;
    }

    private static LyricLine getThirdLine(List<LyricLine> lyrics, long elapsedMs){
        Iterator<LyricLine> iterator = lyrics.iterator();
        iterator.next();
        iterator.next();
        LyricLine next = null;
        for (LyricLine line : lyrics){
            if (line.getStartMs() <= elapsedMs){
                next = iterator.next();
            }else break;
        }
        return next;
    }

    private static PitchFrame getCurrentPitch(List<PitchFrame> pitches, long elapsedMs) {
        PitchFrame current = null;
        for (PitchFrame frame : pitches) {
            if (frame.getMs() <= elapsedMs) current = frame;
            else break;
        }
        return current;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (currentLine != null) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(minecraftFont);
            g2d.setColor(customColor);
            FontMetrics fm = g2d.getFontMetrics();
            int x = centerX - fm.stringWidth(currentLine.getLine()) / 2;
            int nextX = centerX - fm.stringWidth(nextLine.getLine()) / 2;
            int thirdX = centerX - fm.stringWidth(thirdLine.getLine())/2;
            g2d.drawString(currentLine.getLine(), x, centerY);
            g2d.drawString(nextLine.getLine(), nextX, centerY + 60);
            g2d.drawString(thirdLine.getLine(), thirdX, centerY + 120);
        }

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 48));
        FontMetrics fmGrade = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(liveGrade, getWidth() - fmGrade.stringWidth(liveGrade) - 20, 60);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.valueOf(currentSongMidi), 20, 30);
        g2d.drawString(String.valueOf(currentUserMidi), 20, 52);
    }
}

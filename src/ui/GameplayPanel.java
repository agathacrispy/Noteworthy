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

    private static final int LINE_HEIGHT = 78;

    private int currentLineIndex = 0;
    private float visualScrollY = 0f;
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
    private int currentDifference = -1;
    private Font minecraftFont;
    String filePath = "settings.properties";
    Properties properties = new Properties();

    public GameplayPanel(String song, Consumer<PerformanceResult> onFinished) {
        try {
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Minecraft.ttf")).deriveFont(40f);
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
            currentLineIndex = findCurrentLineIndex(sl.lyrics, elapsed);
            processLivePitch(elapsed);
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
            currentDifference = Math.abs(userMidi - songMidi);

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

    @Override
    protected void tick() {
        float target = currentLineIndex * LINE_HEIGHT;
        visualScrollY += (target - visualScrollY) * 0.09f;
    }

    private static int findCurrentLineIndex(List<LyricLine> lyrics, long elapsedMs) {
        int idx = 0;
        for (int i = 0; i < lyrics.size(); i++) {
            if (lyrics.get(i).getStartMs() <= elapsedMs) idx = i;
            else break;
        }
        return idx;
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

        if (!sl.lyrics.isEmpty()) {
            g2d.setFont(minecraftFont);
            FontMetrics fm = g2d.getFontMetrics();
            int anchorY = (int) (getHeight() * 0.52);

            for (int i = Math.max(0, currentLineIndex - 1); i < Math.min(sl.lyrics.size(), currentLineIndex + 4); i++) {
                float dist = i * LINE_HEIGHT - visualScrollY;
                float absDist = Math.abs(dist);

                float alpha;
                if (absDist < LINE_HEIGHT * 0.4f) {
                    alpha = 255;
                } else if (absDist > LINE_HEIGHT * 2.2f) {
                    continue;
                } else {
                    alpha = 255 * (1 - (absDist - LINE_HEIGHT * 0.4f) / (LINE_HEIGHT * 1.8f));
                }
                int a = Math.max(0, Math.min(255, (int) alpha));

                String text = sl.lyrics.get(i).getLine();
                int lineY = anchorY + (int) dist;
                int x = centerX - fm.stringWidth(text) / 2;

                g2d.setColor(new Color(0, 0, 0, (int)(a * 0.55f)));
                g2d.drawString(text, x + 1, lineY + 1);

                if (i == currentLineIndex) {
                    g2d.setColor(new Color(0xd6, 0x72, 0xcc, a));
                } else {
                    g2d.setColor(new Color(0xff, 0xff, 0xff, a));
                }
                g2d.drawString(text, x, lineY);
            }
        }

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 48));
        FontMetrics fmGrade = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(liveGrade, getWidth() - fmGrade.stringWidth(liveGrade) - 20, 60);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(currentSongMidi), 20, 30);
        g2d.drawString(String.valueOf(currentUserMidi), 20, 52);
        g2d.drawString(String.valueOf(currentDifference), 20, 74);
    }
}

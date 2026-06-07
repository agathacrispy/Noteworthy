package audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayOutputStream;

// captures raw pcm audio from the selected mic into bucket
// bucket is passed to PitchDetector after recording stops for scoring
// playBack() replays the captured audio for testing purposes

public class AudioInputManager {
    static TargetDataLine line;
    private volatile boolean isRecording = false;
    private boolean playReady = false;
    ByteArrayOutputStream bucket = new ByteArrayOutputStream();

    public void startRecording(String selectedDevice) {
        bucket.reset();
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");
        }
        try { // looks for a specific device, else falls back onto default
            Mixer.Info selectedMixerInfo = null;
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerInfo.getName().equals(selectedDevice)) {
                    selectedMixerInfo = mixerInfo;
                    break;
                }
            }
            if (selectedMixerInfo != null) {
                Mixer mixer = AudioSystem.getMixer(selectedMixerInfo);
                line = (TargetDataLine) mixer.getLine(info);
            } else {
                System.out.println("Device not found");
                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Line not working");
                    return;
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
            }
            line.open(format);
            line.start();

            isRecording = true;

            // capture loop must run on a separate thread?? something like that
            Thread captureThread = new Thread(() -> {
                byte[] buffer = new byte[8192];
                while (isRecording) {
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    bucket.write(buffer, 0, bytesRead);
                }
                line.close();
            });

            captureThread.start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    // used in TestPanel and SettingsPanel for mic testing
    public void playBack() {
        byte[] audioData = bucket.toByteArray();

        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try {
            SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(format);
            speaker.start();

            new Thread(() -> {
                speaker.write(audioData, 0, audioData.length);
                speaker.drain();
                speaker.close();
            }).start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        isRecording = false;
        if (line != null) {
            line.stop();
        }
        playReady = true;
    }

    // returns the raw pcm bytes for pitch processing
    public byte[] getBucket() {
        return bucket.toByteArray();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isPlayReady() {
        return playReady;
    }
}

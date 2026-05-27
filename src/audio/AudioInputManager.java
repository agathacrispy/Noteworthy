package audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayOutputStream;

public class AudioInputManager {
    static TargetDataLine line;
    private boolean isRecording = false;
    private boolean playReady = false;
    ByteArrayOutputStream bucket = new ByteArrayOutputStream();

    public void startRecording(){
        bucket.reset();
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            isRecording = true;

            // We MUST run the capture loop in a separate thread.
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

    public void playBack(){
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

    public boolean isRecording(){
        return isRecording;
    }

    public boolean isPlayReady(){
        return playReady;
    }
}

package audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Target;

public class AudioInputManager {
    private TargetDataLine line;
    private volatile boolean isRecording = false;
    private boolean playReady = false;
    private ByteArrayOutputStream bucket = new ByteArrayOutputStream();

    public void startRecording(String selectedDevice){
        if (isRecording || line != null){
            isRecording = false;
            if (line != null) {
                line.stop();
                line.close();
            }
            try{Thread.sleep(100);} catch(InterruptedException e){}
        }

        bucket = new ByteArrayOutputStream();
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, null);
            Mixer.Info selectedMixerInfo = null;
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerInfo.getName().toLowerCase().contains(selectedDevice.toLowerCase())) {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    if (mixer.isLineSupported(info)) {
                        selectedMixerInfo = mixerInfo;
                        break;
                    }
                }
            }

            if (selectedMixerInfo != null){
                System.out.println("target device: " + selectedMixerInfo.getName());
                try{
                    line = AudioSystem.getTargetDataLine(format, selectedMixerInfo);
                }catch (IllegalArgumentException e){
                    System.out.println("wasnt right hz");
                    selectedMixerInfo = null;
                }
            }

            if (selectedMixerInfo == null) {
                System.out.println("Using default mic");
                DataLine.Info defaultInfo = new DataLine.Info(TargetDataLine.class, format);
                if (!AudioSystem.isLineSupported(defaultInfo)){
                    System.err.println("Default not working");
                    return;
                }
                line = (TargetDataLine) AudioSystem.getLine(defaultInfo);
            }

            line.open(format);
            line.start();
            isRecording = true;
            playReady = false;

            final TargetDataLine threadLine = line;
            final ByteArrayOutputStream threadBucket = bucket;

            // We MUST run the capture loop in a separate thread.
            Thread captureThread = new Thread(() -> {
                byte[] buffer = new byte[8192];
                while (isRecording) {
                    int bytesRead = threadLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        threadBucket.write(buffer, 0, bytesRead);
                    }
                }
                threadLine.stop();
                threadLine.close();
            });

            captureThread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playBack(){
        byte[] audioData = bucket.toByteArray();
        if (audioData.length == 0){
            return;
        }

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
        playReady = true;
        if (line != null){
            line.stop();
            line.close();
        }
    }

    public boolean isRecording(){
        return isRecording;
    }

    public boolean isPlayReady(){
        return playReady;
    }
}

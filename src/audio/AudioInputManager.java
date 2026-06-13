package audio;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioInputManager {
    static TargetDataLine line;
    ByteArrayOutputStream bucket = new ByteArrayOutputStream();
    private volatile boolean isRecording = false;
    private boolean playReady = false;

    private volatile int currentPlaybackVolume = 50;
    private volatile int currentMicSens = 50;

    public void setCurrentPlaybackVolume(int volume){
        this.currentPlaybackVolume = volume;
    }

    public void setCurrentMicSens(int sens){
        this.currentMicSens = sens;
    }

    public void startRecording(String selectedDevice) {
        bucket.reset();
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Line not supported");
        }
        try {
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

            Thread captureThread = new Thread(() -> {
                byte[] buffer = new byte[8192];
                ByteBuffer shortBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
                while (isRecording) {
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    double micMultiplier = currentMicSens / 50.0;
                    int processableBytes = bytesRead - (bytesRead % 2);

                    for (int i = 0; i < bytesRead; i += 2){
                        int sample = shortBuffer.getShort(i);
                        sample = (int) (sample * micMultiplier);
                        if (sample > 32767) sample = 32767;
                        else if (sample < -32768) sample = -32768;

                        shortBuffer.putShort(i, (short) sample);
                    }

                    bucket.write(buffer, 0, bytesRead);
                }
                line.close();
            });

            captureThread.start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
    public void playBack() {
    //public void playBack() {
        byte[] audioData = bucket.toByteArray();

        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try {
            SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(format);

            speaker.start();

            new Thread(() -> {
                ByteBuffer readBuffer = ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN);
                byte[] chunkBuffer = new byte[8192];
                ByteBuffer writeBuffer = ByteBuffer.wrap(chunkBuffer).order(ByteOrder.LITTLE_ENDIAN);

                while (readBuffer.remaining() >= 2) {
                    int bytesToRead = Math.min(readBuffer.remaining(), chunkBuffer.length);
                    writeBuffer.clear();

                    bytesToRead -= bytesToRead % 2;

                    double volumeMultiplier = Math.pow(currentPlaybackVolume / 100.0, 2);

                    for (int i = 0; i < bytesToRead; i += 2) {
                        int sample = readBuffer.getShort();
                        sample = (int) (sample * volumeMultiplier);

                        if (sample > 32767) sample = 32767;
                        else if (sample < -32768) sample = -32768;

                        writeBuffer.putShort((short) sample);
                    }
                    speaker.write(chunkBuffer, 0, bytesToRead);
                }
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

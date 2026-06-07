package audio;

import model.PitchFrame;

import java.util.ArrayList;
import java.util.List;

// processes raw pcm bytes from AudioInputManager.getBucket() into a list of PitchFrames
public class PitchDetector {

    public static final int SILENCE = -1; // when a frame is too quiet to contain a pitched note

    private static final int SAMPLE_RATE = 44100;
    private static final int HOP_MS = 23;
    private static final int HOP_SAMPLES = (int) (SAMPLE_RATE * HOP_MS / 1000.0);
    private static final float SILENCE_THRESHOLD = 0.01f;   // rms below this = silence
    private static final int MIN_FREQ = 65; // ~c2
    private static final int MAX_FREQ = 1047; // ~c6

    public static List<PitchFrame> processRecording(byte[] pcm) { // raw pcm bytes from AIM to pitch frames
        float[] samples = pcmToFloat(pcm);
        List<PitchFrame> frames = new ArrayList<>();

        for (int i = 0; i + HOP_SAMPLES < samples.length; i += HOP_SAMPLES) {
            float[] frame = new float[HOP_SAMPLES];
            System.arraycopy(samples, i, frame, 0, HOP_SAMPLES);

            long timestampMs = Math.round((double) i / SAMPLE_RATE * 1000);
            int midi = detectMidi(frame);
            frames.add(new PitchFrame(timestampMs, midi));
        }
        return frames;
    }

    private static float[] pcmToFloat(byte[] pcm) {
        float[] samples = new float[pcm.length / 2];
        for (int i = 0; i < samples.length; i++) {
            short s = (short) ((pcm[i * 2 + 1] << 8) | (pcm[i * 2] & 0xFF));
            samples[i] = s / 32768.0f;
        }
        return samples;
    }

    // returns midi note number for the frame
    private static int detectMidi(float[] frame) {
        // silence check
        float rms = 0;
        for (float s : frame) rms += s * s;
        rms = (float) Math.sqrt(rms / frame.length);
        if (rms < SILENCE_THRESHOLD) return SILENCE;

        int minLag = SAMPLE_RATE / MAX_FREQ;
        int maxLag = SAMPLE_RATE / MIN_FREQ;
        if (maxLag >= frame.length) return SILENCE;

        double bestCorr = -1;
        int bestLag = minLag;
        for (int lag = minLag; lag <= maxLag; lag++) {
            double corr = 0;
            for (int j = 0; j < frame.length - lag; j++) {
                corr += frame[j] * frame[j + lag];
            }
            if (corr > bestCorr) {
                bestCorr = corr;
                bestLag = lag;
            }
        }

        double freq = (double) SAMPLE_RATE / bestLag;
        return hzToMidi(freq);
    }

    private static int hzToMidi(double hz) {
        if (hz <= 0) return SILENCE;
        return (int) Math.round(69 + 12 * Math.log(hz / 440.0) / Math.log(2));
    }
}

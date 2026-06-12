package model;

public class PitchFrame {
    private final long ms;
    private final int pitch;

    public PitchFrame(long ms, int pitch) {
        this.ms = ms;
        this.pitch = pitch;
    }

    public long getMs() {
        return ms;
    }

    public int getPitch() {
        return pitch;
    }
}

package engine;

import model.PitchFrame;

import java.util.List;

public class PitchSync {
    public static PitchFrame getCurrentPitch(List<PitchFrame> pitches, long elapsedMs) {
        PitchFrame current = null;
        for (PitchFrame frame : pitches) {
            if (frame.getMs() <= elapsedMs) current = frame;
            else break;
        }
        return current;
    }
}

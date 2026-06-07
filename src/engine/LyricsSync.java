package engine;

import model.LyricLine;

import java.util.List;

public class LyricsSync {

    public static LyricLine getCurrentLine(List<LyricLine> lyrics, long elapsedMs) {
        LyricLine current = null;
        for (LyricLine line : lyrics) {
            if (line.getStartMs() <= elapsedMs) current = line;
            else break;
        }
        return current;
    }
}

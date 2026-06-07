package model;

public class LyricLine {
    private String text;
    private long startMs;   // time in ms when this line begins

    public LyricLine(String text, long startMs) {
        this.text = text;
        this.startMs = startMs;
    }

    public String getLine() {
        return text;
    }

    public long getStartMs() {
        return startMs;
    }
}

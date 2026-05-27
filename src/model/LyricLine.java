package model;

public class LyricLine {
    private String text;
    private long startMs;
    public LyricLine(String text, long startMs) {
        this.text = text;
        this.startMs = startMs;
    }

    public String getLine() { return text; }
    public long getStartMs() { return startMs; };
}

package model;

import java.util.List;

public class PerformanceResult {
    private List<Double> frameScores; // score per pitch frame: 100 * e^(-k * diff^2)
    private double similarity; // as a pct
    private String grade; // letter grade from similarity
    private List<PitchFrame> userPitches;
    private List<PitchFrame> songPitches;

    public PerformanceResult(List<Double> frameScores, double similarity, String grade,
                             List<PitchFrame> userPitches, List<PitchFrame> songPitches) {
        this.frameScores = frameScores;
        this.similarity = similarity;
        this.grade = grade;
        this.userPitches = userPitches;
        this.songPitches = songPitches;
    }

    public String getGrade() {
        return grade;
    }

    public double getSimilarity() {
        return similarity;
    }

    public List<Double> getFrameScores() {
        return frameScores;
    }

    public List<PitchFrame> getUserPitches() {
        return userPitches;
    }

    public List<PitchFrame> getSongPitches() {
        return songPitches;
    }
}

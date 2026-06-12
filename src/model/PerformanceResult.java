package model;

import java.util.List;

public class PerformanceResult {
    private List<Double> frameScores;
    private double similarity;
    private String grade;
    private List<PitchFrame> userPitches;
    private List<PitchFrame> songPitches;

    public PerformanceResult(List<Double> frameScores, double similarity, String grade, List<PitchFrame> userPitches, List<PitchFrame> songPitches) {
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

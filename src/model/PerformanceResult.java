package model;

import java.util.List;

public class PerformanceResult {
    private List<Double> frameScores;
    private double similarity; // as pct
    private String grade;
    private List<PitchFrame> userPitches;
}

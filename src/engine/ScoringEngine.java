package engine;

import model.PerformanceResult;
import model.PitchFrame;
import java.util.ArrayList;
import java.util.List;

public class ScoringEngine {

    private static final double K = 0.001;

    public static PerformanceResult score(List<PitchFrame> userPitches, List<PitchFrame> songPitches) {
        List<Double> frameScores = new ArrayList<>();

        int n = Math.min(userPitches.size(), songPitches.size());

        for (int i = 0; i < n; i++) {
            int userMidi = userPitches.get(i).getPitch();
            int songMidi = songPitches.get(i).getPitch();

            if (songMidi == -1 || userMidi == -1) continue;

            //if (userMidi == -1) {
            //    frameScores.add(0.0);
            //    continue;
            //}

            int diff = Math.abs(userMidi - songMidi);

            double frameScore = 100.0 * Math.exp(-K * diff * diff);
            if (diff <= 25) {
                frameScores.add(frameScore);
            }
        }

        double similarity = 0;
        if (!frameScores.isEmpty()) {
            double sum = 0;
            for (double s : frameScores) sum += s;
            similarity = sum / frameScores.size();
        }

        String grade = computeGrade(similarity);
        return new PerformanceResult(frameScores, similarity, grade, userPitches, songPitches);
    }

    public static double getK() {
        return K;
    }

    public static String computeGrade(double similarity) {
        if (similarity >= 80) return "A+";
        if (similarity >= 75) return "A";
        if (similarity >= 70) return "A-";
        if (similarity >= 65) return "B+";
        if (similarity >= 60) return "B";
        if (similarity >= 55) return "B-";
        if (similarity >= 50) return "C+";
        if (similarity >= 45) return "C";
        if (similarity >= 40) return "C-";
        if (similarity >= 35) return "D";
        if (similarity >= 25) return "F";
        return "Eric";
    }
}

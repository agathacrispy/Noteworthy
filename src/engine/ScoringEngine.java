package engine;

import model.PerformanceResult;
import model.PitchFrame;
import java.util.ArrayList;
import java.util.List;

public class ScoringEngine {

    private static final double K = 0.003;

    public static double scoreFrame(int userMidi, int songMidi) {
        if (userMidi == -1 || songMidi == -1) return -1;
        int raw = Math.abs(userMidi - songMidi);
        int diff = raw % 12;
        diff = Math.min(diff, 12 - diff);
        return 100.0 * Math.exp(-K * diff * diff);
    }

    public static PerformanceResult score(List<PitchFrame> userPitches, List<PitchFrame> songPitches) {
        List<Double> frameScores = new ArrayList<>();

        int n = Math.min(userPitches.size(), songPitches.size());

        for (int i = 0; i < n; i++) {
            int userMidi = userPitches.get(i).getPitch();
            int songMidi = songPitches.get(i).getPitch();

            double s = scoreFrame(userMidi, songMidi);
            if (s >= 0) frameScores.add(s);
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

    public static String computeGrade(double similarity) {
        if (similarity >= 95) return "A+";
        if (similarity >= 90) return "A";
        if (similarity >= 85) return "A-";
        if (similarity >= 78) return "B+";
        if (similarity >= 70) return "B";
        if (similarity >= 60) return "B-";
        if (similarity >= 50) return "C+";
        if (similarity >= 42) return "C";
        if (similarity >= 35) return "C-";
        if (similarity >= 25) return "D";
        if (similarity >= 10) return "F";
        return "Eric";
    }
}

package midi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.MusicScore;

public class GestureRecognizer {

    public MusicScore findMatchingScore(
            List<MusicScore> scores,
            List<List<Integer>> playedGesture) {

        if (playedGesture == null || playedGesture.isEmpty()) {
            return null;
        }

        List<List<Integer>> normalizedPlayedGesture =
                normalizeGesture(playedGesture);

        for (MusicScore score : scores) {

            if (!score.hasMidiGesture()) {
                continue;
            }

            List<List<Integer>> savedGesture =
                    normalizeGesture(score.getMidiGesture());

            if (savedGesture.equals(normalizedPlayedGesture)) {
                return score;
            }
        }

        return null;
    }

    private List<List<Integer>> normalizeGesture(List<List<Integer>> gesture) {
        List<List<Integer>> normalizedGesture = new ArrayList<>();

        for (List<Integer> group : gesture) {
            if (group == null || group.isEmpty()) {
                continue;
            }

            List<Integer> normalizedGroup = new ArrayList<>();

            for (Integer note : group) {
                if (note != null && !normalizedGroup.contains(note)) {
                    normalizedGroup.add(note);
                }
            }

            Collections.sort(normalizedGroup);
            normalizedGesture.add(normalizedGroup);
        }

        return normalizedGesture;
    }
}
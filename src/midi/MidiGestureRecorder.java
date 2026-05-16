package midi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MidiGestureRecorder {

    private static final long CHORD_TIME_WINDOW_MS = 150;

    private boolean recording;
    private List<List<Integer>> recordedGesture;
    private long lastNoteTime;

    public MidiGestureRecorder() {
        recordedGesture = new ArrayList<>();
        recording = false;
        lastNoteTime = -1;
    }

    public void startRecording() {
        recordedGesture.clear();
        recording = true;
        lastNoteTime = -1;
    }

    public void stopRecording() {
        recording = false;
    }

    public boolean isRecording() {
        return recording;
    }

    public void recordNote(int note) {
        if (!recording) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (recordedGesture.isEmpty()
                || lastNoteTime == -1
                || currentTime - lastNoteTime > CHORD_TIME_WINDOW_MS) {

            recordedGesture.add(new ArrayList<>());
        }

        List<Integer> currentGroup =
                recordedGesture.get(recordedGesture.size() - 1);

        if (!currentGroup.contains(note)) {
            currentGroup.add(note);
            Collections.sort(currentGroup);
        }

        lastNoteTime = currentTime;
    }

    public List<List<Integer>> getRecordedGesture() {
        List<List<Integer>> copy = new ArrayList<>();

        for (List<Integer> group : recordedGesture) {
            copy.add(new ArrayList<>(group));
        }

        return copy;
    }
}
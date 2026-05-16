package model;

import java.util.ArrayList;
import java.util.List;

public class MusicScore {

    private String title;
    private String filePath;
    private List<String> categories;
    private List<List<Integer>> midiGesture;

    public MusicScore(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
        this.categories = new ArrayList<>();
        this.midiGesture = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<String> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }

        return categories;
    }

    public void addCategory(String category) {
        if (categories == null) {
            categories = new ArrayList<>();
        }

        if (category == null || category.isBlank()) {
            return;
        }

        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public boolean hasCategory(String category) {
        if (categories == null) {
            return false;
        }

        return categories.contains(category);
    }

    public List<List<Integer>> getMidiGesture() {
        if (midiGesture == null) {
            midiGesture = new ArrayList<>();
        }

        return midiGesture;
    }

    public void setMidiGesture(List<List<Integer>> midiGesture) {
        this.midiGesture = midiGesture;
    }

    public boolean hasMidiGesture() {
        return midiGesture != null && !midiGesture.isEmpty();
    }

    @Override
    public String toString() {
        return title;
    }
}
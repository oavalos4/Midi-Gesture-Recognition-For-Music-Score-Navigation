package model;

import java.util.ArrayList;
import java.util.List;

public class LibraryData {

    private List<MusicScore> scores;
    private List<String> categories;

    public LibraryData() {
        scores = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public List<MusicScore> getScores() {
        return scores;
    }

    public void setScores(List<MusicScore> scores) {
        this.scores = scores;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
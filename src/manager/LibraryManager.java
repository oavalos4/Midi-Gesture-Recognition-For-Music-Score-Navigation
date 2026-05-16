package manager;

import java.util.ArrayList;
import java.util.List;

import model.MusicScore;

public class LibraryManager {

    private List<MusicScore> scores;
    private List<String> categories;

    public LibraryManager() {
        scores = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public void addScore(MusicScore score) {
        scores.add(score);
    }

    public void removeScore(MusicScore score) {
        if (score != null) {
            scores.remove(score);
        }
    }

    public List<MusicScore> getAllScores() {
        return scores;
    }

    public void setScores(List<MusicScore> scores) {
        this.scores = scores;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void addCategory(String category) {
        if (category == null || category.isBlank()) {
            return;
        }

        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(String category) {
        if (category == null || category.isBlank()) {
            return;
        }

        categories.remove(category);

        for (MusicScore score : scores) {
            score.getCategories().remove(category);
        }
    }
    
    public void addCategoryToScore(MusicScore score, String category) {
        if (score != null && category != null && !category.isBlank()) {
            score.addCategory(category);
        }
    }

    public List<String> getCategories() {
        return categories;
    }
}
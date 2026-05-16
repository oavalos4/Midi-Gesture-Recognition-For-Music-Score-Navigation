package ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import manager.LibraryManager;
import manager.LibraryStorage;
import midi.GestureRecognizer;
import midi.MidiGestureRecorder;
import midi.MidiInputHandler;
import midi.MidiNoteListener;
import model.LibraryData;
import model.MusicScore;

public class MainWindow {

    private LibraryManager libraryManager = new LibraryManager();
    private LibraryStorage libraryStorage = new LibraryStorage();

    private MidiInputHandler midiInputHandler = new MidiInputHandler();
    private MidiGestureRecorder gestureRecorder = new MidiGestureRecorder();
    private GestureRecognizer gestureRecognizer = new GestureRecognizer();

    private MusicScore scoreBeingAssigned;

    private boolean assigningMidiGesture = false;
    private boolean scoreOpenedFromGesture = false;
    private boolean listeningForGesture = false;

    private PauseTransition gestureMatchDelay;

    private static final int LISTENING_PEDAL = 66;
    private static final double GESTURE_MATCH_DELAY_MS = 180;

    private ViewMode currentViewMode = ViewMode.SCORES;

    private enum ViewMode {
        SCORES,
        CATEGORIES
    }

    @FXML
    private Label listeningStatusLabel;

    @FXML
    private Label midiStatusLabel;

    @FXML
    private Button addCategoryButton;

    @FXML
    private Button importButton;

    @FXML
    private ToggleButton allPiecesButton;

    @FXML
    private ToggleButton categoryButton;

    @FXML
    private ListView<Object> scoreListView;

    @FXML
    public void initialize() {
        loadLibraryData();

        showAllPieces();
        setupButtons();
        setupScoreListView();
        setupScoreContextMenu();
        setupMidi();
    }

    /* =========================================================
       Library loading and saving
       ========================================================= */

    private void loadLibraryData() {
        LibraryData data = libraryStorage.loadLibrary();

        libraryManager.setScores(data.getScores());
        libraryManager.setCategories(data.getCategories());
    }

    private void saveLibrary() {
        LibraryData data = new LibraryData();

        data.setScores(libraryManager.getAllScores());
        data.setCategories(libraryManager.getCategories());

        libraryStorage.saveLibrary(data);
    }

    /* =========================================================
       Button setup
       ========================================================= */

    private void setupButtons() {
        allPiecesButton.setOnAction(e -> showAllPieces());

        categoryButton.setOnAction(e -> showCategories());

        importButton.setOnAction(e -> importScore());

        addCategoryButton.setOnAction(e -> openAddCategoryDialog());
    }

    /* =========================================================
       ListView display and navigation
       ========================================================= */

    private void setupScoreListView() {
        scoreListView.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) {
                return;
            }

            Object selectedItem =
                    scoreListView.getSelectionModel().getSelectedItem();

            if (selectedItem == null) {
                return;
            }

            if (currentViewMode == ViewMode.CATEGORIES && selectedItem instanceof String) {
                String selectedCategory = (String) selectedItem;
                showScoresInCategory(selectedCategory);
                return;
            }

            if (selectedItem instanceof MusicScore && e.getClickCount() == 2) {
                MusicScore selectedScore = (MusicScore) selectedItem;
                openScoreWindow(selectedScore);
            }
        });
    }

    private void showAllPieces() {
        currentViewMode = ViewMode.SCORES;
        scoreListView.getItems().setAll(libraryManager.getAllScores());
    }

    private void showCategories() {
        currentViewMode = ViewMode.CATEGORIES;
        scoreListView.getItems().setAll(libraryManager.getCategories());
    }

    private void showScoresInCategory(String category) {
        currentViewMode = ViewMode.SCORES;

        scoreListView.getItems().clear();

        for (MusicScore score : libraryManager.getAllScores()) {
            if (score.hasCategory(category)) {
                scoreListView.getItems().add(score);
            }
        }
    }

    /* =========================================================
       Right-click context menu
       ========================================================= */

    private void setupScoreContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        scoreListView.setOnContextMenuRequested(e -> {
            contextMenu.getItems().clear();

            Object selectedItem =
                    scoreListView.getSelectionModel().getSelectedItem();

            if (selectedItem instanceof MusicScore) {
                MusicScore selectedScore = (MusicScore) selectedItem;

                MenuItem assignMidiGestureItem = new MenuItem("Assign MIDI Gesture");

                assignMidiGestureItem.setOnAction(event -> {
                    startMidiGestureAssignment(selectedScore);
                });

                Menu addToCategoryMenu = createAddToCategoryMenu();

                MenuItem deleteScoreItem = new MenuItem("Delete Music Score");

                deleteScoreItem.setOnAction(event -> {
                    deleteMusicScore(selectedScore);
                });

                contextMenu.getItems().add(assignMidiGestureItem);
                contextMenu.getItems().add(addToCategoryMenu);
                contextMenu.getItems().add(deleteScoreItem);
            }

            else if (selectedItem instanceof String && currentViewMode == ViewMode.CATEGORIES) {
                String selectedCategory = (String) selectedItem;

                MenuItem deleteCategoryItem = new MenuItem("Delete Category");

                deleteCategoryItem.setOnAction(event -> {
                    deleteCategory(selectedCategory);
                });

                contextMenu.getItems().add(deleteCategoryItem);
            }

            if (!contextMenu.getItems().isEmpty()) {
                contextMenu.show(scoreListView, e.getScreenX(), e.getScreenY());
            }

            e.consume();
        });
    }

    private Menu createAddToCategoryMenu() {
        Menu addToCategoryMenu = new Menu("Add to Category");

        if (libraryManager.getCategories().isEmpty()) {
            MenuItem noCategoriesItem = new MenuItem("No categories created");
            noCategoriesItem.setDisable(true);
            addToCategoryMenu.getItems().add(noCategoriesItem);
            return addToCategoryMenu;
        }

        for (String category : libraryManager.getCategories()) {
            MenuItem categoryItem = new MenuItem(category);

            categoryItem.setOnAction(e -> {
                Object selectedItem =
                        scoreListView.getSelectionModel().getSelectedItem();

                if (!(selectedItem instanceof MusicScore)) {
                    return;
                }

                MusicScore selectedScore = (MusicScore) selectedItem;

                libraryManager.addCategoryToScore(selectedScore, category);

                saveLibrary();

                scoreListView.refresh();

                System.out.println(
                        selectedScore.getTitle() + " added to category: " + category
                );
            });

            addToCategoryMenu.getItems().add(categoryItem);
        }

        return addToCategoryMenu;
    }

    /* =========================================================
       Importing scores and adding categories
       ========================================================= */

    private void importScore() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Music Score");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile =
                fileChooser.showOpenDialog(importButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                String copiedPath =
                        libraryStorage.copyScoreToLibrary(selectedFile);

                MusicScore score = new MusicScore(
                        selectedFile.getName(),
                        copiedPath
                );

                libraryManager.addScore(score);

                saveLibrary();

                showAllPieces();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openAddCategoryDialog() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/ui/AddCategoryDialog.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Category");
            stage.setScene(new Scene(root, 401, 227));
            stage.setResizable(false);
            stage.showAndWait();

            AddCategoryDialog controller = loader.getController();
            String newCategory = controller.getCreatedCategory();

            if (newCategory != null && !newCategory.isBlank()) {
                libraryManager.addCategory(newCategory);

                saveLibrary();

                if (currentViewMode == ViewMode.CATEGORIES) {
                    showCategories();
                }

                setupScoreContextMenu();

                System.out.println("Categories:");
                for (String category : libraryManager.getCategories()) {
                    System.out.println(category);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================================================
       Deleting scores and categories
       ========================================================= */

    private void deleteMusicScore(MusicScore score) {
        if (score == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Music Score");
        alert.setHeaderText("Delete this music score?");
        alert.setContentText("This will remove \"" + score.getTitle() + "\" from the library.");

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result != ButtonType.OK) {
            return;
        }

        libraryManager.removeScore(score);

        saveLibrary();

        if (currentViewMode == ViewMode.CATEGORIES) {
            showCategories();
        } else {
            showAllPieces();
        }

        System.out.println("Deleted music score: " + score.getTitle());
    }

    private void deleteCategory(String category) {
        if (category == null || category.isBlank()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText("Delete this category?");
        alert.setContentText(
                "This will remove the category \"" + category + "\" from the library and from all scores."
        );

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result != ButtonType.OK) {
            return;
        }

        libraryManager.removeCategory(category);

        saveLibrary();

        showCategories();

        setupScoreContextMenu();

        System.out.println("Deleted category: " + category);
    }

    /* =========================================================
       Opening score viewer
       ========================================================= */

    private void openScoreWindow(MusicScore score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ScoreViewer.fxml"));
            Parent root = loader.load();

            ScoreViewer controller = loader.getController();
            controller.displayScore(score.getFilePath());

            Stage stage = new Stage();
            stage.setTitle(score.getTitle());
            stage.setScene(new Scene(root, 770, 1090));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================================================
       MIDI setup and gesture recognition
       ========================================================= */

    private void setupMidi() {
        listeningStatusLabel.setText("Listening Status: Not Listening");

        gestureMatchDelay = new PauseTransition(Duration.millis(GESTURE_MATCH_DELAY_MS));

        gestureMatchDelay.setOnFinished(e -> {
            if (listeningForGesture && !assigningMidiGesture) {
                checkForGestureMatch();
            }
        });

        midiInputHandler.setNoteListener(new MidiNoteListener() {

            @Override
            public void onNoteOn(int note, int velocity) {
                if (!listeningForGesture) {
                    return;
                }

                gestureRecorder.recordNote(note);

                List<List<Integer>> currentGesture =
                        gestureRecorder.getRecordedGesture();

                updateListeningStatus(
                        "Listening Status: Listening... Gesture: " + currentGesture
                );

                System.out.println("Gesture input accepted: " + note);
                System.out.println("Current gesture: " + currentGesture);

                if (!assigningMidiGesture) {
                    Platform.runLater(() -> gestureMatchDelay.playFromStart());
                }
            }

            @Override
            public void onNoteOff(int note) {
                // Not needed for the current gesture system.
            }

            @Override
            public void onControlChange(int controller, int value) {
                if (controller == LISTENING_PEDAL) {
                    handleListeningPedal(value);
                }
            }
        });

        boolean connected = midiInputHandler.startListening();

        if (connected) {
            midiStatusLabel.setText("MIDI Status: Connected");
        } else {
            midiStatusLabel.setText("MIDI Status: Not connected");
        }
    }

    private void handleListeningPedal(int value) {
        if (value > 0) {
            listeningForGesture = true;
            scoreOpenedFromGesture = false;
            gestureRecorder.startRecording();

            updateListeningStatus("Listening Status: Listening...");

            System.out.println("Gesture listening ON");

        } else {
            listeningForGesture = false;

            Platform.runLater(() -> gestureMatchDelay.stop());

            updateListeningStatus("Listening Status: Not Listening");

            System.out.println("Gesture listening OFF");

            if (assigningMidiGesture) {
                finishMidiGestureAssignment();
            }

            gestureRecorder.stopRecording();
        }
    }

    private void checkForGestureMatch() {
        if (scoreOpenedFromGesture) {
            return;
        }

        List<List<Integer>> playedGesture =
                gestureRecorder.getRecordedGesture();

        if (playedGesture.isEmpty()) {
            return;
        }

        MusicScore matchingScore =
                gestureRecognizer.findMatchingScore(
                        libraryManager.getAllScores(),
                        playedGesture
                );

        if (matchingScore != null) {
            scoreOpenedFromGesture = true;

            updateListeningStatus("Listening Status: Gesture matched!");

            System.out.println("Gesture matched: " + matchingScore.getTitle());

            Platform.runLater(() -> {
                openScoreWindow(matchingScore);
            });
        }
    }

    /* =========================================================
       MIDI gesture assignment
       ========================================================= */

    private void startMidiGestureAssignment(MusicScore score) {
        scoreBeingAssigned = score;
        assigningMidiGesture = true;
        scoreOpenedFromGesture = false;

        System.out.println("Assigning MIDI gesture to: " + score.getTitle());
        System.out.println("Hold the middle pedal, play the gesture, then release the pedal to save.");
    }

    private void finishMidiGestureAssignment() {
        if (scoreBeingAssigned == null) {
            assigningMidiGesture = false;
            return;
        }

        gestureRecorder.stopRecording();

        List<List<Integer>> gesture =
                gestureRecorder.getRecordedGesture();

        if (gesture.isEmpty()) {
            updateListeningStatus("Listening Status: No MIDI notes recorded");

            System.out.println("No MIDI notes recorded.");
            scoreBeingAssigned = null;
            assigningMidiGesture = false;
            return;
        }

        scoreBeingAssigned.setMidiGesture(gesture);

        saveLibrary();

        updateListeningStatus("Listening Status: Gesture saved!");

        System.out.println("Saved MIDI gesture for: " + scoreBeingAssigned.getTitle());
        System.out.println("Gesture: " + gesture);

        scoreBeingAssigned = null;
        assigningMidiGesture = false;
    }

    /* =========================================================
       UI status updates
       ========================================================= */

    private void updateListeningStatus(String message) {
        Platform.runLater(() -> {
            listeningStatusLabel.setText(message);
        });
    }
}
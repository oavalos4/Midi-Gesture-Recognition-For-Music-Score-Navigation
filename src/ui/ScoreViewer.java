package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.PdfRenderer;

public class ScoreViewer {

    @FXML
    private ImageView scoreImageView;

    @FXML
    private Button previousPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageNumberLabel;

    private String pdfPath;
    private int currentPage;
    private int totalPages;

    public void displayScore(String pdfPath) {
        this.pdfPath = pdfPath;
        this.currentPage = 0;
        this.totalPages = PdfRenderer.getPageCount(pdfPath);

        displayCurrentPage();
    }

    private void displayCurrentPage() {
        Image image = PdfRenderer.renderPdfPage(pdfPath, currentPage);
        scoreImageView.setImage(image);

        pageNumberLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);

        previousPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage == totalPages - 1);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayCurrentPage();
        }
    }
    
    @FXML
    private void initialize() {
        scoreImageView.setPreserveRatio(true);

        scoreImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                scoreImageView.fitWidthProperty().bind(newScene.widthProperty());
                scoreImageView.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }
}
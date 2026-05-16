package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategoryDialog {

    @FXML
    private TextField newCategoryTextfield;

    @FXML
    private Button createCategoryButton;

    private String createdCategory;

    @FXML
    public void initialize() {

        createCategoryButton.setOnAction(e -> createCategory());
    }

    private void createCategory() {

        createdCategory =
                newCategoryTextfield.getText().trim();

        Stage stage =
                (Stage) createCategoryButton.getScene().getWindow();

        stage.close();
    }

    public String getCreatedCategory() {
        return createdCategory;
    }
}
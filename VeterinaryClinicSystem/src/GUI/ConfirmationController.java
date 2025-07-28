package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Objects;
import static GUI.GUI.setUpConfirmAmdDeclineButtons;

public class ConfirmationController {

    @FXML
    private Button confirmButton;

    @FXML
    private Button declineButton;

    @FXML
    private Label label5;

    @FXML
    private ImageView logo;

    @FXML
    private GridPane mainPane;

    private Stage stage;

    private Stage parentStage;

    public void initialize() {
        logo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/clinic_logo.png"))));
        setUpConfirmAmdDeclineButtons(confirmButton, declineButton);
        confirmButton.setOnAction(e -> {
            stage.close();
            parentStage.close();
        });
        declineButton.setOnAction(e -> stage.close());
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                stage.close();
            } else if (e.getCode() == KeyCode.ENTER) {
                stage.close();
                parentStage.close();
            }
        });
    }

    public void setMessage(String message) {
        label5.setText(message);
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

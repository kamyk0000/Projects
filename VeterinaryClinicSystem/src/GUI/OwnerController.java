package GUI;

import Base.ObjectPlus;
import Person.Owner;
import Person.Person;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.Objects;
import static GUI.GUI.*;

public class OwnerController {

    @FXML
    private TextField NIPField;

    @FXML
    private TextField addressfield;

    @FXML
    private CheckBox breederCheck;

    @FXML
    private Button confirmButton;

    @FXML
    private Button declineButton;

    @FXML
    private Label label;

    @FXML
    private Label label2;

    @FXML
    private ImageView logo;

    @FXML
    private GridPane mainPane;

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField secondNameField;

    @FXML
    private TextField surnameField;

    @FXML
    private Pane secondPane;

    private ObjectCallback objectCallback;

    public void initialize() {
        logo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/clinic_logo.png"))));
        setUpButtons();
        confirmButton.setOnAction(e -> addNewOwner());
        declineButton.setOnAction(e -> openConfirmationScene("Do you wish to exit this menu?"));
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                openConfirmationScene("Do you wish to exit this menu?");
            } else if (e.getCode() == KeyCode.ENTER) {
                addNewOwner();
            } else if (e.isControlDown() && e.getCode() == KeyCode.Z) {
                System.out.println("ctrl;z");
            }
        });
    }

    private void addNewOwner() {
        try {
            Owner o1 = (Person.create(nameField.getText(), secondNameField.getText(), surnameField.getText(), addressfield.getText(), phoneField.getText())).linkOwner(NIPField.getText(), breederCheck.isSelected());
            objectCallback.onDataReceived(o1);
            ObjectPlus.showExtent(Person.class);
            ObjectPlus.showExtent(Owner.class);
            ((Stage) confirmButton.getScene().getWindow()).close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            openConfirmationScene(e.getMessage() + "!\nDo you wish to cancel the process?");
        }
    }

    private void openConfirmationScene(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../scenes/confirmationScene.fxml"));
        Stage stage = openNewConfirmationSceneOnTop(fxmlLoader, confirmButton, message);

        stage.showAndWait();
    }

    private void setUpButtons() {
        // Set up CnD Buttons
        setUpConfirmAmdDeclineButtons(confirmButton, declineButton);
    }

    public void setObjectCallback(ObjectCallback objectCallback) {
        this.objectCallback = objectCallback;
    }
}

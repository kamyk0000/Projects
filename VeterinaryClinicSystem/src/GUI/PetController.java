package GUI;

import Base.ObjectPlus;
import Person.Owner;
import Pet.Pet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static GUI.GUI.*;

public class PetController implements ObjectCallback{

    @FXML
    private ChoiceBox<Character> genderBox;

    @FXML
    private TextField breedField;

    @FXML
    private TextField chipIdField;

    @FXML
    private Button confirmButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button declineButton;

    @FXML
    private Label label;

    @FXML
    private Label label2;

    @FXML
    private Label label3;

    @FXML
    private ImageView logo;

    @FXML
    private GridPane mainPane;

    @FXML
    private TextField nameField;

    @FXML
    private Button ownerAdd;

    @FXML
    private Button ownerAddNew;

    @FXML
    private TextField ownerBar;

    @FXML
    private Button ownerRemove;

    @FXML
    private ListView<ObjectPlus> ownerSelected;

    @FXML
    private ListView<ObjectPlus> ownerView;

    @FXML
    private Pane secondPane;

    @FXML
    private TextField speciesField;

    private ObjectCallback objectCallback;


    public void initialize() {
        logo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/clinic_logo.png"))));
        setUpGenderBox();
        setUpButtons();
        setUpListAndSearchbar();
        ownerAddNew.setOnAction(e -> openNewOwnerScene());
        ownerAddNew.setTooltip(new Tooltip("Create and add new Owner"));
        confirmButton.setOnAction(e -> addNewPet());
        declineButton.setOnAction(e -> openConfirmationScene("Do you wish to exit this menu?"));
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                openConfirmationScene("Do you wish to exit this menu?");
            } else if (e.getCode() == KeyCode.ENTER) {
                addNewPet();
            }
        });
    }

    private void addNewPet() {
        try {
            List<ObjectPlus> ownerList = ownerSelected.getItems();
            List<Owner> owners = new ArrayList<>();
            ownerList.forEach(o -> owners.add((Owner) o));
            if (owners.isEmpty()) {
                throw new Exception("Please select at least one Owner");
            }
            LocalDate birthday = datePicker.getValue();
            if (birthday == null) birthday = LocalDate.now();
            char gender = (genderBox.getValue() != null) ? genderBox.getValue() : 'x';
            if (gender == 'x') throw new Exception("Please choose a gender!");
            Pet p1 = Pet.create(nameField.getText(), speciesField.getText(), breedField.getText(), gender, birthday, (chipIdField.getText().isEmpty())? null : chipIdField.getText(), owners.get(0));
            owners.remove(owners.get(0));
            if (!owners.isEmpty()) {
                for (Owner owner : owners) {
                    p1.linkOwner(owner);
                }
            }
            objectCallback.onDataReceived(p1);
            ((Stage) ownerAddNew.getScene().getWindow()).close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            openConfirmationScene(e.getMessage() + "!\nDo you wish to cancel the process?");
        }
    }

    private void openNewOwnerScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("../scenes/newOwnerScene.fxml"));
        Stage stage = openNewSceneOnTop(fxmlLoader, "New Owner");
        OwnerController controller = fxmlLoader.getController();
        controller.setObjectCallback(this);

        stage.showAndWait();
    }

    private void openConfirmationScene(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../scenes/confirmationScene.fxml"));
        Stage stage = openNewConfirmationSceneOnTop(fxmlLoader, ownerAddNew, message);

        stage.showAndWait();
    }

    private void setUpListAndSearchbar() {
        // Load extents into list
        ObservableList<ObjectPlus> owners = FXCollections.observableArrayList(loadExtentList(Owner.class)).sorted();

        // Create searchbar
        createSearchbar(owners, ownerBar, ownerView);

        // Set up remove logic
        setUpAddRemoveActions(ownerView, ownerSelected, ownerAdd, ownerRemove);
    }

    private void setUpButtons() {
        // Set up hover effect
        ColorAdjust hoverEffect = new ColorAdjust();
        hoverEffect.setBrightness(-0.5);

        // Normal button size
        int sqBtnWidthAndHeight = 35;

        // Set up normal buttons
        ownerAddNew.setGraphic(createImageView("images/paw_button_plus.png", sqBtnWidthAndHeight));
        ownerAdd.setGraphic(createImageView("images/paw_button_add.png", sqBtnWidthAndHeight));
        ownerRemove.setGraphic(createImageView("images/paw_button_remove.png", sqBtnWidthAndHeight));

        // Set up hover effects and actions for buttons
        setUpHoverEffect(ownerAddNew, hoverEffect);
        setUpHoverEffect(ownerAdd, hoverEffect);
        setUpHoverEffect(ownerRemove, hoverEffect);

        // Set up CnD Buttons
        setUpConfirmAmdDeclineButtons(confirmButton, declineButton);
    }

    private void setUpGenderBox() {
        genderBox.getItems().addAll('M','F');
    }

    public void setObjectCallback(ObjectCallback objectCallback) {
        this.objectCallback = objectCallback;
    }

    @Override
    public void onDataReceived(ObjectPlus object) {
        try {
            ObjectPlus.writeExtents("extents.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setUpListAndSearchbar();
        System.out.println(object);
        ownerSelected.getItems().add(object);

    }
}

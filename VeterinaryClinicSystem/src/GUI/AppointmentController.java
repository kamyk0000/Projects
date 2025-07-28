package GUI;

import Appointment.Appointment;
import Base.ObjectPlus;
import MedicalProcedure.ByType.Immunotherapy;
import MedicalProcedure.ByType.Other;
import MedicalProcedure.ByType.TherapeuticVaccination;
import MedicalProcedure.ByType.Vaccination;
import MedicalProcedure.MedicalProcedure;
import Person.Intern;
import Person.Vet;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import static GUI.GUI.*;

public class AppointmentController implements ObjectCallback {

    @FXML
    private Button confirmButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button declineButton;

    @FXML
    private ChoiceBox<String> hourPicker;

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
    private Button personelAdd;

    @FXML
    private TextField personelBar;

    @FXML
    private TextField noteField;

    @FXML
    private Button personelRemove;

    @FXML
    private ListView<ObjectPlus> personelSelected;

    @FXML
    private ListView<ObjectPlus> personelView;

    @FXML
    private Button petAdd;

    @FXML
    private TextField petBar;

    @FXML
    private ListView<ObjectPlus> petView;

    @FXML
    private Button procedureAdd;

    @FXML
    private TextField procedureBar;

    @FXML
    private Button procedureRemove;

    @FXML
    private ListView<ObjectPlus> procedureSelected;

    @FXML
    private ListView<ObjectPlus> procedureView;

    @FXML
    private Pane secondPane;

    private ObjectPlus lastObject;

    public void initialize() {
        logo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/clinic_banner.png"))));
        hourPicker.setValue("TIME");
        setUpButtons();
        setUpHourPicker();
        setUpListsAndSearchbars();
        petAdd.setOnAction(e -> openNewPetScene());
        petAdd.setTooltip(new Tooltip("Create and add new Pet"));
        confirmButton.setOnAction(e -> addNewAppointment());
        declineButton.setOnAction(e -> openConfirmationScene("Do you wish to exit the program?"));
        mainPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                openConfirmationScene("Do you wish to exit the program?");
            } else if (e.getCode() == KeyCode.ENTER) {
                addNewAppointment();
            } else if (e.isControlDown() && e.getCode() == KeyCode.Z) {
                if (lastObject != null) {
                    if (lastObject instanceof Appointment){
                        try {
                            ((Appointment) lastObject).remove();
                        } catch (Exception ignored) {
                        }
                    } else if (lastObject instanceof Pet){
                        try {
                            ((Pet) lastObject).remove();
                        } catch (Exception ignored) {
                        }
                    }
                    try {
                        ObjectPlus.writeExtents("extents.json");
                    } catch (IOException ignored) {
                    }
                    setUpListsAndSearchbars();
                    System.out.println("Successfully removed " + lastObject);
                    lastObject = null;
                } else System.out.println("No last object");
            }
        });
    }

    private void addNewAppointment() {
        try {
            Pet pet = (Pet) petView.getSelectionModel().getSelectedItem();

            List<ObjectPlus> personnelList = personelSelected.getItems();
            List<Vet> vets = personnelList.stream()
                    .filter(item -> item instanceof Vet)
                    .map(item -> (Vet) item)
                    .toList();
            Intern intern = (Intern) personnelList.stream()
                    .filter(item -> item instanceof Intern).findFirst().orElse(null);

            List<ObjectPlus> procedureList = procedureSelected.getItems();
            List<MedicalProcedure> procedures = new ArrayList<>();
            procedureList.forEach(p -> procedures.add((MedicalProcedure) p));

            LocalDateTime appointmentDate = getDate();

            Appointment ap = Appointment.create(pet, procedures, vets, intern, appointmentDate, (noteField.getText().isEmpty() ? "No note" : noteField.getText()));
            lastObject = ap;
            // For debug purposes
            System.out.println(ap);
            ObjectPlus.showExtent(Appointment.class);
            Appointment.showAllSchedules();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            openConfirmationScene(e.getMessage() + "!\nDo you wish to cancel the process?");
        }
    }

    private LocalDateTime getDate() throws Exception {
        if (datePicker.getValue() == null) {
            throw new Exception("Please select a proper date");
        }
        LocalDate date = datePicker.getValue();
        if (hourPicker.getValue() == null || Objects.equals(hourPicker.getValue(), "TIME")) {
            throw new Exception("Please select a proper time");
        }
        LocalTime time = LocalTime.parse(hourPicker.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(date, time);
    }

    private void openNewPetScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("../scenes/newPetScene.fxml"));
        Stage stage = openNewSceneOnTop(fxmlLoader, "New Pet");
        PetController controller = fxmlLoader.getController();
        controller.setObjectCallback(this);

        stage.showAndWait();
    }

    private void openConfirmationScene(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../scenes/confirmationScene.fxml"));
        Stage stage = openNewConfirmationSceneOnTop(fxmlLoader, confirmButton, message);

        stage.showAndWait();
    }

    private void setUpListsAndSearchbars() {
        // Load extents into lists
        ObservableList<ObjectPlus> pets = FXCollections.observableArrayList(loadExtentList(Pet.class)).sorted();
        ObservableList<ObjectPlus> personnel = FXCollections.observableArrayList(Stream.concat(loadExtentList(Vet.class).stream(), loadExtentList(Intern.class).stream()).toList()).sorted();
        List<ObjectPlus> procedureList = new ArrayList<>();
        procedureList.addAll(loadExtentList(Other.class));
        procedureList.addAll(loadExtentList(TherapeuticVaccination.class));
        procedureList.addAll(loadExtentList(Vaccination.class));
        procedureList.addAll(loadExtentList(Immunotherapy.class));
        ObservableList<ObjectPlus> procedures = FXCollections.observableArrayList(procedureList).sorted();

        // Create search bars
        createSearchbar(pets, petBar, petView);
        createSearchbar(personnel, personelBar, personelView);
        createSearchbar(procedures, procedureBar, procedureView);

        // Set up remove logic
        setUpAddRemoveActions(personelView, personelSelected, personelAdd, personelRemove);
        setUpAddRemoveActions(procedureView, procedureSelected, procedureAdd, procedureRemove);
    }

    private void setUpButtons() {
        // Set up hover effect
        ColorAdjust hoverEffect = new ColorAdjust();
        hoverEffect.setBrightness(-0.5);

        // Normal button size
        int sqBtnWidthAndHeight = 35;

        // Set up normal buttons
        petAdd.setGraphic(createImageView("images/paw_button_plus.png", sqBtnWidthAndHeight));
        personelAdd.setGraphic(createImageView("images/paw_button_add.png", sqBtnWidthAndHeight));
        personelRemove.setGraphic(createImageView("images/paw_button_remove.png", sqBtnWidthAndHeight));
        procedureAdd.setGraphic(createImageView("images/paw_button_add.png", sqBtnWidthAndHeight));
        procedureRemove.setGraphic(createImageView("images/paw_button_remove.png", sqBtnWidthAndHeight));

        // Set up hover effects and actions for buttons
        setUpHoverEffect(petAdd, hoverEffect);
        setUpHoverEffect(personelAdd, hoverEffect);
        setUpHoverEffect(personelRemove, hoverEffect);
        setUpHoverEffect(procedureAdd, hoverEffect);
        setUpHoverEffect(procedureRemove, hoverEffect);

        // Set up CnD Buttons
        setUpConfirmAmdDeclineButtons(confirmButton, declineButton);
    }

    private void setUpHourPicker() {
        List<String> hours = new ArrayList<>();
        for (int i = 6; i < 20; i++) {
            if (i < 10) {
                hours.add("0" + i + ":00");
                hours.add("0" + i + ":30");
            } else {
                hours.add(i + ":00");
                hours.add(i + ":30");
            }
        }
        hourPicker.getItems().addAll(hours);
    }

    @Override
    public void onDataReceived(ObjectPlus object) {
        try {
            ObjectPlus.writeExtents("extents.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setUpListsAndSearchbars();
        System.out.println(object);
        petView.getSelectionModel().select(object);
        lastObject = object;
    }
}




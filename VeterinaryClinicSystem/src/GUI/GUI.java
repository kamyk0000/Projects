package GUI;

import Base.ObjectPlus;
import Person.Intern;
import Person.Vet;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load extents from file
        ObjectPlus.readExtents("extents.json");

        // Set up main GUI
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../scenes/newAppointmentScene.fxml"));
        Parent root = fxmlLoader.load();
        String css = Objects.requireNonNull(this.getClass().getResource("../scenes/stylesheet.css")).toExternalForm();
        Image icon = new Image("images/clinic_logo.png");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);

        stage.getIcons().add(icon);
        stage.setTitle("MEOW App - New Appointment");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.sizeToScene();
        stage.show();

        stage.setOnCloseRequest(e -> {
            e.consume();
            FXMLLoader newFxmlLoader = new FXMLLoader(getClass().getResource("../scenes/confirmationScene.fxml"));
            Stage newStage = openNewConfirmationSceneOnTop(newFxmlLoader, root, "Do you wish to exit the program?");
            newStage.showAndWait();
        });
    }

    public static Stage openNewSceneOnTop(FXMLLoader fxmlLoader, String title) {
        try {
            Parent root = fxmlLoader.load();
            String css = Objects.requireNonNull(GUI.class.getResource("../scenes/stylesheet.css")).toExternalForm();
            Image icon = new Image("images/clinic_logo.png");

            Scene newScene = new Scene(root);
            newScene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.getIcons().add(icon);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("MEOW App - " + title);
            stage.setScene(newScene);
            if (fxmlLoader.getController() instanceof ConfirmationController) {
                ConfirmationController controller = fxmlLoader.getController();
                controller.setStage(stage);
            }
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Stage openNewConfirmationSceneOnTop(FXMLLoader fxmlLoader, Node node, String message) {
        try {
            Stage confStage = openNewSceneOnTop(fxmlLoader, "Warning!");
            ConfirmationController controller = fxmlLoader.getController();
            controller.setMessage(message);
            controller.setParentStage((Stage) node.getScene().getWindow());
            return confStage;
        } catch (Exception ignored) {}
        return null;
    }

    public static void setUpConfirmAmdDeclineButtons(Button confirmButton, Button declineButton) {
        confirmButton.setGraphic(createImageView("images/paw_button_confirm.png", 50, 170));
        declineButton.setGraphic(createImageView("images/paw_button_decline.png", 50, 170));

        setUpScaleEffect(confirmButton, new Scale(0.9, 0.9));
        setUpScaleEffect(declineButton, new Scale(0.9, 0.9));
    }

    public static void setUpHoverEffect(Node node, ColorAdjust hoverEffect) {
        node.setOnMouseEntered(e -> node.setEffect(hoverEffect));
        node.setOnMouseExited(e -> node.setEffect(null));
    }

    public static void setUpScaleEffect(Node node, Scale scale) {
        node.setOnMouseEntered(e -> node.getTransforms().setAll(scale));
        node.setOnMouseExited(e -> node.getTransforms().remove(scale));
    }

    public static ImageView createImageView(String path, int height, int width) {
        ImageView imageView = new ImageView(path);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        return imageView;
    }

    public static ImageView createImageView(String path, int size) {
        return createImageView(path, size, size);
    }

    public static void createSearchbar(ObservableList<ObjectPlus> list, TextField bar, ListView<ObjectPlus> view) {
        FilteredList<ObjectPlus> filteredItems = new FilteredList<>(list, p -> true);
        bar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return item.toString().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<ObjectPlus> sortedItems = new SortedList<>(filteredItems);
        view.setItems(sortedItems);
    }

    public static void setUpAddRemoveActions(ListView<ObjectPlus> sourceView, ListView<ObjectPlus> targetView, Button addButton, Button removeButton) {
        addButton.setOnAction(e -> {
            setUpAddAction(sourceView, targetView);
        });
        addButton.setTooltip(new Tooltip("Add to selection"));
        sourceView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                setUpAddAction(sourceView, targetView);
            }
        });
        removeButton.setOnAction(e -> {
            ObjectPlus selectedItem = targetView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                targetView.getItems().remove(selectedItem);
            }
        });
        removeButton.setTooltip(new Tooltip("Remove from selection"));
        targetView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                ObjectPlus selectedItem = targetView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    targetView.getItems().remove(selectedItem);
                }
            }
        });
    }

    private static void setUpAddAction(ListView<ObjectPlus> sourceView, ListView<ObjectPlus> targetView) {
        ObjectPlus selectedItem = sourceView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !targetView.getItems().contains(selectedItem)) {
            long vetCount = targetView.getItems().stream().filter(item -> item instanceof Vet).count();
            long internCount = targetView.getItems().stream().filter(item -> item instanceof Intern).count();

            if (!(selectedItem instanceof Intern) && !(selectedItem instanceof Vet)) {
                targetView.getItems().add(selectedItem);
            } else if (selectedItem instanceof Vet && vetCount < 2) {
                targetView.getItems().add(selectedItem);
            } else if (selectedItem instanceof Intern && internCount < 1) {
                targetView.getItems().add(selectedItem);
            }
        }
    }


    public static List<ObjectPlus> loadExtentList(Class type) {
        Iterable<ObjectPlus> iterable = null;
        List<ObjectPlus> list = new ArrayList<>();
        try {
            iterable = ObjectPlus.getExtent(type);
        } catch (ClassNotFoundException ignored) {
            return list;
        }
        iterable.forEach(list::add);
        return list;
    }
}

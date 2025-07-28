package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Objects;
import java.util.Optional;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene1.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        Scene scene1 = new Scene(root);
        primaryStage.setTitle("Your Sheet!");
        Image icon = new Image("sample/ikonki/icon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene1);
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            try {
                exitMyStage(controller, primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();
    }

    public void exitMyStage(Controller controller, Stage stage) throws Exception {
        ButtonType no = new ButtonType("Wyjdź", ButtonBar.ButtonData.NO);
        ButtonType yes = new ButtonType("Zapisz i wyjdź", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getButtonTypes().addAll(no,yes,cancel);
        alert.setTitle("Wychodzenie z aplikacji");
        alert.setHeaderText("Czy chcesz zapisać zmiany przed wyjściem?");

        alert.setResizable(false);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get()==yes){
            controller.saveData();
            stage.close();
        } else if (result.isPresent() && result.get()==no){
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

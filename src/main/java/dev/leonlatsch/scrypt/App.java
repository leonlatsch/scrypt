package dev.leonlatsch.scrypt;

import dev.leonlatsch.scrypt.controllers.MainController;
import dev.leonlatsch.scrypt.util.InfoCreator;
import dev.leonlatsch.scrypt.util.OptionPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class App extends Application {

    public static File CREDITS;

    public static void run(String[] args) {
        try {
            CREDITS = Files.createTempFile("scrypt_info_", ".html").toFile();
            InfoCreator.create(CREDITS);
        } catch (IOException e) {
            OptionPane.showAlertDialog(null, "ERROR", "Error creating info file. Please restart the Application", AlertType.ERROR);
        }

        launch(args);

        boolean success = CREDITS.delete();
        if (!success) {
            System.err.println("Error deleting " + CREDITS.getName()); // Should not happen
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/Main.fxml"));
        Stage stage = loader.load();
        ((MainController) loader.getController()).setStage(stage);
        stage.show();
    }

}

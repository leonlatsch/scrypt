package de.leonl.scrypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import de.leonl.scrypt.ui.cmd.Terminal;
import de.leonl.scrypt.ui.gui.MainController;
import de.leonl.scrypt.util.OptionPane;
import de.leonl.scrypt.util.TmpCreator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    public static File CREDITS;

    public static void main(String[] args) throws Exception {
        try {
            CREDITS = Files.createTempFile("credits_", ".html").toFile();
            TmpCreator.create(CREDITS);
        } catch (IOException e) {
            OptionPane.showAlertDialog(null, "ERROR", "Error creating credits file", AlertType.ERROR);
        }
        if (Terminal.validTerminalArgs(args)) {
            new Terminal(args).run();
        } else {
            launch(args);
        }
        CREDITS.delete();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/leonl/scrypt/ui/gui/Main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(primaryStage);
        Scene scene = new Scene(root, 430, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SCrypt");
        primaryStage.getIcons().add(new Image(App.class.getClassLoader().getResourceAsStream("logo_256.png")));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}

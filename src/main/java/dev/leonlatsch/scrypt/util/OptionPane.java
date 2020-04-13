package dev.leonlatsch.scrypt.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * @author Leon Latsch
 * @since 1.0
 */
public class OptionPane {

    /**
     * A simple alert dialog which acts like a pop-up and blocks the main application while it displays
     * 
     * @param title The title shown in the dialog; default is: "Alert"
     * @param message The message displayed in the Dialog; default is: "Alert!"
     */
    public static void showAlertDialog(Stage parent, String title, String message, AlertType alertType) {
        if (title == null) {
            title = "Alert";
        }
        if (message == null) {
            message = "Alert!";
        }

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(parent);

        alert.showAndWait();
    }

    /**
     * A simple confirmation dialog which acts like a pop-up and blocks the main application while it displays
     * 
     * @param title The title shown in the dialog; default is: "Are you sure?"
     * @param message The message displayed in the Dialog; default is: "Are you sure?"
     * @return true if yes and false if no
     * 
     * @see OptionPane#showAlertDialog(Stage, String, String, AlertType)
     */
    public static boolean showConfirmDialog(Stage parent, String title, String message) {
        if (title == null) {
            title = "Are you sure?";
        }
        if (message == null) {
            message = "Are you sure?";
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(parent);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}

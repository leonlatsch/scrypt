package dev.leonlatsch.scrypt.controllers;

import dev.leonlatsch.scrypt.App;
import dev.leonlatsch.scrypt.data.*;
import dev.leonlatsch.scrypt.util.MavenUtils;
import dev.leonlatsch.scrypt.util.OptionPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author Leon Latsch
 * @since 1.0
 */
public class MainController {

    private static final String LBL_ENCRYPT = "Encrypt";
    private static final String LBL_DECRYPT = "Decrypt";

    private static final String FILE_TYPE = ".crypt";

    private Mode mode;

    private Stage stage;

    private EncryptionManager encryptionManager;

    private File inFile, outFile;

    private Thread workingThread;

    ////////////////////// FXML Components //////////////////////

    @FXML
    private ChoiceBox<String> cbMode;

    @FXML
    private Button btnRun, btnLookUp;

    @FXML
    private TextField tfIn, tfPassword;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblOut, lblStatus, lblSize, lblSizeHeader, lblVersion;

    @FXML
    private ProgressBar progress;

    @FXML
    private CheckBox showPw;

    @FXML
    public void initialize() {
        // Load search.png to button
        Image img = new Image(getClass().getResourceAsStream("/img/search.png"));
        btnLookUp.setGraphic(new ImageView(img));

        // Add listeners
        tfIn.textProperty().addListener(fileListener);
        tfIn.textProperty().addListener(readyListener);
        cbMode.valueProperty().addListener(modeListener);
        cbMode.valueProperty().addListener(readyListener);
        pfPassword.textProperty().addListener(readyListener);
        showPw.selectedProperty().addListener(passwordListener);

        // Combine text and password field
        pfPassword.textProperty().bindBidirectional(tfPassword.textProperty());

        // Init mode
        cbMode.setValue(LBL_ENCRYPT);
        mode = Mode.ENCRYPT;
        btnRun.setDisable(true);
        lblVersion.setText(MavenUtils.getVersion());

        encryptionManager = EncryptionManager.getInstance();
    }

    /**
     * Map the input fields to the {@link File} objects
     */
    private void mapInputsToFiles() {
        inFile = new File(tfIn.getText());
        outFile = new File(lblOut.getText());
    }

    /**
     * Show "Success" or "Failed"
     *
     * @param success true/false
     */
    private void showStatus(boolean success) {
        Platform.runLater(() -> {
            lblStatus.setManaged(true);
            lblStatus.setVisible(true);
            if (success) {
                lblStatus.setText("Success");
                lblStatus.setTextFill(Color.GREEN);
            } else {
                lblStatus.setText("Failed");
                lblStatus.setTextFill(Color.RED);
            }
        });
    }

    /**
     * Show "Encrypting..." or "Decrypting..."
     */
    private void showStatus() {
        Platform.runLater(() -> {
            lblStatus.setManaged(true);
            lblStatus.setVisible(true);
            lblStatus.setTextFill(Color.BLACK);

            switch (mode) {
                case ENCRYPT:
                    lblStatus.setText("Encrypting...");
                    break;
                case DECRYPT:
                    lblStatus.setText("Decrypting...");
                    break;
            }
        });

    }

    private void hideStatus() {
        Platform.runLater(() -> {
            lblStatus.setManaged(false);
            lblStatus.setVisible(false);
        });
    }

    /**
     * Dis or enables all gui elements. Used while encryption
     *
     * @param bool true/false
     */
    private void running(boolean bool) {
        btnRun.setDisable(bool);
        btnLookUp.setDisable(bool);
        tfIn.setDisable(bool);
        pfPassword.setDisable(bool);
        tfPassword.setDisable(bool);
        cbMode.setDisable(bool);
        progress.setManaged(bool);
        progress.setVisible(bool);
        showPw.setDisable(bool);
    }

    private void requestFocusOnPasswordFiles() {
        if (pfPassword.isVisible()) {
            pfPassword.requestFocus();
        } else if (tfPassword.isVisible()) {
            tfPassword.requestFocus();
        }
    }

    /**
     * FileListener<br/>
     * <p>
     * Listens on tfIn's text property. Controls the lblOut and manages the .crypt additions
     */
    private ChangeListener<String> fileListener = new ChangeListener<>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (newValue == null) {
                return;
            }

            String path = tfIn.getText();

            if (path.isEmpty()) {
                lblOut.setText("");
                showSize(false);
                return;
            }

            switch (mode) {
                case ENCRYPT:
                    path = path + FILE_TYPE;
                    lblOut.setText(path);
                    break;

                case DECRYPT:
                    path = path.replace(FILE_TYPE, "");
                    lblOut.setText(path);
                    break;
            }
            mapInputsToFiles();
            showSize(true);
        }
    };

    /**
     * ModeListener<br/>
     * <p>
     * Listens on the choice box and flips the mode from encryption to decryption or the other way.<br/>
     * Triggers the {@link #fileListener}
     */
    private ChangeListener<String> modeListener = new ChangeListener<>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (newValue == null) {
                return;
            }

            switch (newValue) {
                case LBL_ENCRYPT:
                    mode = Mode.ENCRYPT;
                    btnRun.setText(LBL_ENCRYPT);
                    fileListener.changed(null, null, tfIn.getText());
                    break;

                case LBL_DECRYPT:
                    mode = Mode.DECRYPT;
                    btnRun.setText(LBL_DECRYPT);
                    fileListener.changed(null, null, tfIn.getText());
                    break;
            }
            mapInputsToFiles();
        }
    };

    /**
     * ReadyListener<br/>
     * <p>
     * Listens on ifIn, the password filed and the choice box.<br/>
     * Checks if every condition is met.<br/>
     * Conditions: The orig file exists and is no directory, the password is at least 4 characters long.<br/>
     * Controls the {@link #btnRun}
     */
    private ChangeListener<String> readyListener = new ChangeListener<>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            hideStatus();
            if (!tfIn.getText().isEmpty() && !lblOut.getText().isEmpty() && !pfPassword.getText().isEmpty()
                    && pfPassword.getText().length() >= 4 && !inFile.isDirectory() && inFile.exists()) {
                switch (mode) {
                    case ENCRYPT:
                        if (!inFile.getName().endsWith(FILE_TYPE)) {
                            btnRun.setDisable(false);
                            return;
                        }
                        break;
                    case DECRYPT:
                        if (inFile.getName().endsWith(FILE_TYPE)) {
                            btnRun.setDisable(false);
                            return;
                        }
                        break;
                }
            }
            btnRun.setDisable(true);

        }
    };

    /**
     * PasswordListener<br/>
     * <p>
     * Listens on the checkbox and switches between password and text field
     */
    private ChangeListener<Boolean> passwordListener = new ChangeListener<>() {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (showPw.isSelected()) {
                tfPassword.setVisible(true);
                pfPassword.setVisible(false);
            } else {
                tfPassword.setVisible(false);
                pfPassword.setVisible(true);
            }
        }
    };

    ////////////////////// Event Methods //////////////////////

    /**
     * Creates a FileDialog to select a file.<br/>
     * Error handling is provided
     *
     */
    public void btnLookUp() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            if (file.isDirectory()) {
                OptionPane.showAlertDialog(stage, "Error", "You cannot select a directory", AlertType.ERROR);
                return;
            }
            if (file.getAbsolutePath().endsWith(FILE_TYPE) && Mode.ENCRYPT.equals(mode)) {
                boolean des = OptionPane.showConfirmDialog(stage, null, file.getName() + " is already encrypted. Are you sure?");
                if (!des) {
                    return;
                }
            }
            tfIn.setText(file.getAbsolutePath());
            mapInputsToFiles();
        }
    }

    /**
     * Runs {@link #btnRun()} if enter is pressed in the password filed
     *
     * @param event KeyEvent
     */
    public void btnReturn(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !btnRun.isDisabled()) {
            btnRun();
        }
    }

    /**
     * Accepts a drag and drop file
     *
     * @param event DragEvent
     */
    public void onFileDragged(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            File firstFile = dragboard.getFiles().get(0);
            if (firstFile.exists()) {
                tfIn.setText(firstFile.getAbsolutePath());
                mapInputsToFiles();
                requestFocusOnPasswordFiles();
                success = true;
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    public void onDragOver(DragEvent dragEvent) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
    }

    private EncryptionCallback encryptionCallback = success -> {
        running(false);
        pfPassword.setText("");
        showStatus(success);
    };

    /**
     * Starts the execution of the main function.<br/>
     * Generates the tasks, binds properties, and runs the {@link #workingThread} with the task.
     */
    public void btnRun() {
        mapInputsToFiles();
        showPw.setSelected(false);
        showStatus();

        BaseTask task = getTask();
        if (task != null) {
            running(true);
            progress.progressProperty().bind(task.progressProperty());
            workingThread = new Thread(task,"WORKING-THREAD");
            workingThread.start();
        }
    }

    /**
     * Shows the credit html file in the browser.
     */
    public void showInfo() {
        new Thread(() -> {
            try {
                Desktop.getDesktop().browse(App.CREDITS.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Shows the size labels.
     *
     * @param bool tru/false
     */
    private void showSize(boolean bool) {
        lblSizeHeader.setManaged(bool);
        lblSizeHeader.setVisible(bool);
        lblSize.setManaged(bool);
        lblSize.setVisible(bool);
        if (bool) {
            lblSize.setText(getFileSize());
        } else {
            lblSize.setText("");
        }
    }

    /**
     * Calculates the displayed file size formats it.
     *
     * @return The files size as format String
     */
    private String getFileSize() {
        DecimalFormat df = new DecimalFormat("0.00");

        double bytes = inFile.length();
        double kiloBytes = bytes / 1024;
        double megaBytes = kiloBytes / 1024;
        double gigaBytes = megaBytes / 1024;

        if (gigaBytes > 1) {
            return df.format(gigaBytes) + "GB";
        } else if (megaBytes > 1) {
            return df.format(megaBytes) + "MB";
        } else if (kiloBytes > 1) {
            return df.format(kiloBytes) + "KB";
        } else {
            return df.format(bytes) + "Bytes";
        }
    }

    private BaseTask getTask() {
        switch (mode) {
            case ENCRYPT:
                return new EncryptionTask(inFile, outFile, encryptionManager.keyGen(pfPassword.getText()), encryptionCallback);
            case DECRYPT:
                return new DecryptionTask(inFile, outFile, encryptionManager.keyGen(pfPassword.getText()), encryptionCallback);
            default:
                return null;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

package view;

import controller.GreenBall;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Club;
import model.ClubDAOException;

public class UpdatePasswordViewController implements Initializable {

    private Stage stage;
    private Club clubInstance;

    private final String[] errorMessages = {
        "Campo obligatorio",
        "Debe contener entre 6 y 64 caracteres",
        "Debe contener una mayuscula, una minuscula, un numero y un caracter especial",
        "La contraseña no coincide",
        "Contraseña incorrecta"
    };

    private final Pattern passwordLengthPt = Pattern.compile("^.{6,64}$");
    private final Pattern passwordCharactersPt = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).+$");

    @FXML
    private PasswordField tfCurrent;
    @FXML
    private PasswordField tfNew;
    @FXML
    private PasswordField tfConfirm;
    @FXML
    private Label errorCurrent;
    @FXML
    private Label errorNew;
    @FXML
    private Label errorConfirm;
    @FXML
    private Button cancel;
    @FXML
    private Button save;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            clubInstance = Club.getInstance();
        } catch (ClubDAOException ex) {
            Logger.getLogger(UpdatePasswordViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UpdatePasswordViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tfCurrent.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw) {
                checkCurrentEmpty();
            }
        });
        tfNew.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw) {
                checkNew();
            }
        });
        tfConfirm.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw) {
                checkConfirm();
            }
        });
    }

    private boolean checkCurrentEmpty() {
        if (tfCurrent.getText().isBlank()) {
            tfCurrent.setText("");
            showError(errorCurrent, tfCurrent, true, errorMessages[0]);
            return false;
        }
        showError(errorCurrent, tfCurrent, false);
        return true;
    }

    private boolean checkCurrent() {
        if (clubInstance.getMemberByCredentials(GreenBall.getIntance().getLoggedUser().getNickName(), tfCurrent.getText()) == null) {
            tfCurrent.setText("");
            showError(errorCurrent, tfCurrent, true, errorMessages[4]);
            return false;
        }
        return true;
    }

    private boolean checkNew() {
        if (tfNew.getText().isBlank()) {
            tfNew.setText("");
            showError(errorNew, tfNew, true, errorMessages[0]);
        } else if (!passwordLengthPt.matcher(tfNew.getText()).matches()) {
            showError(errorNew, tfNew, true, errorMessages[1]);
        } else if (!passwordCharactersPt.matcher(tfNew.getText()).matches()) {
            showError(errorNew, tfNew, true, errorMessages[2]);
        } else {
            showError(errorNew, tfNew, false);
            return true;
        }
        return false;
    }

    private boolean checkConfirm() {
        if (tfConfirm.getText().isBlank()) {
            tfConfirm.setText("");
            showError(errorConfirm, tfConfirm, true, errorMessages[0]);
        } else if (!tfConfirm.getText().equals(tfNew.getText())) {
            showError(errorConfirm, tfConfirm, true, errorMessages[3]);
        } else {
            showError(errorConfirm, tfConfirm, false);
            return true;
        }
        return false;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void showError(Label label, TextField field, boolean show, String message) {
        label.setText(message);
        showError(label, field, show);
    }

    private void showError(Label label, TextField field, boolean show) {
        if (show) {
            if (!field.getStyleClass().contains("error-field")) {
                field.getStyleClass().add("error-field");
            }
            label.setPrefHeight(Region.USE_COMPUTED_SIZE);
        } else {
            if (field.getStyleClass().contains("error-field")) {
                field.getStyleClass().remove("error-field");
            }
            label.setPrefHeight(0);
        }
    }

    @FXML
    private void save(ActionEvent event) {
        if (checkNew() && checkConfirm() && checkCurrentEmpty() && checkCurrent()) {
            GreenBall.getIntance().getLoggedUser().setPassword(tfNew.getText());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contraseña cambiada");
            alert.setHeaderText("Contraseña modificada con exito");
            alert.setContentText(null);
            alert.showAndWait();
            close(null);
        }
    }

    @FXML
    private void close(ActionEvent event) {
        stage.close();
    }

}

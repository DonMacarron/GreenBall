package view.components;

import controller.GreenBall;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import model.Club;
import model.ClubDAOException;
import model.Member;

public class StartUpOptionsComponentViewController implements Initializable {

    @FXML
    private TextField user;
    @FXML
    private Label errorUser;
    @FXML
    private PasswordField password;
    @FXML
    private Label errorPassword;

    private final String[] errorMessages = {
        "Usuario obligatorio",
        "Contraseña obligatoria",
        "El usuario no existe",
        "Contraseña incorrecta"
    };
    private Club club;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            club = Club.getInstance();
        } catch (ClubDAOException ex) {
            Logger.getLogger(StartUpOptionsComponentViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StartUpOptionsComponentViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        user.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw) {
                user.setText(user.getText().trim());
            }
        });
    }

    @FXML
    private void logIn(ActionEvent event) {
        if (!isEmptyFields() && existsUser()) {
            Member member = getMember();
            if (member == null) {
                showError(errorPassword, password, true, errorMessages[3]);
                password.setText("");
                password.requestFocus();
            } else {
                GreenBall.getIntance().logIn(member);
            }
        }
    }

    @FXML
    private void register(ActionEvent event) {
        GreenBall.getIntance().register();
    }

    private boolean isEmptyFields() {
        boolean isEmpty = false;
        if (password.getText().isEmpty()) {
            showError(errorPassword, password, true, errorMessages[1]);
            password.requestFocus();
            isEmpty = true;
        }
        if (user.getText().isEmpty()) {
            showError(errorUser, user, true, errorMessages[0]);
            user.requestFocus();
            isEmpty = true;
        }
        return isEmpty;
    }

    private boolean existsUser() {
        if (club.existsLogin(user.getText())) {
            return true;
        }
        showError(errorUser, user, true, errorMessages[2]);
        user.setText("");
        user.requestFocus();
        return false;
    }

    private Member getMember() {
        return club.getMemberByCredentials(user.getText(), password.getText());
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
    private void fieldUpdated(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        if (field.getText().isEmpty()) {
            return;
        }

        if (field.equals(user)) {
            showError(errorUser, user, false);
        } else {
            showError(errorPassword, password, false);
        }
    }
}

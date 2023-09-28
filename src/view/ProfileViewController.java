package view;

import controller.GreenBall;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Club;
import model.ClubDAOException;
import model.Member;
import view.components.MenuComponentViewController;

public class ProfileViewController implements Initializable {

    @FXML
    private Button imgDelete;
    @FXML
    private Button imgSelect;
    @FXML
    private TextField user;
    @FXML
    private Label errorUser;
    @FXML
    private TextField name;
    @FXML
    private Label errorName;
    @FXML
    private TextField surname;
    @FXML
    private Label errorSurname;
    @FXML
    private TextField phone;
    @FXML
    private Label errorPhone;
    @FXML
    private TextField card;
    @FXML
    private Label errorCard;
    @FXML
    private TextField svc;
    @FXML
    private Label errorSvc;
    @FXML
    private Button cardDelete;
    @FXML
    private ImageView imgUser;
    @FXML
    private HBox imgEdit;
    @FXML
    private Button save;
    @FXML
    private Button cancel;
    @FXML
    private Button edit;
    @FXML
    private HBox controls;
    @FXML
    private VBox registerPassword;
    @FXML
    private PasswordField password;
    @FXML
    private Label errorPassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label errorConfirmPassword;
    @FXML
    private VBox changePassword;

    private Member loggedUser;
    private BooleanProperty isEditable;

    private final Pattern userLengthPt = Pattern.compile("^.{2,24}$");
    private final Pattern userCharactersPt = Pattern.compile("^[a-zA-Z0-9]*$");
    private final Pattern phonePt = Pattern.compile("^((?:\\+|00)34)?\\d{9}$");
    private final Pattern namePt = Pattern.compile("^[A-Za-z\\s\\']{2,32}$");
    private final Pattern surnamePt = Pattern.compile("^[A-Za-z\\s\\']{2,64}$");
    private final Pattern passwordLengthPt = Pattern.compile("^.{6,64}$");
    private final Pattern passwordCharactersPt = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).+$");
    private final Pattern cardPt = Pattern.compile("^\\d{16}$");
    private final Pattern svcPt = Pattern.compile("^\\d{3}$");

    private final String[] errorMessages = {
        "Campo obligatorio",
        "Campo requerido",
        "Debe contener entre 2 y 24 caracteres",
        "Solo se permiten números y letas",
        "Nombre invalido",
        "Apellidos invalidos",
        "Teléfono invalido",
        "Debe de estar formada por 16 digitos",
        "Debe de contener 3 digitos",
        "Debe contener entre 6 y 64 caracteres",
        "Debe contener una mayuscula, una minuscula, un numero y un caracter especial",
        "La contraseña no coincide"
    };

    private final Image defaultImage = new Image("/resources/images/profile-b.png");

    private final BooleanProperty isRegister = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Save user
        loggedUser = GreenBall.getIntance().getLoggedUser();

        //Load data
        //setMemberData();
        //Bind fields
        isEditable = new SimpleBooleanProperty(true);
        user.disableProperty().bind(isRegister.not());
        name.disableProperty().bind(isEditable.not());
        surname.disableProperty().bind(isEditable.not());
        phone.disableProperty().bind(isEditable.not());
        card.disableProperty().bind(isEditable.not());
        svc.disableProperty().bind(isEditable.not());

        //Bind field cuttons
        imgDelete.visibleProperty().bind(isEditable);
        imgSelect.visibleProperty().bind(isEditable);
        cardDelete.visibleProperty().bind(isEditable);
        imgEdit.prefHeightProperty().bind(Bindings.when(isEditable).then(Region.USE_COMPUTED_SIZE).otherwise(0));

        //Bind control buttons
        cancel.visibleProperty().bind(isEditable);
        save.visibleProperty().bind(isEditable);
        edit.visibleProperty().bind(isEditable.not());
        controls.alignmentProperty().bind(Bindings.when(isEditable).then(Pos.BOTTOM_LEFT).otherwise(Pos.BOTTOM_RIGHT));
        controls.nodeOrientationProperty().bind(Bindings.when(isEditable).then(NodeOrientation.RIGHT_TO_LEFT).otherwise(NodeOrientation.LEFT_TO_RIGHT));

        changePassword.maxHeightProperty().bind(Bindings.when(isRegister).then(0).otherwise(Region.USE_COMPUTED_SIZE));
        changePassword.visibleProperty().bind(isRegister.not());

        registerPassword.maxHeightProperty().bind(Bindings.when(isRegister).then(Region.USE_COMPUTED_SIZE).otherwise(0));
        registerPassword.visibleProperty().bind(isRegister);

        //Add check fields
        user.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(user);
            }
        });
        name.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(name);
            }
        });
        surname.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(surname);
            }
        });
        phone.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(phone);
            }
        });
        card.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(card);
            }
        });
        card.textProperty().addListener((ov, ol, nw) -> {
            if (nw.length() > 16) {
                card.setText(ol);
            }

            if ("".equals(nw)) {
                return;
            }

            if (!nw.matches("\\d*")) {
                card.setText(nw.replaceAll("[^\\d]", ""));
            }

        });
        svc.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(svc);
            }
        });
        svc.textProperty().addListener((ov, ol, nw) -> {
            if (nw.length() > 3) {
                svc.setText(ol);
            }
            if ("".equals(nw)) {
                return;
            }

            if (!nw.matches("\\d*")) {
                svc.setText(nw.replaceAll("[^\\d]", ""));
            }

        });
        password.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(password);
            }
        });

        confirmPassword.focusedProperty().addListener((ov, ol, nw) -> {
            if (!nw && isEditable.get()) {
                fieldUpdated(confirmPassword);
            }
        });

        edit.setOnAction((nw) -> isEditable.set(true));
    }

    private final BooleanProperty selected = new SimpleBooleanProperty();

    private void cleanErrors() {
        showError(errorUser, user, false);
        showError(errorName, name, false);
        showError(errorSurname, surname, false);
        showError(errorPhone, phone, false);
        showError(errorPassword, password, false);
        showError(errorConfirmPassword, confirmPassword, false);
        showError(errorCard, card, false);
        showError(errorSvc, svc, false);
    }

    public final BooleanProperty selectedProperty() {
        return selected;
    }

    public final boolean isSelected() {
        return selectedProperty().get();
    }

    public final void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    public void setMemberData() {
        user.setText(loggedUser.getNickName());
        name.setText(loggedUser.getName());
        surname.setText(loggedUser.getSurname());
        phone.setText(loggedUser.getTelephone());
        card.setText(loggedUser.getCreditCard());
        svc.setText(loggedUser.getSvc() == -1 ? "" : Integer.toString(loggedUser.getSvc()));
        setUserImage(loggedUser.getImage());

        isRegister.setValue(false);
        isEditable.setValue(false);
    }

    private void setUserImage(Image image) {
        imgUser.imageProperty().set(image);

        double width = image.getWidth();
        double height = image.getHeight();
        double minDimension = Math.min(width, height);
        double xOffset = (width - minDimension) / 2;
        double yOffset = (height - minDimension) / 2;

        // Set the viewport to crop the image
        Rectangle2D viewport = new Rectangle2D(xOffset, yOffset, minDimension, minDimension);
        imgUser.setViewport(viewport);
    }

    @FXML
    private void selectUserImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All images", "*.img", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("IMG Files", "*.img"),
                new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );

        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            setUserImage(image);
        }
    }

    public boolean checkMandatory() {
        return checkUser() && checkName() && checkSurname() && checkPhone();
    }

    public boolean checkAllFields() {
        if (isRegister.get()) {
            return checkMandatory() && checkCardAndSvc() && checkPassword() && checkConfirmPassword();
        } else {
            return checkMandatory() && checkCardAndSvc();
        }
    }

    private boolean checkUser() {
        if (user.getText().isBlank()) {
            user.setText("");
            showError(errorUser, user, true, errorMessages[0]);
        } else if (!userLengthPt.matcher(user.getText()).matches()) {
            showError(errorUser, user, true, errorMessages[2]);
        } else if (!userCharactersPt.matcher(user.getText()).matches()) {
            showError(errorUser, user, true, errorMessages[3]);
        } else {
            showError(errorUser, user, false);
            return true;
        }
        return false;
    }

    private boolean checkName() {
        if (name.getText().isBlank()) {
            name.setText("");
            showError(errorName, name, true, errorMessages[0]);
        } else if (!namePt.matcher(name.getText()).matches()) {
            showError(errorName, name, true, errorMessages[4]);
        } else {
            showError(errorName, name, false);
            return true;
        }
        return false;
    }

    private boolean checkSurname() {
        if (surname.getText().isBlank()) {
            surname.setText("");
            showError(errorSurname, surname, true, errorMessages[0]);
        } else if (!surnamePt.matcher(surname.getText()).matches()) {
            showError(errorSurname, surname, true, errorMessages[5]);
        } else {
            showError(errorSurname, surname, false);
            return true;
        }
        return false;
    }

    private boolean checkPhone() {
        if (phone.getText().isBlank()) {
            phone.setText("");
            showError(errorPhone, phone, true, errorMessages[0]);
        } else if (!phonePt.matcher(phone.getText()).matches()) {
            showError(errorPhone, phone, true, errorMessages[6]);
        } else {
            showError(errorPhone, phone, false);
            return true;
        }
        return false;
    }

    private boolean checkPassword() {
        if (password.getText().isBlank()) {
            password.setText("");
            showError(errorPassword, password, true, errorMessages[0]);
        } else if (!passwordLengthPt.matcher(password.getText()).matches()) {
            showError(errorPassword, password, true, errorMessages[9]);
        } else if (!passwordCharactersPt.matcher(password.getText()).matches()) {
            showError(errorPassword, password, true, errorMessages[10]);
        } else {
            showError(errorPassword, password, false);
            return true;
        }
        return false;
    }

    private boolean checkConfirmPassword() {
        if (confirmPassword.getText().isBlank()) {
            confirmPassword.setText("");
            showError(errorConfirmPassword, confirmPassword, true, errorMessages[0]);
        } else if (!confirmPassword.getText().equals(password.getText())) {
            showError(errorConfirmPassword, confirmPassword, true, errorMessages[11]);
        } else {
            showError(errorConfirmPassword, confirmPassword, false);
            return true;
        }
        return false;
    }

    private boolean checkCardAndSvc() {
        if (!card.getText().isEmpty() || !svc.getText().isEmpty()) {
            boolean cardCorrect = false;
            boolean svcCorrect = false;
            //card
            if (card.getText().isEmpty()) {
                showError(errorCard, card, true, errorMessages[1]);
            } else if (!cardPt.matcher(card.getText()).matches()) {
                showError(errorCard, card, true, errorMessages[7]);
            } else {
                showError(errorCard, card, false);
                cardCorrect = true;
            }

            //svc
            if (svc.getText().isEmpty()) {
                showError(errorSvc, svc, true, errorMessages[1]);
            } else if (!svcPt.matcher(svc.getText()).matches()) {
                showError(errorSvc, svc, true, errorMessages[8]);
            } else {
                showError(errorSvc, svc, false);
                svcCorrect = true;
            }

            return cardCorrect && svcCorrect;
        } else {
            showError(errorCard, card, false);
            showError(errorSvc, svc, false);
            return true;
        }
    }

    private void fieldUpdated(TextField field) {

        switch (field.getId()) {
            case "user":
                checkUser();
                break;
            case "name":
                checkName();
                break;
            case "surname":
                checkSurname();
                break;
            case "phone":
                checkPhone();
                break;
            case "password":
                System.out.println("a");
                checkPassword();
                break;
            case "confirmPassword":
                System.out.println("b");
                checkConfirmPassword();
                break;
            case "card":
            case "svc":
                checkCardAndSvc();
                break;
            default:
                System.err.println("NO SE QUE HA PASADO");
        }
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
    private void changePassword(ActionEvent event) {
        try {
            GreenBall.getIntance().loadUpdatePassword();
        } catch (IOException ex) {
            Logger.getLogger(MenuComponentViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteUserImage(ActionEvent event) {
        setUserImage(defaultImage);
    }

    private void deleteCard(MouseEvent event) {
        
    }

    @FXML
    private void save(ActionEvent event) {
        if (isRegister.get()) {
            if (checkAllFields()) {

                Club club;
                try {
                    club = Club.getInstance();
                    if (club.existsLogin(user.getText())) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error usuario");
                        alert.setHeaderText("El usuario ya existe");
                        alert.setContentText(null);
                        alert.showAndWait();
                        user.setText("");
                        return;
                    }
                    int tmpSvc = svc.getText().isBlank() ? -1 : Integer.valueOf(svc.getText());
                    club.registerMember(name.getText(), surname.getText(), phone.getText(), user.getText(), password.getText(), card.getText(), tmpSvc, imgUser.getImage());
                } catch (ClubDAOException ex) {
                    Logger.getLogger(ProfileViewController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ProfileViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Usuario registrado");
                alert.setHeaderText("Se ha registrado el usario correctamente.");
                alert.setContentText(null);
                alert.showAndWait();
                ((Stage) user.getScene().getWindow()).close();
            }
        } else {
            if (checkAllFields()) {
                Member tmp = GreenBall.getIntance().getLoggedUser();
                if (!name.getText().equals(tmp.getName())) {
                    tmp.setName(name.getText());
                }
                if (!surname.getText().equals(tmp.getSurname())) {
                    tmp.setSurname(surname.getText());
                }
                if (!phone.getText().equals(tmp.getTelephone())) {
                    tmp.setTelephone(phone.getText());
                }
                if (!card.getText().equals(tmp.getCreditCard())) {
                    tmp.setCreditCard(card.getText());
                }
                int tmpSvc = svc.getText().isBlank() ? -1 : Integer.valueOf(svc.getText());
                if (tmpSvc != tmp.getSvc()) {
                    tmp.setSvc(tmpSvc);
                }
                if (!imgUser.getImage().equals(tmp.getImage())) {
                    tmp.setImage(imgUser.getImage());
                }
                setMemberData();
                isEditable.set(false);
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        if (isRegister.get()) {
            ((Stage) user.getScene().getWindow()).close();
        } else {
            cleanErrors();
            setMemberData();
            isEditable.set(false);
        }

    }

    @FXML
    private void deleteCard(ActionEvent event) {
        card.textProperty().set("");
        svc.textProperty().set("");
        checkCardAndSvc();
    }

}

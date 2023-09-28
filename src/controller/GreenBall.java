package controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Booking;
import model.Member;
import view.BookingsViewController;
import view.CalendarViewController;
import view.ProfileViewController;
import view.UISkeletonViewController;
import view.UpdatePasswordViewController;
import view.components.BookingEditController;

public class GreenBall extends Application {

    private Stage stage;
    private Member loggedUser;
    private UISkeletonViewController uiController;

    public BooleanProperty loggedUserProperty;

    private static GreenBall instance;

    public GreenBall() {
        instance = this;
    }

    public static GreenBall getIntance() {
        return instance;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loggedUserProperty = new SimpleBooleanProperty(false);
        stage = primaryStage;
        loadUi();
        stage.show();

    }

    public void loadUi() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UISkeletonView.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.setMaximized(true);
            uiController = loader.getController();

            if (loggedUser == null) {
                loadStartUp();
            } else {
                loadMenu();
            }

            loadCalendar();

        } catch (IOException ex) {
            Logger.getLogger(GreenBall.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadMenu() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/MenuComponentView.fxml"));
        uiController.setLateralPanel(loader);
    }

    public void loadStartUp() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/StartUpOptionsComponentView.fxml"));
        uiController.setLateralPanel(loader);
    }

    public void loadCalendar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CalendarView.fxml"));
        uiController.setCentralPanel(loader);
        stage.setTitle("GreenBall - Calendario");
    }

    public void logOut() {
        loggedUser = null;
        loggedUserProperty.setValue(false);
        loadStartUp();
        Object tmp = uiController.getCentralController();
        if (tmp instanceof CalendarViewController) {
            CalendarViewController controller = (CalendarViewController) tmp;
            controller.reload();
        } else {
            loadCalendar();
        }
    }

    public void logIn(Member member) {
        loggedUser = member;
        loggedUserProperty.setValue(true);
        Object tmp = uiController.getCentralController();
        if (tmp instanceof CalendarViewController) {
            CalendarViewController controller = (CalendarViewController) tmp;
            controller.reload();
        }
        loadMenu();
    }

    public void register() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileView.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GreenBall.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Stage stage = new Stage();
        stage.setMinWidth(40);
        stage.setMinHeight(400);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("GreenBall - Registro de ususario");
        stage.showAndWait();
    }

    public void loadProfile() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileView.fxml"));
        uiController.setCentralPanel(loader);
        ((ProfileViewController) loader.getController()).setMemberData();
        stage.setTitle("GreenBall - Perfil");

    }

    public void loadBookings() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookingsView.fxml"));
        uiController.setCentralPanel(loader);
        stage.setTitle("GreenBall - Mis Reservas");
    }

    public Member getLoggedUser() {
        return loggedUser;
    }

    public Object getCentralController() {
        return uiController.getCentralController();
    }

    public void loadUpdatePassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdatePasswordView.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setResizable(false);
        ((UpdatePasswordViewController) loader.getController()).setStage(stage);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("GreenBall - Actualizar contraseña");
        stage.showAndWait();
    }

    public void loadBookingDetails(Booking booking) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/BookingEdit.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        ((BookingEditController) loader.getController()).initializeData(booking, stage);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("GreenBall - Reserva");
        stage.showAndWait();
    }

}

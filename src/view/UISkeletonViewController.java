package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class UISkeletonViewController implements Initializable {

    @FXML
    private BorderPane mainView;

    private Object centralController;
    private Object lateralController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public Parent setLateralPanel(FXMLLoader loader) {
        try {
            Parent rootLateral = loader.load();
            mainView.setLeft(rootLateral);
            lateralController = loader.getController();
            return rootLateral;
        } catch (IOException ex) {
            Logger.getLogger(UISkeletonViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Parent setCentralPanel(FXMLLoader loader) {
        try {
            Parent rootCentral = loader.load();
            mainView.setCenter(rootCentral);
            centralController = loader.getController();
            return rootCentral;
        } catch (IOException ex) {
            Logger.getLogger(UISkeletonViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Object getCentralController() {
        return centralController;
    }

    public Object getLateralController() {
        return lateralController;
    }

}

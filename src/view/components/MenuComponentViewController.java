package view.components;

import controller.GreenBall;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MenuComponentViewController implements Initializable {

    @FXML
    private VBox menu;
    @FXML
    private HBox menuTile;
    @FXML
    private Button burgerMenuButton;
    @FXML
    private VBox menuItemsBox;
    @FXML
    private ImageView burgerMenuButtonIcon;
    @FXML
    private Button logOut;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnCalendar;
    @FXML
    private Button btnBookings;
    @FXML
    private VBox MenuLogoBox;
    @FXML
    private ImageView textLogoImage;

    BooleanProperty menuExpanded;
    Image burgerOpen;
    Image burgerClose;
    Image textLogo;
    
    int widthMenuBig = 200;
    int widthMenuSmall = 50;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuExpanded = new SimpleBooleanProperty(true);
        burgerClose = new Image("/resources/images/open-menu-b.png");
        burgerOpen = new Image("/resources/images/close-menu-b.png");
        textLogo = new Image("/resources/images/greenball-logo-texto.png");

        //Toggles between 2 images whem menu expanded or contracted
        burgerMenuButtonIcon.imageProperty().bind(Bindings.when(menuExpanded).then(burgerOpen).otherwise(burgerClose));
        //Removes the logo from the menu when contracted
        textLogoImage.imageProperty().bind(Bindings.when(menuExpanded).then(textLogo).otherwise((Image) null));
        textLogoImage.fitWidthProperty().bind(Bindings.when(menuExpanded).then(100).otherwise(0));
        MenuLogoBox.maxWidthProperty().bind(Bindings.when(menuExpanded).then(Region.USE_COMPUTED_SIZE).otherwise(0));
        //Centers toggle icon when menu contracted
        burgerMenuButton.prefWidthProperty().bind(Bindings.when(menuExpanded).then(Region.USE_COMPUTED_SIZE).otherwise(2048));
        //Expands and contracts the menu
        menu.prefWidthProperty().bind(Bindings.when(menuExpanded).then(widthMenuBig).otherwise(widthMenuSmall));
        //Set look and position when buttons expanded or contracted
        for (Node node : menuItemsBox.getChildren()) {
            ((Button) node).contentDisplayProperty().bind(Bindings.when(menuExpanded).then(ContentDisplay.LEFT).otherwise(ContentDisplay.GRAPHIC_ONLY));
            ((Button) node).alignmentProperty().bind(Bindings.when(menuExpanded).then(Pos.BASELINE_LEFT).otherwise(Pos.CENTER));
        }
        //Set look and position when logOut button expanded or contracted
        logOut.contentDisplayProperty().bind(Bindings.when(menuExpanded).then(ContentDisplay.LEFT).otherwise(ContentDisplay.GRAPHIC_ONLY));
        logOut.alignmentProperty().bind(Bindings.when(menuExpanded).then(Pos.BASELINE_LEFT).otherwise(Pos.CENTER));

        btnCalendar.disableProperty().addListener((ov, ol, nw) -> {
            ImageView imv = (ImageView) btnCalendar.getGraphic();
            if (nw) {
                imv.setImage(new Image("/resources/images/calendar-w.png"));
            } else {
                imv.setImage(new Image("/resources/images/calendar-b.png"));
            }
        });
        btnBookings.disableProperty().addListener((ov, ol, nw) -> {
            ImageView imv = (ImageView) btnBookings.getGraphic();
            if (nw) {
                imv.setImage(new Image("/resources/images/bookings-w.png"));
            } else {
                imv.setImage(new Image("/resources/images/bookings-b.png"));
            }
        });
        btnProfile.disableProperty().addListener((ov, ol, nw) -> {
            ImageView imv = (ImageView) btnProfile.getGraphic();
            if (nw) {
                imv.setImage(new Image("/resources/images/profile-w.png"));
            } else {
                imv.setImage(new Image("/resources/images/profile-b.png"));
            }
        });
        
        btnCalendar.disableProperty().set(true);

    }

    @FXML
    private void toggleMenu(ActionEvent event) {
        menuExpanded.set(!menuExpanded.get());
    }

    @FXML
    private void loadProfile(ActionEvent event) {
        GreenBall.getIntance().loadProfile();
        btnProfile.disableProperty().set(true);
        btnCalendar.disableProperty().set(false);
        btnBookings.disableProperty().set(false);
    }

    @FXML
    private void loadCalendar(ActionEvent event) {
        GreenBall.getIntance().loadCalendar();
        btnProfile.disableProperty().set(false);
        btnCalendar.disableProperty().set(true);
        btnBookings.disableProperty().set(false);
    }

    @FXML
    private void loadBookings(ActionEvent event) {
        GreenBall.getIntance().loadBookings();
        btnProfile.disableProperty().set(false);
        btnCalendar.disableProperty().set(false);
        btnBookings.disableProperty().set(true);
    }

    @FXML
    private void logOut(ActionEvent event) {
        GreenBall.getIntance().logOut();
    }

}

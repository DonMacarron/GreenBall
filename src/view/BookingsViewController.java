package view;

import controller.GreenBall;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import view.components.TimeSlotComponentController;

/**
 * FXML Controller class
 *
 * @author Usuario
 */
public class BookingsViewController implements Initializable {

    @FXML
    private Button previous;
    @FXML
    private Button next;
    @FXML
    private GridPane grid;
    @FXML
    private Label currPage;
    @FXML
    private Label totalPage;

    private Club clubInstance;
    private ArrayList<Booking> userBookings;
    private ArrayList<Booking> userPastBookings;
    private List<Node> slotViews;
    private List<TimeSlotComponentController> slotControllers;

    private IntegerProperty page;
    private IntegerProperty totalBookings;
    private final int maxInGrid = 10;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        page = new SimpleIntegerProperty(0);
        totalBookings = new SimpleIntegerProperty(0);
        slotControllers = new ArrayList<>();
        slotViews = new ArrayList<>();

        currPage.textProperty().bind(page.add(1).asString());

        try {
            clubInstance = Club.getInstance();
        } catch (ClubDAOException ex) {
            Logger.getLogger(CalendarViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CalendarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (clubInstance == null) {
            System.err.println("ERRO DB");
            return;
        }

        fetchBookings();

        previous.disableProperty().bind(page.isEqualTo(0));
        next.disableProperty().bind(totalBookings.greaterThan(page.multiply(maxInGrid).add(maxInGrid)).not());
    }

    public void fetchBookings() {
        userBookings = new ArrayList<>(clubInstance.getUserBookings(GreenBall.getIntance().getLoggedUser().getNickName()));
        userPastBookings = new ArrayList<>();

        Iterator<Booking> iterator = userBookings.iterator();
        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            LocalDateTime tmp = booking.getMadeForDay().atTime(booking.getFromTime());
            if (tmp.isBefore(LocalDateTime.now())) {
                userPastBookings.add(booking);
                iterator.remove();
            } else {
                break;
            }
        }
        Collections.reverse(userPastBookings);
        userBookings.addAll(userPastBookings);

        page.setValue(0);
        totalBookings.setValue(userBookings.size());

        if (totalBookings.get() % maxInGrid == 0) {
            totalPage.setText(Integer.toString(totalBookings.get() / maxInGrid));
        } else {
            totalPage.setText(Integer.toString((totalBookings.get() / maxInGrid) + 1));
        }

        loadBookings();
    }

    private void loadBookings() {
        ObservableList<Node> children = grid.getChildren();
        for (Node node : slotViews) {
            children.remove(node);
        }

        slotControllers = new ArrayList<>();
        slotViews = new ArrayList<>();

        int slotIndex = 0;
        int initialIndex = page.get() * maxInGrid;
        int finalIndex = initialIndex + 10;

        for (int i = initialIndex; i < finalIndex && i < userBookings.size(); i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/TimeSlotComponent.fxml"));
            Parent root;
            try {
                root = loader.load();
            } catch (IOException ex) {
                Logger.getLogger(CalendarViewController.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }

            TimeSlotComponentController slotController = (TimeSlotComponentController) loader.getController();
            slotController.setForBookings(userBookings.get(i));

            selectedHandler(root);
            slotViews.add(root);
            slotControllers.add(slotController);

            grid.add(root, 0, slotIndex);
            slotIndex++;
        }
    }

    private void selectedHandler(Node node) {
        node.setOnMouseClicked((MouseEvent event) -> {
            if (!slotControllers.get(slotViews.indexOf(node)).isSelected()) {
                for (int i = 0; i < slotViews.size(); i++) {
                    slotControllers.get(i).setSelected(slotViews.get(i) == node);
                }
            }
        });
    }

    @FXML
    private void previous(ActionEvent event) {
        page.setValue(page.getValue() - 1);
        loadBookings();
    }

    @FXML
    private void next(ActionEvent event) {
        page.setValue(page.getValue() + 1);
        loadBookings();
    }
}

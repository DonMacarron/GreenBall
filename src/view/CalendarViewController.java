package view;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import model.Court;
import view.components.TimeSlotComponentController;

public class CalendarViewController implements Initializable {

    private final LocalTime firstSlotStart = LocalTime.of(9, 0);
    private final Duration slotLength = Duration.ofMinutes(60);
    private final LocalTime lastSlotStart = LocalTime.of(21, 0);

    private Club clubInstance;

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private List<Node> slotViews = new ArrayList<>();
    private List<TimeSlotComponentController> slotControllers = new ArrayList<>();

    @FXML
    private ComboBox<Court> courtSelector;
    @FXML
    private DatePicker dateSelector;
    @FXML
    private GridPane grid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        setUpComboBox();
        setUpDatePicker();
        reload();
    }

    private void setUpBookings(LocalDate date, Court court) {
        ArrayList<Booking> courtBookings = new ArrayList<>(clubInstance.getCourtBookings(court.getName(), date));

        ObservableList<Node> children = grid.getChildren();
        for (Node timeSlot : slotViews) {
            children.remove(timeSlot);
        }
        slotControllers = new ArrayList<>();
        slotViews = new ArrayList<>();

        int slotIndex = 0;

        for (LocalDateTime startTime = date.atTime(firstSlotStart);
                !startTime.isAfter(date.atTime(lastSlotStart));
                startTime = startTime.plus(slotLength)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/components/TimeSlotComponent.fxml"));
            Parent root;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(slotIndex);
                Logger.getLogger(CalendarViewController.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }

            TimeSlotComponentController slotController = (TimeSlotComponentController) loader.getController();
            if (!courtBookings.isEmpty() && startTime.getHour() == courtBookings.get(0).getFromTime().getHour()) {
                slotController.setForCalendar(courtBookings.get(0));
                courtBookings.remove(0);
            } else {
                slotController.setForCalendar(startTime.toLocalDate(), startTime.toLocalTime(), court);
            }

            slotViews.add(root);
            slotControllers.add(slotController);
            selectedHandler(root);

            grid.add(root, 1, slotIndex);
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
    private void updateCalendar(ActionEvent event) {
        reload();
    }

    public void reload() {
        setUpBookings(dateSelector.getValue(), courtSelector.getValue());
    }

    private void setUpDatePicker() {
        dateSelector.setValue(LocalDate.now());
        dateSelector.setDayCellFactory((DatePicker picker) -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || date.compareTo(today) < 0);
                }
            };
        });
    }

    private void setUpComboBox() {
        List<Court> courts = clubInstance.getCourts();
        courtSelector.getItems().addAll(courts);
        courtSelector.setCellFactory(c -> new CourtNameCell());
        courtSelector.setButtonCell(new CourtNameCell());
        courtSelector.getSelectionModel().selectFirst();
    }

    class CourtNameCell extends ComboBoxListCell<Court> {

        @Override
        public void updateItem(Court court, boolean empty) {
            super.updateItem(court, empty);
            if (empty || court == null) {
                setText(null);
            } else {
                setText(court.getName());
            }
        }
    }

}

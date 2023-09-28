package view.components;

import controller.GreenBall;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import view.BookingsViewController;
import view.CalendarViewController;

public class BookingEditController implements Initializable {

    @FXML
    private VBox bookingEditFrame;
    @FXML
    private Label dateBooking;
    @FXML
    private Label timeBooking;
    @FXML
    private Label durationBooking;
    @FXML
    private Label courtBooking;
    @FXML
    private Label dateMadeBooking;
    @FXML
    private Label paidBooking;
    @FXML
    private Button close;
    @FXML
    private Button delete;
    @FXML
    private Button pay;

    private Booking booking;
    private Stage stage;
    private final BooleanProperty isPaid = new SimpleBooleanProperty(false);
    private final BooleanProperty canDelete = new SimpleBooleanProperty(false);
    private boolean dataChanged = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pay.visibleProperty().bind(isPaid.not());
        delete.visibleProperty().bind(canDelete);
        paidBooking.textProperty().bind(Bindings.when(isPaid).then("Pagado").otherwise("No pagado"));

    }

    public void initializeData(Booking booking, Stage stage) {
        DateTimeFormatter dateF = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("es"));
        DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm");

        this.booking = booking;
        this.stage = stage;
        dateBooking.setText(booking.getMadeForDay().format(dateF));
        timeBooking.setText(booking.getFromTime().format(timeF));
        durationBooking.setText("60 min.");
        courtBooking.setText(booking.getCourt().getName());
        dateMadeBooking.setText(booking.getBookingDate().format(dateF));

        isPaid.setValue(booking.getPaid());
        canDelete.set(booking.getMadeForDay().atTime(booking.getFromTime()).isAfter(LocalDateTime.now().plusDays(1)));
        stage.setOnCloseRequest((ev) -> {
            ev.consume();
            returnAndClose();
        });
    }

    @FXML
    private void delete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar cita");
        alert.setHeaderText("¿Esta seguro de que desea eliminar la reserva?");
        alert.setContentText("Esta acción es irreversible.");

        ButtonType btnDelete = new ButtonType("Eliminar");
        ButtonType btnCancel = new ButtonType("Cancelar");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(btnDelete, btnCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnDelete) {
            try {
                Club.getInstance().removeBooking(booking);
            } catch (ClubDAOException ex) {
                Logger.getLogger(BookingEditController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BookingEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataChanged = true;
            returnAndClose();
        }
    }

    private void returnAndClose() {
        Object invocatorController = GreenBall.getIntance().getCentralController();
        if (dataChanged) {
            if (invocatorController instanceof CalendarViewController) {
                ((CalendarViewController) invocatorController).reload();
            } else if (invocatorController instanceof BookingsViewController) {
                ((BookingsViewController) invocatorController).fetchBookings();
            }
        }
        stage.close();
    }

    @FXML
    private void pay(ActionEvent event) {
        if (GreenBall.getIntance().getLoggedUser().checkHasCreditInfo()) {
            booking.setPaid(true);
            isPaid.setValue(true);
            dataChanged = true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error pago");
            alert.setHeaderText("Error durante el pago");
            alert.setContentText("El usuario no ha asigando tarjeta de credito.");
            alert.showAndWait();
        }
    }

    @FXML
    private void close(ActionEvent event) {
        returnAndClose();
    }

}

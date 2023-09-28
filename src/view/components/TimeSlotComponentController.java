package view.components;

import controller.GreenBall;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Booking;
import model.Club;
import model.ClubDAOException;
import model.Court;
import model.Member;
import view.CalendarViewController;

public class TimeSlotComponentController implements Initializable {

    @FXML
    private VBox userInfo;
    @FXML
    private HBox bookingInfo;
    @FXML
    private Label date;
    @FXML
    private Label hour;
    @FXML
    private Label court;
    @FXML
    private Label duration;
    @FXML
    private Label payment;
    @FXML
    private ImageView action;
    @FXML
    private VBox infoSection;
    @FXML
    private VBox iconSection;
    @FXML
    private HBox slot;
    @FXML
    private Label user;

    private Booking booking;
    private LocalDate dateFor;
    private LocalTime timeFor;
    private Court courtFor;
    private Image addImg, editImg, seeImg;

    private BooleanProperty isEmpty;
    private BooleanProperty isCalendar;
    private BooleanProperty isEditable;
    private BooleanProperty isBookable;
    private BooleanProperty isUser;
    private BooleanProperty selected;

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isEmpty = new SimpleBooleanProperty(true);
        isCalendar = new SimpleBooleanProperty(true);
        isEditable = new SimpleBooleanProperty(false);
        isBookable = new SimpleBooleanProperty(false);
        isUser = new SimpleBooleanProperty(true);
        selected = new SimpleBooleanProperty(false);

        addImg = new Image("/resources/images/add-b.png");
        editImg = new Image("/resources/images/edit-b.png");
        seeImg = new Image("/resources/images/see-b.png");

        dateFor = LocalDate.now();
        timeFor = LocalTime.now().withMinute(0);

        infoSection.visibleProperty().bind(isEmpty.not());
        infoSection.maxWidthProperty().bind(Bindings.when(isEmpty).then(0).otherwise(Region.USE_COMPUTED_SIZE));

        iconSection.maxWidthProperty().bind(Bindings.when(isEmpty).then(Region.USE_COMPUTED_SIZE).otherwise(Region.USE_PREF_SIZE));

        bookingInfo.visibleProperty().bind(isCalendar.not());
        bookingInfo.maxHeightProperty().bind(Bindings.when(isCalendar).then(0).otherwise(Region.USE_COMPUTED_SIZE));

        userInfo.visibleProperty().bind(isCalendar);
        userInfo.maxHeightProperty().bind(Bindings.when(isCalendar).then(Region.USE_COMPUTED_SIZE).otherwise(0));

        selected.addListener((ov, ol, nw) -> slot.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, nw));

        slot.setOnMousePressed((ev) -> {

            if (selected.get() && isUser.get()) {
                if (isBookable.get()) {
                    try {
                        newBook();
                    } catch (IOException ex) {
                        Logger.getLogger(TimeSlotComponentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClubDAOException ex) {
                        Logger.getLogger(TimeSlotComponentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (isEditable.get()) {
                    try {
                        GreenBall.getIntance().loadBookingDetails(booking);
                    } catch (IOException ex) {
                        Logger.getLogger(TimeSlotComponentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        GreenBall.getIntance().loadBookingDetails(booking);
                    } catch (IOException ex) {
                        Logger.getLogger(TimeSlotComponentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private void newBook() throws IOException, ClubDAOException {
        List<Booking> courtBookings = Club.getInstance().getCourtBookings(courtFor.getName(), dateFor);
        Member mem = GreenBall.getIntance().getLoggedUser();
        boolean isBook[] = {false, false, false, false};
        for (Booking bok : courtBookings) {
            if (bok.getMember().equals(mem)) {
                if (bok.getFromTime().plusHours(2).equals(timeFor)) {
                    isBook[0] = true;
                } else if (bok.getFromTime().plusHours(1).equals(timeFor)) {
                    isBook[1] = true;
                } else if (bok.getFromTime().minusHours(1).equals(timeFor)) {
                    isBook[2] = true;
                } else if (bok.getFromTime().minusHours(2).equals(timeFor)) {
                    isBook[3] = true;
                }
            }
        }

        if (isBook[0] && isBook[1] || isBook[1] && isBook[2] || isBook[2] && isBook[3]) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reservar cita");
            alert.setHeaderText("No se pueden reservar 3h seguidas en la misma pista");
            alert.setContentText(null);
            alert.showAndWait();
            return;
        }

        DateTimeFormatter dateF = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("es"));
        DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reservar cita");
        alert.setHeaderText("¿Desea continuar con la reserva?");
        alert.setContentText("Dia " + dateFor.format(dateF) + " a las " + timeFor.format(timeF));

        ButtonType btnConfirm = new ButtonType("Confirmar");
        ButtonType btnCancel = new ButtonType("Cancelar");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(btnConfirm, btnCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnConfirm) {
            Member member = GreenBall.getIntance().getLoggedUser();
            Club.getInstance().registerBooking(LocalDateTime.now(), dateFor, timeFor, member.checkHasCreditInfo(), courtFor, member);

            Object centralController = GreenBall.getIntance().getCentralController();
            if (centralController instanceof CalendarViewController) {
                ((CalendarViewController) centralController).reload();
            }
        }
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

    public void setForCalendar(Booking booking) {
        isCalendar.setValue(true);
        setData(booking);
    }

    public void setForBookings(Booking booking) {
        isCalendar.setValue(false);
        setData(booking);
    }

    public void setForCalendar(LocalDate date, LocalTime time, Court court) {
        isCalendar.setValue(true);
        dateFor = date;
        timeFor = time;
        courtFor = court;

        checkIsBookable();
        if (!isBookable.get() || GreenBall.getIntance().loggedUserProperty.not().get()) {
            slot.setDisable(true);
            action.setOpacity(0);
        }
    }

    private void setData(Booking booking) {
        DateTimeFormatter dateF = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("es"));
        date.setText(booking.getMadeForDay().format(dateF));
        dateFor = booking.getMadeForDay();
        DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm");
        hour.setText(booking.getFromTime().format(timeF));
        timeFor = booking.getFromTime();
        court.setText(booking.getCourt().getName());
        payment.setVisible(!booking.getPaid());
        user.setText(booking.getMember().getNickName());
        this.booking = booking;

        checkIsEditable();

        Member logged = GreenBall.getIntance().getLoggedUser();
        if (logged == null || !booking.getMember().getNickName().equals(logged.getNickName())) {
            isUser.setValue(false);
        }

        if (isUser.get()) {
            if (isEditable.get()) {
                action.setImage(editImg);
            } else {
                action.setImage(seeImg);
            }
        } else {
            iconSection.setVisible(false);
        }

        if (!isUser.get()) {
            slot.setDisable(true);
        }

        isEmpty.setValue(false);
    }

    private void checkIsEditable() {
        isEditable.setValue(LocalDateTime.now().plusDays(1).isBefore(dateFor.atTime(timeFor)));
    }

    private void checkIsBookable() {
        isBookable.setValue(LocalDateTime.now().isBefore(dateFor.atTime(timeFor)));
    }
}

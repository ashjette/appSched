package Controller;

import Model.appointment;
import Model.contact;
import Model.customer;
import Model.user;
import helper.JDBC;
import helper.dataAccessors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * add appointment controller
 * @author Ashley Jette
 */

public class addAppointmentViewController implements Initializable {
    public Label idLabel;
    public Label titleLabel;
    public Label typeLabel;
    public Label locationLabel;
    public Label descriptionLabel;
    public TextField idField;
    public TextField titleField;
    public TextField typeField;
    public TextField locationField;
    public TextArea descriptionField;
    public Label startDateLabel;
    public Label startTimeLabel;
    public Label endDateLabel;
    public Label endTimeLabel;
    public DatePicker startDatePicker;
    public ComboBox startTimeChoiceBox;
    public DatePicker endDatePicker;
    public Label customerIDLabel;
    public Label userIDLabel;
    public Label contactLabel;
    public ComboBox customerIdChoiceBox;
    public ComboBox userIdChoiceBox;
    public ComboBox contactChoiceBox;
    public Button saveButton;
    public Button cancelButton;
    public ComboBox endTimeChoiceBox;
    public TextField privateField;

    /**
     * initialize add appointment screen
     * LAMBDA used to iterate through customers in order to get customer ID
     * @param url
     * @param resourceBundle
     */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /**
         * disables weekends and past days from selection
         */

        startDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0 || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            }
        });
        /**
         *          * disables weekends and past days from selection
         */

        endDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0 || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            }
        });
        endDatePicker.setValue(startDatePicker.getValue());
        /**
         * populate time for combo boxes
         */

        ObservableList<String> appTime = FXCollections.observableArrayList();
        /**
         * appointment start time must be after local time 8:00
         * end time must be after local time 22:00
         */

        LocalTime appStartTime = LocalTime.of(00, 00);
        LocalTime appEndTime = LocalTime.of(23, 45);
        /**
         * buffering by 15 min
         */

        appTime.add(String.valueOf(appStartTime));
        while (appStartTime.isBefore(appEndTime)) {
            appStartTime = appStartTime.plusMinutes(15);
            appTime.add(String.valueOf(appStartTime));
            /**
             * add time options to combo box
             *
             */
        }
        startTimeChoiceBox.setItems(appTime);
        endTimeChoiceBox.setItems(appTime);
        ObservableList<customer> custList = dataAccessors.getCustomers();
        ObservableList<Integer> custId = FXCollections.observableArrayList();
        /**
         * <p> LAMBDA : iterate through customers to get customer ID</p>
         * populate combo box with customer list
         */
        custList.forEach(customer -> custId.add(customer.getCustId()));
        Collections.sort(custId);

        customerIdChoiceBox.setItems(custId);
        ObservableList<contact> contactsList = dataAccessors.getContacts();
        ObservableList<String> contactsName = FXCollections.observableArrayList();
        /**
         * populate contact names
         */
        contactsList.forEach(contact -> contactsName.add(contact.getContactName()));
        Collections.sort(contactsName);
        contactChoiceBox.setItems(contactsName);

        ObservableList<user> usersList = dataAccessors.getUsers();
        ObservableList<Integer> userId = FXCollections.observableArrayList();
        /**
         * populate user ids
         */
        usersList.forEach(user -> userId.add(user.getUserId()));
        Collections.sort(userId);
        userIdChoiceBox.setItems(userId);


    }


    /**
     * save new appointment to database
     * @param actionEvent on save button
     */
    public void onSave(ActionEvent actionEvent) {
        ObservableList<appointment> appList = dataAccessors.getAppointments();
        try {
            int appId = appList.size() + (int) (Math.random() * 100);
            String appTitle = titleField.getText();
            String appDescription = descriptionField.getText();
            String appLocation = locationField.getText();
            String appContact = contactChoiceBox.getValue().toString();
            String apptype = typeField.getText();
            LocalDate appStartDate = startDatePicker.getValue();
            LocalTime appStartTime = LocalTime.parse(startTimeChoiceBox.getValue().toString(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDate appEndDate = endDatePicker.getValue();
            LocalTime appEndTime = LocalTime.parse(endTimeChoiceBox.getValue().toString(), DateTimeFormatter.ofPattern("HH:mm"));
            int custId = Integer.parseInt(customerIdChoiceBox.getValue().toString());
            int userId = Integer.parseInt(userIdChoiceBox.getValue().toString());
            /**
             * time conversions
             */

            LocalDateTime localStartTime = LocalDateTime.of(appStartDate, appStartTime);
            LocalDateTime localEndTime = LocalDateTime.of(appEndDate, appEndTime);
            /**
             * convert to system default time
             */

            ZonedDateTime sysStartTime = ZonedDateTime.of(localStartTime, ZoneId.systemDefault());
            ZonedDateTime sysEndTime = ZonedDateTime.of(localEndTime, ZoneId.systemDefault());

            /**
             * convert to est
             */
            ZonedDateTime sysESTStartTime = sysStartTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime sysESTEndTime = sysEndTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            /**
             * save appointment once it has passed all checks
             */

            boolean bool = false;
            boolean pass = false;
            /**
             * check that no fields are blank
             */

            if (appTitle.isBlank() || appDescription.isBlank() || appLocation.isBlank() ||
                    appContact == null || apptype.isBlank())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please enter a value for all fields.");
                alert.show();
                return;
                /**
                 * appointment can't start and end on different days
                 */
            }
            else if (!appStartDate.isEqual(appEndDate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Appointment must have same start and end date.");
                alert.show();
                return;
                /**
                 * start time must be after end time
                 */
            }
            else if (localStartTime.isAfter(localEndTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Start time must be before end time.");
                alert.show();
                return;
                /**
                 * start and end time must be differen t
                 */
            }
            else if (localStartTime.isEqual(localEndTime) || localEndTime.isEqual(localStartTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Start time must be before end time.");
                alert.show();
                return;
            }
            /**
             * appointment must take place during business hours (8a-10pest)
             */
            else if (sysESTStartTime.toLocalTime().isBefore(LocalTime.of(8, 00)) ||
                    sysESTStartTime.toLocalTime().isAfter(LocalTime.of(22, 00)) ||
                    sysESTEndTime.toLocalTime().isBefore(LocalTime.of(8, 00)) ||
                    sysESTEndTime.toLocalTime().isAfter(LocalTime.of(22, 00))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Appointment must take place during business hours. (8:00-22:00 EST)");
                alert.show();
                return;
            }
            /**
             * first check
             */
            else
            {
                pass = true;
                /**System.out.println("pass=true");**/
            }
            /**
             * checks to avoid overlapping appointments
             */
            for (appointment appointment : dataAccessors.getAppointments()) {
                if ((custId == appointment.getCustId()) && ((localStartTime.isAfter(appointment.getStartTime()) || (localStartTime.isEqual(appointment.getStartTime()))) &&
                        (localStartTime.isBefore(appointment.getEndTime())))) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Appointments cannot overlap.");
                    alert.show();
                    return;
                }
                if ((custId == appointment.getCustId()) && (localEndTime.isAfter(appointment.getStartTime()) &&
                        ((localEndTime.isBefore(appointment.getEndTime()) || (localEndTime.isEqual(appointment.getEndTime())))))) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Appointments cannot overlap.");
                    alert.show();
                    return;
                }

                if ((custId == appointment.getCustId()) && (((localStartTime.isBefore(appointment.getStartTime()) || localStartTime.isEqual(appointment.getStartTime()))) &&
                        ((localEndTime.isAfter(appointment.getEndTime())) || localEndTime.isEqual(appointment.getEndTime())))) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Appointments cannot overlap.");
                    alert.show();
                    return;
                }
            }
            /**
             * second logic check
             */

            bool = true;
            /**System.out.println("bool=true");**/
            /**
             * both conditions passed, no scheduling conflicts
             * save appointment to db
             */

            if (pass && bool) {
                System.out.println("pass and bool = true");

                String sql = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);

                ps.setInt(1, appId);
                ps.setString(2, appTitle);
                ps.setString(3, appDescription);
                ps.setString(4, appLocation);
                ps.setString(5, apptype);
                ps.setTimestamp(6, Timestamp.valueOf(sysStartTime.toLocalDateTime()));
                ps.setTimestamp(7, Timestamp.valueOf(sysEndTime.toLocalDateTime()));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "Ashley Jette");
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(11, "Ashley Jette");
                ps.setInt(12, custId);
                ps.setInt(13, userId);
                ps.setInt(14, Integer.parseInt(privateField.getText()));

                ps.executeUpdate();
                System.out.println("execute update successful");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Appointment has been created.");
                alert.show();

                Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 876, 462);
                stage.setTitle("Main Menu");
                stage.setScene(scene);
                stage.show();
            }
            /**
             * check for blank fields
             */
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
            return;
        }
    }

    /**
     * return to main menu
     * @param actionEvent
     * @throws IOException
     */

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 876, 462);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * drop down for contact sleection
     * @param actionEvent
     */

    public void onContactChoiceBox(ActionEvent actionEvent) {
        ObservableList<Integer> contactId = FXCollections.observableArrayList();
        try {
            String sql = "select Contact_ID, Contact_Name from contacts where Contact_Name = '" + contactChoiceBox.getValue() + "'";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()){
                int curId = searchResult.getInt("Contact_ID");
                contactId.add(curId);
            }
            if (!contactId.isEmpty()){
                privateField.setText(String.valueOf(contactId.get(0)));
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        }
    }
}


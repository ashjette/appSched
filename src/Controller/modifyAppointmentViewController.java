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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * modify appointment controller class
 * @author Ashley Jette
 */


public class modifyAppointmentViewController implements Initializable {
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
    public ChoiceBox startTimeChoiceBox;
    public DatePicker endDatePicker;
    public Label customerIDLabel;
    public Label userIDLabel;
    public Label contactLabel;
    public ChoiceBox customerIdChoiceBox;
    public ChoiceBox userIdChoiceBox;
    public ChoiceBox contactChoiceBox;
    public Button saveButton;
    public Button cancelButton;
    public ChoiceBox endTimeChoiceBox;
    public TextField privateField;
    private appointment appointmentSelection;

    /**
     * initialize selected appointment's information
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        /**
         * get appointment to be modified
         */
        appointmentSelection = mainviewController.modifyAppointment();
        /**
         * populate start date picker with local time info, removing weekends/off hours
         */

        startDatePicker.setDayCellFactory(param -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean bool){
                super.updateItem(date, bool);
                setDisable(bool || date.compareTo(LocalDate.now()) < 0 || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            }
        });
        /**
         * populate end date picker with local time info, removing weekends/off hours
         */

        endDatePicker.setDayCellFactory(param -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean bool){
                super.updateItem(date, bool);
                setDisable(bool || date.compareTo(LocalDate.now()) < 0 || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            }

        });

        ObservableList<String> appTime = FXCollections.observableArrayList();

        LocalTime appStartTime = LocalTime.of(00,00);
        LocalTime appEndTime = LocalTime.of(23,45);

        appTime.add(String.valueOf(appStartTime));
        while (appStartTime.isBefore(appEndTime)){
            appStartTime = appStartTime.plusMinutes(15);
            appTime.add(String.valueOf(appStartTime));
        }
        /**
         * populate with existing info from database
         */
        startTimeChoiceBox.setItems(appTime);
        endTimeChoiceBox.setItems(appTime);

        ObservableList<customer> customerData = dataAccessors.getCustomers();
        ObservableList<Integer> custId = FXCollections.observableArrayList();
        /**
         * adding contact name info
         */

        customerData.forEach(customer -> custId.add(customer.getCustId()));
        Collections.sort(custId);
        customerIdChoiceBox.setItems(custId);

        ObservableList<contact> contactData = dataAccessors.getContacts();
        ObservableList<String> contactId = FXCollections.observableArrayList();

        contactData.forEach(contact -> contactId.add(contact.getContactName()));
        Collections.sort(contactId);
        contactChoiceBox.setItems(contactId);

        ObservableList<user> userData = dataAccessors.getUsers();
        ObservableList<Integer> userId = FXCollections.observableArrayList();
        /**
         * adding user id info
         */

        userData.forEach(user -> userId.add(user.getUserId()));
        Collections.sort(userId);
        userIdChoiceBox.setItems(userId);

        idField.setText(String.valueOf(appointmentSelection.getAppId()));
        titleField.setText(String.valueOf(appointmentSelection.getAppTitle()));
        descriptionField.setText(String.valueOf(appointmentSelection.getAppTitle()));
        locationField.setText(String.valueOf(appointmentSelection.getAppLocation()));
        contactChoiceBox.setValue(String.valueOf(contactId.get(0)));
        typeField.setText(String.valueOf(appointmentSelection.getAppType()));
        startDatePicker.setValue(appointmentSelection.getStartTime().toLocalDate());
        startTimeChoiceBox.setValue(String.valueOf(appointmentSelection.getStartTime().toLocalTime()));
        endDatePicker.setValue(appointmentSelection.getEndTime().toLocalDate());
        endTimeChoiceBox.setValue(String.valueOf(appointmentSelection.getEndTime().toLocalTime()));
        customerIdChoiceBox.setValue(String.valueOf(appointmentSelection.getCustId()));
        userIdChoiceBox.setValue(String.valueOf(appointmentSelection.getUserId()));
        privateField.setText(String.valueOf(appointmentSelection.getContact()));
        /**
         * save updated information to database
         */

    }
    public void onSave(ActionEvent actionEvent) {
        ObservableList<appointment> appointmentData = dataAccessors.getAppointments();
        try {
            int appId = Integer.parseInt(idField.getText());
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

            LocalDateTime localStartTime = LocalDateTime.of(appStartDate, appStartTime);
            LocalDateTime localEndTime = LocalDateTime.of(appEndDate, appEndTime);

            ZonedDateTime sysStartTime = ZonedDateTime.of(localStartTime, ZoneId.systemDefault());
            ZonedDateTime sysEndTime = ZonedDateTime.of(localEndTime, ZoneId.systemDefault());

            ZonedDateTime sysESTStartTime = sysStartTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime sysESTEndTime = sysEndTime.withZoneSameInstant(ZoneId.of("America/New_York"));

            boolean bool = false;
            boolean pass = false;
            /**
             * checks for blank fields
             */

            if (appTitle.isBlank() || appDescription.isBlank() || appLocation.isBlank() ||
                    appContact == null || apptype.isBlank())
            {
                /**
                 * throw errors if field left blank
                 */
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please enter a value for all fields.");
                alert.show();
                return;
            }
            else if (!appStartDate.isEqual(appEndDate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Appointment must have same start and end date.");
                alert.show();
                return;
            }
            else if (localStartTime.isAfter(localEndTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Start time must be before end time.");
                alert.show();
                return;
            }
            else if (localStartTime.isEqual(localEndTime) || localEndTime.isEqual(localStartTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Start time must be before end time.");
                alert.show();
                return;
            }
            /**
             * error for scheduling outside of business hours
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
            else
            {
                pass = true;
                System.out.println("pass=true");
            }
            /**
             * check for overlapping appointments, throwing error if so
             */

            for (appointment appointment : dataAccessors.getAppointments()){
                if ((appId != appointment.getAppId()) && (custId == appointment.getCustId()) && (localStartTime.isAfter(appointment.getStartTime()) || (localStartTime.isEqual(appointment.getStartTime()))) &&
                localStartTime.isBefore(appointment.getEndTime())){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("xAppointments cannot overlap.");
                    alert.show();
                    return;
                }

                if ((appId != appointment.getAppId()) && (custId == appointment.getCustId()) && (localEndTime.isAfter(appointment.getStartTime()) &&
                        localEndTime.isBefore(appointment.getEndTime()) || (localEndTime.isEqual(appointment.getEndTime())))){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("yAppointments cannot overlap.");
                    alert.show();
                    return;
                }
                if ((appId != appointment.getAppId()) && (custId == appointment.getCustId()) && (((localStartTime.isBefore(appointment.getStartTime()) || localStartTime.isEqual(appointment.getStartTime()))) &&
                        ((localEndTime.isAfter(appointment.getEndTime())) || localEndTime.isEqual(appointment.getEndTime())))){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("zAppointments cannot overlap.");
                    alert.show();
                    return;
                }

            }
            bool = true;
            /**
             * if all checks are passed, save updated information to database
             */

            if (pass && bool) {
                System.out.println("pass and bool = true");

                String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
                PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);

                ps.setString(1, appTitle);
                ps.setString(2, appDescription);
                ps.setString(3, appLocation);
                ps.setString(4, apptype);
                ps.setTimestamp(5, Timestamp.valueOf(sysStartTime.toLocalDateTime()));
                ps.setTimestamp(6, Timestamp.valueOf(sysEndTime.toLocalDateTime()));
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(8, "Ashley Jette");
                ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(10, "Ashley Jette");
                ps.setInt(11, custId);
                ps.setInt(12, userId);
                ps.setInt(13, Integer.parseInt(privateField.getText()));
                ps.setInt(14, appId);

                ps.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Appointment has been updated.");
                alert.show();

                Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 876, 462);
                stage.setTitle("Main Menu");
                stage.setScene(scene);
                stage.show();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();

    }

    }

    /**
     * return to main menu upon cancellation
     * @param actionEvent click cancel button
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
     * get contact id in order to save appointment to database (requires contact ID instead of name)
     * @param mouseEvent
     */

    public void onContactChoiceBox(MouseEvent mouseEvent) {
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
            /**
             * error if field is left blank
             */
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        }
    }
}

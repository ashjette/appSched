package Controller;
/**
 * @author Ashley Jette
 */

import Model.appointment;
import Model.customer;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**main menu controller class
 * @author Ashley Jette
 */
public class mainviewController implements Initializable {
    public TableColumn customerIdCol;
    public TableColumn customerNameCol;
    public TableColumn customerAddressCol;
    public TableColumn customerPhoneCol;
    public TableColumn customerStateCol;
    public TableColumn costomerPostalCol;
    public Button addCustomerButton;
    public Button updateCustomerButton;
    public Button deleteCustomerButton;
    public Button reportsButton;
    public Button logoutButton;
    public TableColumn appointmentIdCol;
    public TableColumn appointmentTitleCol;
    public TableColumn appointmentTypeCol;
    public TableColumn appointmentDescriptionCol;
    public TableColumn appointmentLocationCol;
    public TableColumn appointmentStartTimeCol;
    public TableColumn appointmentEndTimeCol;
    public TableColumn appointmentContactCol;
    public TableColumn appointmentCustomerIdCol;
    public TableColumn appointmentUserIdCol;
    public RadioButton currentWeekRadioButton;
    public ToggleGroup timeframe;
    public RadioButton currentMonthRadioButton;
    public RadioButton allAppsRadioButton;
    public Button addAppointmentButton;
    public Button updateAppointmentButton;
    public Button deleteAppointmentButton;
    public TableView appointmentsTable;
    public TableView customersTable;
    private static appointment appointmentSelection;
    private static customer customerSelection;
    public TableColumn customerCountryCol;

    /**
     * used to populate customer information for modification
     * @return customer data
     */

    public static customer modifyCustomer(){
       return customerSelection;
   }

    /**
     * populate appointment information for modification
     * @return appointment data
     */
    public static appointment modifyAppointment() { return appointmentSelection;}

    /**
     * initialize data for customer and appointment tables
     * LAMBDA - first lambda is used in onCurrentMonth to iterate through appointments in order to match dates with current month
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<customer> customerData = dataAccessors.getCustomers();

        customersTable.setItems(customerData);

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("custId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("custName"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("custPhone"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("custAddress"));
        costomerPostalCol.setCellValueFactory(new PropertyValueFactory<>("custPostal"));
        customerStateCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("custCountry"));

        customersTable.getSortOrder().addAll(customerIdCol);


        ObservableList<appointment> appointmentData = dataAccessors.getAppointments();

        appointmentsTable.setItems(appointmentData);

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appId"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("appTitle"));
        appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("appDescription"));
        appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("appLocation"));
        appointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("appType"));
        appointmentStartTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEndTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("custId"));
        appointmentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        customersTable.getSortOrder().addAll(customerIdCol);

    }

    /**
     * launch add customer screen
     * @param actionEvent click add customer button
     * @throws IOException
     */


    public void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/addCustomerView.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,551,377);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * launch update customer menu with selected customers data
     * @param actionEvent click update customer button
     * @throws IOException
     */

    public void onUpdateCustomer(ActionEvent actionEvent) throws IOException {
        customerSelection = (customer) customersTable.getSelectionModel().getSelectedItem();
        /**
         * throw alert if no customer has been seelcted
         */

        if (customerSelection == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please select a customer to modify.");
            alert.show();
        }
        /**
         * loads modify customer view, populated with current customer info
         */
        else {
            Parent root = FXMLLoader.load(getClass().getResource("/View/modifyCustomerView.fxml"));
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root,551,377);
            stage.setTitle("Update Customer");
            stage.setScene(scene);
            stage.show();
        }


    }

    /**
     * delete selected customer
     * @param actionEvent click delete customer button
     * @throws IOException
     * @throws SQLException
     */

    public void onDeleteCustomer(ActionEvent actionEvent) throws IOException, SQLException{
        boolean hasBeenDeleted = false;
        customerSelection = (customer) customersTable.getSelectionModel().getSelectedItem();
        /**
         * alert if no customer has been selected for deletion
         */

        if (customerSelection == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You must select a customer.");
            alert.show();
            /**
             * confirmation message for deletion
             */


        }
        else {
            int customerID = customerSelection.getCustId();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Notice");
            alert.setHeaderText(null);
            alert.setContentText("Delete customer and associated appointments?");
            Optional<ButtonType> result = alert.showAndWait();
            /**
             * if deletion is confirmed, remove related customer from database
             */

            if (result.get() == ButtonType.OK){
                String databaseAppointment = "DELETE FROM appointments WHERE Customer_ID = ?";
                PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(databaseAppointment);
                preparedStatement.setInt(1, customerID);
                preparedStatement.executeUpdate();

                String sql = "DELETE FROM customers WHERE Customer_ID = ?";
                PreparedStatement deleteCust = JDBC.getConnection().prepareStatement(sql);
                deleteCust.setInt(1, customerID);
                deleteCust.executeUpdate();

                hasBeenDeleted = true;
                /**
                 * once customer has been deleted provide confirmation with customer id
                 */

                if (hasBeenDeleted){
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Notice");
                    alert.setHeaderText(null);
                    alert.setContentText("Customer (ID:" + customerID + ") and all associated appointments have been deleted");
                    alert.show();
                    /**
                     * return to main menu
                     */

                    Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 876, 462);
                    stage.setTitle("Main Menu");
                    stage.setScene(scene);
                    stage.show();
                }
            }
        }
    }

    /**
     * load report menu
     * @param actionEvent click reports button
     * @throws IOException
     */

    public void onReports(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/reportsView.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,808,464);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * logout of user session
     * @param actionEvent click logout button
     * @throws IOException
     */

    public void onLogout(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/loginView.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,447,505);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * filter appointment tableview by current week
     * @param actionEvent select current week radio button
     */

    public void onCurrentWeek(ActionEvent actionEvent) {
        allAppsRadioButton.setSelected(false);
        currentWeekRadioButton.setSelected(true);
        currentMonthRadioButton.setSelected(false);

        try {
            ObservableList<appointment> appointmentData = dataAccessors.getAppointments();
            ObservableList<appointment> sortByWeek = FXCollections.observableArrayList();

            if (appointmentData != null){
                appointmentData.forEach(appointment -> {
                    if (appointment.getEndTime().isAfter(LocalDateTime.now().minusWeeks(1)) && appointment.getEndTime().isBefore(LocalDateTime.now().plusWeeks(1))){
                        sortByWeek.add(appointment);
                    }
                    appointmentsTable.setItems(sortByWeek);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p> - LAMBDA Iterate through appointments to match dates with current month </p>
     * filter appointment table view by current month
     * @param actionEvent select current month radio button
     */

    public void onCurrentMonth(ActionEvent actionEvent) {
        allAppsRadioButton.setSelected(false);
        currentWeekRadioButton.setSelected(false);
        currentMonthRadioButton.setSelected(true);

        try {
            ObservableList<appointment> appointmentData = dataAccessors.getAppointments();
            ObservableList<appointment> sortByMonth = FXCollections.observableArrayList();

            if (appointmentData != null){
                appointmentData.forEach(appointment -> {
                    if (appointment.getEndTime().isAfter(LocalDateTime.now().minusMonths(1)) && appointment.getEndTime().isBefore(LocalDateTime.now().plusMonths(1))){
                        sortByMonth.add(appointment);
                    }
                    appointmentsTable.setItems(sortByMonth);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * display all appointments in tableview
     * @param actionEvent click all appointments radio button
     */

    public void onAllAppointments(ActionEvent actionEvent) {
        allAppsRadioButton.setSelected(true);
        currentWeekRadioButton.setSelected(false);
        currentMonthRadioButton.setSelected(false);

        try{
            ObservableList<appointment> appointmentData = dataAccessors.getAppointments();

            if (appointmentData != null){
                appointmentsTable.setItems(appointmentData);
                appointmentsTable.getSortOrder().addAll(appointmentIdCol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * launch add appointment menu
     * @param actionEvent click add appointment button
     * @throws IOException
     */

    public void onAddAppointment(ActionEvent actionEvent) throws IOException {
       Parent root = FXMLLoader.load(getClass().getResource("/View/addAppointmentView.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,600,543);
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * launch update appointment menu, populating selected appointments' data
     * @param actionEvent click update appointment button
     * @throws IOException
     */

    public void onUpdateAppointment(ActionEvent actionEvent) throws IOException {
       appointmentSelection = (appointment) appointmentsTable.getSelectionModel().getSelectedItem();
        /**
         * throw error if no appointment has been selected
         */

       if(appointmentSelection == null){
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Error");
           alert.setContentText("Please select an appointment to modify.");
           alert.show();
           /**
            * if appointment has been selected, launch modify appointment menu
            */
       }
       else{
           Parent root = FXMLLoader.load(getClass().getResource("/View/modifyAppointmentView.fxml"));
           Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
           Scene scene = new Scene(root, 600, 543);
           stage.setTitle("Modify Appointment");
           stage.setScene(scene);
           stage.show();
       }
    }

    /**
     * delete selected appointment
     * @param actionEvent click delete appointment button
     */

    public void onDeleteAppointment(ActionEvent actionEvent) {
        boolean isDeleted = false;
        try{
            appointmentSelection = (appointment) appointmentsTable.getSelectionModel().getSelectedItem();
            int appId = appointmentSelection.getAppId();
            String appType = appointmentSelection.getAppType();
            /**
             * throw error if no appointment has been selected
             */

            if (appointmentSelection == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("You must select an appointment.");
                alert.show();
                /**
                 * if appointment has been selected, provide confirmation message prior to deletion
                 */
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Notice");
                alert.setHeaderText("Confirm Deletion");
                alert.setContentText("Are you sure you want to delete this appointment?");
                Optional<ButtonType> result = alert.showAndWait();
                /**
                 * upon confirmation, remove selected appointment from database
                 */

                if (result.get() == ButtonType.OK){
                    String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
                    PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
                    ps.setInt(1, appId);
                    ps.executeUpdate();
                    isDeleted = true;
                    /**
                     * provide deletion confirmation message with appointment id and typoe
                     */

                    if (isDeleted){
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Appointment Deleted");
                        alert.setHeaderText(null);
                        alert.setContentText("Appointment ID: " + appId + "\nType: " + appType + "\nHas been deleted.");
                        alert.show();
                        /**
                         * return to main menu
                         */

                        Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root, 876, 462);
                        stage.setTitle("Main Menu");
                        stage.setScene(scene);
                        stage.show();


                    }
                    /**
                     * returns to main menu if user cancels deletion
                      */
                }
                /**
                 * load main menu
                 */
                else{
                    Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 876, 462);
                    stage.setTitle("Main Menu");
                    stage.setScene(scene);
                    stage.show();
                }
            }
            /**
             * error if no appointment has been selected
             */
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You must select an appointment to delete.");
            alert.show();
        }
    }

}

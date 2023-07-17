package Controller;

import Model.*;
import Model.reportType;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * report view controller class
 * @author Ashley Jette
 */


public class reportsViewController implements Initializable {
    public TableColumn appontmentIdCol;
    public TableColumn appointmentTitleCol;
    public TableColumn appointmentTypeCol;
    public TableColumn appointmentDescriptionCol;
    public TableColumn appointmentLocationCol;
    public TableColumn startTimeCol;
    public TableColumn endTimeCol;
    public TableColumn customerIdCol;
    public Label contactLabel;
    public ComboBox contactChoiceBox;
    public Button logoutButton;
    public Button backButton;
    public TableColumn appointmentByMonthCol;
    public TableColumn totalMonthAppointmentsCol;
    public TableColumn countryCol;
    public TableColumn totalbyCountryCol;
    public TableColumn appMonthCol1;
    public TableColumn appTypeCol1;
    public TableView t2;
    public TableView t4;
    public TableView t3;
    public TableView t1;
    public TableColumn t2AppType;
    public TableColumn t2AppTotal;

    /**
     * initialize data for report tables
     * Reports are as follows:
     * Show contact information
     * Tables to sort appointments by month, type, and country
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        ObservableList<contact> contactData = dataAccessors.getContacts();
        ObservableList<String> contactName = FXCollections.observableArrayList();
        /**
         * populate contact names
         */

        contactData.forEach(contact -> contactName.add(contact.getContactName()));
        Collections.sort(contactName);
        contactChoiceBox.setItems(contactName);
        /**
         * appointments by month
         */


       ObservableList<reportMonth> appsByMonth = dataAccessors.getAppointmentMonth();
       t3.setItems(appsByMonth);

       appointmentByMonthCol.setCellValueFactory(new PropertyValueFactory<>("appMonth"));
       totalMonthAppointmentsCol.setCellValueFactory(new PropertyValueFactory<>("appTotal"));
        /**
         * appointments by country
         */

       ObservableList<countries> appsByCountry = dataAccessors.getAppointmentCountry();
       t4.setItems(appsByCountry);
       countryCol.setCellValueFactory(new PropertyValueFactory<>("countryName"));
       totalbyCountryCol.setCellValueFactory(new PropertyValueFactory<>("countryId"));

        /**
         * appointments by type
         */

        ObservableList<reportType> appsByType = dataAccessors.getAppointmentType();
       t2.setItems(appsByType);
       t2AppType.setCellValueFactory(new PropertyValueFactory<>("appType"));
       t2AppTotal.setCellValueFactory(new PropertyValueFactory<>("appTotal"));


        
    }

    /**
     * return to main menu
     * @param actionEvent click back button
     * @throws IOException
     */

    public void onBack(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 876, 462);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * logout current user and return to login screen
     * @param actionEvent click logout button
     * @throws IOException
     */

    public void onLogout(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/View/loginView.fxml"));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root,447,406);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * populate table with data for corresponding contact
     * @param actionEvent make contact selection
     */

    public void onContactChoiceBox(ActionEvent actionEvent) {
        ObservableList<appointment> appointmentData = FXCollections.observableArrayList();
        try {
            {
                String sql = "select Appointment_ID,Title,Description,Location, a.Contact_ID,Type,Start,End,Customer_ID,User_ID, c.Contact_Name from appointments a\n" +
                        "inner join contacts c on a.Contact_ID = c.Contact_ID WHERE c.Contact_Name = '" + contactChoiceBox.getValue() + "'";
                PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
                ResultSet searchResult = preparedStatement.executeQuery();

                while (searchResult.next()){
                    int appId = searchResult.getInt("Appointment_ID");
                    String appTitle = searchResult.getString("Title");
                    String appDescription = searchResult.getString("Description");
                    String appLocation = searchResult.getString("Location");
                    int contactId = searchResult.getInt("Contact_ID");
                    String appType = searchResult.getString("Type");
                    LocalDateTime appStart = searchResult.getTimestamp("Start").toLocalDateTime();
                    LocalDateTime appEnd = searchResult.getTimestamp("End").toLocalDateTime();
                    int custID = searchResult.getInt("Customer_ID");
                    int userId = searchResult.getInt("User_ID");

                    appointment appointment = new appointment(appId, appTitle, appDescription, appLocation, contactId, appType, appStart, appEnd, custID, userId);
                    appointmentData.add(appointment);
                }
                t1.setItems(appointmentData);

                appontmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appId"));
                appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("appTitle"));
                appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("appDescription"));
                appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("appType"));
                appointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("appLocation"));
                startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
                endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
                customerIdCol.setCellValueFactory(new PropertyValueFactory<>("custId"));
                t1.getSortOrder().addAll(appontmentIdCol);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package Controller;

import Model.countries;
import Model.customer;
import Model.divisions;
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

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * modify customer view controller
 * @author Ashley Jette
 */


public class modifyCustomerViewController implements Initializable {


    public Label idLabel;
    public Label nameLabel;
    public Label phoneLabel;
    public TextField idField;
    public TextField nameField;
    public TextField phoneField;
    public Label addressLabel;
    public Label stateLabel;
    public Label countryLabel;
    public Label postalLabel;
    public TextField addressField;
    public ComboBox stateChoiceBox;
    public ComboBox countryChoiceBox;
    public TextField postalField;
    public Button saveButton;
    public Button cancelButton;
    public TextField privateField;
    private customer customerSelection;

    /**
     * initialize information for selected customer data
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        customerSelection = mainviewController.modifyCustomer();

        ObservableList<countries> countryData = dataAccessors.getCountries();
        ObservableList<String> countryName = FXCollections.observableArrayList();

        countryData.forEach(countries -> countryName.add(countries.getCountryName()));
        Collections.sort(countryName);
        countryChoiceBox.setItems(countryName);

        ObservableList<divisions> divisionData = dataAccessors.getDivisions();
        ObservableList<String> divisionName = FXCollections.observableArrayList();

        divisionData.forEach(divisions -> divisionName.add(divisions.getDivisionName()));
        Collections.sort(divisionName);
        stateChoiceBox.setItems(divisionName);

        idField.setText(String.valueOf(customerSelection.getCustId()));
        nameField.setText(String.valueOf(customerSelection.getCustName()));
        phoneField.setText(String.valueOf(customerSelection.getCustPhone()));
        addressField.setText(String.valueOf(customerSelection.getCustAddress()));
        countryChoiceBox.setValue(String.valueOf(customerSelection.getCustCountry()));
        stateChoiceBox.setValue(String.valueOf(customerSelection.getDivisionName()));
        postalField.setText(String.valueOf(customerSelection.getCustPostal()));
        privateField.setText(String.valueOf(dataAccessors.getDivId().get(0)));
    }

    /**
     * save updated customer information to database
     * @param actionEvent click save button
     * @throws IOException
     */

    public void onSave(ActionEvent actionEvent) throws IOException {
        int id = Integer.parseInt(idField.getText());
        String custName = nameField.getText().toString();
        String custAddress = addressField.getText().toString();
        String custPostal = postalField.getText().toString();
        String custPhone = phoneField.getText().toString();
        String custCountry = countryChoiceBox.getValue().toString();
        String custProvinces = stateChoiceBox.getValue().toString();
        String divID = privateField.getText();
        /**
         * check for blank fields
         */

        try {
            if (custName.isBlank() || custAddress.isBlank() || custPostal.isBlank() || custPhone.isBlank()
                    || custCountry.isBlank() || custProvinces.isBlank() || divID.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please enter a value for all fields.");
                alert.show();
                /**
                 * if checks are passed update customer information in database
                 */

            } else {
                String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
                PreparedStatement modCustQuery = JDBC.getConnection().prepareStatement(query);
                modCustQuery.setString(1, custName);
                modCustQuery.setString(2, custAddress);
                modCustQuery.setString(3, custPostal);
                modCustQuery.setString(4, custPhone);
                modCustQuery.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                modCustQuery.setString(6, "Ashley Jette");
                modCustQuery.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                modCustQuery.setString(8, "Ashley Jette");
                modCustQuery.setInt(9, Integer.parseInt(divID));
                modCustQuery.setInt(10, id);

                modCustQuery.executeUpdate();
                /**
                 * confirmation message once information has been updated
                 */

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Notice");
                alert.setHeaderText(null);
                alert.setContentText("Customer information has been updated.");
                alert.showAndWait();
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
             * throw error for blank field
             */

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
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
     * use country selection to populate division data
     * @param actionEvent click country box
     */


    public void onCountryChoiceBox(ActionEvent actionEvent) {
        ObservableList<divisions> divisionData = FXCollections.observableArrayList();
        try {
            String sql = "select f.Division, c.Country from first_level_divisions f inner join countries c \n" +
                    "on f.Country_ID = c.Country_ID where Country = '" + countryChoiceBox.getValue() + "'";

            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = preparedStatement.executeQuery();

            while (searchResult.next()){
                String divisionName = searchResult.getString("Division");
                String divisionCountry = searchResult.getString("Country");


                divisions divList = new divisions(divisionCountry, divisionName);
                divisionData.add(divList);
            }



            ObservableList<String> divisionName = FXCollections.observableArrayList();

            divisionData.forEach(divisions -> divisionName.add(divisions.getDivisionName()));
            Collections.sort(divisionName);
            stateChoiceBox.setItems(divisionName);
            /**
             * throw error for blank fields
             */


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        }
    }

    /**
     *      * use division information to get division id for private field in order to save info to database
     * @param actionEvent click division box
     */

    public void onStateChoiceBox(ActionEvent actionEvent) {
        ObservableList<Integer> divId = FXCollections.observableArrayList();
        try {
            String sql = "select Division_ID, Division from first_level_divisions where Division = '" + stateChoiceBox.getValue() + "'";

            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = preparedStatement.executeQuery();

            while (searchResult.next()){
                int divvId = searchResult.getInt("Division_ID");
                divId.add(divvId);
            }
            if (!divId.isEmpty()){
                privateField.setText(String.valueOf(divId.get(0)));
            }
            /**
             * throw alert for blank fields
             */
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fieldxs.");
            alert.show();
        }
    }
}

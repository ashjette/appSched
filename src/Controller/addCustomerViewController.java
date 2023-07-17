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
import javafx.scene.input.MouseEvent;
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
 * add customer class
 * @author Ashley Jette
 */


public class addCustomerViewController implements Initializable {
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

    /**
     * initialize add customer menu
     * laods data to country selection combo box
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        countryChoiceBox.setItems(dataAccessors.getCountries());
        ObservableList<countries> countryData = dataAccessors.getCountries();
        ObservableList<String> countryName = FXCollections.observableArrayList();

        countryData.forEach(countries -> countryName.add(countries.getCountryName()));
        Collections.sort(countryName);
        countryChoiceBox.setItems(countryName);

    }

    /**
     * save new customer to database
     * @param actionEvent click save
     * @throws IOException
     * @throws SQLException
     */

    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        ObservableList<customer> customerData = dataAccessors.getCustomers();
        try {
            int custId = customerData.size() + (int) (Math.random()*100);
            String custName = nameField.getText().toString();
            String custAddress = addressField.getText().toString();
            String custPostal = postalField.getText().toString();
            String custPhone = phoneField.getText().toString();
            String custCountry = countryChoiceBox.getValue().toString();
            String custDivision = stateChoiceBox.getValue().toString();
            String divId = privateField.getText();
            /**
             * checks for blank fields
             */

            if (custName.isBlank() || custAddress.isBlank() || custPostal.isBlank() || custPhone.isBlank() || custCountry == null || custDivision == null || divId.isBlank()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please enter a value for all fields.");
                alert.show();
            }
            /**
             * if no fields are left blank, insert new customer in db
             */
            else {
                String sql = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);

                preparedStatement.setInt(1, custId);
                preparedStatement.setString(2, custName);
                preparedStatement.setString(3, custAddress);
                preparedStatement.setString(4, custPostal);
                preparedStatement.setString(5, custPhone);
                preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(7, "Ashley Jette");
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(9, "Ashley Jette");
                preparedStatement.setInt(10, Integer.parseInt(divId));

                preparedStatement.executeUpdate();
                /**
                 * confirmation message
                 */

                Alert alert = new Alert(Alert.AlertType.INFORMATION);alert.setTitle("Confirmation");alert.setHeaderText(null);alert.setContentText("Customer has been added.");alert.show();
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
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        }
    }

    /**
     * return to main menu
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
     * use division information to get division id for private field in order to save info to database
     * @param actionEvent click division combo box
     */


    public void onStateChoiceBox(ActionEvent actionEvent) {
        ObservableList<Integer> divisionData = FXCollections.observableArrayList();
        try {
            String sql = "select Division_ID, Division from first_level_divisions where Division = '" + stateChoiceBox.getValue() + "'";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = preparedStatement.executeQuery();

            while (searchResult.next()){
                int divId = searchResult.getInt("Division_ID");
                divisionData.add(divId);

            }
            if (!divisionData.isEmpty()){
                privateField.setText(String.valueOf(divisionData.get(0)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        }
    }

    /**
     * populate data for country combo box
     * @param actionEvent on click country choice box
     */

    public void onCountryChoiceBox(ActionEvent actionEvent) {
        ObservableList<divisions> divisionData = FXCollections.observableArrayList();
        try {
            String sql = "select f.Division, c.Country from first_level_divisions f inner join countries c \n" +
                    "on f.Country_ID = c.Country_ID where Country = '" + countryChoiceBox.getValue() + "'";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = preparedStatement.executeQuery();

            while (searchResult.next()){
                String countryName = searchResult.getString("Country");
                String divisionName = searchResult.getString("Division");

                divisions divList = new divisions(countryName, divisionName);
                divisionData.add(divList);
            }
            ObservableList<String> divisionName = FXCollections.observableArrayList();

            divisionData.forEach(divisions -> divisionName.add(divisions.getDivisionName()));
            Collections.sort(divisionName);
            stateChoiceBox.setItems(divisionName);


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value for all fields.");
            alert.show();
        }
    }
}

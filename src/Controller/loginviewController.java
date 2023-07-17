package Controller;
/**
 * @author Ashley jette
 */

import Model.appointment;
import helper.JDBC;
import helper.dataAccessors;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Login view controller class
 *
 */

public class loginviewController implements Initializable {
    public TextField usernameField;
    public TextField passwordField;
    public Label timeZoneText;
    public Label timeZoneLabel;
    public Button loginButton;
    public Button exitButton;
    public Label passwordLabel;
    public Label usernameLabel;

    /**
     * Initialize login controller
     * @param url
     * @param resourceBundle
     */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Login screen launched");
        timeZoneText.setText(String.valueOf(ZoneId.systemDefault()));

        ResourceBundle languageBundle = resourceBundle.getBundle("helper/language", Locale.getDefault());
        /**
         * change text based on sys default (english/french)
         */

        usernameLabel.setText(languageBundle.getString("username"));
        passwordLabel.setText(languageBundle.getString("password"));
        loginButton.setText(languageBundle.getString("login"));
        exitButton.setText(languageBundle.getString("exit"));
        timeZoneLabel.setText(languageBundle.getString("time"));
    }

    /**
     * loads main menu upon successful login
     * @param actionEvent click login button
     * @throws IOException
     * @throws SQLException
     */

    public void onLoginButton(ActionEvent actionEvent) throws IOException, SQLException {
        FileWriter fileWrite = new FileWriter("login_activity.txt", true);
        PrintWriter printOutput = new PrintWriter(fileWrite);

        ResourceBundle resourceBundle = null;
        ResourceBundle rb = resourceBundle.getBundle("helper/language", Locale.getDefault());
        /**
         * checks for correct username and blank fields, upon passing checks checks for upcoming appointments and provides notice
         */

        if (usernameField.getText().isBlank() == false && passwordField.getText().isBlank() == false){
            JDBC.getConnection();
            /**
             * compare data in fields to users in database
             */

            String verification = "select count(1) from users where User_Name ='" + usernameField.getText() + "' and Password = '" + passwordField.getText() +"'";

            try {
                Statement validateStatement = JDBC.connection.createStatement();
                ResultSet searchResult = validateStatement.executeQuery(verification);

                while (searchResult.next()){
                    /**
                     * 1 = successful check
                     */

                    if (searchResult.getInt(1) == 1){
                        /**
                         * log attempt
                         */
                        printOutput.println("User " + usernameField.getText() + " logged in at " + Timestamp.valueOf(LocalDateTime.now()));
                        /**
                         * 15 min reminder
                         */

                        try{
                            ObservableList<appointment> appointmentData = dataAccessors.getAppointments();

                            boolean hasScheduledAppointment = false;
                            int localAppId = 0;
                            LocalDateTime localAppTime = null;
                            /**
                             * notice to be shown if user has appointment within 15 minutes from login timestamp
                             */
                            if (appointmentData != null){
                                for (appointment appointment: dataAccessors.getAppointments()){
                                    if (appointment.getStartTime().isAfter(LocalDateTime.now().minusMinutes(1)) && appointment.getStartTime().isBefore(LocalDateTime.now().plusMinutes(15))){
                                        localAppId = appointment.getAppId();
                                        localAppTime = appointment.getStartTime();
                                        hasScheduledAppointment = true;

                                    }
                                }
                            }
                            /**
                             * upcoming appointment reminder
                             */
                            if (hasScheduledAppointment){
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Reminder: upcoming appointment in 15 minutes.\n" + "Appointment ID:" + localAppId + "\n" + "Start time:" + localAppTime);
                                Optional<ButtonType> confirmation = alert.showAndWait();
                            }
                            /**
                             * no upcoming appointments
                             */
                            else {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have no appointments in the next 15 minutes.");
                                Optional<ButtonType> confirmation = alert.showAndWait();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            /**
                             * load main screen upon successful login
                             */
                        }
                        Parent root = FXMLLoader.load(getClass().getResource("/View/mainview.fxml"));
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root, 876, 462);
                        stage.setTitle("Main Menu");
                        stage.setScene(scene);
                        stage.show();
                    }
                    else{
                        /**
                         * maintaining log file for unsuccessful login attempts
                         */
                        printOutput.println("User '" + usernameField.getText() + "' failed login at " + Timestamp.valueOf(LocalDateTime.now()));
                        /**
                         * alert for bad credentials
                         */

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(rb.getString("errorTitle"));
                        alert.setContentText(rb.getString("badLoginBlurb"));
                        alert.show();
                }

            }
                printOutput.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        /**
         * alert for blank fields
         */
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorTitle"));
            alert.setContentText(rb.getString("noLoginInfo"));
            alert.show();
        }
    }

    /**
     * close application
     * @param actionEvent click exit button
     */

    public void onExitButton(ActionEvent actionEvent) {
        JDBC.closeConnection();
        System.exit(0);
    }
}

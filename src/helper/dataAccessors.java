package helper;

import Controller.mainviewController;
import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * data accessor class
 * @author Ashley Jette
 */
public class dataAccessors {
    /**
     * get customer data from database
     * @return customerdata
     */

    public static ObservableList<customer> getCustomers(){

        ObservableList<customer> customerData = FXCollections.observableArrayList();

        try{
            String sql = "select c.Customer_ID, c.Customer_Name, c.Phone, c.Address, e.Country, d.Division, c.Postal_Code from customers c \n" +
                    "inner join first_level_divisions d\n" +
                    "on c.Division_ID = d.Division_ID \n" +
                    "inner join countries e\n" +
                    "on d.Country_ID = e.Country_ID";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while(searchResult.next()){
                int custId = searchResult.getInt("Customer_ID");
                String custName = searchResult.getString("Customer_Name");
                String custPhone = searchResult.getString("Phone");
                String custAddress = searchResult.getString("Address");
                String custCountry = searchResult.getString("Country");
                String divisionName = searchResult.getString("Division");
                String custPostal = searchResult.getString("Postal_Code");

                customer customer = new customer(custId, custName, custAddress, custPhone, custPostal, divisionName, custCountry);
                customerData.add(customer);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return customerData;
    }

    /**
     * get all country data from database
     * @return countryData
     */

    public static ObservableList<countries> getCountries() {
        ObservableList<countries> countryData = FXCollections.observableArrayList();

        try{
            String sql = "select Country_ID, Country from countries";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()){
                int countryId = searchResult.getInt("Country_ID");
                String countryName = searchResult.getString("Country");



                countries newList = new countries (countryId, countryName);
                countryData.add(newList);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return countryData;
    }

    /**
     * get all division data from database
     * @return DivisionData
     */

    public static ObservableList<divisions> getDivisions(){

        ObservableList<divisions> divisionData = FXCollections.observableArrayList();

        customer customerSelection;
        customerSelection = mainviewController.modifyCustomer();

        try{
            String sql = "select f.Division, c.Country from first_level_divisions f inner join countries c \n" +
                        "on f.Country_ID = c.Country_ID where Country = '" + customerSelection.getCustCountry() + "'";

            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = preparedStatement.executeQuery();

            while (searchResult.next()){
                String countryName = searchResult.getString("Country");
                String divisionName = searchResult.getString("Division");

                divisions divList = new divisions(countryName, divisionName);
                divisionData.add(divList);
            }
        }
        catch (SQLException e) {
                e.printStackTrace();

        }
        return divisionData;
    }

    /**
     * get division id
     * @return divId
     */


    public static ObservableList<Integer> getDivId(){

        ObservableList<Integer> divisionID = FXCollections.observableArrayList();

        customer customerSelection;
        customerSelection = mainviewController.modifyCustomer();

        try {
            String sql = "select Division_ID, Division from first_level_divisions where Division = '" + customerSelection.getDivisionName() + "'";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()) {
                int divId = searchResult.getInt("Division_ID");
                divisionID.add(divId);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return divisionID;
        }

    /**
     * get all appointment data from database
     * @return appointmentData
     */


    public static ObservableList<appointment> getAppointments(){
        ObservableList<appointment> appointmentData = FXCollections.observableArrayList();

        try{
            String sql = "select Appointment_ID,Title,Description,Location,Contact_ID,Type,Start,End,Customer_ID,User_ID from appointments";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()){
                int appId = searchResult.getInt("Appointment_ID");
                String appTitle = searchResult.getString("Title");
                String appDescription = searchResult.getString("Description");
                String appLocation = searchResult.getString("Location");
                int contact = searchResult.getInt("Contact_ID");
                String appType = searchResult.getString("Type");
                LocalDateTime start = searchResult.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = searchResult.getTimestamp("End").toLocalDateTime();
                int custId = searchResult.getInt("Customer_ID");
                int userId = searchResult.getInt("User_ID");

                appointment appointment = new appointment(appId, appTitle, appDescription, appLocation, contact, appType, start, end, custId, userId);
                appointmentData.add(appointment);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return appointmentData;
    }

    /**
     * get all contact data from database
     * @return contactData
     */

    public static ObservableList<contact> getContacts(){

        ObservableList<contact> contactData = FXCollections.observableArrayList();

        try {
            String sql = "select * from contacts;";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()){
                int contactId = searchResult.getInt("Contact_ID");
                String contactName = searchResult.getString("Contact_Name");
                String contactEmail = searchResult.getString("Email");

                contact contact = new contact(contactId, contactName, contactEmail);
                contactData.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contactData;
        }

    /**
     * get all user data from database
     * @return userData
     */

    public static ObservableList<user> getUsers(){

        ObservableList<user> userData = FXCollections.observableArrayList();

        try {
            String sql = "select User_ID, User_Name from users;";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResults = statement.executeQuery();

            while (searchResults.next()){
                int userId = searchResults.getInt("User_ID");
                String userName = searchResults.getString("User_Name");

                user user = new user(userId, userName);
                userData.add(user);
            }
        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return userData;
        }

    /**
     * get appointment type
     * @return appointmentType
     */

    public static ObservableList<reportType> getAppointmentType(){

        ObservableList<reportType> appointmentTypeList = FXCollections.observableArrayList();

        try {
            String sql = "select Type as 'Appointment_Type' , count(*) as 'Total_Appointments' from appointments group by Type;";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResults = statement.executeQuery();

            while (searchResults.next()){
                String typeType = searchResults.getString("Appointment_Type");
                int typeTotal = searchResults.getInt("Total_Appointments");

                reportType appType = new reportType(typeType, typeTotal);
                appointmentTypeList.add(appType);
            }
        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return appointmentTypeList;
        }

    /**
     * get appointment month
     * @return appointmentMonthList
     */

    public static ObservableList<reportMonth> getAppointmentMonth(){

        ObservableList<reportMonth> appointmentMonthList = FXCollections.observableArrayList();

        try {
            String sql = "select MONTHNAME(Start) as bymonth , count(*) as total from appointments group by MONTHNAME(Start);";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()){
                String appMonth = searchResult.getString("bymonth");
                int appTotal = searchResult.getInt("total");

                reportMonth appointments = new reportMonth(appMonth, appTotal);
                appointmentMonthList.add(appointments);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  appointmentMonthList;
        }

        public static ObservableList<countries> getAppointmentCountry(){

        ObservableList<countries> appointmentCountry = FXCollections.observableArrayList();

        try {
            String sql = "select cl.Country as Country, count(*) as total from appointments a inner join customers c on a.Customer_ID = c.Customer_ID\n" +
                    "inner join first_level_divisions d on c.Division_ID = d.Division_ID left join countries cl on d.Country_ID = cl.Country_ID group by cl.Country;";

            PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
            ResultSet searchResult = statement.executeQuery();

            while (searchResult.next()){
                String countryName = searchResult.getString("Country");
                int countryTotal = searchResult.getInt("total");

                countries appointments = new countries(countryTotal, countryName);
                appointmentCountry.add(appointments);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointmentCountry;
        }
}

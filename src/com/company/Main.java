package com.company;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Ashley Jette
 */

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("/view/loginview.fxml"));
        stage.setTitle("Appointment Manager");
        stage.setScene(new Scene(root, 447, 406));
        stage.show();
    }

    public static void main(String[] args){

        if(Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("fr")){
            Locale.getDefault();
        }

        else {
            Locale.setDefault(new Locale("en"));
        }

        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}

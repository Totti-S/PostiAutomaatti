/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ht;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author p3132
 */
public class HT extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Pane p = (Pane) FXMLLoader.load(getClass().getResource("user.fxml"));
        Scene scene = new Scene(p);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public static FXMLDocumentController getFXML() {
        return UserController.getFXML();
    }
    
}

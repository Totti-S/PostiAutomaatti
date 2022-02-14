/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ht;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Führer from SVV
 */
public class ForgotpasswordController implements Initializable {
    private DataManager dm = null;
    @FXML
    private TextField userField;
    @FXML
    private TextArea questionField;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordAgain;
    @FXML
    private Button newPassword;
    @FXML
    private Button cancelActionButton;
    @FXML
    private TextField questionAnsField;
    @FXML
    private Tooltip errorMessage;
    @FXML
    private Label correctLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dm = DataManager.getInstance();
    }    

    @FXML
    private void bringQuestion(ActionEvent event) {
        System.out.println(userField.getText());
        if (!userField.getText().isEmpty()) {
            if(dm.checkUser(userField.getText())) {
                questionField.setText(dm.getUserQuestion(userField.getText()));
            } else {
                System.out.println("jotain");
                questionField.setText(null);
            }
        }
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        Stage stage = (Stage) cancelActionButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void newPasswordAction(ActionEvent event) {
        User u = dm.getUser(userField.getText());
        int id = u.getID();
        dm.makePasswordUpdate("update;"+id+";user;password;" + password.getText(), u, password.getText());
        Stage stage = (Stage) newPassword.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void checkAnswer(ActionEvent event) {
        if (!questionAnsField.getText().isEmpty()) {
            if(dm.checkAnswer(questionAnsField.getText(), userField.getText())) {
                password.setEditable(true);
                passwordAgain.setEditable(true);
                correctLabel.setVisible(true);
            } else {
                password.setEditable(false);
                passwordAgain.setEditable(false);
                correctLabel.setVisible(false);
            }
        } 
    }
    
    private void updateNewButton() {
        if (!password.getText().isEmpty() && !passwordAgain.getText().isEmpty()
                && password.getText().equals(passwordAgain.getText())) {
            newPassword.setDisable(false);
        } else {
            if (!password.getText().isEmpty() && !passwordAgain.getText().isEmpty()
                && !password.getText().equals(passwordAgain.getText())) {
                errorMessage.setText(errorMessage.getText()+"\nSalasanat eivät täsmää");
            }
            if(password.getText().isEmpty() || passwordAgain.getText().isEmpty()) {
                errorMessage.setText(errorMessage.getText() + "\nSalasana puuttuu");
            }
            errorMessage.setText(errorMessage.getText().substring(1)); 
        }
    }  

    @FXML
    private void checkValue(KeyEvent event) {
        updateNewButton();
    }
}

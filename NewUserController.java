package ht;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author p3132
 */
public class NewUserController implements Initializable {
    private DataManager dm = null;
    @FXML
    private TextField userTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField password2Field;
    @FXML
    private ComboBox<String> questionBox;
    @FXML
    private TextField questionAnsField;
    @FXML
    private Tooltip errorMessage;
    @FXML
    private Button newAccountButton;
    @FXML
    private Button cancelNewUserButton;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateNewAccountButton();
        dm = DataManager.getInstance();
        makeComboBox();
    }    

    @FXML
    private void newAccount(ActionEvent event) {
        String psw = passwordField.getText();
        String user = userTextField.getText();
        int id = dm.getNewUserId();
        String question = questionBox.getValue();
        String questionAns = questionAnsField.getText();
        dm.addUser(new User(user, psw, id, question, questionAns));
        Stage stage = (Stage) newAccountButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void checkPassword(KeyEvent event) {
        if (passwordField.getText().equals(password2Field.getText()) ) {
            //labelnot.setVisable(false);
            //labelsuccess.setVisable(true); 
        } else if (!password2Field.getText().isEmpty() && !passwordField.getText().isEmpty()){
            //labelnot.setVisable(false);
            //labelsuccess.setVisable(false); 
        }
        updateNewAccountButton();
    }
    
    @FXML
    private void new_checkValues(KeyEvent event) {
        if (!userTextField.getText().isEmpty() && !dm.checkUser(userTextField.getText())) {
            //label.setVisable(false);  
        }
        updateNewAccountButton();
    }
    
    @FXML
    private void checkAnsField(KeyEvent event) {
        updateNewAccountButton();
    }
    
    @FXML
    private void checkAnsField2(ActionEvent event) {
        updateNewAccountButton();
    }

    private void updateNewAccountButton() {
        if( !passwordField.getText().isEmpty() && !password2Field.getText().isEmpty() 
                && !userTextField.getText().isEmpty() && questionBox.getValue() != null 
                && !questionAnsField.getText().isEmpty() && !dm.checkUser(userTextField.getText()) 
                && passwordField.getText().equals(password2Field.getText())) {
            newAccountButton.setDisable(false);
        } else {
            errorMessage.setText("");
            newAccountButton.setDisable(true);
            if (!userTextField.getText().isEmpty() && dm.checkUser(userTextField.getText())) {
                errorMessage.setText(errorMessage.getText()+"\nKäyttäjänimi on jo viety");
                //label.setVisable(true);
            }
            if (!passwordField.getText().isEmpty() && !password2Field.getText().isEmpty()
                && !passwordField.getText().equals(password2Field.getText())) {
                
                errorMessage.setText(errorMessage.getText()+"\nSalasanat eivät täsmää");
                //labelnot.setVisable(true);
                //labelsuccess.setVisable(false);
            }
            if(userTextField.getText().isEmpty()) {
                errorMessage.setText(errorMessage.getText() + "\nKäyttäjänimi puuttuu");
            }
            if(passwordField.getText().isEmpty() || password2Field.getText().isEmpty()) {
                errorMessage.setText(errorMessage.getText() + "\nSalasana puuttuu");
            }
            if (questionBox.getValue() == null || questionAnsField.getText().isEmpty()) {
                errorMessage.setText(errorMessage.getText() +"\nTarkistuskysymykseen ei ole vastattu");
            }
            errorMessage.setText(errorMessage.getText().substring(1)); 
        }
    }

    private void makeComboBox() {
        questionBox.getItems().add("Mikä on null:an oikea nimi");
        questionBox.getItems().add("Paras ohjelmointikieli");
        questionBox.getItems().add("Suosikki fasisti");
        questionBox.getItems().add("Ensimmäisen auton merkki, jonka omistit");
        questionBox.getItems().add("Ensimmäinen GF");
    }

    @FXML
    private void cancelNewUser(ActionEvent event) {
        Stage stage = (Stage) cancelNewUserButton.getScene().getWindow();
        stage.close();
    }
}

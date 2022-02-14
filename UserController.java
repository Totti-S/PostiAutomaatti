package ht;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserController implements Initializable {
    DataManager dm = null;
    private static FXMLLoader fl = new FXMLLoader();
    private static FXMLDocumentController lol = null;
    
    @FXML
    private Text usernameError;
    @FXML
    private Text passwordError;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dm = DataManager.getInstance();
    }    

    @FXML
    private void loginAction(ActionEvent event) {
        String name = userName.getText().trim();
        String psw = passwordField.getText();
        if(name != null & psw != null) {
            if( dm.checkUser(name) ) {
                if ( dm.checkUserPassword(name, psw)) {
                    try {
                        dm.setCurrentUser(name);
                        Stage stage = (Stage)loginButton.getScene().getWindow();
                        stage.close();
                        Pane p = (Pane) fl.load(getClass().getResource("FXMLDocument.fxml").openStream());
                        lol = (FXMLDocumentController) fl.getController();
                        openFXML(p);
                    } catch (IOException ex) {
                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    usernameError.setVisible(false);
                    passwordField.setText(null);
                    passwordError.setVisible(true);
                }
            } else {
                usernameError.setVisible(true);
                passwordField.setText(null);
                passwordError.setVisible(true);
            }
        }
    }

    @FXML
    private void openRecoverPassword(ActionEvent event) {
        try {
            Pane p = FXMLLoader.load(getClass().getResource("Forgotpassword.fxml"));
            openFXML(p);
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void openNewAccountView(ActionEvent event) {
        try {
            Pane p = FXMLLoader.load(getClass().getResource("newUser.fxml"));
            openFXML(p);
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void openFXML(Pane p) {
        Stage box = new Stage();
        Scene scene = new Scene(p);
        box.setScene(scene);
        box.setResizable(false);
        box.show();
    }
    
    public static FXMLDocumentController getFXML() {
        return lol;
    }
}

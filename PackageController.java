package ht;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class PackageController implements Initializable {
    DataManager dm = null;
    
    @FXML
    private TextField nameField;
    @FXML
    private TextField sizeField;
    @FXML
    private TextField weightField;
    @FXML
    private CheckBox yourSoul;
    @FXML
    private Button cancelButton;
    @FXML
    private Button createNew;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dm = DataManager.getInstance();
    }    

    @FXML
    private void cancelAction(ActionEvent event) {
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void createNewThing(ActionEvent event) {
        if (!nameField.getText().isEmpty() & !sizeField.getText().isEmpty() & !weightField.getText().isEmpty()) {
            // We take values from the "create new" sections text fields
            String name = nameField.getText(); 
            int size = Integer.valueOf(sizeField.getText());
            String weigth = weightField.getText();
            // Getting value from "breakable"
            String soul;
            if (yourSoul.isSelected()) {
                soul = "yes";
            } else {
                soul = "no";
            }
            int id = dm.getNewItemId();
            Item item = new Item(name, id, Float.valueOf(weigth), size , soul);
            dm.addItem(item);
            HT.getFXML().newLogEntry("Luotiin uusi esine " + item.getName());
            Stage stage = (Stage)cancelButton.getScene().getWindow();
            stage.close();
        }
    }
    
// <- NICE
}
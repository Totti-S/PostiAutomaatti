package ht;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLDocumentController implements Initializable{ 
    DataManager dm = null;
    private MediaPlayer mp = null;
    
    @FXML
    public WebView mapView;
    @FXML
    private Button packageButton;
    @FXML
    private Button findAutomat;
    @FXML
    private ComboBox<String> cityPoint;
    @FXML
    public ComboBox<String> pointAutomat;
    @FXML
    private AnchorPane packageMenu;
    @FXML
    private ToggleGroup packageClass;
    @FXML
    private Button newItem;
    @FXML
    private AnchorPane main;
    @FXML
    private ComboBox<String> startCity;
    @FXML
    private ComboBox<String> startAutomat;
    @FXML
    private ComboBox<String> endAutomat;
    @FXML
    private ComboBox<String> endCity;
    @FXML
    private ComboBox<Item> itemCombo;
    @FXML
    private Tooltip errorMessage;
    @FXML
    private ComboBox<String> packageCombo;
    @FXML
    private ListView<Package> packageView;
    @FXML
    private Button createButton;
    @FXML
    private Tooltip createPackageTip;
    @FXML
    private Tooltip sendToolTip;
    @FXML
    private Tooltip errorSend;
    @FXML
    private Button sendPackageButton;
    @FXML
    private ComboBox<String> paStartAutomat;
    @FXML
    private ComboBox<String> paEndAutomat;
    @FXML
    private ComboBox<String> paEndCity;
    @FXML
    private ComboBox<String> paStartCity;
    @FXML
    private Tooltip paSendError;
    @FXML
    private Button send_pa_package;
    @FXML
    private Tooltip paSendTooltip;
    @FXML
    private ToggleGroup paClass;
    @FXML
    private ListView<Log> logView;
    @FXML
    private RadioButton firstClass;
    @FXML
    private RadioButton secondClass;
    @FXML
    private RadioButton thirdClass;
    @FXML
    private RadioButton paFirstClass;
    @FXML
    private RadioButton paSecondClass;
    @FXML
    private RadioButton paThirdClass;
    @FXML
    private Tooltip paSaveTooltip;
    @FXML
    private Tooltip disableSaveTooltip;
    @FXML
    private Button saveButton;
    @FXML
    private Label successLabel;
    @FXML
    private TextField lengthMeter;
    @FXML
    private Button delete_pa_button;
    @FXML
    private Button deleteAllButton;
    @FXML
    private TextField lengthPaMeter;
    @FXML
    private Button saveMain;
    @FXML
    private Tooltip saveMainError;
    @FXML
    private Font x1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapView.getEngine().load(getClass().getResource("index.html").toExternalForm());
        packageMenu.setStyle("-fx-background-color: #fdf5e6");
        main.setStyle("-fx-background-color: #fdf5e6");
        dm = DataManager.getInstance();
        placeSetClear(); // Getting all the cities to comboboxes
        packageUpdate();
        updateButton();
        updateSendButton();
        update_pa_SendButton();
        update_pa_DeleteButton();
        saveButton.setDisable(true);
        saveMain.setDisable(true);
        update_pa_DeleteAllButton();
        createPackageTip.setText("Luodaan paketti varastoon\nPaketit ovat nähtävissä 'paketit' - välilehdessä");
        sendToolTip.setText("Lähetetään paketti annettujen tietojen perusteella.");
        paSendTooltip.setText("Lähetetään valittu paketti listasta");
        disableSaveTooltip.setText("Ei tallennettavia muutoksia");
        saveMainError.setText("Ei tallennettavia muutoksia");
        paSaveTooltip.setText("Tallennetaan kaikki mitä käyttäjä on tehnyt\n\tPaketit, esineet, lokit");
        makePackageComboValues();
        dm.getAllUsers();
        getLogs();
    }   
    
    /****************************************/
    /*         BUTTON FUNCTIONS             */
    /****************************************/
   
    /* AUTOMAT UPDATE ACTIONS */
    
    @FXML
    private void updatePointAutomat(ActionEvent event) {
        updateAutomat(cityPoint, pointAutomat);
        pointAutomat.getItems().add("Kaikki automaatit");
    }

    @FXML
    private void updateEndAutomat(ActionEvent event) {
        updateAutomat(endCity, endAutomat);
        updateSendButton();
    }

    @FXML
    private void updateStartAutomat(ActionEvent event) {
        updateAutomat(startCity, startAutomat);
        updateSendButton();
    }
    
    @FXML
    private void update_Pa_StartAutomat(ActionEvent event) {
        updateAutomat(paStartCity, paStartAutomat);
        update_pa_SendButton();
    }

    @FXML
    private void updatePaEndAutomat(ActionEvent event) {
        updateAutomat(paEndCity, paEndAutomat);
        update_pa_SendButton();
    }

    /* SOME OTHER RANDOM SHIT */
    
    @FXML
    private void newItemAction(ActionEvent event) {
        try {
            Stage box = new Stage();
            Parent page = FXMLLoader.load(getClass().getResource("package.fxml"));
            
            Scene scene = new Scene(page);
            box.setScene(scene);
            box.show();
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void drawAutomat(ActionEvent event) {
        if (pointAutomat.getValue() != null ) {
            if (!pointAutomat.getValue().equals("Kaikki automaatit")) { 
                    SmartPost sp = dm.getSmartPost(pointAutomat.getValue());
                    executeScript(sp);
            } else {
                ArrayList<SmartPost> al = dm.getAllAutomats(cityPoint.getValue());
                for (SmartPost sp : al) {
                    executeScript(sp);
                }
            }
        }   
    }

    @FXML
    private void makeNewPackacage(ActionEvent event) {
        if (!packageMenu.isVisible()) {
            packageMenu.setVisible(true);
        }
    }
    
    @FXML
    private void hideNewPackage(ActionEvent event) {
        if (packageMenu.isVisible()) {
            packageMenu.setVisible(false);
        }
    }
    
    /* RESET BUTTON ACTIONS */
    
    @FXML
    private void resetAction(ActionEvent event) {
        updateAutomat(startCity, startAutomat);
        updateAutomat(endCity,endAutomat);
        cityUpdate(startCity);
        cityUpdate(endCity);
        itemUpdate(itemCombo);
        makePackageComboValues();
        updateSendButton();
        updateButton();
        updateDistanceMeter();
    }
    
    @FXML
    private void resetPaAction(ActionEvent event) {
        updateAutomat(paStartCity, paStartAutomat);
        updateAutomat(paEndCity, paEndAutomat);
        cityUpdate(paStartCity);
        cityUpdate(paEndCity);
        packageView.getSelectionModel().clearSelection();
        update_pa_SendButton();
        update_pa_DeleteButton();
        update_pa_DeleteAllButton();
        updateDistancePaMeter();
    }
    
    /* LOL */
    
    @FXML
    private void sendPackageAction(ActionEvent event) { 
        Item item = itemCombo.getValue();
        int size = dm.parseSize(packageCombo.getValue());
        Package p = new Package(item, size, -1); // Filler Package so it goes through the "sendPackage" method
        sendPackage(startAutomat, endAutomat, startCity, endCity, packageClass, p);
    }
    
    @FXML
    private void sendPackageListView(ActionEvent event) {
        Package pa = packageView.getSelectionModel().getSelectedItem();
        sendPackage(paStartAutomat, paEndAutomat, paStartCity, paEndCity, paClass, pa);
    }
 
    @FXML
    private void placeUpdate(ActionEvent event) {
        dm.placeUpdate();
        placeSetClear();
    }
    
    @FXML
    private void clearAutomats(ActionEvent event) {
        mapView.getEngine().executeScript("document.deletePoints()");
        getLogs();
    }

    @FXML
    private void clearMap(ActionEvent event) {
        mapView.getEngine().executeScript("document.deletePoints()");
        mapView.getEngine().executeScript("document.deletePaths()");
    }
    
    @FXML
    private void clearPaths(ActionEvent event) {
        mapView.getEngine().executeScript("document.deletePaths()"); 
    }
    
    @FXML
    private void checkValues(ActionEvent event) {
        updateButton();
        updateSendButton();
        updateDistanceMeter();
    }
    
    @FXML
    private void check_Pa_Values(ActionEvent event) {
        update_pa_SendButton();
        updateDistancePaMeter();
    }
    
    @FXML
    private void check_Pa_ValuesM(MouseEvent event) {
        update_pa_SendButton();
        update_pa_DeleteButton();
    }
    
    @FXML
    private void createPackage(ActionEvent event) {
        int itemid = itemCombo.getValue().getId();
        Item item = dm.getItem(itemid);
        int id = dm.getNewPackageId();
        int size = dm.parseSize(packageCombo.getValue());
        Package pa = new Package(item, size , id);
        dm.addBox(pa);
        packageUpdate();
        updateButton();
        newLogEntry("Tehtiin uusi paketti: " + item.getName() + 
                ", jonka koko on " + size + " dm^3");
        dm.insertPackage(pa);
        update_pa_DeleteAllButton();
    }
    

    @FXML
    private void deletePackage(ActionEvent event) {
        Package pa = packageView.getSelectionModel().getSelectedItem();
        String name = pa.getItem().getName();
        String size = String.valueOf(pa.getSize());
        newLogEntry("Poistettiin paketti, joka sisälsi esineen '" + name + "' ja oli kokoa "+ size + "dm^3");
        packageView.getItems().remove(pa);
        if (packageView.getItems().isEmpty()) {
            dm.deleteAllPackages();
        } else {
            dm.deletePackage(pa);
        }
        update_pa_DeleteAllButton();
    }

    @FXML
    private void deleteAllPackage(ActionEvent event) {
        int maara = 0;
        packageView.getSelectionModel().clearSelection();
        packageView.getSelectionModel().selectAll();
        maara = packageView.getItems().size();
        dm.deleteAllPackages();
        packageView.getItems().clear();
        newLogEntry("Poistettiin kaikki paketit (" + maara + ")");
        update_pa_DeleteAllButton();

    }
     
    @FXML
    private void saveAllChanges(ActionEvent event) {
        dm.executeChanges();
        updateSaveButton();
        System.out.println("jotain");
    }
    
    /****************************************/
    /*   PRIVATE METOHDS and Some Public    */
    /****************************************/
    
    private void cityUpdate(ComboBox<String> city) {
        city.getItems().clear();
        ArrayList<String> al = dm.getCity();
        for (String c : al) {
            city.getItems().add(c);
        }
    }
    
    private void packageUpdate() {
        packageView.getItems().clear();
        ArrayList<Package> al = dm.getPackages();
        for (Package p: al) {
            packageView.getItems().add(p);
        }
    }
    
    public void itemUpdate(ComboBox<Item> item) {
        item.getItems().clear();
        ArrayList<Item> al = dm.getItems();
        item.getItems().addAll(al);
    }
    
    private void executeScript(SmartPost sp) {
        String deadBaby = sp.getAddress() + "," 
            + String.valueOf(sp.getCode()) + "," + sp.getCity();
        String americanSlave = sp.getName() + sp.getOpenH();
        mapView.getEngine().executeScript("document.goToLocation(\""+ deadBaby 
            +"\",\""+ americanSlave +"\", \"red\")");
    }
    
    private void updateAutomat(ComboBox<String> city ,ComboBox<String> auto) {
        if (city.getValue() != null ) {
            auto.getItems().clear();
            ArrayList<String> al = dm.getAutomats(city.getValue());
            for (String name : al) {
                auto.getItems().add(name);
            }
            auto.setValue(null);      
        } else {
            auto.getItems().clear();
            auto.setValue(null);
        }
    }

    private void updateButton() {
        if (itemCombo.getValue() != null && packageCombo.getValue() != null && 
      dm.parseSize(packageCombo.getValue()) >= itemCombo.getValue().getSize()) {
            if (checkForDublicate()) {
                createButton.setDisable(false);
            } else {
                errorMessage.setText("Varastossa on identtinen paketti");
                createButton.setDisable(true);
            }
        } else {
            errorMessage.setText("");
            if (packageCombo.getValue() == null && itemCombo.getValue() == null) {
                errorMessage.setText("Valitse esine\nValitse paketti");
            } else if(itemCombo.getValue() == null) {
                errorMessage.setText("Valitse esine pakettiin");
            } else if (packageCombo.getValue() == null) {
                errorMessage.setText("Valitse esineelle paketti");
            } else if (dm.parseSize(packageCombo.getValue()) < itemCombo.getValue().getSize()) {
                errorMessage.setText("Liian pieni paketti esineelle");
            }
            createButton.setDisable(true);
        }
    }
    
    private void updateSendButton() {
        if((itemCombo.getValue() != null)) {
            if(itemCombo.getValue().getName().equals("Hitler")) {
                sendPackageButton.setDisable(false);
            } else {
                lolxd(startCity,startAutomat,endCity, endAutomat, 
                    sendPackageButton, errorSend, true, firstClass);
            }

        } else  {
            lolxd(startCity,startAutomat,endCity, endAutomat, 
                sendPackageButton, errorSend, true, firstClass);
        }
    }
    
    private void update_pa_SendButton() {
        lolxd(paStartCity, paStartAutomat, paStartCity, paEndAutomat,
            send_pa_package, paSendError, false, paFirstClass);
    }
        
    private void makePackageComboValues() {
        packageCombo.getItems().clear();
        packageCombo.getItems().add("50 dm^3");
        packageCombo.getItems().add("100 dm^3");
        packageCombo.getItems().add("150 dm^3");
        packageCombo.getItems().add("200 dm^3");
        packageCombo.getItems().add("250 dm^3");
        packageCombo.getItems().add("300 dm^3");
        packageCombo.getItems().add("400 dm^3");
        packageCombo.getItems().add("500 dm^3");
    }
    
    private void placeSetClear() {
        cityUpdate(startCity);
        cityUpdate(endCity);
        cityUpdate(paStartCity);
        cityUpdate(paEndCity);
        cityUpdate(cityPoint);
        itemUpdate(itemCombo);
    }

    private boolean checkForDublicate() {
        int itemid = itemCombo.getValue().getId();
        Item item = dm.getItem(itemid);
        int size = dm.parseSize(packageCombo.getValue());
        boolean check = dm.checkForCopy(item, size);
        return check;
    }
    
    private void sendPackage(ComboBox<String> startA, ComboBox<String> endA, 
            ComboBox<String> sCity, ComboBox<String> eCity, ToggleGroup tg,
            Package p) {
            if (startA.getValue() != null & endA.getValue() != null) {
                /* Finding the coordinates from the object database*/
                double start [] = dm.getCoordinates(startA.getValue());
                double end [] = dm.getCoordinates(endA.getValue());
                /* To execute the path, the coordinates have to be in type array*/
                ArrayList<String> path = new ArrayList();
                path.add(String.valueOf(start[0]));path.add(String.valueOf(start[1]));
                // Getting to the end point to Path Array
                path.add(String.valueOf(end[0]));path.add(String.valueOf(end[1]));
                // To make start and end markers we need Smarpost objects (to method)
                SmartPost spStart = dm.getSmartPost(startA.getValue());
                SmartPost spEnd = dm.getSmartPost(endA.getValue());
                /* Getting the speed of the package. It get its value from UI name 
                 * Speed can be 1,2 or 3 */
                RadioButton rb = (RadioButton)tg.getSelectedToggle();
                int speed = Integer.parseInt(rb.getText().substring(0, 1));
                /*Some easter egg memes*/
                if (eCity.getValue().equals("KOUVOLA")) {
                    getSong("Hello Darkness.mp3");
                    mp.setStopTime(Duration.seconds(30));
                    mp.play();  
                } else if (speed == 1) {
                    getSong("Initial D - Deja Vu.mp3");
                    mp.setStartTime(Duration.seconds(64));
                    mp.setStopTime(Duration.seconds(88));
                    mp.play();
                }
                // Drawing the Markers to start and end point
                executeScript(spStart);
                executeScript(spEnd);
                // Scriptfunction method to draw path to google maps
                drawFunction(path, speed);
                // Parsing right looking log infromation
                // Splitting automat names, to gate right info
                String autoS[] = startA.getValue().split(",");
                String autoE[] = endA.getValue().split(",");
                // Get city infromation and put Strings together
                String s = sCity.getValue() + " " + autoS[1];
                String e = eCity.getValue() + " " + autoE[1];
                // commiting the logpost to Datamanagers "changeLog"
                newLogEntry("Lähetettiin "+speed+".luokassa "+p.logString() + 
                        "Lähtö:" + s +" Kohde:" + e);
                if(checkItemBroken(speed, p)) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("                                                       :(");
                    alert.setHeaderText(null);
                    alert.setContentText("Esineesi meni rikki kuljetuksessa"
                    + "\nToivottovasti vakuutuksesi on kunnossa, koska TIMOTEI™ "
                    + "kustanna kuljetusessa tapahtuvia vaurioita \n(Ei fyysisiä, eikä henkisiä)");
                    alert.showAndWait();
                }
            }
    }
    
    private void getSong(String song) {
        try {
            String path = getClass().getResource(song).toURI().toString();
            Media media = new Media(path);
            if (mp != null) {
                mp.stop();
            }
            mp = new MediaPlayer(media);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void lolxd1(ComboBox sCity, ComboBox<String> sAuto, ComboBox eCity, 
            ComboBox<String> eAuto, Button b, Tooltip error, boolean value, RadioButton first) {
        startCity = sCity.getValue();
        endCity = eCity.getValue();
        startAuto = sAuto.getValue();
        endAuto = eAuto.getValue();
        item = itemCombo.getValue();
        box = packageCombo.getValue();
        readyBox = packageView.getSelectionModel().isEmpty(); % boolean
        
        error.setText("");
     
        if(startCity == null) {
            b.setDisable(true);
            error.setText(error.getText() + "\nValitse lähtökaupunki");
        }
        else if(endCity == null) {
            b.setDisable(true);
            error.setText(error.getText() + "\nValitse kohdekaupunki");
        }
        
        if(startAuto == null) {
            b.setDisable(true);
            error.setText(error.getText() + "\nValitse lähtöautomaatti");
        }
        else if(endAuto == null) {
            b.setDisable(true);
            error.setText(error.getText() + "\nValitse kohdeautomaatti");
        }
        else if(startAuto == endAuto) {
            b.setDisable(true);
            error.setText("\nPakettia ei voi lähettää samaan automaattiin\n\t-Tarkista automaattien arvot");
        }
        else if(getDistance(startAuto,endAuto) > 150 && first.isSelected()) {
            b.setDisable(true);
            error.setText("\nLähetysluokalle liian pitkä matka");
        }
        
        if(value) {
            if(itemCombo.getValue() == null) {
                b.setDisable(true);
                error.setText(error.getText() + "\nValitse esine");
            }
            else if(packageCombo.getValue() == null) {
                b.setDisable(true);
                error.setText(error.getText() + "\nValitse paketti");
            }
            else if(item.getSize() > dm.parseSize(box)) {
                b.setDisable(true);
                error.setText(error.getText() + "\nLiian pieni paketti esineelle");
            }
        }
        else {
            if(readyBox) {
                b.setDisable(true);
                error.setText(error.getText() + "\nValitse paketti");
            }
        }
    }
    
    private void lolxd(ComboBox sCity, ComboBox<String> sAuto, ComboBox eCity, 
            ComboBox<String> eAuto, Button b, Tooltip error, boolean value, RadioButton first) {
        // I recommend just to trust <if> 
        if (sCity.getValue() != null && sAuto.getValue() != null 
                && eCity.getValue() != null && eAuto.getValue() != null 
                && ((value && itemCombo.getValue() != null  && packageCombo.getValue() != null) || (!value && !packageView.getSelectionModel().isEmpty()))
                && ((itemCombo.getValue().getSize() <= dm.parseSize(packageCombo.getValue())) || !value)
                && sAuto.getValue() != eAuto.getValue() 
                && (getDistance(sAuto.getValue(),eAuto.getValue()) <= 150 || !first.isSelected()) ) {
        // </if> Trust me, i know what i'am doing
                b.setDisable(false); 
        } else {
            error.setText("");
            b.setDisable(true);
            if (sAuto.getValue() != null && eAuto.getValue() != null 
                    && sAuto.getValue() == eAuto.getValue()) {
                error.setText("\nPakettia ei voi lähettää samaan automaattiin\n\t-Tarkista automaattien arvot");
            } else if (sAuto.getValue() != null && eAuto.getValue() != null && 
                    (getDistance(sAuto.getValue(),eAuto.getValue()) > 150 && first.isSelected()) ) {
                error.setText("\nLähetysluokalle liian pitkä matka");
            }
            if(sCity.getValue() == null) {
                error.setText(error.getText() + "\nValitse lähtökaupunki");
            }
            if(sAuto.getValue() == null) {
                error.setText(error.getText() + "\nValitse lähtöautomaatti");
            }
            if(eCity.getValue() == null) {
                error.setText(error.getText() + "\nValitse kohdekaupunki");
            }
            if(eAuto.getValue() == null) {
                error.setText(error.getText() + "\nValitse kohdeautomaatti");
            }
            if (value) {
                if(itemCombo.getValue() != null && packageCombo.getValue() != null && 
        itemCombo.getValue().getSize() > dm.parseSize(packageCombo.getValue())) {
                    error.setText(error.getText() + "\nLiian pieni paketti esineelle");
                }
                if(itemCombo.getValue() == null) {
                    error.setText(error.getText() + "\nValitse esine");
                }
                if(packageCombo.getValue() == null) {
                    error.setText(error.getText() + "\nValitse paketti");
                }
            } else {
                if(packageView.getSelectionModel().isEmpty()) {
                    error.setText(error.getText() + "\nValitse paketti");
                }   
            }
            error.setText(error.getText().substring(1)); 
        }
    }
    
    private void drawFunction(ArrayList al, int speed) {
        mapView.getEngine().executeScript("document.createPath("
        +al +", \"red\" ,"+ speed +")");
    }

    private void getLogs() {
        logView.getItems().clear();
        if (dm.getCurrentUser() != null) {
            logView.getItems().addAll(dm.getLogs());
        }
    }

    public void newLogEntry(String string) {
        dm.makeNewLogEntry(string);
        logView.getItems().add(dm.getLastLogEntry());
        updateSaveButton();
    }
    
    public void updateItemCombo(Item item) {
        itemCombo.getItems().add(item);
    }
    
    private double getDistance(String start, String end){
        ArrayList al = new ArrayList();
        double []s = dm.getCoordinates(start);
        double []e = dm.getCoordinates(end);
        al.add(s[0]);al.add(s[1]);al.add(e[0]);al.add(e[1]);
        double d = (double)mapView.getEngine().executeScript("document.countDistance("+al+")");
        System.out.println(d);
        return d;
    }
    
    private void updateSaveButton() {
        if (dm.changeLogIsEmpty()) {
            saveMain.setDisable(true);
            saveButton.setDisable(true);
            successLabel.setVisible(true);
        } else {
           saveMain.setDisable(false);
           saveButton.setDisable(false);
           successLabel.setVisible(false);
        }
    }

    private boolean checkItemBroken(int speed, Package pa) {
        Item item = pa.getItem();
        if ( item.getFragile().equals("yes")  && ((speed == 1) 
                || (speed == 2 && 100 <= pa.getSize() - item.getSize()) 
                || (speed == 3 && (pa.getSize() < 300 || item.getWeight() <= 10)) ) ) {
            return true;
        }
        return false;
    }

    private void update_pa_DeleteButton() {
        if (packageView.getSelectionModel().isEmpty() || packageView.getItems().isEmpty()) {
            delete_pa_button.setDisable(true);
        } else {
            delete_pa_button.setDisable(false);
        }
        
    }
    
    private void update_pa_DeleteAllButton() {
        if (packageView.getItems().isEmpty()) {
            deleteAllButton.setDisable(true);
        } else {
            deleteAllButton.setDisable(false);
        }
    }

    private void updateDistanceMeter() {
        if (startAutomat.getValue() != null && endAutomat.getValue() != null) {
            lengthMeter.setText(String.valueOf(getDistance(startAutomat.getValue(),endAutomat.getValue())));
        } else {
            lengthMeter.setText(null);
        }
    }
    
    private void updateDistancePaMeter() {
        if (paStartAutomat.getValue() != null && paEndAutomat.getValue() != null) {
            lengthPaMeter.setText(String.valueOf(getDistance(paStartAutomat.getValue(),paEndAutomat.getValue())));
        } else {
            lengthPaMeter.setText(null);
        }
    }

    @FXML
    private void getHelpAction(ActionEvent event) {
        try {
            Stage box = new Stage();
            Parent page = FXMLLoader.load(getClass().getResource("Help.fxml"));
            
            Scene scene = new Scene(page);
            box.setScene(scene);
            box.show();
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
// eof

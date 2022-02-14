package ht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class DataManager {
    static DataManager dm = null; // Singelton object that controlls all data
    private User currentUser = null; 
    private ArrayList<SmartPost> list = null; // All smartpost places in Database
    private ArrayList<String> cities = null; // All unique cities in database
    private ArrayList<Item> items = null; // All items from all the users
    private ArrayList<Package> boxes = null; // All packages in database  
    private ArrayList <User> users = null; // All users in database
    private ArrayList<Item> userItems = new ArrayList(); // only the new items made on current run of the program
    private ArrayList<Package> userBoxes = new ArrayList();
    private ArrayList <Log> userLogs = new ArrayList();
    private ArrayList <String> changeLog = new ArrayList(); // Describes all the changes needed to make to the database

            
    DataManager() {
        list = new ArrayList();
        makeSmartPostList();
        items = makeItemsList("SELECT * FROM \"item\"");
        boxes = makeBoxes("SELECT * FROM \"package\"");
        makeUsers();
    }
    
    static public DataManager getInstance() {
        if (dm == null) {
            dm = new DataManager();
        }
        return dm;
    }
    /* Makes update to possible chances in database from URL */
    public void placeUpdate() {
        try {
            Document doc;
            String data = "";
            String line;
            URL url = new URL("http://iseteenindus.smartpost.ee/api/?request=destinations&country=FI&type=APT");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            
            while( (line = br.readLine()) != null) {
                data += line + "\n"; 
            }
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(data)));
            doc.getDocumentElement().normalize();
            
            NodeList nodes = doc.getElementsByTagName("item");
            try (Connection c = openConnection()) {
                Statement sta = c.createStatement();
                sta.execute("DELETE FROM \"automat\"");
                for(int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    Element e = (Element) node;
                    
                    String city, address, avail, name;
                    int code;
                    double lat, lng;
                    
                    code = Integer.parseInt(getElementValue("postalcode", e));
                    city = getElementValue("city", e);
                    address = getElementValue("address", e);
                    avail = getElementValue("availability", e);
                    name = getElementValue("name", e);
                    lat = Float.parseFloat(getElementValue("lat", e));
                    lng = Float.parseFloat(getElementValue("lng", e));
                    Statement st = c.createStatement();
                    st.execute("INSERT INTO \"automat\" (code, city, address,"
                            + "availability, lat, lng, name) VALUES("
                            +code+ ",\"" + city +"\",\"" +address+"\""
                            + ",\""+avail+"\","+lat+","+lng+",\""+name+"\")");
                }
                c.close();
                makeSmartPostList();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(PackageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(PackageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void makeSmartPostList() {
        Connection c = openConnection();
        try {
            String question = "SELECT * FROM \"automat\" ORDER BY \"city\"";
            Statement sta = c.createStatement();
            ResultSet rs = sta.executeQuery(question);
            cities = new ArrayList();
            while (rs.next()) {
                String city = rs.getString("city");
                if (!cities.contains(city.toUpperCase())) {
                    cities.add(city.toUpperCase());
                }
                String name = rs.getString("name"); 
                int code = rs.getInt("code");
                String openH = rs.getString("availability");
                double lat = (double) rs.getFloat("lat");
                double lng = (double) rs.getFloat("lng");
                String address = rs.getString("address");
                list.add(new SmartPost(code, city.toUpperCase(), address, openH, lat, lng, name));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ArrayList<Item> makeItemsList(String question) {
        Connection c = openConnection();
        ArrayList<Item> al = new ArrayList();
        try {
            Statement sta = c.createStatement();
            ResultSet rs = sta.executeQuery(question);
            while (rs.next()) {
                String name = rs.getString("name"); 
                int size = rs.getInt("size");
                float weight = rs.getFloat("weight");
                String fragile = rs.getString("fragile");
                int id = rs.getInt("itemid");
                al.add(new Item(name, id, weight, size, fragile));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return al;
    }
    
    private ArrayList<Package> makeBoxes(String question) {
        Connection c = openConnection();
        ArrayList<Package> al = new ArrayList();
        try {
            Statement sta = c.createStatement();
            ResultSet rs = sta.executeQuery(question);
            while (rs.next()) {
                int itemid = rs.getInt("itemid");
                Item item = getItem(itemid);
                int size = rs.getInt("size");
                int id = rs.getInt("packageid");
                al.add(new Package(item, size, id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return al;
    }
    
    private ArrayList<Log> makeLogs(String question) {
        Connection c = openConnection();
        ArrayList<Log> al = new ArrayList();
        try {
            Statement sta = c.createStatement();
            ResultSet rs = sta.executeQuery(question);
            while (rs.next()) {
                String date = rs.getString("time");
                int id = rs.getInt("logid");
                String action = rs.getString("action");
                al.add(new Log(date, action, id));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return al;
    }
    
    private void makeUsers() {
        Connection c = openConnection();
        try {
            String question = "SELECT * FROM \"user\"";
            Statement sta = c.createStatement();
            ResultSet rs = sta.executeQuery(question);
            users = new ArrayList();
            
            while (rs.next()) {
                String name = rs.getString("name");
                String psw = rs.getString("password");
                String userQuestion = rs.getString("question");
                String answer = rs.getString("answer");
                String userid = String.valueOf(rs.getInt("userid"));
                int id = rs.getInt("userid");
                users.add(new User(name, psw, id, userQuestion, answer));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Connection openConnection(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String path = System.getProperty("user.dir");
            String url = "jdbc:sqlite:" + path + "/src/ht/htdb.db";
            c = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(PackageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }
 
    public ArrayList<String> getCity() {
        return cities;
    }

    public ArrayList<String> getAutomats(String city) {
        ArrayList<String> al = new ArrayList();
        for (SmartPost sp : list) {
            if (sp.getCity().equals(city)) {
                al.add(sp.getName());
            }
        }  
        return al;
    }

    public SmartPost getSmartPost(String place) {
        for (SmartPost sp : list) {
            if (sp.getName().equals(place)) {
                return sp;
            }
        }
        return null;
    }

    public ArrayList<SmartPost> getAllAutomats(String city) {
        ArrayList<SmartPost> al = new ArrayList();
        for (SmartPost sp : list) {
            if (sp.getCity().equals(city)) {
                al.add(sp);
            }
        }
        return al;
    }
    
    public void getAllUsers() {
        for(User u : users){
            //System.out.println(u.getName()+ " " + u.getPassword());
        }
    }
    
    private String getElementValue(String tag, Element e) {
        return e.getElementsByTagName(tag).item(0).getTextContent();
    }
    
    public ArrayList<Item> getItems() {
        return userItems;
    }
    
    public Item getItem(int id) {
        for (Item i : items) {
            if (id == i.getId()) {
                return i;
            }
        }
        for (Item i : userItems) {
            if (id == i.getId()) {
                return i;
            }
        }
        return null;
    }
    
    public double [] getCoordinates(String place) {
        double co [] = new double[2];
        for (SmartPost sp : list) {
            if (sp.getName().equals(place)) {
                co[0] = sp.getLatitude();
                co[1] = sp.getLongitude();
                return co;
            }
        }
        return null;
    }
    
    public void setCurrentUser(String name) {
        for(User u : users) {
            if(u.getName().equals(name)) {
                currentUser = u;
                break;
            }
        }
        if (currentUser != null) {
            userBoxes = makeBoxes("SELECT * FROM \"viewPackage\" WHERE \"userid\" = " + currentUser.getID());
            userItems =  makeItemsList("SELECT * FROM \"viewItems\" WHERE \"userid\" = " + currentUser.getID());
            userLogs = makeLogs("SELECT * FROM \"viewLog\" WHERE \"userid\" = " + currentUser.getID());
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public ArrayList<Package> getPackages() {
        return userBoxes;
    }
    
    public int getNewItemId() {
        int id = -2;
        for (Item item : items) {
            if (item.getId() > id) {
                id = item.getId();
            }
        }
        for (Item item : userItems) {
            if (item.getId() > id) {
                id = item.getId();
            }
        }
        return id + 1;
    }
    
    public int parseSize(String s) {
        String size = s.substring(0,s.indexOf(" "));
        return Integer.valueOf(size);
    }
    
    public int getNewPackageId() {
        int id = -2;
        for (Package thing : boxes) {
            if (thing.getId() > id) {
                id = thing.getId();
            }
        }
        for (Package thing : userBoxes) {
            if (thing.getId() > id) {
                id = thing.getId();
            }
        }
        return id + 1;
    }
    
    public int getNewUserId() {
        int id = -2;
        for (User u : users) {
            if (u.getID() > id) {
                id = u.getID();
            }
        }
        return id + 1;
    }
    
    public boolean checkForCopy(Item item, int size) {
        for (Package p : userBoxes) {
            if (p.getItem().equals(item) && p.getSize() == size) {
                return false;
            }
        }
        return true;
    }
    
    public void makeNewLogEntry(String entry) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date now = new Date();
        String rlyNow = sdfDate.format(now);
        int id = getNewLogId();
        userLogs.add(new Log(rlyNow, entry, id));
        makeChange2log("insert;" + id + ";log;action;" + entry + ";time;" + rlyNow + ";userid;" + currentUser.getID() + ";logid;" + id);
    }
    
    public int getNewLogId() {
        int id = -1;
        for (Log l : userLogs) {
            if (id < l.getId()) {
                id = l.getId();
            }
        }   
        return id + 1;
    }

    public boolean checkUser(String user) {
        for (User u : users) {
            if (u.getName().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkUserPassword(String user, String psw) {
        for (User u : users) {
            if (u.getName().equals(user)) {
                return u.getPassword().equals(psw);
            }
        }
        return false;
    }

    public void addItem(Item item) {
        userItems.add(item);
        HT.getFXML().updateItemCombo(item);
        makeChange2log("insert;" + item.getId() + ";item;name;"+ item.getName()
                + ";size;" + item.getSize() + ";weight;" + item.getWeight() +
                ";fragile;"+ item.getFragile());
    }

    public void deletePackage(Package pa) {
        makeChange2log("delete;" + pa.getId() + ";package");
        userBoxes.remove(pa);
    }
    
    private void makeChange2log(String action) {
        Iterator<String> iter;
        String [] newS = action.split(";");
        switch (newS[0]) {
            case "delete":
                boolean checkIfRemove = true;
                iter = changeLog.iterator();
                while (iter.hasNext()) {   
                String []check = iter.next().split(";");
                    if((check[0].equals("insert") || check[0].equals("update")) &&
                             check[2].equals(newS[2])) {
                        if (check [1].equals(newS[1])) {
                            iter.remove();
                            checkIfRemove = false;
                        }
                    }
                }
                if (checkIfRemove) {
                    changeLog.add(action);
                }
                break;
            case "update":
                boolean lol = true;
                ListIterator<String> lister = changeLog.listIterator();
                while (lister.hasNext()) {
                    String []check = lister.next().split(";");
                    if(check[0].equals("insert") && check[2].equals(newS[2])) {
                        if (check [1].equals(newS[1])) {
                            for (int i = 0; i < (newS.length - 3) / 2 ; i++) {
                                for (int j = 0; i < (check.length - 3) / 2 ; j++) {
                                    if(newS[3 + j * 2].equals(check[3 + 2 * i])) {
                                        check[4 + 2 * i] = newS[4 + j * 2];
                                    }
                                }
                            }
                            String newStr = "";
                            for (String s : check) {
                                newStr = s + ";";
                            }
                            lister.set(newStr);
                            lol = false;
                            break;
                        }
                    }
                }
                if (lol) {
                   changeLog.add(action);
                }
                break;
            case "deleteall":
                iter = changeLog.iterator();
                while (iter.hasNext()) {
                    
                String []check = iter.next().split(";");
                    if((check[0].equals("insert") || check[0].equals("update") 
                            || check[0].equals("delete") 
                            || check[0].equals("deleteall") ) 
                            &&(check[2].equals(newS[2])) ) {
                        iter.remove();
                    }
                }   
                changeLog.add(action);
                break;
            default:
                changeLog.add(action);
                break;
        }
        System.out.println(changeLog);
    }
    
    public void executeChanges() {
        try (Connection c = openConnection()) {
            Statement st = c.createStatement();
            for (String action : changeLog) {
                String []str = action.split(";");
                String statement;
                switch (str[0]) {
                    case "delete":
                        statement = "delete from \"" + str[2] +  "\" where "
                                + "\"" + str[2] + "id\" = " + str[1];
                        st.execute(statement);
                        statement = "delete from \"user" + str[2] + "\" "
                                + "where \""+ str[2] +"_id\" = " + str[1] + 
                                " and \"user_id\" = " + currentUser.getID();
                        System.out.println(statement);
                        st.execute(statement);
                        break;
                    case "insert":
                        executeInsert(action, c);
                        break;
                    case "update":
                        executeUpdate(action, c);
                        break;
                    case "deleteall":
                        statement = "delete from \"" + str[2] +  "\" where "
                                + "\"userid\" = " + currentUser.getID();
                        st.execute(statement);
                        statement = "delete from \"user" + str[2] + "\" "
                                + "where \"user_id\" = " + currentUser.getID();
                        st.execute(statement);
                        break;
                }
            }
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        changeLog.clear();
    }
    
    private void executeInsert(String action, Connection c) {
        try {
            // Preparing SQL-statement. We use variable method to avoid SQL injection
            String []s = action.split(";");
            String insertion = "INSERT INTO \"" + s[2] + "\" (";
            for (int i = 0; i < (s.length -3) / 2; i++) { 
                if (i != 0) {
                    insertion += ",";
                }
                insertion += s[3+2*i];
            }
            insertion += ") values (";
            for (int i = 0; i < (s.length -3) / 2; i++) { 
               if (i != 0) {
                    insertion += ",";
                }
               insertion += "?";
            }
            insertion += ")";
            //Insertion all values to statement and make the commit
            makeCommit(insertion, s, c);
            // adding to the connection table, so that user can use the 
            // item/package after user closes the program(and saves beforehand);
            //System.out.println("userid = " + currentUser.getID() + "packageid = " + s[1]);
            if (!s[2].equals("log") && !s[2].equals("user")) {
                insertion = "insert into \"user" + s[2] + "\" values (" +s[1] +","+ currentUser.getID() +")";
                Statement st = c.createStatement();
                st.execute(insertion);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PackageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteAllPackages() {
        makeChange2log("deleteall;-1;package");
        userBoxes.clear();
    }

    public void insertPackage(Package pa) {
        makeChange2log("insert;" + pa.getId() + ";package;itemid;"+ pa.getItem().getId()
                +";size;" + pa.getSize());
        userBoxes.add(pa);
    }

    private void executeUpdate(String action, Connection c) {
        String []str = action.split(";");
        String statement = "UPDATE " + "\"" + str[2] + "\" SET ";
        for (int i = 0; i < (str.length -3) / 2; i++) {
            if (i != 0) {
                statement += ",";
            }
            statement += "\"" + str[3+2*i] + "\" = ?";
        }
        statement += "where \"" + str[2] + "id\" = " + str[1];
        makeCommit(statement, str, c);
    }
    
    private void makeCommit(String statement, String []s, Connection c){
        try {
            // We set autocommit off so it dosent do it for us in random time just to be sure
            c.setAutoCommit(false);
            // REAL preparing of the SQL-statement
            PreparedStatement ps = c.prepareStatement(statement);
            // We set the values to the variables to the Prepared Statement
            for (int i = 1; i < ((s.length -3) / 2) + 1; i++) {
                switch (s[3 + 2 * (i-1)]) {
                    case "name":
                    case "fragile":
                    case "action":
                    case "time":
                    case "question":
                    case "answer":
                    case "password":
                        ps.setString(i, s[4 + 2 * (i-1)]);
                        break;
                    case "weight":
                        ps.setFloat(i, Float.parseFloat(s[4 + 2 * (i-1)]));
                        break;
                    default:
                        ps.setInt(i, Integer.parseInt(s[4 + 2 * (i-1)]));
                        break;
                }
            }
            // Executing the SQL statement
            ps.execute();
            // Actually saving the damn thing
            c.commit();
            // Options back to default
            c.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<Log> getLogs() {
        return userLogs;
    }
    
    public Log getLastLogEntry() {
        return userLogs.get(userLogs.size()-1);
    }

    public boolean changeLogIsEmpty() {
        return changeLog.isEmpty();
    }

    public void addUser(User u) {
        users.add(u);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date now = new Date();
        String rlyNow = sdfDate.format(now);
        Connection c = openConnection();
        executeInsert("insert;"+u.getID()+";user;name;" + u.getName()+";userid;"
                +u.getID()+";password;" + u.getPassword() +";question;"
                + u.getQuestion() +";answer;"+ u.getAnswer(), c);
        executeInsert("insert;" + 1 + ";log;action;Käyttäjä " + u.getName()+ 
                " luotiin;time;" + rlyNow + ";userid;" + u.getID()+ ";logid;1", c);
        try {
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getUserQuestion(String text) {
        for (User use : users) {
            if(use.getName().equals(text)) {
                return use.getQuestion();
            }
        }
        return null;
    }

    public boolean checkAnswer(String text, String user) {
        User u  = null;
        for (User use : users) {
            if(use.getName().equals(user)) {
                u = use;
                break;
            }    
        }
        if (u == null) {
            return false;
        } else {
            return u.checkAns(text);
        }
    } 

    public User getUser(String user) {
        User u = null;
        for (User use : users) {
            if(use.getName().equals(user)) {
                u= use;
                break;
            }    
        }
        return u;
    }

    public void makePasswordUpdate(String action, User u, String psw) {
        Connection c = openConnection();
        executeUpdate(action, c);
        u.setPassword(psw);
        try {
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addBox(Package pa) {
       userBoxes.add(pa); 
    }
}
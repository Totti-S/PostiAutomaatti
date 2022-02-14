package ht;

public class User {
    final private int id;
    final private String name;
    private String password;
    final private String question;
    final private String questionAns;
  /*  private ArrayList <Log> logList = null;
    private ArrayList <Package> boxes = null;
    private ArrayList <Item> items = null;*/
    
    User (String name, String password, int id, String question, String ans) {
        this.name = name;
        this.password = password;
        this.id = id;
        this.question = question;
        this.questionAns = ans;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getID () {
        return id;
    }
    
    public String getQuestion(String name) {
        if (this.name.equals(name)) {
            return question;
        }
        return null;
    }
    
    public String getAnswer() {
        return this.questionAns;
    }
    
    public String getQuestion() {
        return this.question;
    }
    
    public boolean checkAns(String ans) {
        return this.questionAns.equals(ans);
    }
  /*  
    public ArrayList<Log> getLog() {
        return logList;
    }
    
    public ArrayList<Item> getItems() {
        return items;
    }
    
    public ArrayList<Package> getPackages() {
        return boxes;
    }
*/
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ht;




public class Log {
    private final String date;
    private final String action;
    private final int id;
    
    Log(String date, String action, int id) {
        this.date = date;
        this.action = action;
        this.id = id;
    }
    
    public String getDate() {
        return date;
    }
    
    public String getAction() {
        return action;
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return date + " " + action;
    }
}


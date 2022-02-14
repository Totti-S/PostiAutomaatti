package ht;

public class Package {
    final private Item item;
    /*final private int height;
    final private int width;
    final private int length;*/
    final private int size;
    final private int id;
    
    Package (Item item, int size, int id) {
        this.item = item;
        this.size = size;
        this.id = id;
    }
    
    public Item getItem() {
        return item;
    }
   /* 
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getLenght() {
        return length;
    }
    */
    public int getId() {
        return id;
    }
    
    public int getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return item.getName() + " koko: " + size + " dm^3 (" + item.getWeight() + " kg) särkyvä: " + item.getFragile();
    }
    
    public String logString() {
        return item.getName() + " paketin koko: " + size + " ";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

// NICE
}
package ht;


public class Item {
    final private String name;
    final private int id;
    final private float weight;
    final private String fragile;
    /*final private int height;
    final private int width;
    final private int length;*/
    final private int size;
    
    Item(String name, int id, float weight,int size, String fragile) {
        this.name = name;
        this.id = id;
        this.weight = weight;
        this.size = size;
        this.fragile = fragile;
    }
    
    String getName() {
        return name;
    }
    
    int getSize() {
        return size;
    }
    
   /* public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getLenght() {
        return length;
    }*/
    
    public float getWeight() {
        return weight;
    }
    
    public String getFragile() {
        return fragile;
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return name + " koko: " + size + " dm^3 (" + weight +" kg)";
    }
}

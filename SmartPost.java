package ht;

public class SmartPost {
    final private int code;
    final private String address;
    final private String openH;
    final private String city;
    final private double lat;
    final private double lng;
    final private String name;
    
    SmartPost(int code, String city, String address, String openH, double lat, double lng, String name) {
        this.city = city;
        this.code = code;
        this.address = address;
        this.openH = openH;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }
    
    public String getCity() {
        return city;
    }
    
    public int getCode() {
        return code;
    }
   
    public String getAddress() {
        return address;
    }

    public String getOpenH() {
        return openH;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    public String getName() {
        return name;
    }  
}

package com.mandatory2.model;

import com.google.firebase.firestore.GeoPoint;

public class Snapinfo {
    public String id;
    public  String imageName;
    public String imageURL;
    public GeoPoint imageLoc;



    public  Snapinfo(){}
    public Snapinfo(String id,String name, String urI,GeoPoint imLoc) {
        this.id = id;
        this.imageName = name;
        this.imageURL = urI;
        this.imageLoc = imLoc;
    }
    public GeoPoint getImageLoc() {
        return imageLoc;
    }

    public void setImageLoc(GeoPoint imageLoc) {
        this.imageLoc = imageLoc;
    }
    public String getImageName(){
        return imageName;
    }
    public String getImageURL() {
        return imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}

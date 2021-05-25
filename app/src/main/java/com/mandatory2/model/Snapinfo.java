package com.mandatory2.model;

public class Snapinfo {
    public String id;
    public  String imageName;
    public String imageURL;
    public  Snapinfo(){}
    public Snapinfo(String id,String name, String urI) {
        this.id = id;
        this.imageName = name;
        this.imageURL = urI;
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

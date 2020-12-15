package com.namrahrasool.i170018_i170010_project;

public class Student {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String name;
    String id;
    String image;
    Student(){
    }
    Student(String id,String name,String image){
        this.name=name;
        this.id=id;
        this.image=image;
    }
}

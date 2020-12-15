package com.namrahrasool.i170018_i170010_project;

public class Course {
    private String name;
    private String description;
    Course(){
    }
    Course(String name, String description){
        this.name=name;
        this.description=description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

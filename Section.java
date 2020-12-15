package com.namrahrasool.i170018_i170010_project;

public class Section {

    String name;
    String course;

    Section(){
    }

    Section(String name, String course){
        this.course=course;
        this.name=name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

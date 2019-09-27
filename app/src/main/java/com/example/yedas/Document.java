package com.example.yedas;

import java.util.Date;

public class Document {
    public String filename;
    public String sender;
    public String type;
    public String date;

    public Document(String file, String sender,String type, String date){
        this.filename = file;
        this.sender = sender;
        this.type = type;
        this.date = date;
    }
    public String getfile() {
        return filename;
    }

    public void setfile(String docname) {
        this.filename = docname;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Document(){
     //default constructor
    }

}

package com.jandjdevlps.yedas;

public class Document {


    private String title;
    private String filename;
    private String sender;
    private String type;
    private String date;
    private String descript; // 설명
    private int decision; // 결정 -1 : 대기 , 0 : 거절 , 1 : 승인

    public Document(String title, String file, String sender, String type, String date, String descript, int decision){
        this.date = date;
        this.decision = decision;
        this.descript = descript;
        this.filename = file;
        this.sender = sender;
        this.type = type;
        this.title = title;
    }
    public String getfilename() {
        return filename;
    }

    public void setfilename(String filename) {
        this.filename = filename;
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

    public String getDescript() {
        return descript;
    }

    public int getDecision() {
        return decision;
    }
    public Document(){
     //default constructor
    }
    public void setDescription(String descript) {
        this.descript = descript;
    }

    public void setDecision(int decision) {
        this.decision = decision;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

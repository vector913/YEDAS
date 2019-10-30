package com.jandjdevlps.yedas;

public class ListViewitem {
    private String docs;
    private String name;
    private String descript;
    private String filename;
    private int decision;

    public void setDecision(int decision) {
        this.decision = decision;
    }
    public int getDecision() {
        return decision;
    }

    public ListViewitem(String docs, String name){
        this.docs = docs;
        this.name = name;
    }

    public String getDescript(){
        return descript;
    }

    public String getFilename(){
        return filename;
    }

    public String getName() {
        return name;
    }
    public String getDocs() {
        return docs;
    }
}

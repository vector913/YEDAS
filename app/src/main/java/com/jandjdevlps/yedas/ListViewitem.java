package com.jandjdevlps.yedas;

public class ListViewitem {

    private String docs;
    private String name;
    private String filename;

    public ListViewitem(String docs, String name){
        this.docs = docs;
        this.name = name;
    }
    public void setDocs(String docs) {
        this.docs = docs;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFilename(String filename) {
        this.filename = filename;
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

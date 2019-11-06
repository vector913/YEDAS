package com.jandjdevlps.yedas;

public class User {
    private String username;
    private String email;
    private String department;
    private String job;
    private boolean verified;

    public User(){
     //default constructor
    }

    public User(String username, String email,String department,String job,Boolean isEmailVerified){
       this.username = username;
       this.email = email;
       this.department = department;
       this. job = job;
       this.verified = isEmailVerified;
    }
    public boolean getisVerified() {
        return verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package com.example.yedas;

public class User {
    public String username;
    public String email;
    public String department;
    public String job;

    public User(){
     //default constructor
    }

    public User(String username, String email,String department,String job){
       this.username = username;
       this.email = email;
       this.department = department;
       this. job = job;
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

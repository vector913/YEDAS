package com.jandjdevlps.yedas;

//Note 로그인에 사용되는
public class User {
    private String username;
    private String email;
    private String department;
    private String job;
    private boolean verified;

    //Note default constructor
    public User(){

    }
    //Note 실제 프로젝트에서 사용되는 Constructer.
    public User(String username, String email,String department,String job,Boolean isEmailVerified){
       this.username = username;
       this.email = email;
       this.department = department;
       this. job = job;
       this.verified = isEmailVerified;
    }
    public boolean isVerified() {
        return verified;
    }

    public String getStrEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStrDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStrJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getStrUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

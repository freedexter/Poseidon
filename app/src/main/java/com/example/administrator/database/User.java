package com.example.administrator.database;

public class User {

    private String username;
    private String password;
    private String stat;

    public User() {
        super();
        // TODO Auto-generated constructor stub
    }
    public User(String username, String password,String stat) {
        super();
        this.username = username;
        this.password = password;
        this.stat = stat;

    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getStat() {
        return stat;
    }
    public void setStat(String stat) {
        this.stat = stat;
    }
    @Override
    public String toString() {
        return "User [ username=" + username + ", password="
                + password + "]";
    }
}

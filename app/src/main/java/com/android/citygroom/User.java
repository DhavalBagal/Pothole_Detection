package com.android.citygroom;

public class User {
    private String email, name, pwd;

    public User()
    {

    }

    public User(String email, String name, String pwd) {
        this.email = email;
        this.name = name;
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }
}

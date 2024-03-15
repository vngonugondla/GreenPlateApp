package com.example.greenplate.model;

public class User {
    private String username;
    private String password;


    private String height;
    private String weight;
    private String gender;
    private static User singleton;

    private User(String username, String password, String height, String weight, String gender) {
        this.username = "";
        this.password = "";
        this.height = "";
        this.weight = "";
        this.gender = "";
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

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getGender() {
        return gender; }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public static User getInstance() {
        if (singleton == null) {
            synchronized (User.class) {
                if (singleton == null) {
                    singleton = new User("", "", "", "", "");
                }
            }
        }
        return singleton;
    }



}
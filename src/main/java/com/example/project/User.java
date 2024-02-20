package com.example.project;

public class User {
    private String name;
    private String password;


    //intrebari create, intrebari la care rasp
    //camp pt chestionare
    //de facuttt

    public User(String name, String pasword  ) {
        this.password = pasword;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return name + "," + password ;
    }
}

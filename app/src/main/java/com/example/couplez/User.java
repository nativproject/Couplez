package com.example.couplez;

public class User {
    public String name, age, email;

    protected User() {
    }

    //Note that password was not included for security reasons
    protected User(String name, String age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
}

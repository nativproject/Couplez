package com.example.couplez;

import java.util.ArrayList;

public class User {
    public String email;
    public ArrayList<String> names, ages;

    protected User() {

    }

    //Note that password was not included for security reasons
    protected User(ArrayList<String> names, ArrayList<String> ages, String email) {
        this.names = names;
        this.ages = ages;
        this.email = email;
    }
}




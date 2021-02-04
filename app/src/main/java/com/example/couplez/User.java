package com.example.couplez;

import java.util.HashMap;

public class User {
    public String email, profile_image, your_name, partner_name, your_age, partner_age, about;
    public HashMap<String, String> names, ages;

    protected User() {

    }

    //Database constructor
    //Note that password was not included for security reasons
    protected User(HashMap<String, String> names, HashMap<String, String> ages, String email, String profile_image, String about) {
        this.names = names;
        this.ages = ages;
        this.email = email;
        this.about = about;
        this.profile_image = profile_image;
    }

    //App constructor
    protected User(String email, String your_name, String your_age, String partner_name, String partner_age, String profile_image, String about) {
        this.email = email;
        this.about = about;
        this.your_age = your_age;
        this.your_name = your_name;
        this.partner_age = partner_age;
        this.partner_name = partner_name;
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "email='" + email + '\'' +
                ", your_name='" + your_name + '\'' +
                ", partner_name='" + partner_name + '\'' +
                ", your_age='" + your_age + '\'' +
                ", partner_age='" + partner_age + '\'';
    }
}




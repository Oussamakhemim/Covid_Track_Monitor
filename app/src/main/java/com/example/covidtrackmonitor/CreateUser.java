package com.example.covidtrackmonitor;

public class CreateUser {
    public String name;
    public String email;
    public String password;
    public String code;
    public String infecte;
    public String lat;
    public String lng;
    public String imageUrl;
    public String userid;


    public CreateUser()
    {}

    public CreateUser(String name, String email, String password, String code, String infecte, String lat, String lng, String imageUrl,String userid) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.code = code;
        this.infecte = infecte;
        this.lat = lat;
        this.lng = lng;
        this.imageUrl = imageUrl;
        this.userid = userid;
    }


}

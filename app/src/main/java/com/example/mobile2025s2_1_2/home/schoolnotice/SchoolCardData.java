package com.example.mobile2025s2_1_2.home.schoolnotice;


public class SchoolCardData {
    public String title;
    public String url;

    public String getTitle() { return title; }

    public String getUrl() { return url; }
    public SchoolCardData(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
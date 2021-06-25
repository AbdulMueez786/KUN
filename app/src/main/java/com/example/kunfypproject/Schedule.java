package com.example.kunfypproject;

public class Schedule {
    String title, logo;

    public Schedule(String title, String logo) {
        this.title = title;
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}

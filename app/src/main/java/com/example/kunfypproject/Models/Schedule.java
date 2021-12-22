package com.example.kunfypproject.Models;

public class Schedule {
    private String date, name, status;
    public Schedule(){
        this.name = "";
        this.status = "";
        this.date="";
    }
    public Schedule(String name, String status,String date) {
        this.name = name;
        this.status = status;
        this.date=date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

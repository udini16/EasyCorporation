package com.example.group_test.model;

public class SubmittedRequest {
    private int id;
    private String address;
    private String notes;
    private String status;
    private RecyclableItems item; // Use the correct model here

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public RecyclableItems getItem() {
        return item;
    }
}

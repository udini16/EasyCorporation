package com.example.group_test.model;

import com.google.gson.annotations.SerializedName;

public class SubmittedRequest {
    @SerializedName("request_id")
    private int request_id;
    private String address;
    private String notes;
    private String status;
    private RecyclableItems item; // Use the correct model here

    // === Constructors ===
    public SubmittedRequest() {
    }

    public SubmittedRequest(int request_id, String address, String notes, String status, RecyclableItems item) {
        this.request_id =request_id;
        this.address = address;
        this.notes = notes;
        this.status = status;
        this.item = item;
    }

    public int getId() {
        return request_id;
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

    public void setId(int id) {
        this.request_id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItem(RecyclableItems item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "SubmittedRequest{" +
                "id=" + request_id +
                ", address='" + address + '\'' +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                ", item=" + item +
                '}';
    }
}

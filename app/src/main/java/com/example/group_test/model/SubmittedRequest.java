package com.example.group_test.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SubmittedRequest implements Serializable {

    @SerializedName("request_id")
    private int request_id;

    private String address;
    private String notes;
    private String status;
    private RecyclableItems item;

    private User user;

    @SerializedName("weight")
    private float weight;

    @SerializedName("total_price")
    private float total_price;

    // === Constructors ===
    public SubmittedRequest() {}

    public SubmittedRequest(int request_id, String address, String notes, String status, RecyclableItems item, float weight, float total_price) {
        this.request_id = request_id;
        this.address = address;
        this.notes = notes;
        this.status = status;
        this.item = item;
        this.weight = weight;
        this.total_price = total_price;
    }

    // === Getters ===
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

    public float getWeight() {
        return weight;
    }

    public float getTotal_price() {
        return total_price;
    }

    public User getUser() {
        return user;
    }

    // === Setters ===
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

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SubmittedRequest{" +
                "id=" + request_id +
                ", address='" + address + '\'' +
                ", notes='" + notes + '\'' +
                ", status='" + status + '\'' +
                ", item=" + item +
                ", user=" + user +
                ", weight=" + weight +
                ", total_price=" + total_price +
                '}';
    }
}

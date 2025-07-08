package com.example.group_test.model;

public class RecyclableItems {
    private int item_id;
    private String item_name;
    private Float price_per_kg;

    public RecyclableItems() {
    }

    public RecyclableItems(int item_id, String item_name, Float price_per_kg) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.price_per_kg = price_per_kg;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Float getPrice_per_kg() {
        return price_per_kg;
    }

    public void setPrice_per_kg(Float price_per_kg) {
        this.price_per_kg = price_per_kg;
    }

    @Override
    public String toString() {
        return item_name + " - RM " + String.format("%.2f", price_per_kg);
    }
}

package com.outsidehacks.outsideeats.model;

/**
 * Created by marcochin on 7/23/16.
 */
public class VendorMenuItem {
    private String mName;
    private String mPrice;
    private int mQuantity;

    public VendorMenuItem(String name, String price) {
        this.mName = name;
        this.mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }
}

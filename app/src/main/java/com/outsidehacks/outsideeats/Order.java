package com.outsidehacks.outsideeats;

import java.util.HashMap;

/**
 * Created by karan.mehta on 7/23/16.
 */
public class Order {
    String orderId;
    boolean delivered;
    HashMap<String, Integer> items;
    double latitude;
    double longitude;
    long userId;
    long vendorId;
    String vendorName;
    long deliveryGuyId;

    Order() {

    }

    Order(boolean delivered, HashMap<String, Integer> items, double latitude, double longitude, long userId, long vendorId, String vendorName) {
        this.delivered = delivered;
        this.items = items;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    public String toString() {
        return orderId + " " + delivered + " " + items.toString() + " " + latitude + " " + longitude + " " + userId + " " + vendorId + " " + vendorName;
    }
}

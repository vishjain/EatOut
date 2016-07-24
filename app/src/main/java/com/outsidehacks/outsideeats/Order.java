package com.outsidehacks.outsideeats;

import java.util.HashMap;

/**
 * Created by karan.mehta on 7/23/16.
 */
public class Order {
    long orderId;
    boolean delivered;
    HashMap<String, String> items;
    double latitude;
    double longitude;
    long userId;
    long vendorId;
    String vendorName;
}

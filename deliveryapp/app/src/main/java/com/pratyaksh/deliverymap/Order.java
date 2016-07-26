package com.pratyaksh.deliverymap;

import java.util.HashMap;

/**
 * Created by pratyaksh on 7/24/16.
 */
public class Order {
    String orderId;
    boolean delivered;
    HashMap<String, String> items;
    double latitude;
    double longitude;
    long userId;
    long vendorId;
    String vendorName;
    long deliveryGuyId;
}

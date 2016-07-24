package com.outsidehacks.outsideeats.utils;

import java.util.Locale;

/**
 * Created by marcochin on 7/24/16.
 */
public class NumberUtils {
    public static String floatToDollarString(float price){
        return String.format(Locale.US, "$%.2f", price);
    }

    public static float dollarStringToFloat(String price){
        String[] splitString = price.split("\\$");
        return Float.parseFloat(splitString[1]);
    }


}

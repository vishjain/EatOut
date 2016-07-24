package com.outsidehacks.outsideeats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcochin on 7/23/16.
 */
public class VendorMenu {

    public static List<VendorMenuItem> getMenu(int vendorId){
        switch (vendorId){
            case Vendor.VENDOR_4505_MEATS:
                return get4505MeatsMenu();
            case Vendor.VENDOR_AZALINAS:
                return getAzalinasMenu();
            case Vendor.VENDOR_BEAST_AND_THE_HARE:
                return getBeastAndTheHareMenu();
            case Vendor.VENDOR_CHARLES_CHOCOLATES:
                return getCharlesChocolatesMenu();
            case Vendor.VENDOR_ESCAPE_FROM_NEW_YORK:
                return getEscapeFromNewYorkPizzaMenu();
//            case Vendor.VENDOR_SMOOTHIE_DETOUR:
//                return getSmoothieDetourMenu();
            case Vendor.VENDOR_WOODHOUSE_FISH_CO:
                return getWoodhouseFishCoMenu();
            case Vendor.VENDOR_NOMBE:
                return getNombeMenu();
        }
        return null;
    }

    private static List<VendorMenuItem> get4505MeatsMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Cheeseburger", "$9.00"));
        menu.add(new VendorMenuItem("Spicy Chimichurri Fries", "$6.00"));
        menu.add(new VendorMenuItem("Chicharrones", "$8.00"));
        return menu;
    }

    private static List<VendorMenuItem> getAzalinasMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Malaysian Chicken Curry Nachos", "$8.50"));
        menu.add(new VendorMenuItem("Malaysian Peanut Tofu Braised Nachos", "$7.50"));
        return menu;
    }

    private static List<VendorMenuItem> getBeastAndTheHareMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Loaded Baked Potatoes: The Basic", "$8.00"));
        menu.add(new VendorMenuItem("Loaded Baked Potatoes: The LightWeight", "$7.50"));
        menu.add(new VendorMenuItem("Loaded Baked Potatoes: The Hella All Out", "$10.00"));
        return menu;
    }

    private static List<VendorMenuItem> getCharlesChocolatesMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Iced & Hot Thai Tea", "$5.00"));
        menu.add(new VendorMenuItem("Frozen & Hot Chocolate", "$6.50"));
        menu.add(new VendorMenuItem("S'mores", "$4.00"));
        menu.add(new VendorMenuItem("Fudge Brownies", "$5.50"));
        menu.add(new VendorMenuItem("Ice Cream Sundaes", "$7.00"));
        menu.add(new VendorMenuItem("Caramel Chocolate Cookies", "$4.50"));
        return menu;
    }

    private static List<VendorMenuItem> getEscapeFromNewYorkPizzaMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Pizza By The Slice: Cheese", "$4.00"));
        menu.add(new VendorMenuItem("Pizza By The Slice: Pepperoni", "$6.00"));
        menu.add(new VendorMenuItem("Pizza By The Slice: Spinach Caprese", "$5.50"));
        menu.add(new VendorMenuItem("Pizza By The Slice: Pesto Potato & Roasted Garlic", "$5.00"));
        return menu;
    }

    private static List<VendorMenuItem> getSmoothieDetourMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Banana Smoothie", "$7.00"));
        menu.add(new VendorMenuItem("Strawberry Smoothie", "$7.50"));
        menu.add(new VendorMenuItem("Blueberry Smoothie", "$7.00"));
        menu.add(new VendorMenuItem("Kale Smoothie", "$8.00"));
        menu.add(new VendorMenuItem("Spinach Smoothie", "$6.00"));
        return menu;
    }

    private static List<VendorMenuItem> getNombeMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Shoyu Ramenburgers", "$9.50"));
        menu.add(new VendorMenuItem("Miso Ramenburgers", "$8.50"));
        menu.add(new VendorMenuItem("Veggie Ramenburgers", "$8.50"));
        menu.add(new VendorMenuItem("Avocado Fries with Serrano Chile Aioli", "$7.00"));
        menu.add(new VendorMenuItem("Japanese Hot Fries", "$8.00"));
        menu.add(new VendorMenuItem("Sushi Burrito - Spicy Tuna", "$10.00"));
        menu.add(new VendorMenuItem("Sushi Burrito - Futomaki", "$9.00"));
        return menu;
    }

    private static List<VendorMenuItem> getWoodhouseFishCoMenu(){
        List<VendorMenuItem> menu  = new ArrayList<>();
        menu.add(new VendorMenuItem("Lobster Roll", "$14.00"));
        menu.add(new VendorMenuItem("Crab Roll", "$13.00"));
        menu.add(new VendorMenuItem("Clam Chowder in a Cup", "$9.00"));
        menu.add(new VendorMenuItem("Clam Chowder in a Cup", "$10.00"));
        menu.add(new VendorMenuItem("Raw and BBQ Oysters", "$12.00"));
        return menu;
    }
}

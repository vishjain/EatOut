package com.outsidehacks.outsideeats.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marcochin on 7/23/16.
 */
public class Vendor implements Parcelable {
    public static final int VENDOR_4505_MEATS = 1;
    public static final int VENDOR_AZALINAS = 2;
    public static final int VENDOR_BEAST_AND_THE_HARE = 3;
    public static final int VENDOR_CHARLES_CHOCOLATES = 4;
    public static final int VENDOR_ESCAPE_FROM_NEW_YORK = 5;
    public static final int VENDOR_NOMBE = 6;
    public static final int VENDOR_WOODHOUSE_FISH_CO = 7;

    private int mVendorId;
    private String mVendorName;
    private String mVendorImgUrl;

    public Vendor(int vendorId, String vendorName, String vendorImgUrl) {
        this.mVendorId = vendorId;
        this.mVendorName = vendorName;
        this.mVendorImgUrl = vendorImgUrl;
    }

    public int getVendorId() {
        return mVendorId;
    }

    public void setVendorId(int vendorId) {
        this.mVendorId = vendorId;
    }

    public String getVendorName() {
        return mVendorName;
    }

    public void setVendorName(String vendorName) {
        this.mVendorName = vendorName;
    }

    public String getVendorImgUrl() {
        return mVendorImgUrl;
    }

    public void setVendorImgUrl(String vendorImgUrl) {
        this.mVendorImgUrl = vendorImgUrl;
    }

    protected Vendor(Parcel in) {
        mVendorId = in.readInt();
        mVendorName = in.readString();
        mVendorImgUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mVendorId);
        dest.writeString(mVendorName);
        dest.writeString(mVendorImgUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Vendor> CREATOR = new Parcelable.Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel in) {
            return new Vendor(in);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };
}
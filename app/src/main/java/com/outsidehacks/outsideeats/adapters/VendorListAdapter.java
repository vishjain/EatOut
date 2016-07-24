package com.outsidehacks.outsideeats.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.outsidehacks.outsideeats.R;
import com.outsidehacks.outsideeats.model.Vendor;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by marcochin on 7/23/16.
 */
public class VendorListAdapter extends BaseAdapter {
    private List<Vendor> mVendorList;
    private Context mContext;

    public VendorListAdapter(Context context, List<Vendor> vendorList) {
        mContext = context;
        mVendorList = vendorList;
    }

    public int getCount() {
        return mVendorList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        VendorViewHolder vendorViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.restaurant_list_item, parent, false);

            vendorViewHolder = new VendorViewHolder();
            vendorViewHolder.mVendorName = (TextView) convertView.findViewById(R.id.vendor_name);
            vendorViewHolder.mVendorImage = (ImageView) convertView.findViewById(R.id.vendor_bg);

            convertView.setTag(vendorViewHolder);

        } else {
            vendorViewHolder = (VendorViewHolder) convertView.getTag();
        }

        vendorViewHolder.mVendorName.setText(mVendorList.get(position).getVendorName());

        Picasso.with(mContext)
                .load(mVendorList.get(position).getVendorImgUrl())
                .into(vendorViewHolder.mVendorImage);

        return convertView;
    }

    static class VendorViewHolder {
        TextView mVendorName;
        ImageView mVendorImage;
    }
}
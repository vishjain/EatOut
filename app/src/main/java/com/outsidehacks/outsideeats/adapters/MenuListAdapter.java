package com.outsidehacks.outsideeats.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.outsidehacks.outsideeats.R;
import com.outsidehacks.outsideeats.model.VendorMenuItem;

import java.util.List;
import java.util.Locale;

/**
 * Created by marcochin on 7/23/16.
 */
public class MenuListAdapter extends BaseAdapter{

    private Context mContext;
    private List<VendorMenuItem> mMenuItemList;
    private OnQuantityChangedListener mOnQuantityChangedListener;

    public interface OnQuantityChangedListener{
        void onMinusClick(int position);
        void onPlusClick(int position);
    }

    public MenuListAdapter(Context context, List<VendorMenuItem> menuList) {
        mContext = context;
        mMenuItemList = menuList;
    }

    @Override
    public int getCount() {
        return mMenuItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("all")
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        MenuViewHolder menuViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.menu_list_item, viewGroup, false);

            menuViewHolder = new MenuViewHolder();
            menuViewHolder.mMenuItemName = (TextView) convertView.findViewById(R.id.menu_item_name);
            menuViewHolder.mMenuItemPrice = (TextView) convertView.findViewById(R.id.menu_item_price);
            menuViewHolder.mMenuItemQuantity = (TextView) convertView.findViewById(R.id.menu_item_quantity);
            menuViewHolder.mMenuItemMinus = convertView.findViewById(R.id.menu_item_minus);
            menuViewHolder.mMenuItemPlus = convertView.findViewById(R.id.menu_item_plus);

            convertView.setTag(menuViewHolder);

        } else {
            menuViewHolder = (MenuViewHolder) convertView.getTag();
        }

        VendorMenuItem vendorMenuItem = mMenuItemList.get(position);

        menuViewHolder.mMenuItemName.setText(vendorMenuItem.getName());
        menuViewHolder.mMenuItemPrice.setText(vendorMenuItem.getPrice());

        if(vendorMenuItem.getQuantity() > 0) {
            menuViewHolder.mMenuItemQuantity.setVisibility(View.VISIBLE);
            menuViewHolder.mMenuItemQuantity.setText(String.format(Locale.US, "%d", vendorMenuItem.getQuantity()));
        }else{
            menuViewHolder.mMenuItemQuantity.setVisibility(View.INVISIBLE);
        }

        menuViewHolder.mMenuItemMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnQuantityChangedListener != null){
                    mOnQuantityChangedListener.onMinusClick(position);
                }
            }
        });

        menuViewHolder.mMenuItemPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnQuantityChangedListener != null){
                    mOnQuantityChangedListener.onPlusClick(position);
                }
            }
        });

        return convertView;
    }

    public void setOnQuantityChangedListener(OnQuantityChangedListener listener){
        mOnQuantityChangedListener = listener;
    }

    static class MenuViewHolder {
        TextView mMenuItemName;
        TextView mMenuItemPrice;
        TextView mMenuItemQuantity;
        View mMenuItemMinus;
        View mMenuItemPlus;
    }
}

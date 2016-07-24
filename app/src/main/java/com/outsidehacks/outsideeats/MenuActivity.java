package com.outsidehacks.outsideeats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.outsidehacks.outsideeats.adapters.MenuListAdapter;
import com.outsidehacks.outsideeats.model.Vendor;
import com.outsidehacks.outsideeats.model.VendorMenu;
import com.outsidehacks.outsideeats.model.VendorMenuItem;
import com.outsidehacks.outsideeats.utils.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by marcochin on 7/23/16.
 */
public class MenuActivity extends AppCompatActivity implements MenuListAdapter.OnQuantityChangedListener, View.OnClickListener {
    public static final String VENDOR = "vendor";
    private List<VendorMenuItem> mVendorMenuItemList;
    private MenuListAdapter mMenuListAdapter;

    private TextView mTotalPriceText;
    private TextView mTotalItemsText;

    private Vendor mVendor;
    private int mTotalItems;
    private float mTotalPrice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        initViews();
    }

    private void initViews() {
        mTotalPriceText = (TextView) findViewById(R.id.menu_total_price_text);
        mTotalItemsText = (TextView) findViewById(R.id.menu_num_items_text);
        ListView menuList = (ListView) findViewById(R.id.menu_list);
        TextView vendorNameText = (TextView) findViewById(R.id.menu_vendor_name);
        findViewById(R.id.menu_checkout_button).setOnClickListener(this);

        mVendor = getIntent().getParcelableExtra(VENDOR);
        vendorNameText.setText(mVendor.getVendorName());
        mVendorMenuItemList = VendorMenu.getMenu(mVendor.getVendorId());
        mMenuListAdapter = new MenuListAdapter(this, mVendorMenuItemList);
        mMenuListAdapter.setOnQuantityChangedListener(this);

        menuList.setAdapter(mMenuListAdapter);
    }

    @Override // MenuListAdapter.OnQuantityChangedListener
    public void onMinusClick(int position) {
        VendorMenuItem vendorMenuItem = mVendorMenuItemList.get(position);
        if (vendorMenuItem.getQuantity() > 0) {
            vendorMenuItem.setQuantity(vendorMenuItem.getQuantity() - 1);

            mTotalItemsText.setText(String.format(Locale.US, "%d Items", --mTotalItems));

            mTotalPrice -= NumberUtils.dollarStringToFloat(vendorMenuItem.getPrice());
            mTotalPriceText.setText(String.format(Locale.US, "Total: $%.2f", mTotalPrice));
        }
        mMenuListAdapter.notifyDataSetChanged();
    }

    @Override // MenuListAdapter.OnQuantityChangedListener
    public void onPlusClick(int position) {
        VendorMenuItem vendorMenuItem = mVendorMenuItemList.get(position);
        vendorMenuItem.setQuantity(vendorMenuItem.getQuantity() + 1);

        mTotalItemsText.setText(String.format(Locale.US, "%d Items", ++mTotalItems));

        mTotalPrice += NumberUtils.dollarStringToFloat(vendorMenuItem.getPrice());
        mTotalPriceText.setText(String.format(Locale.US, "Total: $%.2f", mTotalPrice));

        mMenuListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_checkout_button:
                goToCheckoutActivity();
                break;
        }
    }

    private void goToCheckoutActivity() {
        if (mTotalItems > 0) {
            HashMap<String, Integer> checkoutMap = new HashMap<>();
            for (VendorMenuItem vendorMenuItem : mVendorMenuItemList) {
                if (vendorMenuItem.getQuantity() > 0) {
                    checkoutMap.put(vendorMenuItem.getName(), vendorMenuItem.getQuantity());
                }
            }

            checkoutMap.put("Total", (int) (mTotalPrice * 100));

            Intent goToCheckoutActivity = new Intent(this, CheckOutActivity.class);
            goToCheckoutActivity.putExtra(CheckOutActivity.SHOPPING_CART, checkoutMap);
            goToCheckoutActivity.putExtra(VENDOR, mVendor);
            startActivity(goToCheckoutActivity);
        } else {
            Toast.makeText(this, "You didn't order any items!", Toast.LENGTH_LONG).show();
        }
    }
}

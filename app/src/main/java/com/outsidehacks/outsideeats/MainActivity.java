package com.outsidehacks.outsideeats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.outsidehacks.outsideeats.adapters.VendorListAdapter;
import com.outsidehacks.outsideeats.model.Vendor;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView mVendorListView;
    List<Vendor> mVendors;
    private AlertDialog mSuccessfulDeliveryAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVendorListView = (ListView) findViewById(R.id.list);
        mVendorListView.setAdapter(new VendorListAdapter(this, getVendors()));
        mVendorListView.setOnItemClickListener(this);

        // Show Delivery Successful Dialog after the app finishes a delivery and goes back to main screen
        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState == null && bundle != null && bundle.getBoolean("ShowThanks") == true) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage("Delivery Successful!")
                    .setCancelable(false)
                    .setNegativeButton("Thank you!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            // create alert dialog
            mSuccessfulDeliveryAlertDialog = alertDialogBuilder.create();
            // show it
            mSuccessfulDeliveryAlertDialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent goToMenuActivity = new Intent(this, MenuActivity.class);
        goToMenuActivity.putExtra(MenuActivity.VENDOR, mVendors.get(position));
        startActivity(goToMenuActivity);
    }

    private List<Vendor> getVendors() {
        mVendors = new ArrayList<>();

        mVendors.add(new Vendor(Vendor.VENDOR_4505_MEATS, "4505 Meats",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69507/custom9b7fe457c0f0602a50c7046626504924.jpg"));

        mVendors.add(new Vendor(Vendor.VENDOR_AZALINAS, "Azalina's",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69513/custom7015853e7ace6f5c6dac150514202ad0.jpg"));

        mVendors.add(new Vendor(Vendor.VENDOR_BEAST_AND_THE_HARE, "Beast and the Hare",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69517/custom008a5b739c689dd1b121e56f3da29e0b.jpg"));

        mVendors.add(new Vendor(Vendor.VENDOR_CHARLES_CHOCOLATES, "Charles Chocolates",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69689/custom08af232373f259978fefd261f52cc60a.jpg"));

        mVendors.add(new Vendor(Vendor.VENDOR_ESCAPE_FROM_NEW_YORK, "Escape From New York Pizza",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69543/custom380a54565d1e1a30c045775771d18ef3.jpg"));

        mVendors.add(new Vendor(Vendor.VENDOR_NOMBE, "Nombe",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69579/custom94d7109b50fbf0363ec0bdb84271b00f.jpg"));

        mVendors.add(new Vendor(Vendor.VENDOR_WOODHOUSE_FISH_CO, "Woodhouse Fish Co.",
                "http://s3.amazonaws.com/dostuff-production/band_alternate_photo/custom_photos/69657/custom93bc4e3c9c05c1ac98c63e4f3acab78b.jpg"));

        return mVendors;
    }
}

package com.outsidehacks.outsideeats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.outsidehacks.outsideeats.model.Vendor;
import com.outsidehacks.outsideeats.utils.DeliveryTrackingActivity;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by marcochin on 7/24/16.
 */
public class CheckOutActivity extends AppCompatActivity {
    public static final String SHOPPING_CART = "ShoppingCart";

    int totalPrice;
    private DatabaseReference mDatabase;
    TextView getMeFoodTextView;
    EditText fullNameEditText;
    EditText phoneNumEditText;
    EditText creditCardEditText;
    EditText expMonthEditText;
    EditText expYearEditText;
    EditText cvvEditText;

    double minDistance;
    Location vendorLocation;
    Location deliveryGuyLocation;
    Order newOrder;
    HashMap<String, Integer> shoppingCart;
    String phoneNum;

    private ProgressDialog mProgressDialog;

    Vendor vendor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initViews();
    }

    View.OnClickListener getMeFoodOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("Get Me Food", "Checkout process started.");
            String fullName = fullNameEditText.getText().toString().trim();
            if (fullName.length() == 0) {
                Toast.makeText(getApplicationContext(), "Full name cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            phoneNum = phoneNumEditText.getText().toString().trim();
            if (phoneNum.length() != 10 || !phoneNum.matches("[0-9]+")) {
                Toast.makeText(getApplicationContext(), "Phone Num is improper.", Toast.LENGTH_SHORT).show();
                return;
            }

            String creditCard = creditCardEditText.getText().toString().trim();
            if (creditCard.length() == 0) {
                Toast.makeText(getApplicationContext(), "Credit card number cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String expMonth = expMonthEditText.getText().toString().trim();
            String expYear = expYearEditText.getText().toString().trim();
            if (expMonth.length() == 0 || expYear.length() == 0) {
                Toast.makeText(getApplicationContext(), "Date cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String cvvNum = cvvEditText.getText().toString().trim();
            if (cvvNum.length() == 0) {
                Toast.makeText(getApplicationContext(), "CVV cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Card card = new Card(creditCard, Integer.parseInt(expMonth), Integer.parseInt(expYear), cvvNum);
            if (!card.validateCard()) {
                Log.e("Get Me Food", "Card validation failed");
                Toast.makeText(getApplicationContext(), "Card Details are not valid!", Toast.LENGTH_LONG).show();
                return;
            }


            try {
                mProgressDialog.show();

                Stripe stripe = new Stripe("pk_test_1orfOejVOwJ2A4gbAQcladUm");
                stripe.createToken(card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Log.e("PayButton", "Error in receiving token");
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Authentication Failed! Please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    @Override
                    public void onSuccess(Token token) {
                        Log.d("Get Me Food", "Token Rcvd: " + token.getId());
                        callBackEndWithToken(token);
                    }
                });
            } catch (AuthenticationException e) {
                Log.e("AuthFailed", e.getMessage());
                Toast.makeText(getApplicationContext(), "Authentication Failed! Please try again.", Toast.LENGTH_LONG).show();
                return;
            }

        }
    };


    void callBackEndWithToken(final Token token) {
        Log.d("Get Me Food", "Sending Token to Backend");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String stripePaymentURL = "http://52.33.48.229:8080/StripePayment/PaymentServlet";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, stripePaymentURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.get("response").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Payment Completed Successfully!\nFinding Deliveries for you!", Toast.LENGTH_LONG).show();
                        vendor = getIntent().getParcelableExtra(MenuActivity.VENDOR);

                        double HARDCODED_LAT = 37.767403;
                        double HARDCODED_LONG = -122.489515;
                        newOrder = new Order(false, shoppingCart, HARDCODED_LAT, HARDCODED_LONG, Long.parseLong(phoneNum), vendor.getVendorId(), vendor.getVendorName());
                        findDeliveryGuy(vendor);

//                        Intent goToDeliveryActivity = new Intent(getApplicationContext(), DeliveryActivity.class);
//                        goToDeliveryActivity.putExtra(MenuActivity.VENDOR, vendor);
//                        startActivity(goToDeliveryActivity);

                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), jsonObject.get("response").toString(), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error in Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stripeToken", token.getId());
                params.put("amount", String.valueOf(totalPrice));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    void findDeliveryGuy(Vendor vendor) {
        minDistance = Double.MAX_VALUE;
        mDatabase.child("vendors").removeEventListener(childEventListenerForVendorDistance);
        mDatabase.child("vendors").orderByKey().equalTo(String.valueOf(vendor.getVendorId())).addChildEventListener(childEventListenerForVendorDistance);

    }

    ChildEventListener childEventListenerForVendorDistance = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            vendorLocation = new Location("");
            vendorLocation.setLatitude((double) dataSnapshot.child("latitude").getValue());
            vendorLocation.setLongitude((double) dataSnapshot.child("longitude").getValue());
            Log.d("Vendor Located", vendorLocation.getLatitude() + "  " + vendorLocation.getLongitude());
            mDatabase.child("delivery").removeEventListener(childEventListenerForDelivery);
            mDatabase.child("delivery").orderByKey().addChildEventListener(childEventListenerForDelivery);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListenerForDelivery = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Log.d("Get Me Food", "Getting a list of free-delivery boys: " + dataSnapshot.getKey());
            boolean deliveryGuyFound = false;
            double distance = Double.MAX_VALUE;
            String name = null;

            if (dataSnapshot.getKey().equals("free")) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("Get Me Food", "DG1 Key: " + dataSnapshot1.getKey());
                    deliveryGuyLocation = new Location("");
                    deliveryGuyLocation.setLatitude((Double) dataSnapshot1.child("latitude").getValue());
                    deliveryGuyLocation.setLongitude((Double) dataSnapshot1.child("longitude").getValue());
                    distance = vendorLocation.distanceTo(deliveryGuyLocation);
                    if (distance < minDistance) {
                        deliveryGuyFound = true;
                        minDistance = distance;
                        name = (String) dataSnapshot1.child("name").getValue();
                        newOrder.deliveryGuyId = Long.parseLong(dataSnapshot1.getKey());
                    }
                }

                if (deliveryGuyFound) {
                    Log.d("Get Me Food", "Delivery guy assigned to: " + name + " " + newOrder.deliveryGuyId + "  distance: " + minDistance);
                    Toast.makeText(getApplicationContext(), "Your delivery is scheduled. " + name + " is on the way!", Toast.LENGTH_LONG).show();
                    mDatabase.child("orders").push().setValue(newOrder);
                }

                mDatabase.child("delivery").removeEventListener(childEventListenerForDelivery);
                mProgressDialog.dismiss();

                // Go to Main Activity after successful delivery
                Intent goToDeliveryTrackingActivity = new Intent(CheckOutActivity.this, DeliveryTrackingActivity.class);
                goToDeliveryTrackingActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                goToDeliveryTrackingActivity.putExtra("vendorLocation", vendorLocation);
                goToDeliveryTrackingActivity.putExtra("vendorId", vendor.getVendorId());
                goToDeliveryTrackingActivity.putExtra("deliveryGuyId", newOrder.deliveryGuyId);
                startActivity(goToDeliveryTrackingActivity);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void initViews() {
        getMeFoodTextView = (TextView) findViewById(R.id.getMeFoodTextView);
        getMeFoodTextView.setOnClickListener(getMeFoodOnClickListener);

        fullNameEditText = (EditText) findViewById(R.id.checkout_full_name);
        phoneNumEditText = (EditText) findViewById(R.id.checkout_phone_number);
        creditCardEditText = (EditText) findViewById(R.id.checkout_credit_card_number);
        expMonthEditText = (EditText) findViewById(R.id.checkout_expiration_month);
        expYearEditText = (EditText) findViewById(R.id.checkout_expiration_year);
        cvvEditText = (EditText) findViewById(R.id.checkout_cvv);

        LinearLayout checkoutItemContainer = (LinearLayout) findViewById(R.id.checkout_item_container);
        shoppingCart = (HashMap) getIntent().getSerializableExtra(SHOPPING_CART);

        View totalPriceView = null;

        for (Map.Entry shopppingCartItem : shoppingCart.entrySet()) {
            String itemName = (String) shopppingCartItem.getKey();
            Integer itemQuantity = (Integer) shopppingCartItem.getValue();

            View v = LayoutInflater.from(this).inflate(R.layout.activity_checkout_item, checkoutItemContainer, false);
            TextView checkoutItemText = (TextView) v.findViewById(R.id.checkout_item_name);
            TextView checkoutQuantityText = (TextView) v.findViewById(R.id.checkout_item_quantity);
            checkoutItemText.setText(itemName);

            if (!itemName.equals("Total")) {
                checkoutQuantityText.setText(String.format(Locale.US, "%d", itemQuantity));
                checkoutItemContainer.addView(v);
            } else {
                checkoutItemText.setText(itemName);
                totalPrice = itemQuantity;
                checkoutQuantityText.setText(String.format(Locale.US, "$%.2f", (float) itemQuantity / 100));
                totalPriceView = v;
            }
        }

        // Add totalView at the end
        checkoutItemContainer.addView(totalPriceView);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Processing Order...");
    }
}

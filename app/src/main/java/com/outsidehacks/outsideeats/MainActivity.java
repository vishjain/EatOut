package com.outsidehacks.outsideeats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addChildEventListener(childEventListener);

    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                Order order = new Order();
                order.delivered = (boolean) dataSnapshot1.child("delivered").getValue();
                order.items = (HashMap<String, String>) dataSnapshot1.child("items").getValue();
                Log.d("OrderList", order.delivered + " " + order.items.toString());
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
            Log.d("DATABASE ERROR", databaseError.getMessage() + "  " + databaseError.getDetails());
        }
    };
}

package com.example.android.ticketing_2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<String> bookedSeatsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        //instantiate custom adapter
        final MyCustomAdapter adapter = new MyCustomAdapter(bookedSeatsList, this);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mBookedSeatsRef = mDatabase.child("bookedSeats");

        mBookedSeatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String seatInfo = dataSnapshot.getKey();
                if (dataSnapshot.getValue().equals(currentUserId)) {

                    String ticketString = "Gate " + SeatStringFunctions.getGate(seatInfo)
                            + " \nRow " + SeatStringFunctions.getRow(seatInfo)
                            + " Seat " + SeatStringFunctions.getSeat(seatInfo)
                            + " \nPrice " + SeatStringFunctions.getPrice(seatInfo) + " €";

                    bookedSeatsList.add(ticketString);
                    //Log.d("Petros", seatInfo);
                    ListView lView = findViewById(R.id.my_listview);
                    lView.setAdapter(adapter);
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
        });

        //Log.d("Petros", "list -->" + bookedSeatsList.get(0));


        //handle listview and assign adapter
        //ListView lView = findViewById(R.id.my_listview);
        //lView.setAdapter(adapter);
    }
}

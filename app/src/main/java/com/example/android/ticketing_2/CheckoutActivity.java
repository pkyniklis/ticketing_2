package com.example.android.ticketing_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    ArrayList<String> keysList = new ArrayList<>();

    final Intent myTicketsIntent = new Intent(this, MyTicketsActivity.class);
    //ListView lView = findViewById(R.id.my_listview);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        final Context context = CheckoutActivity.this;

        final Intent myTicketsIntent = new Intent(this, MyTicketsActivity.class);

        //instantiate custom adapter
        final MyCustomAdapter adapter = new MyCustomAdapter(bookedSeatsList, keysList, this);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mBookedSeatsRef = mDatabase.child("bookedSeats");

        mBookedSeatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String seatInfo = dataSnapshot.getKey();
                if (dataSnapshot.getValue().equals(currentUserId)) {

                    keysList.add(seatInfo);

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

        final Button buyButton = findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                int cost = 0;
                for (String seatInfo : keysList) {
                    cost += SeatStringFunctions.getPrice(seatInfo);
                }

                dialogBuilder.setMessage("Pay " + cost + "€ ?");
                dialogBuilder.setCancelable(true);

                dialogBuilder.setPositiveButton(
                        "Pay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                for (String seatString : keysList) {
                                    //Log.d("Petros", seatString + " // ");
                                    final DatabaseReference mSeatRef = mDatabase.child("Seats")
                                            .child("Gate" + SeatStringFunctions.getGate(seatString))
                                            .child("r" + SeatStringFunctions.getRowAndSeat(seatString));
                                    mSeatRef.child("booked").setValue(false);
                                    mSeatRef.child("free").setValue(false);
                                    mDatabase.child("bookedSeats").child(seatString).removeValue();
                                    mDatabase.child("tickets").child(seatString).setValue(currentUserId);
                                }
                                startActivity(myTicketsIntent);

                            }
                        });

                dialogBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = dialogBuilder.create();
                alert.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.checkout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_myTickets) {
            final Intent myTicketsIntent = new Intent(this, MyTicketsActivity.class);
            startActivity(myTicketsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

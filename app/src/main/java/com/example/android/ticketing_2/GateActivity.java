package com.example.android.ticketing_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GateActivity extends AppCompatActivity {

    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Gate gate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);

        // see https://github.com/alexvasilkov/GestureViews
        GestureView gestureView = findViewById(R.id.gate_gestureView);
        gestureView.getController().getSettings()
                .setMaxZoom(10f)
                .setDoubleTapZoom(-1f) // Falls back to max zoom level
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(false)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(true)
                .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);

        //Load stub images. These will change later.
        final ImageView backImageView = findViewById(R.id.gate_back);
        final ImageView frontImageView = findViewById(R.id.gate_front);
        final ImageView viewImageView = findViewById(R.id.gate_view);

        // Get gateNo from the intent
        Intent intent = getIntent();
        final String gateNo = intent.getStringExtra("Gate");
        final String gatePrice = intent.getStringExtra("Price");
        //Toast.makeText(GateActivity.this, "Gate "+gateNo, Toast.LENGTH_SHORT).show();

        setTitle("Gate " + gateNo);

        // Using the gateNo, load the correct imageViews from the resources.
        String backGateName = "gate" + gateNo + "_back";
        String frontGateName = "gate" + gateNo + "_front";
        String viewName = "view" + gateNo;
        final Context context = backImageView.getContext();
        int backID = context.getResources().getIdentifier(backGateName, "drawable", context.getPackageName());
        int frontID = context.getResources().getIdentifier(frontGateName, "drawable", context.getPackageName());
        int viewID = context.getResources().getIdentifier(viewName, "drawable", context.getPackageName());
        backImageView.setImageResource(backID);
        frontImageView.setImageResource(frontID);
        viewImageView.setImageResource(viewID);

        final ArrayList<Seat> seatsList = new ArrayList<>();
        // see https://github.com/devsideal/VectorChildFinder
        final VectorChildFinder vector = new VectorChildFinder(this, frontID, frontImageView);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mSeatsGateRef = mDatabase.child("Seats").child("Gate" + gateNo);
        final DatabaseReference mGateRef = mDatabase.child("Gates").child(gateNo);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gate = dataSnapshot.child("Gates").child(gateNo).getValue(Gate.class);
                Log.d("Petros", "gate" + gate.getGateNo());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mSeatsGateRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Seat seat = dataSnapshot.getValue(Seat.class);
                //Log.d("Petros", seat.getUserId());
                seatsList.add(seat);

                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);

                if (seat.isFree() && seat.isBooked() && seat.getUserId().equals(currentUserId)) {
                    seatPath.setFillColor(0xfff9a825); //orange
                }
                if (seat.isFree() && !seat.isBooked()) {
                    seatPath.setFillColor(0xff64dd17); //green
                }
                if (!seat.isFree() || (seat.isBooked() && !seat.getUserId().equals(currentUserId))) {
                    seatPath.setFillColor(0xff9e9e9e); //gray
                }
                frontImageView.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Seat seat = dataSnapshot.getValue(Seat.class);
                updateSeatsList(seatsList, seat);

                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                //seatsList.update(seatPathName, seat);
                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);

                if (seat.isFree() && seat.isBooked() && seat.getUserId().equals(currentUserId)) {
                    seatPath.setFillColor(0xfff9a825); //orange
                }
                if (seat.isFree() && !seat.isBooked()) {
                    seatPath.setFillColor(0xff64dd17); //green
                }
                if (!seat.isFree() || (seat.isBooked() && !seat.getUserId().equals(currentUserId))) {
                    seatPath.setFillColor(0xff9e9e9e); //gray
                }
                frontImageView.invalidate();
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


        backImageView.setOnTouchListener(new View.OnTouchListener() {
            final int DOUBLE_TAP_DURATION = 500;
            long tapTime = System.currentTimeMillis();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                final int x = (int) event.getX();
                final int y = (int) event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    // if double tap has been detected
                    if (System.currentTimeMillis() - tapTime < DOUBLE_TAP_DURATION) {
                        backImageView.setDrawingCacheEnabled(true);
                        Bitmap hotspots = Bitmap.createBitmap(backImageView.getDrawingCache());
                        backImageView.setDrawingCacheEnabled(false);
                        int touchColor = hotspots.getPixel(x, y);

                        // Loop through all seats and find the one with the same color
                        for (Seat seat : seatsList) {
                            final Seat currentSeat = seat;
                            int seatColor = Color.parseColor(seat.getColor());

                            if (touchColor == seatColor) {
                                boolean seatIsAvailable = seat.isFree() && (!seat.isBooked());
                                if (seatIsAvailable) {

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                    dialogBuilder.setMessage(
                                            "Row " + seat.getRow() +
                                                    "  Seat " + seat.getCol() + "\n" +
                                                    "Price : " + gatePrice + " €");
                                    dialogBuilder.setCancelable(true);

                                    dialogBuilder.setPositiveButton(
                                            "Add to basket",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    Log.d("Petros", "add seat");
                                                    String seatPathName = "r" + currentSeat.getRow() + "s" + currentSeat.getCol();
                                                    currentSeat.setBooked(true);
                                                    //Log.d("Petros", user.getUid());
                                                    currentSeat.setUserId(currentUserId);
                                                    mSeatsGateRef.child(seatPathName).setValue(currentSeat);
                                                    if (gate != null) {
                                                        gate.decreaseFreeSeats();
                                                        mGateRef.setValue(gate);
                                                        mDatabase.child("bookedSeats")
                                                                .child("g" + gateNo + seatPathName + "p" + gate.getPrice())
                                                                .setValue(currentUserId);
                                                    }

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
                            }
                        }
                    }
                    tapTime = System.currentTimeMillis();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gate_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_checkout) {
            final Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
            startActivity(checkoutIntent);
            return true;
        }

        if (item.getItemId() == R.id.action_myTickets) {
            final Intent myTicketsIntent = new Intent(this, MyTicketsActivity.class);
            startActivity(myTicketsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateSeatsList(ArrayList<Seat> seatsList, Seat seat) {
        for (Seat s : seatsList) {
            if (seat.getRow() == s.getRow() && seat.getCol() == s.getCol()) {
                s.setFree(seat.isFree());
                s.setBooked(seat.isBooked());
            }
        }
    }
}

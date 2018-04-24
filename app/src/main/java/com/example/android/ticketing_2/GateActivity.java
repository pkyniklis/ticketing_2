package com.example.android.ticketing_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GateActivity extends AppCompatActivity {

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
        String gateNo = intent.getStringExtra("Gate");
        //Toast.makeText(GateActivity.this, "Gate "+gateNo, Toast.LENGTH_SHORT).show();

        setTitle("Gate " + gateNo);

        // Using the gateNo, load the correct imageViews from the resources.
        String backGateName = "gate" + gateNo + "_back";
        String frontGateName = "gate" + gateNo + "_front";
        String viewName = "view" + gateNo;
        Context context = backImageView.getContext();
        int backID = context.getResources().getIdentifier(backGateName, "drawable", context.getPackageName());
        int frontID = context.getResources().getIdentifier(frontGateName, "drawable", context.getPackageName());
        int viewID = context.getResources().getIdentifier(viewName, "drawable", context.getPackageName());
        backImageView.setImageResource(backID);
        frontImageView.setImageResource(frontID);
        viewImageView.setImageResource(viewID);

        final ArrayList<Seat> seatsList = new ArrayList<>();
        // see https://github.com/devsideal/VectorChildFinder
        final VectorChildFinder vector = new VectorChildFinder(this, frontID, frontImageView);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Seats");
        final DatabaseReference mGateRef = mDatabase.child("Gate" + gateNo);
        mGateRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Seat seat = dataSnapshot.getValue(Seat.class);
                seatsList.add(seat);

                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);

                if (seat.isFree() && seat.isBooked()) seatPath.setFillColor(0xfff9a825); //orange
                if (seat.isFree() && !seat.isBooked()) seatPath.setFillColor(0xff64dd17); //green
                if (!seat.isFree()) seatPath.setFillColor(0xff9e9e9e); //gray

                frontImageView.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Seat seat = dataSnapshot.getValue(Seat.class);
                //seatsList.add(seat);

                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                //seatsList.update(seatPathName, seat);
                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);

                if (seat.isFree() && seat.isBooked()) seatPath.setFillColor(0xfff9a825); //orange
                if (seat.isFree() && !seat.isBooked()) seatPath.setFillColor(0xff64dd17); //green
                if (!seat.isFree()) seatPath.setFillColor(0xff9e9e9e); //gray
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
                        /*
                        for (Seat seat : seatsList) {
                            int seatColor = Color.parseColor(seat.getColor());
                            //if (touchColor == seatColor && seat.isFree()) {
                            if (touchColor == seatColor) {
                                // For this seat, do something (temporarily change color)
                                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                                seat.setFree(!seat.isFree());
                                mGateRef.child(seatPathName).setValue(seat);
                                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);
                                //toggleColor(seatPath);
                                //frontImageView.invalidate();
                            }
                        }
                        */
                        for (Seat seat : seatsList) {
                            int seatColor = Color.parseColor(seat.getColor());

                            if (touchColor == seatColor && seat.isFree()) {

                                //if ( !seat.isBooked() ) {
                                if (seat.isFree()) {
                                    Log.d("Petros", "add seat");
                                    String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                                    //seat.setBooked(true);
                                    seat.setBooked(!seat.isBooked());
                                    mGateRef.child(seatPathName).setValue(seat);
                                    //final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);
                                    //setOrangeColor(seatPath);
                                    //frontImageView.invalidate();
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

    private void toggleAlpha(VectorDrawableCompat.VFullPath seat) {
        if (seat.getFillAlpha() != 128) seat.setFillAlpha(128);
        else seat.setFillAlpha(1);
    }

    private void toggleColor(VectorDrawableCompat.VFullPath seat) {
        if (seat.getFillColor() != 0xff9e9e9e) seat.setFillColor(0xff9e9e9e);
        else seat.setFillColor(0xff64dd17);
    }

    private void setOrangeColor(VectorDrawableCompat.VFullPath seat) {
        seat.setFillColor(0xfff9a825);
    }
}

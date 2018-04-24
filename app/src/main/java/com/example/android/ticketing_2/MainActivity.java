package com.example.android.ticketing_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final ArrayList<Gate> gatesList = new ArrayList<>();

    //final GlobalClass global = (GlobalClass)getApplicationContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Gates");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Gate gate = dataSnapshot.getValue(Gate.class);
                gatesList.add(gate);
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

        // see https://github.com/alexvasilkov/GestureViews
        GestureView gestureView = findViewById(R.id.gestureView);
        gestureView.getController().getSettings()
                .setMaxZoom(10f)
                .setDoubleTapZoom(-1f) // Falls back to max zoom level
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(false)
                .setRotationEnabled(true)
                .setRestrictRotation(false)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(true)
                .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);

        final ImageView backImageView = findViewById(R.id.back);
        final Intent intent = new Intent(this, GateActivity.class);

        backImageView.setOnTouchListener(new View.OnTouchListener() {
            final int DOUBLE_TAP_DURATION = 500;
            long tapTime = System.currentTimeMillis();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                final int x = (int) event.getX();
                final int y = (int) event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    if (System.currentTimeMillis() - tapTime < DOUBLE_TAP_DURATION) {
                        backImageView.setDrawingCacheEnabled(true);
                        Bitmap hotspots = Bitmap.createBitmap(backImageView.getDrawingCache());
                        backImageView.setDrawingCacheEnabled(false);

                        int touchColor = hotspots.getPixel(x, y);
                        //Toast.makeText(MainActivity.this, "Touch color "+ touchColor, Toast.LENGTH_SHORT).show();

                        // Loop through all gates and find the one with the same color
                        for (Gate gate : gatesList) {
                            int gateColor = Color.parseColor(gate.getColor());

                            if (touchColor == gateColor) {
                                final Gate chosenGate = gate;
                                Context context = backImageView.getContext();
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                dialogBuilder.setMessage("Gate " + chosenGate.getGateNo() + "\n" +
                                        "Price : " + chosenGate.getPrice() + " euro \n" +
                                        "Free seats : " + chosenGate.getFreeSeats());
                                dialogBuilder.setCancelable(true);

                                dialogBuilder.setPositiveButton(
                                        "Buy tickets",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                intent.putExtra("Gate", chosenGate.getGateNo());
                                                startActivity(intent);
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
                    tapTime = System.currentTimeMillis();
                }

                return false;
            }
        });
    }
}

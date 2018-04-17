package com.example.android.ticketing_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.interfaces.GestureView;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.util.ArrayList;

public class GateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);

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

        // Get gateNo from intent
        Intent intent = getIntent();
        String gateNo = intent.getStringExtra("Gate");
        //Toast.makeText(GateActivity.this, "Gate "+gateNo, Toast.LENGTH_SHORT).show();

        // The ArrayList will be filled with data from the database.
        // The gateNo will be used to query the database.
        final ArrayList<Seat> seats = new ArrayList<>();
        if (gateNo.equals("111")) {
            seats.add(new Seat(gateNo, 16, 1, true, 0xff010000));
            seats.add(new Seat(gateNo, 16, 2, true, 0xff020000));
            seats.add(new Seat(gateNo, 16, 3, false, 0xff030000));
            seats.add(new Seat(gateNo, 16, 4, true, 0xff040000));
            seats.add(new Seat(gateNo, 16, 5, true, 0xff050000));
            seats.add(new Seat(gateNo, 15, 1, true, 0xff170000));
            seats.add(new Seat(gateNo, 15, 2, true, 0xff180000));
            seats.add(new Seat(gateNo, 15, 3, true, 0xff190000));
            seats.add(new Seat(gateNo, 15, 4, false, 0xff200000));
        }
        if (gateNo.equals("218")) {
            seats.add(new Seat(gateNo, 16, 1, true, 0xff010000));
            seats.add(new Seat(gateNo, 16, 2, false, 0xff020000));
            seats.add(new Seat(gateNo, 16, 3, false, 0xff030000));
            seats.add(new Seat(gateNo, 16, 4, true, 0xff040000));
            seats.add(new Seat(gateNo, 15, 1, true, 0xff170000));
            seats.add(new Seat(gateNo, 15, 2, true, 0xff180000));
            seats.add(new Seat(gateNo, 15, 3, false, 0xff190000));
            seats.add(new Seat(gateNo, 15, 4, false, 0xff200000));
        }
        if (gateNo.equals("219")) {
            seats.add(new Seat(gateNo, 1, 5, false, 0xff000100));
            seats.add(new Seat(gateNo, 1, 6, true, 0xff000200));
            seats.add(new Seat(gateNo, 1, 7, true, 0xff000300));
            seats.add(new Seat(gateNo, 1, 8, true, 0xff000400));
        }

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

        final VectorChildFinder vector = new VectorChildFinder(this, frontID, frontImageView);

        // Paint gray all seats that are not free
        for (Seat seat : seats) {
            if (!seat.isFree()) {
                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);
                seatPath.setFillColor(0xff9e9e9e);
            }
        }
        frontImageView.invalidate();

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
                        for (Seat seat : seats) {
                            int seatColor = seat.getColor();
                            if (touchColor == seatColor && seat.isFree()) {
                                // For this seat, do something (temporarily change color)
                                String seatPathName = "r" + seat.getRow() + "s" + seat.getCol();
                                final VectorDrawableCompat.VFullPath seatPath = vector.findPathByName(seatPathName);
                                toggleColor(seatPath);
                                frontImageView.invalidate();
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
}

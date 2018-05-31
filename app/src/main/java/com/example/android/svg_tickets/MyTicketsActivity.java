package com.example.android.svg_tickets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyTicketsActivity extends AppCompatActivity {

    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<String> myTicketsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        //instantiate custom adapter
        final TicketsCustomAdapter adapter = new TicketsCustomAdapter(myTicketsList, this);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mTicketsRef = mDatabase.child("tickets");

        mTicketsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String seatInfo = dataSnapshot.getKey();
                if (dataSnapshot.getValue().equals(currentUserId)) {

                    myTicketsList.add(seatInfo);
                    ListView lView = findViewById(R.id.my_tickets_listview);
                    lView.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_tickets_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_main) {
            final Intent checkoutIntent = new Intent(this, MainActivity.class);
            startActivity(checkoutIntent);
            return true;
        }

        if (item.getItemId() == R.id.action_checkout) {
            final Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
            startActivity(checkoutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

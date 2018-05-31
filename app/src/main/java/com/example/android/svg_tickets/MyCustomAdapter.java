package com.example.android.svg_tickets;

// see https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    long freeSeats;
    private Context context;
    //String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> keysList;

    public MyCustomAdapter(ArrayList<String> list, ArrayList<String> keysList, Context context) {
        this.list = list;
        this.keysList = keysList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.booked_seats_list_item, null);
        }

        //Handle TextView and display string from your list
        final TextView listItemText = view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seatString = keysList.get(position);
                Log.d("Petros", "seat to be removed = " + seatString);

                mDatabase.child("bookedSeats").child(seatString).removeValue();

                // see also https://stackoverflow.com/questions/44102312/how-to-increment-number-in-firebase
                final DatabaseReference mFreeSeatsRef = mDatabase.child("Gates")
                        .child(SeatStringFunctions.getGate(seatString))
                        .child("freeSeats");
                mFreeSeatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        freeSeats = (long) dataSnapshot.getValue();
                        Log.d("Petros", "free seats = " + freeSeats);
                        mFreeSeatsRef.setValue(++freeSeats);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

                final DatabaseReference mSeatRef = mDatabase.child("Seats")
                        .child("Gate" + SeatStringFunctions.getGate(seatString))
                        .child("r" + SeatStringFunctions.getRowAndSeat(seatString));
                mSeatRef.child("booked").setValue(false);
                mSeatRef.child("userId").setValue("-");

                list.remove(position);
                keysList.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}

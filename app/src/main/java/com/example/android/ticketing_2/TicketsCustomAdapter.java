package com.example.android.ticketing_2;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TicketsCustomAdapter extends BaseAdapter implements ListAdapter {
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> ticketsList = new ArrayList<String>();
    private Context context;

    public TicketsCustomAdapter(ArrayList<String> ticketsList, Context context) {
        this.ticketsList = ticketsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ticketsList.size();
    }

    @Override
    public Object getItem(int pos) {
        return ticketsList.get(pos);
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
            view = inflater.inflate(R.layout.tickets_list_item, null);
        }

        //Handle TextView and display string from your list
        final TextView listItemText = view.findViewById(R.id.list_ticket_string);
        listItemText.setText(ticketsList.get(position));

        //Handle buttons and add onClickListeners
        Button qrBtn = view.findViewById(R.id.qr_btn);

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ticketString = ticketsList.get(position) + " - " + currentUserName;
                Log.d("Petros", "ticket string = " + ticketString);


            }
        });

        return view;
    }
}

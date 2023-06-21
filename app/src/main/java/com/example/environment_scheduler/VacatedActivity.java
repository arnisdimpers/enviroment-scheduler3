package com.example.environment_scheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VacatedActivity extends AppCompatActivity {

    // Importing buttons, recyclerView
    Button gobackButton, confirmButton;

    Spinner spinner;
    ArrayList<String> dropList;

    RecyclerView recyclerView; // RecyclerView to store the Firebase 'list' array generated in MyAdapter.java
    DatabaseReference database, database2;
    MyAdapter myAdapter;
    ArrayList<Meeting> list;


    FirebaseAuth fAuth; //to register new users in firebase
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacated);

        //initializing buttons//
        gobackButton = findViewById(R.id.gobackButton);
        confirmButton = findViewById(R.id.confirmButton);
        spinner = findViewById(R.id.spinner);

        //  necessary components for every class //
        //  necessary components for every class //
        fAuth = FirebaseAuth.getInstance(); //initializing fAuth, fStore
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentFirebaseUser.getUid();
        //make firedatabase connected to this instance
        FirebaseDatabase budgetText = FirebaseDatabase.getInstance();
        DatabaseReference db = budgetText.getReference("Users");
        database = FirebaseDatabase.getInstance().getReference("MeetingRooms");
        //  necessary components for every class //
        //  necessary components for every class //

        // Initializing Firebase RecyclerView requirements
        recyclerView = findViewById(R.id.vacatedList);
        database2 = FirebaseDatabase.getInstance().getReference("Users/" + currentUserID + "/vacatedRooms");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialized Spinner (drop down menu) dropList, should be updated each array 'dropList' iteration: dropAdapter.notifyDataSetChanged();
        dropList = new ArrayList<>();
        ArrayAdapter<String> dropAdapter = new ArrayAdapter<String>(VacatedActivity.this, android.R.layout.simple_spinner_item, dropList);
        dropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list = new ArrayList<>(); // Create the list array
        myAdapter = new MyAdapter(this, list, "vacatedroom"); // Populate the array
        recyclerView.setAdapter(myAdapter);


        // Read Firebase to load in RecyclerView
        database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Meeting meeting = dataSnapshot.getValue(Meeting.class); // Reading from Users/ all sub folders to insert into List
                    list.add(meeting);

                    // Creating drop-down menu to book EMPTY only rooms
                    String room = meeting.getRoom();

//                    String date = meeting.getDate();
//                    String message = meeting.getMessage();
//
//                    String FullName = meeting.getFullName();
//                    String PhoneNumber = meeting.getPhoneNumber();
//                    String UserEmail = meeting.getUserEmail();
//                    String timeofbooking = meeting.getTimeofbooking();

                    dropList.add(room);
                    dropAdapter.notifyDataSetChanged();
                    Log.d("Added: ", room);

                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("Spinner1", "Updating spinner adapter");
        // Spinner listener for drop down menu selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                Log.d("dropDownSelected: ", value);
                Toast.makeText(VacatedActivity.this, "Selected Room: #" + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Final setAdapter to update the above Spinner
        spinner.setAdapter(dropAdapter);




        gobackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(VacatedActivity.this, "Going back to main page", Toast.LENGTH_SHORT).show();
                Intent gobackActivity = new Intent(VacatedActivity.this, MainActivity.class);
                startActivity(gobackActivity);

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(VacatedActivity.this, "Selected Vacated Room ..", Toast.LENGTH_SHORT).show();
                Intent confirmActivity = new Intent(VacatedActivity.this, ClearRoomActivity.class);
                confirmActivity.putExtra("spinnerItem", spinner.getSelectedItem().toString()); // Transferring data to another class
                startActivity(confirmActivity);

            }
        });

    }
}
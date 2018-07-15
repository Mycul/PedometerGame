package com.example.michael.pedometertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard extends AppCompatActivity {

    DatabaseReference databaseUsers;
    ListView listViewUsers;
    List<UserModel> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);
        users = new ArrayList<>();



    }


    @Override
    protected void onStart(){
        super.onStart();
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    UserModel user = postSnapshot.getValue(UserModel.class);
                    users.add(user);
                }
                Collections.sort(users);
                UserList userListAdapter = new UserList(Leaderboard.this, users);
                listViewUsers.setAdapter(userListAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

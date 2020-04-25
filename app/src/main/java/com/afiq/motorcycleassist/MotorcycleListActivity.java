package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.Models.Session;
import com.afiq.motorcycleassist.MotorcycleListRecyclerView.MotorcycleListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MotorcycleListActivity extends AppCompatActivity implements MotorcycleListAdapter.OnListListener {

private Button mCreate;

    private RecyclerView mMotorcycleListReyclerView;
    private RecyclerView.Adapter mMotorcycleListAdapter;
    private RecyclerView.LayoutManager mMotorcycleListLayoutManager;

    private FirebaseAuth mAuth;

    private ArrayList<Motorcycle> motorcycleList = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorcycle_list);
        
        mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent intent = new Intent(MotorcycleListActivity.this, CreateMotorcycleActivity.class);
                startActivity(intent);
                finish();
                
            }
        });

        mMotorcycleListReyclerView = (RecyclerView) findViewById(R.id.MotorcycleListRecyclerView);
        mMotorcycleListReyclerView.setNestedScrollingEnabled(false);

        mMotorcycleListLayoutManager = new LinearLayoutManager(MotorcycleListActivity.this);
        mMotorcycleListReyclerView.setLayoutManager(mMotorcycleListLayoutManager);

        mMotorcycleListAdapter = new MotorcycleListAdapter(motorcycleList, this);


        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference dataMotor = FirebaseDatabase.getInstance().getReference().child("User").child("Motorcyclist").child(user_id).child("Motorcycle");

        dataMotor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child : children) {
                    Motorcycle motor = child.getValue(Motorcycle.class);
                    Toast.makeText(MotorcycleListActivity.this, motor.getMotorId(), Toast.LENGTH_SHORT).show();
                    motorcycleList.add(motor);
                }
                mMotorcycleListReyclerView.setAdapter(mMotorcycleListAdapter);
                mMotorcycleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onListClick(int position) {
        Toast.makeText(MotorcycleListActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
        Session session = new Session(MotorcycleListActivity.this);
        session.setMotorcycle(motorcycleList.get(position));

        Intent intent = new Intent(MotorcycleListActivity.this, SelectHelpActivity.class);
        Motorcycle M = new Motorcycle();
        intent.putExtra("Motor",motorcycleList.get(position));
        startActivity(intent);

    }

    @Override
    public void onEditClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }
}

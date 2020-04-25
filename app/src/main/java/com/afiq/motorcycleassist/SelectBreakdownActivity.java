package com.afiq.motorcycleassist;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afiq.motorcycleassist.Models.Motorcycle;
import com.afiq.motorcycleassist.Models.Session;

public class SelectBreakdownActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_breakdown);

        Session session = new Session(SelectBreakdownActivity.this);
        Motorcycle motorcycle = session.getMotorcycle();
        Toast.makeText(SelectBreakdownActivity.this,motorcycle.getMotorBrand(),Toast.LENGTH_SHORT).show();
    }
}

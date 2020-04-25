package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.afiq.motorcycleassist.Models.Motorcycle;

public class SelectHelpActivity extends AppCompatActivity {

    private Button mFuel, mBreakdown;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_help);

        mFuel = (Button)findViewById(R.id.fuel);
        mBreakdown = (Button)findViewById(R.id.breakdown);

        intent = getIntent();
        Motorcycle motor = intent.getParcelableExtra("Motor");

        mFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SelectHelpActivity.this,MotorcyclistMapActivity.class);
                startActivity(intent);
            }
        });

        mBreakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SelectHelpActivity.this,MotorcycleBreakdownActivity.class);
                startActivity(intent);
            }
        });


    }
}

package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.ViewModel.ShopVM;
import com.google.firebase.auth.FirebaseAuth;

public class RestrictedActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restricted);

        TextView message = findViewById(R.id.message);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(RestrictedActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        String Uid = FirebaseAuth.getInstance().getUid();
        ShopVM shopVM =  ViewModelProviders.of(RestrictedActivity.this).get(ShopVM.class);
        shopVM.getShop(Uid).observe(this, shop -> {
            Log.d("TAG", "Shop : "+shop);
            if(shop != null){
                if(shop.getShopStatus().equalsIgnoreCase("Pending")){
                    message.setText("Your shop is pending for approval. You can use all the features once it has been approved");
                }
            }
            else{
                message.setText("Your shop registration has been rejected");
            }
        });




    }
}

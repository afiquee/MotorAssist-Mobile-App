package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afiq.motorcycleassist.Fragments.MechanicAccountFragment;
import com.afiq.motorcycleassist.Fragments.MechanicHistoryFragment;
import com.afiq.motorcycleassist.Fragments.MechanicHomeFragment;
import com.afiq.motorcycleassist.ViewModel.MechanicVM;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MechanicActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);

        String Uid = FirebaseAuth.getInstance().getUid();

        if(Uid == null){
            Intent intent = new Intent(MechanicActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        MechanicVM mechanicVM =  ViewModelProviders.of(MechanicActivity.this).get(MechanicVM.class);
        mechanicVM.getMechanic(Uid).observe(this, mechanic -> {
            if(mechanic != null){
                if(mechanic.getMechStatus().equalsIgnoreCase("Retired")){
                    Toast.makeText(this, "Retired account cannot login", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MechanicActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    fragmentManager = getSupportFragmentManager();
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.main_container, new MechanicHomeFragment()).commit();
                }

            }
        });


        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);




        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_home:
                        fragment = new MechanicHomeFragment();
                        break;
                    case R.id.action_history:
                        fragment = new MechanicHistoryFragment();
                        break;

                    case R.id.action_account:
                        fragment = new MechanicAccountFragment();
                        break;

                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
    }
}

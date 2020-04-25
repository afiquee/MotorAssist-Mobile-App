package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afiq.motorcycleassist.Fragments.GoodSamaritansRequestFragment;
import com.afiq.motorcycleassist.Fragments.AccountFragment;
import com.afiq.motorcycleassist.Fragments.MotorcyclistHistoryFragment;
import com.afiq.motorcycleassist.Fragments.MotorcyclistHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MotorcyclistActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent = new Intent(MotorcyclistActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment motorcyclistFragment = new MotorcyclistHomeFragment();
        String gsRequestKey = getIntent().getStringExtra("gsRequestKey");
        if(gsRequestKey != null){
            Bundle args = new Bundle();
            args.putString("gsRequestKey", gsRequestKey);
            motorcyclistFragment.setArguments(args);
        }
        transaction.replace(R.id.main_container, motorcyclistFragment).commit();





        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_home:
                        fragment = new MotorcyclistHomeFragment();
                        break;
                    case R.id.action_request:
                        fragment = new GoodSamaritansRequestFragment();
                        break;
                    case R.id.action_history:
                        fragment = new MotorcyclistHistoryFragment();
                        break;

                    case R.id.action_account:
                        fragment = new AccountFragment();
                        break;

                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });


    }
}

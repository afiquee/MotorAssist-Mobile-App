package com.afiq.motorcycleassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afiq.motorcycleassist.Fragments.MechanicListFragment;
import com.afiq.motorcycleassist.Fragments.ShopAccountFragment;
import com.afiq.motorcycleassist.Fragments.ShopHistoryFragment;
import com.afiq.motorcycleassist.Fragments.ShopHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent = new Intent(ShopActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, new ShopHomeFragment()).commit();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_home:
                        fragment = new ShopHomeFragment();
                        break;
                    case R.id.action_history:
                        fragment = new ShopHistoryFragment();
                        break;

                    case R.id.action_mechanic:
                        fragment = new MechanicListFragment();
                        break;

                    case R.id.action_account:
                        fragment = new ShopAccountFragment();
                        break;

                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
    }
}

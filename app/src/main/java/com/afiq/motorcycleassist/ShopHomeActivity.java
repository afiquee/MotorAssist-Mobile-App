package com.afiq.motorcycleassist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ShopHomeActivity extends AppCompatActivity {

    private TextView mShopId;
    private Button mCopy;
    private Button logout;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_home);

        mShopId = (TextView) findViewById(R.id.shopId);

        mCopy = (Button) findViewById(R.id.copy);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mShopId.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mShopId.setText(user_id);

        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ShopHomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }
}

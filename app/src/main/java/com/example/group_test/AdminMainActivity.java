package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_test.model.User;
import com.example.group_test.sharedpref.SharedPrefManager;

public class AdminMainActivity extends AppCompatActivity {
    private TextView tvHello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // get references
        tvHello = findViewById(R.id.tvHello);

        // greet the user
        // if the user is not logged in we will directly them to LoginActivity
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            //stop this MainActivity
            finish();

            // forward to Login Page (Update this part)
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            User user = spm.getUser();
            tvHello.setText("Hello " + user.getUsername());
        }

    }
    public void logoutClicked(View view) {

        // Clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        //display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        //terminate this MainActivity
        finish();

        //forward to login page
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void viewItemsClicked(View view) {
        Intent intent = new Intent(AdminMainActivity.this, RecyclableListActivity.class);
        startActivity(intent);
    }
}

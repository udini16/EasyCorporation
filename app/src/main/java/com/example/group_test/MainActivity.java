package com.example.group_test;

import static android.app.ProgressDialog.show;

import static com.example.group_test.R.id.tvHello;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.group_test.model.User;
import com.example.group_test.sharedpref.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        startActivity(intent);
    }

    public void requestFormClicked(View view) {
        Intent intent = new Intent(MainActivity.this, FormRequest.class);
        startActivity(intent);
    }


}
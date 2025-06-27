package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.group_test.model.FailLogin;
import com.example.group_test.model.User;
import com.example.group_test.remote.UserService;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
    }

    public void loginClicked(View view) {
        // Get the username and password from the EditText fields
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();

        if (validateLogin(username, password)){
            doLogin(username, password);
        }
    }
    private void doLogin(String username, String password) {
        // Create an instance of UserService
        UserService userService = ApiUtils.getUserService();

        Call<User> call;
        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {  // code 200
                    // parse response to POJO
                    User user = (User) response.body();
                    if (user != null && user.getToken() != null) {
                        // successful login. server replies a token value
                        displayToast("Login successful");
                        displayToast("Token: " + user.getToken());

                        //store value in Shared Preferences
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        //forward to MainActivity
                        Intent intent = new Intent(getApplicationContext(), com.example.group_test.MainActivity.class);
                        startActivity(intent);
                        finish();
                        ;
                    } else {
                        // server return success but no user info replied
                        displayToast("Login error");
                    }
                } else {  // other than 200
                    // try to parse the response to FailLogin POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp:", e.toString()); // print error details to error log
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("MyApp:", t.toString()); // print error details to error log
                displayToast("Error");
                Log.e("My App:", t.toString());
            }
        });

    }
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username or Email is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Display a Toast message
     * @param message message to be displayed inside toast
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
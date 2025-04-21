package com.example.attendancemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", null);
        if (savedUsername != null) {

            Intent intent = new Intent(MainActivity.this, TabbedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        databaseHelper = new DatabaseHelper(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkUser = databaseHelper.checkUserCredentials(username, password);
                    if (checkUser) {
                        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);  // Store the username
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, TabbedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkUsername = databaseHelper.checkUsername(username);
                    if (!checkUsername) {
                        boolean insert = databaseHelper.insertUser(username, password);
                        if (insert) {
                            Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, TabbedActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
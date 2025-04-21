package com.example.attendancemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        usernameTextView = view.findViewById(R.id.usernameTextView);
        logoutButton = view.findViewById(R.id.logoutButton);

        loadUserData();


        logoutButton.setOnClickListener(v -> logout());

        return view;
    }


    private void loadUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest"); // Default to "Guest" if not found

        usernameTextView.setText("Username: " + username);
    }

    private void logout() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

        getActivity().finish();
    }
}

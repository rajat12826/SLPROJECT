package com.example.attendancemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class AttendanceFragment extends Fragment {

    private EditText editTextSubject, editTextAttended, editTextTotal;
    private Button buttonAdd;
    private TableLayout tableLayout;
    private TextView textViewAttendanceStats;
    private ProgressBar progressBar;

    private DatabaseHelper databaseHelper;
    private boolean isUpdateMode = false;
    private int currentAttendanceId = -1;

    public AttendanceFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        editTextSubject = view.findViewById(R.id.editTextSubject);
        editTextAttended = view.findViewById(R.id.editTextAttended);
        editTextTotal = view.findViewById(R.id.editTextTotal);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        tableLayout = view.findViewById(R.id.tableLayout);
        textViewAttendanceStats = view.findViewById(R.id.textViewAttendanceStats);
        progressBar = view.findViewById(R.id.progressBar);

        buttonAdd.setOnClickListener(v -> addOrUpdateAttendance());
        loadAttendanceData();

        return view;
    }

    private void addOrUpdateAttendance() {
        String subject = editTextSubject.getText().toString();
        String attendedStr = editTextAttended.getText().toString();
        String totalStr = editTextTotal.getText().toString();

        if (subject.isEmpty() || attendedStr.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int attended = Integer.parseInt(attendedStr);
            int total = Integer.parseInt(totalStr);

            if (attended > total) {
                Toast.makeText(getContext(), "Attended can't be more than total", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            AttendanceModel model = new AttendanceModel(
                    isUpdateMode ? currentAttendanceId : -1,
                    subject, attended, total
            );

            if (isUpdateMode) {
                success = databaseHelper.updateAttendance(model);
                Toast.makeText(getContext(), success ? "Record updated" : "Update failed", Toast.LENGTH_SHORT).show();
                isUpdateMode = false;
                currentAttendanceId = -1;
                buttonAdd.setText("Add");
            } else {
                success = databaseHelper.insertAttendance(model);
                Toast.makeText(getContext(), success ? "Record added" : "Add failed", Toast.LENGTH_SHORT).show();
            }

            if (success) {
                editTextSubject.setText("");
                editTextAttended.setText("");
                editTextTotal.setText("");
                loadAttendanceData();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAttendanceData() {
        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));
        ArrayList<AttendanceModel> attendanceList = databaseHelper.getAllAttendance();

        int totalAttended = 0, totalClasses = 0;

        if (attendanceList.isEmpty()) {
            TableRow row = new TableRow(getContext());
            TextView emptyText = new TextView(getContext());
            emptyText.setText("No attendance records. Add your first subject.");
            emptyText.setPadding(10, 20, 10, 20);
            emptyText.setTextColor(Color.GRAY);
            row.addView(emptyText);
            tableLayout.addView(row);
        } else {
            for (AttendanceModel attendance : attendanceList) {
                TableRow row = new TableRow(getContext());

                TextView tvSubject = new TextView(getContext());
                tvSubject.setText(attendance.getSubject());
                tvSubject.setTextColor(Color.BLACK);
                tvSubject.setPadding(10, 10, 10, 10);

                TextView tvAttended = new TextView(getContext());
                tvAttended.setText(String.valueOf(attendance.getAttendedClasses()));
                tvAttended.setTextColor(Color.BLACK);
                tvAttended.setPadding(10, 10, 10, 10);

                TextView tvTotal = new TextView(getContext());
                tvTotal.setText(String.valueOf(attendance.getTotalClasses()));
                tvTotal.setTextColor(Color.BLACK);
                tvTotal.setPadding(10, 10, 10, 10);

                float percentage = (float) attendance.getAttendedClasses() / attendance.getTotalClasses() * 100;
                TextView tvPercentage = new TextView(getContext());
                tvPercentage.setText(String.format("%.1f%%", percentage));
                tvPercentage.setTextColor(Color.BLACK);
                tvPercentage.setPadding(10, 10, 10, 10);

                LinearLayout actionLayout = new LinearLayout(getContext());
                actionLayout.setOrientation(LinearLayout.HORIZONTAL);

                ImageButton btnUpdate = new ImageButton(getContext());
                btnUpdate.setImageResource(android.R.drawable.ic_menu_edit);
                btnUpdate.setBackgroundResource(0);
                btnUpdate.setOnClickListener(v -> {
                    editTextSubject.setText(attendance.getSubject());
                    editTextAttended.setText(String.valueOf(attendance.getAttendedClasses()));
                    editTextTotal.setText(String.valueOf(attendance.getTotalClasses()));
                    isUpdateMode = true;
                    currentAttendanceId = attendance.getId();
                    buttonAdd.setText("Update");
                });

                ImageButton btnDelete = new ImageButton(getContext());
                btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
                btnDelete.setBackgroundResource(0);
                btnDelete.setOnClickListener(v -> {
                    boolean deleted = databaseHelper.deleteAttendance(attendance.getId());
                    Toast.makeText(getContext(),
                            deleted ? "Deleted" : "Delete failed", Toast.LENGTH_SHORT).show();
                    if (deleted) {
                        if (isUpdateMode && currentAttendanceId == attendance.getId()) {
                            isUpdateMode = false;
                            currentAttendanceId = -1;
                            buttonAdd.setText("Add");
                        }
                        loadAttendanceData();
                    }
                });

                actionLayout.addView(btnUpdate);
                actionLayout.addView(btnDelete);

                row.addView(tvSubject);
                row.addView(tvAttended);
                row.addView(tvTotal);
                row.addView(tvPercentage);
                row.addView(actionLayout);

                tableLayout.addView(row);

                totalAttended += attendance.getAttendedClasses();
                totalClasses += attendance.getTotalClasses();
            }
        }

        if (totalClasses > 0) {
            float overallPercentage = (float) totalAttended / totalClasses * 100;
            textViewAttendanceStats.setText(
                    String.format("Overall Attendance: %d/%d (%.1f%%)", totalAttended, totalClasses, overallPercentage));
            progressBar.setProgress((int) overallPercentage);
        } else {
            textViewAttendanceStats.setText("No attendance data");
        }
    }
}

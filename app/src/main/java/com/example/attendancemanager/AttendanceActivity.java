package com.example.attendancemanager;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity {
    private static final String TAG = "AttendanceActivity";
    private EditText editTextSubject, editTextAttended, editTextTotal;
    private Button buttonAdd;
    private TableLayout tableLayout;
    private TextView textViewAttendanceStats;
    private DatabaseHelper databaseHelper;

    private boolean isUpdateMode = false;
    private int currentAttendanceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Log.d(TAG, "AttendanceActivity onCreate called");

        try {
            databaseHelper = new DatabaseHelper(this);

            editTextSubject = findViewById(R.id.editTextSubject);
            editTextAttended = findViewById(R.id.editTextAttended);
            editTextTotal = findViewById(R.id.editTextTotal);
            buttonAdd = findViewById(R.id.buttonAdd);
            tableLayout = findViewById(R.id.tableLayout);
            textViewAttendanceStats = findViewById(R.id.textViewAttendanceStats);

            loadAttendanceData();

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subject = editTextSubject.getText().toString();
                    String attendedStr = editTextAttended.getText().toString();
                    String totalStr = editTextTotal.getText().toString();

                    if (subject.isEmpty() || attendedStr.isEmpty() || totalStr.isEmpty()) {
                        Toast.makeText(AttendanceActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int attended = Integer.parseInt(attendedStr);
                        int total = Integer.parseInt(totalStr);

                        if (attended > total) {
                            Toast.makeText(AttendanceActivity.this, "Attended classes cannot be more than total classes", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean success;

                        if (isUpdateMode) {

                            AttendanceModel attendance = new AttendanceModel(currentAttendanceId, subject, attended, total);
                            success = databaseHelper.updateAttendance(attendance);
                            if (success) {
                                Toast.makeText(AttendanceActivity.this, "Record updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AttendanceActivity.this, "Failed to update record", Toast.LENGTH_SHORT).show();
                            }


                            isUpdateMode = false;
                            currentAttendanceId = -1;
                            buttonAdd.setText("Add");
                        } else {

                            AttendanceModel attendance = new AttendanceModel(-1, subject, attended, total);
                            success = databaseHelper.insertAttendance(attendance);
                            if (success) {
                                Toast.makeText(AttendanceActivity.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AttendanceActivity.this, "Failed to add record", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (success) {

                            editTextSubject.setText("");
                            editTextAttended.setText("");
                            editTextTotal.setText("");


                            loadAttendanceData();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(AttendanceActivity.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing app. Please restart.", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAttendanceData() {
        try {

            int childCount = tableLayout.getChildCount();
            if (childCount > 1) {
                tableLayout.removeViews(1, childCount - 1);
            }


            ArrayList<AttendanceModel> attendanceList = databaseHelper.getAllAttendance();


            int totalAttended = 0;
            int totalClasses = 0;


            if (attendanceList.isEmpty()) {
                TableRow emptyRow = new TableRow(this);

                TextView emptyText = new TextView(this);
                emptyText.setText("No attendance records yet. Add your first subject above.");
                emptyText.setPadding(10, 20, 10, 20);
                emptyText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                emptyText.setLayoutParams(new TableRow.LayoutParams());
                ((TableRow.LayoutParams) emptyText.getLayoutParams()).span = 5; // Span all columns

                emptyRow.addView(emptyText);
                tableLayout.addView(emptyRow);
            } else {

                for (AttendanceModel attendance : attendanceList) {
                    TableRow row = new TableRow(this);

                    TextView textViewSubject = new TextView(this);
                    textViewSubject.setText(attendance.getSubject());
                    textViewSubject.setPadding(10, 10, 10, 10);
                    textViewSubject.setMinWidth(100);
                    textViewSubject.setTextColor(Color.BLACK);  // Set text color to black
                    row.addView(textViewSubject);


                    TextView textViewAttended = new TextView(this);
                    textViewAttended.setText(String.valueOf(attendance.getAttendedClasses()));
                    textViewAttended.setPadding(10, 10, 10, 10);
                    textViewAttended.setGravity(android.view.Gravity.CENTER);
                    textViewAttended.setMinWidth(70);
                    textViewAttended.setTextColor(Color.BLACK);  // Set text color to black
                    row.addView(textViewAttended);


                    TextView textViewTotal = new TextView(this);
                    textViewTotal.setText(String.valueOf(attendance.getTotalClasses()));
                    textViewTotal.setPadding(10, 10, 10, 10);
                    textViewTotal.setGravity(android.view.Gravity.CENTER);
                    textViewTotal.setMinWidth(70);
                    textViewTotal.setTextColor(Color.BLACK);  // Set text color to black
                    row.addView(textViewTotal);


                    TextView textViewPercentage = new TextView(this);
                    float percentage = (float) attendance.getAttendedClasses() / attendance.getTotalClasses() * 100;
                    textViewPercentage.setText(String.format("%.1f%%", percentage));
                    textViewPercentage.setPadding(10, 10, 10, 10);
                    textViewPercentage.setGravity(android.view.Gravity.CENTER);
                    textViewPercentage.setMinWidth(90);
                    textViewPercentage.setTextColor(Color.BLACK);  // Set text color to black
                    row.addView(textViewPercentage);

                    LinearLayout actionLayout = new LinearLayout(this);
                    actionLayout.setOrientation(LinearLayout.HORIZONTAL);
                    actionLayout.setMinimumWidth(100);
                    actionLayout.setGravity(android.view.Gravity.CENTER);


                    ImageButton buttonUpdate = new ImageButton(this);
                    buttonUpdate.setImageResource(android.R.drawable.ic_menu_edit);
                    buttonUpdate.setBackgroundResource(0);
                    buttonUpdate.setPadding(10, 10, 10, 10);
                    buttonUpdate.setContentDescription("Update");


                    buttonUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            editTextSubject.setText(attendance.getSubject());
                            editTextAttended.setText(String.valueOf(attendance.getAttendedClasses()));
                            editTextTotal.setText(String.valueOf(attendance.getTotalClasses()));


                            isUpdateMode = true;
                            currentAttendanceId = attendance.getId();

                            buttonAdd.setText("Update");


                            editTextSubject.requestFocus();
                        }
                    });


                    ImageButton buttonDelete = new ImageButton(this);
                    buttonDelete.setImageResource(android.R.drawable.ic_menu_delete);
                    buttonDelete.setBackgroundResource(0);
                    buttonDelete.setPadding(10, 10, 10, 10);
                    buttonDelete.setContentDescription("Delete");


                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean success = databaseHelper.deleteAttendance(attendance.getId());
                            if (success) {
                                Toast.makeText(AttendanceActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();


                                if (isUpdateMode && currentAttendanceId == attendance.getId()) {
                                    editTextSubject.setText("");
                                    editTextAttended.setText("");
                                    editTextTotal.setText("");
                                    isUpdateMode = false;
                                    currentAttendanceId = -1;
                                    buttonAdd.setText("Add");
                                }


                                loadAttendanceData();
                            } else {
                                Toast.makeText(AttendanceActivity.this, "Failed to delete record", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    actionLayout.addView(buttonUpdate);
                    actionLayout.addView(buttonDelete);


                    row.addView(actionLayout);


                    tableLayout.addView(row);


                    totalAttended += attendance.getAttendedClasses();
                    totalClasses += attendance.getTotalClasses();
                }
            }
            ProgressBar progressBar = findViewById(R.id.progressBar);

            if (totalClasses > 0) {
                float overallPercentage = (float) totalAttended / totalClasses * 100;
                textViewAttendanceStats.setText(String.format("Overall Attendance: %d/%d (%.1f%%)",
                        totalAttended, totalClasses, overallPercentage));
                progressBar.setProgress((int)overallPercentage);
            } else {
                textViewAttendanceStats.setText("No attendance records yet");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading attendance data: " + e.getMessage());
            Toast.makeText(this, "Error loading attendance data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "AttendanceActivity onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "AttendanceActivity onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AttendanceActivity onDestroy called");
    }
}
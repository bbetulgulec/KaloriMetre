package com.betulgulec.app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HedefFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private TextView day1TextView; // TextView for day 1
    private TextView day2TextView; // TextView for day 2
    private TextView day3TextView; // TextView for day 3
    private TextView day4TextView; // TextView for day 4
    private TextView day5TextView; // TextView for day 5
    private TextView day6TextView; // TextView for day 6
    private TextView day7TextView; // TextView for day 7
    private ProgressBar progressBar1; // Progress bar 1
    private ProgressBar progressBar2; // Progress bar 2
    private ProgressBar progressBar3; // Progress bar 3
    private ProgressBar progressBar4; // Progress bar 4
    private ProgressBar progressBar5; // Progress bar 5
    private ProgressBar progressBar6; // Progress bar 6
    private ProgressBar progressBar7; // Progress bar 7

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hedef, container, false);

        // Firebase user and references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        // Initialize views
        day1TextView = view.findViewById(R.id.day1);
        day2TextView = view.findViewById(R.id.day2);
        day3TextView = view.findViewById(R.id.day3);
        day4TextView = view.findViewById(R.id.day4);
        day5TextView = view.findViewById(R.id.day5);
        day6TextView = view.findViewById(R.id.day6);
        day7TextView = view.findViewById(R.id.day7);
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar3 = view.findViewById(R.id.progressBar3);
        progressBar4 = view.findViewById(R.id.progressBar4);
        progressBar5 = view.findViewById(R.id.progressBar5);
        progressBar6 = view.findViewById(R.id.progressBar6);
        progressBar7 = view.findViewById(R.id.progressBar7);

        // Get today's date
        Calendar calendar = Calendar.getInstance();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Set max value of all progress bars to targetCalories under user ID
        userRef.child("targetCalories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int targetCalories = snapshot.getValue(Integer.class);
                    progressBar1.setMax(targetCalories);
                    progressBar2.setMax(targetCalories);
                    progressBar3.setMax(targetCalories);
                    progressBar4.setMax(targetCalories);
                    progressBar5.setMax(targetCalories);
                    progressBar6.setMax(targetCalories);
                    progressBar7.setMax(targetCalories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        // Update each progress bar value with respective date's calories
        updateProgressBar(userRef, progressBar1, todayDate);
        updateProgressBar(userRef, progressBar2, getPreviousDate(todayDate, 1));
        updateProgressBar(userRef, progressBar3, getPreviousDate(todayDate, 2));
        updateProgressBar(userRef, progressBar4, getPreviousDate(todayDate, 3));
        updateProgressBar(userRef, progressBar5, getPreviousDate(todayDate, 4));
        updateProgressBar(userRef, progressBar6, getPreviousDate(todayDate, 5));
        updateProgressBar(userRef, progressBar7, getPreviousDate(todayDate, 6));

        // Update TextViews with respective dates
        day1TextView.setText(todayDate);
        day2TextView.setText(getPreviousDate(todayDate, 1));
        day3TextView.setText(getPreviousDate(todayDate, 2));
        day4TextView.setText(getPreviousDate(todayDate, 3));
        day5TextView.setText(getPreviousDate(todayDate, 4));
        day6TextView.setText(getPreviousDate(todayDate, 5));
        day7TextView.setText(getPreviousDate(todayDate, 6));

        return view;
    }

    // Helper method to get previous date
    private String getPreviousDate(String currentDate, int daysBefore) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(currentDate));
            calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return currentDate;
        }
    }

    // Helper method to update progress bar with respective date's calories
    private void updateProgressBar(DatabaseReference userRef, ProgressBar progressBar, String date) {
        DatabaseReference caloriesRef = userRef.child("weeklycalories").child(date);
        caloriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int calories = snapshot.getValue(Integer.class);
                    progressBar.setProgress(calories);
                    setProgressBarColor(progressBar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    // Helper method to set progress bar color based on progress percentage
    private void setProgressBarColor(ProgressBar progressBar) {
        int totalCalories = progressBar.getProgress();
        int targetCalories = progressBar.getMax();
        double progressPercentage = (double) totalCalories / targetCalories * 100;
        int progressColor;
        if (progressPercentage >= 75) {
            progressColor = Color.RED; // Red if progress is 75% or more
        } else if (progressPercentage >= 50) {
            progressColor = Color.parseColor("#FFA500"); // Orange if progress is between 50% and 75%
        } else if (progressPercentage >= 25) {
            progressColor = Color.YELLOW; // Yellow if progress is between 25% and 50%
        } else {
            progressColor = Color.GREEN; // Default color is green
        }
        progressBar.setProgressTintList(ColorStateList.valueOf(progressColor));
    }
}
package com.betulgulec.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HedefFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private TextView[] dayLabels;
    private ProgressBar[] progressBars;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hedef, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Gün etiketlerini ve progress barları tanımla
        dayLabels = new TextView[]{
                view.findViewById(R.id.monday_label),
                view.findViewById(R.id.tuesday_label),
                view.findViewById(R.id.wednesday_label),
                view.findViewById(R.id.thursday_label),
                view.findViewById(R.id.friday_label),
                view.findViewById(R.id.saturday_label),
                view.findViewById(R.id.sunday_label)
        };
        progressBars = new ProgressBar[]{
                view.findViewById(R.id.progressBar1),
                view.findViewById(R.id.progressBar2),
                view.findViewById(R.id.progressBar3),
                view.findViewById(R.id.progressBar4),
                view.findViewById(R.id.progressBar5),
                view.findViewById(R.id.progressBar6),
                view.findViewById(R.id.progressBar7)
        };

        // Gün etiketlerini güncelle
        updateDayLabels();

        // ProgressBar'ların değerlerini güncelle
        updateProgressBars();

        return view;
    }

    // Gün etiketlerini güncelleyen metod
    private void updateDayLabels() {
        // Haftanın günlerini al
        Calendar calendar = Calendar.getInstance();
        List<String> daysOfWeek = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            daysOfWeek.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Bir sonraki güne geç
        }

        // Veritabanından haftanın tarihlerini çek
        mDatabase.child("users").child(mCurrentUser.getUid()).child("weeklycalories")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Tarihler listesi
                            List<String> dates = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                dates.add(snapshot.getKey());
                            }

                            // Tarihleri sırala
                            for (int i = 0; i < Math.min(daysOfWeek.size(), dates.size()); i++) {
                                TextView dayLabel = dayLabels[i];
                                String date = dates.get(i);
                                // Gün etiketlerini tarihlerle güncelle
                                dayLabel.setText(date);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Veritabanı hatası
                    }
                });
    }

    // ProgressBar'ların değerlerini güncelleyen metod
    private void updateProgressBars() {
        // Haftanın tarihlerini al
        List<String> datesOfWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            datesOfWeek.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Bir sonraki güne geç
        }

        // Başlangıç değeri ve maksimum değeri ayarla
        int startValue = 3000; // Başlangıç değeri
        int maxValue = startValue; // Maksimum değer

        // Weeklycalories altındaki tarihlerin değerlerini ayarla
        for (int i = 0; i < progressBars.length; i++) {
            progressBars[i].setProgress(startValue);
            progressBars[i].setMax(startValue);
        }

        // progressBar7'nin maksimum değerini güncelle
        progressBars[6].setMax(maxValue);
    }

}


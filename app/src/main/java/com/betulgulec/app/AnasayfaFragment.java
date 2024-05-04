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

public class AnasayfaFragment extends Fragment {

    private TextView textViewHedef;
    private ProgressBar progressBar;
    private int targetCalories;
    private DatabaseReference todaysCaloriesRef;
    private ValueEventListener todaysCaloriesListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anasayfa, container, false);
        textViewHedef = view.findViewById(R.id.textViewHedef);
        progressBar = view.findViewById(R.id.progressBar);

        // Firebase'den hedef kaloriyi al ve textViewHedef'e ayarla
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            targetCalories = user.getTargetCalories();
                            textViewHedef.setText(String.valueOf(targetCalories));

                            // Progres barının maksimum değerini hedef kalori olarak ayarla
                            progressBar.setMax(targetCalories);

                            // todaystotalcalories değerini dinlemek için referans oluştur
                            todaysCaloriesRef = userRef.child("dailydata").child(getTodayDate()).child("todaysfood").child("todaystotalcalories");

                            // todaystotalcalories değerini dinlemeye başla
                            todaysCaloriesListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        int todaysTotalCalories = snapshot.getValue(Integer.class);
                                        // Progres barının ilerlemesini todaystotalcalories değerine ayarla
                                        progressBar.setProgress(todaysTotalCalories);

                                        // İlerleme yüzdesine göre renk değiştirme
                                        double progressPercentage = (double) todaysTotalCalories / targetCalories * 100;
                                        int progressColor = Color.GREEN; // Başlangıç rengi yeşil
                                        if (progressPercentage >= 75) {
                                            progressColor = Color.RED; // %75 ve üzeri ise kırmızı
                                        } else if (progressPercentage >= 50) {
                                            progressColor = Color.parseColor("#FFA500"); // %50 - %75 arası turuncu
                                        } else if (progressPercentage >= 25) {
                                            progressColor = Color.YELLOW; // %25 - %50 arası sarı
                                        }
                                        progressBar.setProgressTintList(ColorStateList.valueOf(progressColor));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Veritabanı erişiminde hata oluştuğunda yapılacak işlemler
                                }
                            };

                            todaysCaloriesRef.addValueEventListener(todaysCaloriesListener);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Veritabanı erişiminde hata oluştuğunda yapılacak işlemler
                }
            });
        }

        return view;
    }

    // Bugünün tarihini almak için yardımcı bir metod
    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // todaystotalcalories değerini dinlemeyi durdur
        if (todaysCaloriesRef != null && todaysCaloriesListener != null) {
            todaysCaloriesRef.removeEventListener(todaysCaloriesListener);
        }
    }
}

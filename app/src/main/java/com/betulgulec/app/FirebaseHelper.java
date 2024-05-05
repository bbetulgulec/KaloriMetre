package com.betulgulec.app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirebaseHelper {
    private DatabaseReference mDatabase;

    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // Kullanıcının temel bilgilerini kaydet
    public void saveUserBasicInformation(String userId, String ad, String soyad, String mail, String telefon, String password) {
        // Kullanıcı bilgilerini User nesnesine yerleştir
        User user = new User(ad, soyad, mail, telefon, password);

        // DatabaseReference kullanarak veriyi belirtilen yola (path) yerleştir
        mDatabase.child("users").child(userId).setValue(user);
    }

    // Kullanıcının ek bilgilerini kaydet
    public void saveAdditionalUserInfoRealtime(String userId, String gender, float weight, float height, int age, int targetCalories) {
        // Kullanıcı bilgilerini User nesnesine yerleştir
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("gender", gender);
        additionalInfo.put("weight", weight);
        additionalInfo.put("height", height);
        additionalInfo.put("age", age);
        additionalInfo.put("targetCalories", targetCalories);

        // DatabaseReference kullanarak veriyi belirtilen yola (path) yerleştir
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        userRef.updateChildren(additionalInfo);

        // Daily data oluştur
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todaysDate = dateFormat.format(calendar.getTime());
        DatabaseReference dailyDataRef = userRef.child("dailydata").child(todaysDate);

        // Todaysfood alt düğümü oluştur
                DatabaseReference todaysFoodRef = dailyDataRef.child("todaysfood");

        // todaystotalcalories alanını 0 olarak başlat
                dailyDataRef.child("todaystotalcalories").setValue(0);

        // foodname alanını boş string olarak başlat
                todaysFoodRef.child("foodname").setValue("");


        // Weekly calories oluştur
        DatabaseReference weeklyCaloriesRef = userRef.child("weeklycalories");
        for (int i = 0; i < 7; i++) {
            // Bugünün tarihinden i gün öncesini al
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String date = dateFormat.format(calendar.getTime());

            // Her bir tarih için bir child oluştur ve değerini 0 olarak ayarla
            weeklyCaloriesRef.child(date).setValue(0);

            // Tarihi tekrar bugünün tarihine geri döndür
            calendar.add(Calendar.DAY_OF_YEAR, i);
        }
    }

    // Kullanıcının hedef kalorisini kaydet
    public void saveUserTargetCalories(String userId, int targetCalories) {
        // Hedef kaloriyi Map yapısında yerleştir
        Map<String, Object> targetCaloriesMap = new HashMap<>();
        targetCaloriesMap.put("targetCalories", targetCalories);

        // DatabaseReference kullanarak veriyi belirtilen yola (path) yerleştir
        mDatabase.child("users").child(userId).updateChildren(targetCaloriesMap);
    }

    // Günlük kalori ihtiyacını hesapla
    public int calculateDailyCalorieNeed(String gender, float weight, float height, int age) {
        int calorieNeed = 0;

        // Kalori hesaplama formülü
        if (gender.equals("Erkek")) {
            calorieNeed = (int) (88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age));
        } else if (gender.equals("Kadın")) {
            calorieNeed = (int) (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age));
        }
        return calorieNeed;
    }

    public void updateTodaysDate(String userId) {
        // Günün tarihini al
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todaysDate = dateFormat.format(calendar.getTime());

        // Kullanıcının dailydata düğümündeki todaysDate alanının değerini güncelle
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("todaysDate", todaysDate);
        mDatabase.child("users").child(userId).child("dailydata").updateChildren(updateMap);
    }



    // Toplam kalorileri kaydet
    public void saveTotalCalories(String userId, String date, int totalCalories) {
        // DatabaseReference kullanarak totalCalories değerini belirtilen yola (path) yerleştir
        mDatabase.child("users").child(userId).child("weeklycalories").child(date).setValue(totalCalories);
    }
    // todaystotalcalories alt düğümüne kaloriyi ekleyen metod
    // todaystotalcalories ve foodname alt düğümlerine kaloriyi ve yiyecek adını ekleyen metod
    public void addCalorieAndFoodNameToToday(String userId, int calorie, String foodName) {
        // Günün tarihini al
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todaysDate = dateFormat.format(calendar.getTime());

        // todaystotalcalories düğümüne kaloriyi ekle
        DatabaseReference todaysTotalCaloriesRef = mDatabase.child("users").child(userId)
                .child("dailydata").child(todaysDate).child("todaystotalcalories");
        todaysTotalCaloriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Mevcut toplam kaloriyi al
                Integer currentCalories = dataSnapshot.getValue(Integer.class);

                if (currentCalories == null) {
                    // Mevcut kalori değeri null ise, sıfır olarak başlat
                    currentCalories = 0;
                }

                // Yeni toplam kaloriyi hesapla ve düğüme ekle
                int newCalories = currentCalories + calorie;
                todaysTotalCaloriesRef.setValue(newCalories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Database", "Veritabanı hatası: " + databaseError.getMessage());
            }
        });

        // foodname düğümüne yiyecek adını ekle
        DatabaseReference foodNameRef = mDatabase.child("users").child(userId)
                .child("dailydata").child(todaysDate).child("todaysfood").child("foodname");
        foodNameRef.setValue(foodName);
    }


}

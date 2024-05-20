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

    // İlk kullanıcının ek bilgilerini kaydet
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

        // Bazal Metabolizma Kullanılarak hesaplama yapıldı.
        if (gender.equals("Erkek")) {
            calorieNeed = (int) (1.3*(66.5+ (13.75 * weight) + (5 * height) - (6.77 * age)));
        } else if (gender.equals("Kadın")) {
            calorieNeed = (int) (655.1 + (9.56 * weight) + (1.85 * height) - (4.67 * age));
        }
        return calorieNeed;
    }

    // todaystotalcalories ve foodname alt düğümlerine kaloriyi ve yiyecek adını ekleyen metod
    public void addCalorieAndFoodNameToToday(String userId, int calorie, String foodName) {
        // Günün tarihini al
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todaysDate = dateFormat.format(calendar.getTime());

        // todaystotalcalories düğümüne kaloriyi ekle
        DatabaseReference dailyDataRef = mDatabase.child("users").child(userId)
                .child("dailydata").child(todaysDate);

        // Mevcut total kaloriyi al
        dailyDataRef.child("todaystotalcalories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentCalories = dataSnapshot.getValue(Integer.class);
                if (currentCalories == null) {
                    currentCalories = 0;
                }

                // Yeni toplam kaloriyi hesapla ve düğüme ekle
                int newCalories = currentCalories + calorie;
                dailyDataRef.child("todaystotalcalories").setValue(newCalories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Database", "Veritabanı hatası: " + databaseError.getMessage());
            }
        });

        // todaysfood alt düğümüne yemek adını key ve kaloriyi value olarak ekle
        DatabaseReference todaysFoodRef = dailyDataRef.child("todaysfood");

        todaysFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean foodExists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String existingFoodName = snapshot.getKey();
                    Integer existingCalories = snapshot.getValue(Integer.class);

                    if (existingFoodName != null && existingFoodName.startsWith(foodName)) {
                        foodExists = true;
                        int currentCount = 1;

                        // İsim formatını kontrol et ve varsa sayıyı artır
                        int startIndex = existingFoodName.lastIndexOf('(');
                        int endIndex = existingFoodName.lastIndexOf(')');
                        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                            try {
                                currentCount = Integer.parseInt(existingFoodName.substring(startIndex + 1, endIndex)) + 1;
                                String newFoodName = foodName + " (" + currentCount + ")";
                                int newCalories = existingCalories + calorie;

                                // Güncellenmiş ismi ve kaloriyi veritabanına kaydet
                                todaysFoodRef.child(existingFoodName).removeValue();
                                todaysFoodRef.child(newFoodName).setValue(newCalories);
                            } catch (NumberFormatException e) {
                                Log.e("Firebase Database", "Yemek ismi format hatası: " + e.getMessage());
                            }
                        } else {
                            // İlk defa sayı ekleniyor, "(2)" olarak güncelle
                            String newFoodName = foodName + " (2)";
                            int newCalories = existingCalories + calorie;

                            // Güncellenmiş ismi ve kaloriyi veritabanına kaydet
                            todaysFoodRef.child(existingFoodName).removeValue();
                            todaysFoodRef.child(newFoodName).setValue(newCalories);
                        }
                        break;
                    }
                }

                if (!foodExists) {
                    // Aynı isimde yiyecek yoksa doğrudan ekle
                    todaysFoodRef.child(foodName).setValue(calorie);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Database", "Veritabanı hatası: " + databaseError.getMessage());
            }
        });
    }

}
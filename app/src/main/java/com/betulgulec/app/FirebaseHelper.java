package com.betulgulec.app;

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
        DatabaseReference todaysFoodRef = dailyDataRef.child("todaysfood");
        todaysFoodRef.child("todaystotalcalories").setValue(0); // İlk olarak 0 kalori olarak başlat

        // Weekly calories oluştur
        DatabaseReference weeklyCaloriesRef = userRef.child("weeklycalories");
        for (int i = 1; i <= 7; i++) {
            weeklyCaloriesRef.child("day" + i).setValue(0); // Her günü 0 kalori olarak başlat
        }
    }

    public void addFoodItemToTodaysFood(String userId, String foodCategory, String foodName, int foodCalories) {
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todaysDate = dateFormat.format(calendar.getTime());
        DatabaseReference todaysFoodRef = userRef.child("dailydata").child(todaysDate).child("todaysfood").push();

        // Yemeğin kalorisini ve ID'sini ekleyin
        Map<String, Object> foodInfo = new HashMap<>();
        foodInfo.put("calories", foodCalories);
        foodInfo.put("name", foodName);

        todaysFoodRef.setValue(foodInfo);
    }

    // Yemek ID'si oluşturur
    private String generateFoodItemId(String userId, String foodCategory, String foodName) {
        // Burada yemek ID'si oluşturun, örneğin: userId_foodCategory_foodName
        return userId + "_" + foodCategory + "_" + foodName;
    }

    public interface UserTargetCaloriesListener {
        void onUserTargetCaloriesReceived(int targetCalories);

        void onError(String errorMessage);
    }

    public void getUserTargetCalories(String userId, UserTargetCaloriesListener listener) {
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int targetCalories = snapshot.child("targetCalories").getValue(Integer.class);
                    listener.onUserTargetCaloriesReceived(targetCalories);
                } else {
                    listener.onError("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }

    // Kullanıcının hedef kalorisini kaydet

    public void saveUserTargetCalories(String userId, int targetCalories) {
        // Hedef kaloriyi Map yapısında yerleştir
        Map<String, Object> targetCaloriesMap = new HashMap<>();
        targetCaloriesMap.put("targetCalories", targetCalories);

        // DatabaseReference kullanarak veriyi belirtilen yola (path) yerleştir
        mDatabase.child("users").child(userId).child("targetCalories").updateChildren(targetCaloriesMap);
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
}
package com.betulgulec.app;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
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
    public void saveAdditionalUserInfo(String userId, String gender, float weight, float height, int age) {
        // Kullanıcı bilgilerini User nesnesine yerleştir
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("gender", gender);
        additionalInfo.put("weight", weight);
        additionalInfo.put("height", height);
        additionalInfo.put("age", age);

        // DatabaseReference kullanarak veriyi belirtilen yola (path) yerleştir
        mDatabase.child("users").child(userId).updateChildren(additionalInfo);
    }
}

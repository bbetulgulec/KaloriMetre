package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ilkkullanici extends AppCompatActivity {
    private EditText editTextKilo, editTextBoy, editTextYas;
    private RadioGroup radioGroupCinsiyet;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilkkullanici);
        initComponents();
        FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();
    }

    private void initComponents() {
        editTextKilo = findViewById(R.id.editTextKilo);
        editTextBoy = findViewById(R.id.editTextBoy);
        editTextYas = findViewById(R.id.editTextYas);
        radioGroupCinsiyet = findViewById(R.id.radioGroupCinsiyet);
        MaterialButton btndegistir = findViewById(R.id.btndegistir);

        btndegistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degistir();
            }
        });
    }


    public void degistir() {
        boolean isValid = true;

        int selectedRadioButtonId = radioGroupCinsiyet.getCheckedRadioButtonId();
        String gender = "";
        if (selectedRadioButtonId == R.id.radioButtonErkek) {
            gender = "Erkek";
        } else if (selectedRadioButtonId == R.id.radioButtonKadin) {
            gender = "Kadın";
        } else {
            Toast.makeText(this, "Cinsiyet seçiniz.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        String kilo = editTextKilo.getText().toString().trim();
        String boy = editTextBoy.getText().toString().trim();
        String yas = editTextYas.getText().toString().trim();

        if (kilo.isEmpty()) {
            editTextKilo.setError("Kilo giriniz.");
            isValid = false;
        }

        if (boy.isEmpty()) {
            editTextBoy.setError("Boy giriniz.");
            isValid = false;
        }

        if (yas.isEmpty()) {
            editTextYas.setError("Yaş giriniz.");
            isValid = false;
        }

        if (isValid) {
            float weight = Float.parseFloat(kilo);
            float height = Float.parseFloat(boy);
            int age = Integer.parseInt(yas);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Günlük kalori ihtiyacını hesapla
            int dailyCalorieNeed = firebaseHelper.calculateDailyCalorieNeed(gender, weight, height, age);

            // Kullanıcı bilgilerini Firebase Realtime Database'e kaydet
            firebaseHelper.saveAdditionalUserInfoRealtime(userId, gender, weight, height, age, dailyCalorieNeed);

            // Hedef sayfasına veriyi gönder
            Intent intent = new Intent(ilkkullanici.this, anasayfa.class);
            intent.putExtra("dailyCalorieNeed", dailyCalorieNeed);
            startActivity(intent);
            finish();
        }
    }


}

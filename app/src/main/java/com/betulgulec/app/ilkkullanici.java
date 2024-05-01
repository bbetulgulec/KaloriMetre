package com.betulgulec.app;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private MaterialButton btndegistir;
    private SharedPreferences sharedPreferences;

    private MaterialButton btncikis;
    private FirebaseHelper firebaseHelper;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilkkullanici);
        initComponents();
        FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();

        // Önceki aktiviteden userId'yi al
        userId = getIntent().getStringExtra("userId");
    }

    private void initComponents() {
        editTextKilo = findViewById(R.id.editTextKilo);
        editTextBoy = findViewById(R.id.editTextBoy);
        editTextYas = findViewById(R.id.editTextYas);
        radioGroupCinsiyet = findViewById(R.id.radioGroupCinsiyet);
        btndegistir=findViewById(R.id.btndegistir);
    }

    public void degistir(View view) {
        boolean isValid = true;

        // Seçilen RadioButton'un ID'sini al
        int selectedRadioButtonId = radioGroupCinsiyet.getCheckedRadioButtonId();

        // RadioButton ID'sine göre cinsiyeti belirle
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

            // Parola bilgisini almak için FirebaseUser nesnesi üzerinden getCurrentUser metodu kullanılır.
            String password = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getProviderId();

            firebaseHelper.saveAdditionalUserInfo(userId, gender, weight, height, age);
            startActivity(new Intent(ilkkullanici.this, anasayfa.class));
            finish();
        }
    }
}

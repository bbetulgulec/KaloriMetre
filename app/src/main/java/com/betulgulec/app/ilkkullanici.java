package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ilkkullanici extends AppCompatActivity {



    private Button btndegistir;

    private EditText editTextKilo, editTextBoy, editTextYas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ilkkullanici);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        editTextKilo = findViewById(R.id.editTextKilo);
        editTextBoy = findViewById(R.id.editTextBoy);
        editTextYas = findViewById(R.id.editTextYas);
        btndegistir= findViewById(R.id.btndegistir);

        checkInputs();

    }

    private void checkInputs() {
        String kilo = editTextKilo.getText().toString().trim();
        String boy = editTextBoy.getText().toString().trim();
        String yas = editTextYas.getText().toString().trim();

        // Kilosu, boyu ve yaşı girilmişse
        if (!kilo.isEmpty() ) {
            btndegistir.setEnabled(true); // Değiştir butonunu aktifleştir
        } else {
            btndegistir.setEnabled(false);
            editTextKilo.setText("Kilo giriniz ");// Değiştir butonunu deaktifleştir
        }

        if (!boy.isEmpty() ) {
            btndegistir.setEnabled(true); // Değiştir butonunu aktifleştir
        } else {
            btndegistir.setEnabled(false);
            editTextKilo.setText("Boy giriniz ");// Değiştir butonunu deaktifleştir
        }

        if (!yas.isEmpty() ) {
            btndegistir.setEnabled(true); // Değiştir butonunu aktifleştir
        } else {
            btndegistir.setEnabled(false);
            editTextKilo.setText("Yas giriniz ");// Değiştir butonunu deaktifleştir
        }

    }
    public void degistir(View view){

        Intent intent=new Intent(this, anasayfa.class);
        startActivity(intent);


    }
}
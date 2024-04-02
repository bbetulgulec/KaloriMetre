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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class ilkkullanici extends AppCompatActivity {

    private EditText editTextKilo, editTextBoy, editTextYas;
    private RadioGroup radioGroupCinsiyet;
    private MaterialButton btndegistir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilkkullanici);
        initComponents();



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

        if (radioGroupCinsiyet.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Cinsiyet seçiniz.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (editTextKilo.getText().toString().trim().isEmpty()) {
            editTextKilo.setError("Kilo giriniz.");
            isValid = false;
        }

        if (editTextBoy.getText().toString().trim().isEmpty()) {
            editTextBoy.setError("Boy giriniz.");
            isValid = false;
        }

        if (editTextYas.getText().toString().trim().isEmpty()) {
            editTextYas.setError("Yaş giriniz.");
            isValid = false;
        }

        if (isValid) {

           btndegistir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ilkkullanici.this, anasayfa.class));
                    finish();
                }
            });

        }
    }
}
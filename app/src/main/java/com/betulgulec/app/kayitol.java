package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class kayitol extends AppCompatActivity {

    private TextInputLayout adLayout, soyadLayout, mailLayout, telefonLayout, passwordLayout;
    private Button btnkayitOl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayitol);
        initComponents();
    }

    private void initComponents() {
        adLayout = findViewById(R.id.adLayout);
        soyadLayout = findViewById(R.id.soyadLayout);
        mailLayout = findViewById(R.id.mailLayout);
        telefonLayout = findViewById(R.id.telefonLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        btnkayitOl = findViewById(R.id.btnkayitOl);
        btnkayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEditText(adLayout) && validateEditText(soyadLayout) &&
                        validateEditText(mailLayout) && validateEditText(telefonLayout) &&
                        validateEditText(passwordLayout)) {
                    startActivity(new Intent(kayitol.this, girisyap.class));
                    finish(); // Bu aktiviteyi kapat
                } else {
                    if (!validateEditText(adLayout)) {
                        adLayout.setError("Lütfen Adınızı giriniz");
                    }
                    if (!validateEditText(soyadLayout)) {
                        soyadLayout.setError("Lütfen Soyadınızı giriniz");
                    }
                    if (!validateEditText(mailLayout)) {
                        mailLayout.setError("Lütfen Mail adresinizi giriniz");
                    }
                    if (!validateEditText(telefonLayout)) {
                        telefonLayout.setError("Lütfen Telefon numaranızı giriniz");
                    }
                    if (!validateEditText(passwordLayout)) {
                        passwordLayout.setError("Lütfen Şifre oluşturunuz ");
                    }
                    Toast.makeText(kayitol.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateEditText(TextInputLayout layout) {
        String input = layout.getEditText().getText().toString().trim();
        return !input.isEmpty();
    }
}
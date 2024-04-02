package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;


public class kayitol extends AppCompatActivity {

    private Button btnkayitOl;
    private TextInputLayout adLayout,soyadLayout,mailLayout,telefonLayout,passwordLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kayitol);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        initComponents();
        registerEventHandlers();
    }

    private void registerEventHandlers() {
        btnkayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isadValid=validateEditText(adLayout);
                boolean issoyadValid=validateEditText(soyadLayout);
                boolean ismailValid=validateEditText(mailLayout);
                boolean istelefonValid=validateEditText(telefonLayout);
                boolean ispasswordValid=validateEditText(passwordLayout);


                if(!isadValid)
                    adLayout.setError("Hatalı Kullanıcı Adı!");
                else
                    adLayout.setError(null);
                if(!issoyadValid)
                    soyadLayout.setError("Hatalı Kullanıcı Soyadı!");
                else
                    soyadLayout.setError(null);
                if(!ismailValid)
                    mailLayout.setError("Hatalı Mail!");
                else
                    mailLayout.setError(null);
                if(!istelefonValid)
                    telefonLayout.setError("Hatalı Kullanıcı Telefonu!");
                else
                    telefonLayout.setError(null);
                if(!ispasswordValid)
                    passwordLayout.setError("Hatalı Kullanıcı Şifresi!");
                else
                    passwordLayout.setError(null);


                if(isadValid && issoyadValid && ismailValid && istelefonValid && ispasswordValid)
                    Toast.makeText(kayitol.this, "Giriş Yapıldı ", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private boolean validateEditText(TextInputLayout layout) {
        String input = layout.getEditText().getText().toString();
        return input.length() > 1;
    }


    private void initComponents() {
        adLayout = findViewById(R.id.adLayout);
        soyadLayout = findViewById(R.id.soyadLayout);
        mailLayout = findViewById(R.id.mailLayout);
        telefonLayout = findViewById(R.id.telefonLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        btnkayitOl = findViewById(R.id.btnkayitOl);

    }

    public void kayitol(View view){

        Intent intent=new Intent(this, girisyap.class);
        startActivity(intent);

    }
}
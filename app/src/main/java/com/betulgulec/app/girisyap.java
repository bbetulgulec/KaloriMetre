package com.betulgulec.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class girisyap extends AppCompatActivity {
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private EditText editTextMail, editTextPassword;

    private Button btnsifreunuttum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girisyap);

        editTextMail = findViewById(R.id.mail);
        editTextPassword = findViewById(R.id.password);
        btnsifreunuttum=findViewById(R.id.btnsifreunuttum);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Eğer kullanıcı daha önce oturum açmışsa ve bu bilgi sharedPreferences'te varsa, direkt ana sayfaya yönlendir
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(girisyap.this, anasayfa.class));
            finish();
        }

        //şifremi unuttum butonuna basarsa

        btnsifreunuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(girisyap.this, SifremiUnuttum.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void girisyapmetod(View view) {
        String email = editTextMail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editTextMail.setError("Lütfen e-posta adresinizi girin.");
            editTextMail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Lütfen şifrenizi girin.");
            editTextPassword.requestFocus();
            return;
        }

        // Kullanıcı giriş işlemleri
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Kullanıcı başarıyla giriş yaptıysa yapılacak işlemler
                        Toast.makeText(girisyap.this, "Başarıyla giriş yaptınız.", Toast.LENGTH_SHORT).show();
                        // Oturum bilgisini sharedPreferences'e kaydet
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();
                        startActivity(new Intent(girisyap.this, anasayfa.class));
                        finish();
                    } else {
                        // Giriş başarısız olduğunda yapılacak işlemler
                        Toast.makeText(girisyap.this, "Giriş başarısız! Lütfen e-posta ve şifrenizi kontrol edin.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void hesapolustur(View view) {
        Intent intent = new Intent(this, kayitol.class);
        startActivity(intent);
    }


}
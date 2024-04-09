package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class girisyap extends AppCompatActivity {

    private EditText editTextMail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_girisyap);

        editTextMail = findViewById(R.id.mail);
        editTextPassword = findViewById(R.id.password);
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

    public void sifreunuttum(View view) {
        // Şifremi unuttum ekranına geçiş yap
    }
}

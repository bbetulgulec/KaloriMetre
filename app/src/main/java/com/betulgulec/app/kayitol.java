package com.betulgulec.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class kayitol extends AppCompatActivity {

    private EditText editTextad, editTextsoyad, editTextmail, editTexttelefon, editTextpassword;
    private FirebaseAuth mAuth;
    private FirebaseHelper firebaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayitol);

        mAuth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper(); // FirebaseHelper örneği oluştur
        editTextad = findViewById(R.id.ad);
        editTextsoyad = findViewById(R.id.soyad);
        editTextmail = findViewById(R.id.mail);
        editTexttelefon = findViewById(R.id.telefon);
        editTextpassword = findViewById(R.id.password);

        // SharedPreferences örneğini oluştur
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        Button kayitbutonu = findViewById(R.id.btnkayitOl);
        kayitbutonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textAd = editTextad.getText().toString();
                String textSoyad = editTextsoyad.getText().toString();
                String textMail = editTextmail.getText().toString();
                String textTelefon = editTexttelefon.getText().toString();
                String textPassword = editTextpassword.getText().toString();

                if (TextUtils.isEmpty(textAd)) {
                    editTextad.setError("Lütfen adınızı giriniz.");
                    editTextad.requestFocus();
                } else if (TextUtils.isEmpty(textSoyad)) {
                    editTextsoyad.setError("Lütfen soyadınızı giriniz.");
                    editTextsoyad.requestFocus();
                } else if (TextUtils.isEmpty(textMail) || !isValidEmail(textMail)) {
                    editTextmail.setError("Lütfen geçerli bir e-posta adresi giriniz.\nÖrnek: example@example.com");
                    editTextmail.requestFocus();
                } else if (TextUtils.isEmpty(textTelefon)) {
                    editTexttelefon.setError("Lütfen telefon numaranızı giriniz.");
                    editTexttelefon.requestFocus();
                } else if (TextUtils.isEmpty(textPassword) || textPassword.length() < 6) {
                    editTextpassword.setError("Şifreniz en az 6 karakter uzunluğunda olmalıdır.");
                    editTextpassword.requestFocus();
                } else {
                    // Kullanıcıyı kaydet
                    registerUser(textAd, textSoyad, textMail, textTelefon, textPassword);
                }
            }
        });
    }

    // Kullanıcıyı kaydetme işlemi
    public void registerUser(String textAd, String textSoyad, String textMail, String textTelefon, String textPassword) {
        mAuth.createUserWithEmailAndPassword(textMail, textPassword)
                .addOnCompleteListener(kayitol.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                // FirebaseHelper kullanarak kullanıcı bilgilerini kaydet
                                firebaseHelper.saveUserBasicInformation(userId, textAd, textSoyad, textMail, textTelefon, textPassword);

                                // SharedPreferences'e kaydet
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                // İlk kullanıcı ekranına yönlendir
                                Intent intent = new Intent(kayitol.this, ilkkullanici.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(kayitol.this, "Kullanıcı kaydedilemedi!" +
                                        " FirebaseUser null.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(kayitol.this, "Kullanıcı kaydedilemedi! " +
                                    "Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // E-posta adresinin geçerli olup olmadığını kontrol et
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Giriş sayfasına yönlendir
    public void kayitol(View view) {
        Intent intent = new Intent(this, girisyap.class);
        startActivity(intent);
    }
}

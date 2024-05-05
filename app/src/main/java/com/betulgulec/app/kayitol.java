package com.betulgulec.app;

import android.content.Intent;
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

    private void registerUser(String textAd, String textSoyad, String textMail, String textTelefon, String textPassword) {
        mAuth.createUserWithEmailAndPassword(textMail, textPassword) // (1) Yeni bir kullanıcı oluşturma işlemini başlatır.
                .addOnCompleteListener(kayitol.this, new OnCompleteListener<AuthResult>() { // (2) Oluşturma işleminin sonucunu dinlemek için bir listener ekler.
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { // (3) Oluşturma işleminin tamamlanma durumunu işler.
                        FirebaseUser firebaseUser = mAuth.getCurrentUser(); // (4) Mevcut Firebase kullanıcısını alır.
                        if (firebaseUser != null) { // (5) Firebase kullanıcısı mevcutsa işlemi devam ettirir.
                            firebaseUser.sendEmailVerification() // (6) Kullanıcıya e-posta doğrulama gönderme işlemini başlatır.
                                    .addOnCompleteListener(new OnCompleteListener<Void>() { // (7) E-posta doğrulama işleminin sonucunu dinler.
                                        @Override
                                        public void onComplete(@NonNull Task<Void> emailTask) { // (8) E-posta doğrulama işleminin tamamlanma durumunu işler.
                                            if (emailTask.isSuccessful()) { // (9) E-posta doğrulama işlemi başarılıysa
                                                Toast.makeText(kayitol.this, "E-posta doğrulama gönderildi. Lütfen e-posta adresinizi kontrol edin ve doğrulayın.", Toast.LENGTH_LONG).show(); // (10) Başarılı gönderim durumunda kullanıcıya bir mesaj gösterir.
                                            } else {
                                                Toast.makeText(kayitol.this, "E-posta doğrulama gönderilemedi! Hata: " + emailTask.getException().getMessage(), Toast.LENGTH_LONG).show(); // (11) Gönderim başarısızsa bir hata mesajı gösterir.
                                            }
                                        }
                                    });

                            if (task.isSuccessful()) { // (12) Kullanıcı oluşturma işlemi başarılıysa
                                mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() { // (13) Mevcut kullanıcının bilgilerini güncellemek için bir işlem başlatır.
                                    @Override
                                    public void onComplete(@NonNull Task<Void> reloadTask) { // (14) Yeniden yükleme işleminin tamamlanma durumunu işler.
                                        if (reloadTask.isSuccessful()) { // (15) Yeniden yükleme işlemi başarılıysa
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser(); // (16) Mevcut Firebase kullanıcısını alır.
                                            if (firebaseUser != null && firebaseUser.isEmailVerified()) { // (17) Firebase kullanıcısı mevcut ve e-postası doğrulanmışsa
                                                String userId = firebaseUser.getUid(); // (18) Kullanıcı kimliğini alır.
                                                firebaseHelper.saveUserBasicInformation(userId, textAd, textSoyad, textMail, textTelefon, textPassword); // (19) Kullanıcı bilgilerini Firebase Realtime Database'e kaydeder.
                                                Intent intent = new Intent(kayitol.this, ilkkullanici.class); // (20) Yeni bir intent oluşturur.
                                                intent.putExtra("userId", userId); // (21) Kullanıcı kimliğini intent'e ekler.
                                                startActivity(intent); // (22) Yeni aktiviteyi başlatır.
                                                finish(); // (23) Kayıt aktivitesini sonlandırır.
                                            } else {
                                                Toast.makeText(kayitol.this, "E-posta adresinizi doğrulayın.", Toast.LENGTH_LONG).show(); // (24) E-posta doğrulaması başarısızsa bir hata mesajı gösterir.
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(kayitol.this, "Kullanıcı kaydedilemedi! Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show(); // (25) Firebase kullanıcısı alınamazsa bir hata mesajı gösterir.
                        }
                    }
                });
    }

    private void saveUserInformationAndNavigateToNextPage(String textAd, String textSoyad, String textMail, String textTelefon, String textPassword) {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> reloadTask) {
                if (reloadTask.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                        String userId = firebaseUser.getUid();
                        firebaseHelper.saveUserBasicInformation(userId, textAd, textSoyad, textMail, textTelefon, textPassword);
                        Intent intent = new Intent(kayitol.this, ilkkullanici.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(kayitol.this, "E-posta adresinizi doğrulayın.", Toast.LENGTH_LONG).show();
                    }
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
package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SifremiUnuttum extends AppCompatActivity {

    private MaterialButton btngeridon;
    private TextInputEditText mail;
    private Button btnsifresifirla;
    FirebaseAuth mAuth;
    private String strEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sifremi_unuttum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btngeridon=findViewById(R.id.btngeridon);
        mail=findViewById(R.id.mail);
        btnsifresifirla=findViewById(R.id.btnsifresifirla);
        mAuth=FirebaseAuth.getInstance();

        // resetlemek için

        btnsifresifirla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 strEmail=mail.getText().toString().trim();
                if(!TextUtils.isEmpty(mail.getText())){
                    resetPassword();
                }else{
                    mail.setError("Girdiğin e mail yanlış bir daha gir ");
                }
            }
        });

        btngeridon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SifremiUnuttum.this, girisyap.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void resetPassword(){
        btnsifresifirla.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SifremiUnuttum.this, "Şifre yenileme işlemi Email'e tanımlandı  ", Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(SifremiUnuttum.this, girisyap.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SifremiUnuttum.this, "Hata "+e.getMessage(), Toast.LENGTH_SHORT).show();
                btnsifresifirla.setVisibility(View.INVISIBLE);
            }
        });
    }
}
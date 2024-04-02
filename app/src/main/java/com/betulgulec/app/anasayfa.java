package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import java.util.List;
public class anasayfa extends AppCompatActivity {

    private ImageButton home,hedef,menu,profil;

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anasayfa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Main thread içinde UI güncellemesi yap
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // 100ms gecikme ile ProgressBar'ı güncelle
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        initCompinant();
    }


    public void initCompinant(){


        home=findViewById(R.id.home);
        hedef=findViewById(R.id.hedef);
        menu=findViewById(R.id.menu);
        profil=findViewById(R.id.profil);

        // ANASAYFA BUTONUNA BASARSA
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(anasayfa.this, anasayfa.class);
                startActivity(intent);
            }
        });

        //HEDEF BUTONUNA BASARSA
        hedef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HEDEF BUTONUNA BASARSA
                Intent intent = new Intent(anasayfa.this, hedef.class);
                startActivity(intent);
            }
        });

        //MENÜ BUTONUNA BASARSA
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(anasayfa.this, menu.class);
                startActivity(intent);
            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(anasayfa.this, ilkkullanici.class);
                startActivity(intent);
            }
        });



    }








}
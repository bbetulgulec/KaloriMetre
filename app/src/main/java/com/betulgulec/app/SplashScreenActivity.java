package com.betulgulec.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash ekranın görüntüleneceği süre (milisaniye cinsinden)
    private static final int SPLASH_DISPLAY_LENGTH = 3000; // 3 saniye

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Splash ekranın görselini ImageView'da göster
        ImageView imageView = findViewById(R.id.imageViewSplash);
        imageView.setImageResource(R.drawable.salad);

        // Belirtilen süre sonra MainActivity'e geçişi sağla
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Bu aktiviteyi sonlandır
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}


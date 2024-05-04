package com.betulgulec.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class anasayfa extends AppCompatActivity {

    private BottomNavigationView battomNavigationView;
    private FrameLayout framelayout;
    SharedPreferences sharedPreferences;
    private boolean isAppInitialized;
    private TextView textViewHedef;
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

        initCompinant();
        loadFragment(new AnasayfaFragment(), true);
        navigationcontrol();

    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.commit();
    }

    private void loadFragment(Fragment fragment) {
        //loadFragment(fragment,false);
        loadFragment(fragment, false); // Varsayılan olarak isAppInitialized değerini false olarak ayarla
    }

    public void initCompinant() {

        battomNavigationView = findViewById(R.id.bottomNavView);
        framelayout = findViewById(R.id.frameLayout);
    }

    public void navigationcontrol() {

        battomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                int itemId = item.getItemId();

                loadFragment(new AnasayfaFragment(),true);

                if (itemId == R.id.navAnasayfa) {
                    loadFragment(new AnasayfaFragment());

                } else if (itemId == R.id.navIstatistik) {
                    loadFragment(new HedefFragment());
                } else if (itemId == R.id.navMenu) {
                    loadFragment(new MenuFragment());
                } else if (itemId == R.id.navProfil) {
                    loadFragment(new ProfilFragment());
                } else {

                    fragment = new AnasayfaFragment();
                }

                sharedPreferences = getSharedPreferences("MyPrefs", MainActivity.MODE_PRIVATE);
                boolean isAppInitialized = sharedPreferences.getBoolean("isAppInitialized", false);

                if (!isAppInitialized) {
                    //Uygulama ilk kez başlatılıyorsa, gerekli işlemleri yapın
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAppInitialized", true);
                    editor.apply();
                }
                return true;
            }
        });


    }
}

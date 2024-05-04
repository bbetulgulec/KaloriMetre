package com.betulgulec.app;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfilFragment extends Fragment {
    private EditText editTextKilo, editTextBoy, editTextYas;
    private RadioGroup radioGroupCinsiyet;
    private FirebaseHelper firebaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        initComponents(view);
        setupButtons(view);
        return view;
    }

    private void initComponents(View view) {
        editTextKilo = view.findViewById(R.id.editTextKilo);
        editTextBoy = view.findViewById(R.id.editTextBoy);
        editTextYas = view.findViewById(R.id.editTextYas);
        radioGroupCinsiyet = view.findViewById(R.id.radioGroupCinsiyet);
        firebaseHelper = new FirebaseHelper();
    }

    private void setupButtons(View view) {
        MaterialButton btndegistir = view.findViewById(R.id.btndegistir);
        btndegistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degistir(v);
            }
        });

        MaterialButton btncikis = view.findViewById(R.id.btncikis);
        btncikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cikisYap();
            }
        });
    }

    private void degistir(View view) {
        boolean isValid = true;

        int selectedRadioButtonId = radioGroupCinsiyet.getCheckedRadioButtonId();
        String gender = "";
        if (selectedRadioButtonId == R.id.radioButtonErkek) {
            gender = "Erkek";
        } else if (selectedRadioButtonId == R.id.radioButtonKadin) {
            gender = "Kadın";
        } else {
            Toast.makeText(getContext(), "Cinsiyet seçiniz.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        String kilo = editTextKilo.getText().toString().trim();
        String boy = editTextBoy.getText().toString().trim();
        String yas = editTextYas.getText().toString().trim();

        if (kilo.isEmpty()) {
            editTextKilo.setError("Kilo giriniz.");
            isValid = false;
        }

        if (boy.isEmpty()) {
            editTextBoy.setError("Boy giriniz.");
            isValid = false;
        }

        if (yas.isEmpty()) {
            editTextYas.setError("Yaş giriniz.");
            isValid = false;
        }

        if (isValid) {
            float weight = Float.parseFloat(kilo);
            float height = Float.parseFloat(boy);
            int age = Integer.parseInt(yas);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Günlük kalori ihtiyacını hesapla
            int dailyCalorieNeed = firebaseHelper.calculateDailyCalorieNeed(gender, weight, height, age);

            // Kullanıcı bilgilerini Firebase Realtime Database'e kaydet
            firebaseHelper.saveAdditionalUserInfoRealtime(userId, gender, weight, height, age, dailyCalorieNeed);

            // Hedef sayfasına veriyi gönder
            Intent intent = new Intent(getActivity(), anasayfa.class);
            intent.putExtra("dailyCalorieNeed", dailyCalorieNeed);
            startActivity(intent);

            getActivity().finish(); // Fragment'ın bağlı olduğu aktiviteyi sonlandır
        }
    }

    private void cikisYap() {
        FirebaseAuth.getInstance().signOut();

        // Oturum bilgisini temizle
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Giriş ekranına yönlendir
        Intent intent = new Intent(getActivity(), girisyap.class);
        startActivity(intent);
        getActivity().finish();
    }
}

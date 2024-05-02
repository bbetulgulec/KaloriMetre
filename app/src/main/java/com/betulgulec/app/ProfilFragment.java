package com.betulgulec.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfilFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private EditText editTextKilo, editTextBoy, editTextYas;
    private RadioGroup radioGroupCinsiyet;
    private FirebaseHelper firebaseHelper;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profil, container, false);
        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        initComponents(rootView);
        firebaseHelper = new FirebaseHelper();

        return rootView;
    }

    private void initComponents(View rootView) {
        // Get views from the layout
        MaterialButton btndegistir = rootView.findViewById(R.id.btndegistir);
        MaterialButton btncikis = rootView.findViewById(R.id.btncikis);


        editTextKilo = rootView.findViewById(R.id.editTextKilo);
        editTextBoy = rootView.findViewById(R.id.editTextBoy);
        editTextYas = rootView.findViewById(R.id.editTextYas);
        radioGroupCinsiyet = rootView.findViewById(R.id.radioGroupCinsiyet);

        // Set click listener for the "Değiştir" button
        btndegistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degistir(v);
            }
        });

        // Set click listener for the "Çıkış" button
        btncikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Çıkış yapıldığında SharedPreferences'teki oturum bilgilerini kaldır
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("isLoggedIn");
                editor.apply();

                // FirebaseAuth ile çıkış yap
                FirebaseAuth.getInstance().signOut();

                // Toast mesajı göster
                Toast.makeText(getActivity(), "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();

                // Giriş ekranına yönlendir
                Intent intent = new Intent(requireActivity(), girisyap.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

    }

    public void degistir(View view) {
        boolean isValid = true;

        // Seçilen RadioButton'un ID'sini al
        int selectedRadioButtonId = radioGroupCinsiyet.getCheckedRadioButtonId();

        // RadioButton ID'sine göre cinsiyeti belirle
        String gender = "";
        if (selectedRadioButtonId == R.id.radioButtonErkek) {
            gender = "Erkek";
        } else if (selectedRadioButtonId == R.id.radioButtonKadin) {
            gender = "Kadın";
        } else {
            Toast.makeText(getActivity(), "Cinsiyet seçiniz.", Toast.LENGTH_SHORT).show();
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

            // Parola bilgisini almak için FirebaseUser nesnesi üzerinden getCurrentUser metodu kullanılır.
            String password = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getProviderId();

            firebaseHelper.saveAdditionalUserInfo(userId, gender, weight, height, age);
            Toast.makeText(getActivity(), "Kullanıcı bilgileri güncellendi.", Toast.LENGTH_SHORT).show();
        }
    }
}
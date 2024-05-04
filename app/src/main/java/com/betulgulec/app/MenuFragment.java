package com.betulgulec.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuFragment extends Fragment {

    private DatabaseReference rootRef;
    private RecyclerView recyclerView;
    private ArrayList<FoodItem> foodList;
    private FoodAdapter foodAdapter;
    private EditText searchEditText;
    private TextView foodName, foodCalori;
    private Button btnekle;
    private CardView cardView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        rootRef = database.getReference(); // rootRef'i food düğümüne değil, root düğümüne eşitle
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Burada Firebase Realtime Database'e veri ekleyebilirsiniz
        DatabaseReference foodRef = rootRef.child("food");

        // Tatlılar alt düğümü
        DatabaseReference tatliRef = foodRef.child("tatli");
        HashMap<String, String> tatliData = new HashMap<>();
        tatliData.put("browni", "300");
        tatliData.put("cheesecake", "400");
        tatliData.put("profiterol", "350");
        tatliData.put("muhallebi", "200");
        tatliData.put("kazandibi", "250");
        tatliRef.setValue(tatliData);

        // Yiyecekler alt düğümü
        DatabaseReference yiyecekRef = foodRef.child("yiyecek");
        HashMap<String, String> yiyecekData = new HashMap<>();
        yiyecekData.put("pizza", "500");
        yiyecekData.put("hamburger", "600");
        yiyecekData.put("salata", "200");
        yiyecekData.put("makarna", "400");
        yiyecekData.put("sushi", "350");
        yiyecekRef.setValue(yiyecekData);

        // İçecekler alt düğümü
        DatabaseReference icecekRef = foodRef.child("icecek");
        HashMap<String, String> icecekData = new HashMap<>();
        icecekData.put("kola", "150");
        icecekData.put("limonata", "100");
        icecekData.put("çay", "50");
        icecekData.put("kahve", "80");
        icecekData.put("portakal suyu", "120");
        icecekRef.setValue(icecekData);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(foodAdapter);

        // TextView'leri initialize etme
        foodName = view.findViewById(R.id.foodName);
        foodCalori = view.findViewById(R.id.foodCalori);

        loadFoodItems();
        initComponent();

        // Arama butonuna onClick özelliğini ekleme
        Button btnAra = view.findViewById(R.id.btnAra);
        btnAra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFood();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // EditText'i başlat
        initComponent();
    }

    // EditText'i başlatan yöntem
    public void initComponent() {
        View rootView = getView();
        if (rootView != null) {
            searchEditText = rootView.findViewById(R.id.searchEditText);
            foodName = rootView.findViewById(R.id.foodName);
            foodCalori = rootView.findViewById(R.id.foodCalori);
            btnekle = rootView.findViewById(R.id.btnekle);
            cardView = rootView.findViewById(R.id.cardView);
        } else {
            Log.e("MenuFragment", "Root view is null");
        }
    }

    private void loadFoodItems() {
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodList.clear();
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    String foodName = foodSnapshot.getKey();
                    HashMap<String, String> foodData = (HashMap<String, String>) foodSnapshot.getValue();
                    String calorie = foodData.get("calori");
                    foodList.add(new FoodItem(foodName, calorie));
                }
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Database", "Veritabanı hatası: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Hata");
                builder.setMessage("Veritabanı hatası oluştu. Lütfen tekrar deneyin.");
                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadFoodItems();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }



    public void searchFood() {
        // Arama kutusundaki metni al
        String searchText = searchEditText.getText().toString().trim();

        // Arama yap
        if (!searchText.isEmpty()) {
            DatabaseReference foodRef = rootRef.child("food");

            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean found = false;
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot foodSnapshot : categorySnapshot.getChildren()) {
                            String foodName = foodSnapshot.getKey();
                            String calorie = foodSnapshot.getValue(String.class); // Calori value'sini al

                            // Aranan yemeğin adıyla eşleşen bir yemek bulunduğunda
                            if (foodName.equalsIgnoreCase(searchText)) {
                                // foodName ve foodCalori TextView'lerine değerleri ata
                                MenuFragment.this.foodName.setText(foodName);
                                MenuFragment.this.foodCalori.setText(calorie);
                                found = true;
                                break; // İç içe döngüden çık
                            }
                        }
                        if (found) break; // Dış döngüden çık
                    }
                    if (!found) {
                        // Eşleşme bulunamadı
                        Toast.makeText(getContext(), "Aranan yemek bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Veritabanı hatası
                    Log.d("Firebase Database", "Veritabanı hatası: " + databaseError.getMessage());
                    Toast.makeText(getContext(), "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Arama kutusu boş
            Toast.makeText(getContext(), "Lütfen aramak için bir yemek adı girin.", Toast.LENGTH_SHORT).show();
     }
}
}
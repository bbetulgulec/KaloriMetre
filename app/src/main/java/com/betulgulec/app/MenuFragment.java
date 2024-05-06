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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MenuFragment extends Fragment {

    private DatabaseReference rootRef;
    private DatabaseReference weeklyCaloriesRef;
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
        weeklyCaloriesRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weeklycalories");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // RecyclerView başlatma ve ayarları
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(foodAdapter);

        // TextView'leri initialize etme
        foodName = view.findViewById(R.id.foodName);
        foodCalori = view.findViewById(R.id.foodCalori);
        btnekle = view.findViewById(R.id.btnekle);
        cardView = view.findViewById(R.id.cardView);

        loadFoodItems();

        // Arama butonuna onClick özelliğini ekleme
        Button btnAra = view.findViewById(R.id.btnAra);
        btnAra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFood();
            }
        });

        // Ekle butonuna onClick özelliğini ekleme
        btnekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kullanıcı oturum açtıysa, kullanıcı kimliğini al
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    // Seçilen kaloriyi al
                    String calorieString = foodCalori.getText().toString();
                    int calorie = Integer.parseInt(calorieString);

                    // Kullanıcının girdiği yiyecek adını al
                    String foodNameText = foodName.getText().toString();

                    // FirebaseHelper sınıfından yeni bir nesne oluştur
                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    // Kaloriyi ve yiyecek adını todaystotalcalories ve foodname alt düğümlerine ekle
                    firebaseHelper.addCalorieAndFoodNameToToday(userId, calorie, foodNameText);
                    // Total kaloriyi güncelle
                    updateWeeklyCalories(calorie);
                } else {
                    // Kullanıcı oturum açmamışsa, uygun bir işlem yapabilirsiniz.
                    // Örneğin, kullanıcıyı oturum açma sayfasına yönlendirebilirsiniz.
                }
            }
        });

        // EditText'i başlat
        searchEditText = view.findViewById(R.id.searchEditText);

        return view;
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

    // Total kalorileri güncelle
    private void updateWeeklyCalories(int calorie) {
        // Günün tarihini al
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todaysDate = dateFormat.format(calendar.getTime());

        weeklyCaloriesRef.child(todaysDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer totalCalories = dataSnapshot.getValue(Integer.class);
                if (totalCalories == null) {
                    totalCalories = calorie;
                } else {
                    totalCalories += calorie;
                }
                weeklyCaloriesRef.child(todaysDate).setValue(totalCalories);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Database", "Veritabanı hatası: " + databaseError.getMessage());
            }
        });
    }
}

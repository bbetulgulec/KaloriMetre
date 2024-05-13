package com.betulgulec.app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowMenu extends AppCompatActivity {

    private LinearLayout cardViewContainer;
    private DatabaseReference foodRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardViewContainer = findViewById(R.id.cardViewContainer);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        foodRef = database.getReference("food");

        // Tüm yiyecekleri göster
        showAllFood();

        // Fab butonlarına tıklama işlemleri
        FloatingActionMenu fabMenu = findViewById(R.id.floatingMenu);
        FloatingActionButton fab_Icecek = findViewById(R.id.fab_Icecek);
        FloatingActionButton fab_Yemek = findViewById(R.id.fab_Yemek);
        FloatingActionButton fab_Tatli = findViewById(R.id.fab_Tatli);

        fab_Icecek.setOnClickListener(view -> {
            retrieveData("icecek");
            fabMenu.close(true);
        });

        fab_Yemek.setOnClickListener(view -> {
            retrieveData("yiyecek"); // Düzeltildi
            fabMenu.close(true);
        });

        fab_Tatli.setOnClickListener(view -> {
            retrieveData("tatli");
            fabMenu.close(true);
        });
    }

    private void showAllFood() {
        cardViewContainer.removeAllViews();

        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot foodSnapshot : categorySnapshot.getChildren()) {
                        String foodName = foodSnapshot.getKey();
                        String foodCalori = foodSnapshot.getValue(String.class);
                        createFoodCard(foodName, foodCalori);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemler
            }
        });
    }

    private void retrieveData(String category) {
        cardViewContainer.removeAllViews();

        foodRef.child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    String foodName = foodSnapshot.getKey();
                    String foodCalori = foodSnapshot.getValue(String.class);
                    createFoodCard(foodName, foodCalori);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacak işlemler
            }
        });
    }

    private void createFoodCard(String foodName, String foodCalori) {
        // Yeni bir kart görünümü oluşturma
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.margin_medium));
        cardView.setLayoutParams(params);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardView.setRadius(getResources().getDimension(R.dimen.card_corner_radius));
        cardView.setCardElevation(getResources().getDimension(R.dimen.card_elevation));

        // Kart içeriğini oluşturma
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                getResources().getDimensionPixelSize(R.dimen.padding_large),
                getResources().getDimensionPixelSize(R.dimen.padding_large),
                getResources().getDimensionPixelSize(R.dimen.padding_large),
                getResources().getDimensionPixelSize(R.dimen.padding_large)
        );

        TextView foodNameTextView = new TextView(this);
        foodNameTextView.setLayoutParams(layoutParams);
        foodNameTextView.setText(foodName);
        foodNameTextView.setTextColor(getResources().getColor(android.R.color.black));
        foodNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        layout.addView(foodNameTextView);

        TextView foodCaloriTextView = new TextView(this);
        foodCaloriTextView.setLayoutParams(layoutParams);
        foodCaloriTextView.setText(foodCalori);
        foodCaloriTextView.setTextColor(getResources().getColor(android.R.color.black));
        foodCaloriTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        layout.addView(foodCaloriTextView);

        cardView.addView(layout);
        cardViewContainer.addView(cardView);
    }
}

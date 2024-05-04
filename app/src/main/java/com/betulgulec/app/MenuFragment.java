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
        rootRef = database.getReference().child("food");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(foodAdapter);

        loadFoodItems();
        initComponent();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        takeEditTextValue();
    }

    private void loadFoodItems() {
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot foodSnapshot : categorySnapshot.getChildren()) {
                        String foodName = foodSnapshot.getKey();
                        String calorie = foodSnapshot.child("calori").getValue(String.class);
                        foodList.add(new FoodItem(foodName, calorie));
                    }
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

    public void initComponent(){
        View rootView = getView();
        if(rootView != null) {
            searchEditText = rootView.findViewById(R.id.searchEditText);
            foodName = rootView.findViewById(R.id.foodName);
            foodCalori = rootView.findViewById(R.id.foodCalori);
            btnekle = rootView.findViewById(R.id.btnekle);
            cardView = rootView.findViewById(R.id.cardView);
        } else {
            Log.e("MenuFragment", "Root view is null");
        }
    }

    public void takeEditTextValue(){
        if(searchEditText != null) {
            String food = searchEditText.getText().toString().trim();
            foodName.setText(food);
        } else {
            Log.e("MenuFragment", "searchEditText is null");
        }
    }
}

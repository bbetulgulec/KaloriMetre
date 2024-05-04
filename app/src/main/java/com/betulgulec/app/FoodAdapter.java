package com.betulgulec.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.AbstractCollection;
import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private ArrayList<FoodItem> foodList;
    private AbstractCollection<CardView> cardViews;

    public FoodAdapter(ArrayList<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_menu, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem currentItem = foodList.get(position);

        holder.nameTextView.setText(currentItem.getName());
        holder.calorieTextView.setText(currentItem.getCalorie());

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Yeni bir FoodItem oluşturan yöntem
    // Yeni bir FoodItem oluşturan yöntem
    private void createFoodItem(String foodName, String calorie) {
        FoodItem newFoodItem = new FoodItem(foodName, calorie);
        foodList.add(newFoodItem);
        ArrayAdapter<Object> foodAdapter = null;
        foodAdapter.notifyDataSetChanged();
    }



    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView calorieTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.foodName);
            calorieTextView = itemView.findViewById(R.id.foodCalori);
        }
    }

    public void filterFoodList(ArrayList<FoodItem> filteredList) {
        foodList = filteredList;
        notifyDataSetChanged();
}
}
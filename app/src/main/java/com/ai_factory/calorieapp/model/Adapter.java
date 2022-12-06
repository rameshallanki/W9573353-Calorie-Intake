package com.ai_factory.calorieapp.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ai_factory.calorieapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String> foods;
    List<String> calories;

    public Adapter(List<String> food, List<String> calory){
        this.foods=food;
        this.calories=calory;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.foodName.setText(foods.get(position));
        holder.foodCalory.setText(calories.get(position));

    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView foodName, foodCalory;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            foodName=itemView.findViewById(R.id.foodName);
            foodCalory=itemView.findViewById(R.id.foodCalory);
        }
    }
}

package com.ai_factory.calorieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ai_factory.calorieapp.model.Adapter;

import java.util.ArrayList;

public class Suggest extends AppCompatActivity {
    RecyclerView items;
    ArrayList<String> food_lt;
    ArrayList<String> calory_lt;

    TextView textview;
    double totalCalories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        textview=findViewById(R.id.totalLeftCalories);
        items=findViewById(R.id.suggestItems);
        Intent intent=getIntent();
        totalCalories=Double.parseDouble(intent.getStringExtra("totalCalories"));
        totalCalories = (double) Math.round(totalCalories * 100d) / 100d;
        totalCalories = Math.max(totalCalories,0.0);
        textview.setText(totalCalories +" Cal");
        double leftCalories=totalCalories;
        String[] foods=new String[]{"Coconut","Raisins","Currants","Dates","Dried figs","Dried fruits",
                "Dried apricots","Prunes","Avocados","egg","Bananas","Sultanas","Energy drink","Mango",
                "milk","Orange juice","Soft drinks","Ice tea"};
        double[] calories=new double[]{455.3d, 294.3d, 289.8d, 276.5d, 248.8d, 245.9d, 236.3d,
                234.8d, 171.9d, 147d, 87.4d, 68d, 61.8d, 61.6d, 51.3d,46.4d,41.1d,1d};
        food_lt=new ArrayList<>();
        calory_lt=new ArrayList<>();
        for(int i=0;i< foods.length;i++){
            if(calories[i]<leftCalories) {
                food_lt.add(foods[i]);
                calory_lt.add(calories[i]+ " Cal");
                leftCalories-=calories[i];
            }
        }

        try {
            Adapter foodAdapter = new Adapter(food_lt, calory_lt);
            items.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            items.setAdapter(foodAdapter);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
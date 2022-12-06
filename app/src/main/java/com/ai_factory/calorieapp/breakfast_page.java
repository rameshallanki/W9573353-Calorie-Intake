package com.ai_factory.calorieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ai_factory.calorieapp.model.Adapter;
import com.ai_factory.calorieapp.model.Food;
import com.ai_factory.calorieapp.model.Results;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
//import org.json.JSONObject;

import java.net.URI;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class breakfast_page extends AppCompatActivity {
    RecyclerView items;
    FloatingActionButton fab;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirestoreRecyclerAdapter<Food, breakfast_page.FoodViewHolder> foodAdapter;
    String type;
    TextView textView;
//    String url="https://dummyjson.com/products/1";
    String key="asFaEA14FcCbqt0dhYNlXQ==jdGQ8eta0wC5q88t";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast_page);
        try{
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (NullPointerException e){}

        Intent intent=getIntent();
        type=intent.getStringExtra("type");
        textView=findViewById(R.id.type_text);
        textView.setText(type.toUpperCase());

        items=findViewById(R.id.itemList);
        fab=findViewById(R.id.addFood);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();

        fstore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        fUser=fAuth.getCurrentUser();
        Query query= fstore.collection("data").document(fUser.getUid()).collection("myFood").orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("date",dateFormat.format(date))
                .whereEqualTo("type",type);

        FirestoreRecyclerOptions<Food> allFoods=new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();


        foodAdapter=new FirestoreRecyclerAdapter<Food, breakfast_page.FoodViewHolder>(allFoods) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull breakfast_page.FoodViewHolder holder, int position, @NonNull @NotNull Food model) {
                holder.foodName.setText(model.getName());
                double calories = model.getCalories();
                if (calories > 999) {
                    calories = (double) Math.round(calories / 1000d * 100d) / 100d;
                    holder.foodCalory.setText(calories + " Kcal");
                } else{
                    calories = (double) Math.round(calories * 100d) / 100d;
                    holder.foodCalory.setText(calories + " Cal");
                }
            }

            @NonNull
            @NotNull
            @Override
            public breakfast_page.FoodViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
                return new breakfast_page.FoodViewHolder(view);
            }
        };

        items.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        items.setAdapter(foodAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Add Your "+type+"!!");

                // Set up the input
                final EditText input = new EditText(view.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setPadding(10,80,10,15);
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        Toast.makeText(getApplicationContext(),"Adding. Please wait...",Toast.LENGTH_SHORT);
                        uploadData(view, m_Text);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void uploadData(View view, String food_name){
        DocumentReference docref=fstore.collection("data").document(fUser.getUid()).collection("myFood").document();
        Map<String, Object> map=new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String url="https://api.calorieninjas.com/v1/nutrition?query="+food_name;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,url,
                new JSONObject(),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String server_data = response.toString();
                            JSONObject object = new JSONObject(server_data);
                            JSONObject obj = (JSONObject) object.getJSONArray("items").get(0);
                            map.put("name",food_name);
                            map.put("date",dateFormat.format(date));
                            map.put("type",type);
//                            Toast.makeText(view.getContext(), obj.getString("calories"), Toast.LENGTH_LONG).show();
                            double calories = Double.parseDouble(obj.getString("calories"));
                            map.put("Calories", calories);
                            docref.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(view.getContext(), "Food Added", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(view.getContext(), "Error, Try Again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),"Error, Try Again",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("X-Api-Key", key);
                        return params;
                    }
                };

        queue.add(jsonObjectRequest);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder{
        TextView foodName, foodCalory;
        public FoodViewHolder(@NonNull View itemView){
            super(itemView);
            foodName=itemView.findViewById(R.id.foodName);
            foodCalory=itemView.findViewById(R.id.foodCalory);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (foodAdapter != null) {
            foodAdapter.stopListening();
        }
    }

}
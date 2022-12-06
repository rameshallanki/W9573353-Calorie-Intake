package com.ai_factory.calorieapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ai_factory.calorieapp.model.Food;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    GridLayout gridLayout;
    CardView breakfast, lunch, dinner, suggest;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    TextView totalCalories;
    double calories;
    double totalCal=0.0d;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        breakfast = view.findViewById(R.id.Breakfast);

        totalCalories=view.findViewById(R.id.totalCalories);
        fstore= FirebaseFirestore.getInstance();
        fAuth= FirebaseAuth.getInstance();
        fUser=fAuth.getCurrentUser();
        gridLayout=view.findViewById(R.id.gridLayout);
        gridLayout.setVerticalScrollBarEnabled(true);

        DocumentReference documentReference=fstore.document("data/"+fAuth.getCurrentUser().getUid()+"/UserDetails/User");
        try {
            documentReference.addSnapshotListener(getActivity() , new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(e != null){
                        return;
                    }
                    if (documentSnapshot!=null && documentSnapshot.exists()) {
                        totalCal=documentSnapshot.getDouble("calories");
                    }
                }
            });
        }catch (Exception e){
//            Toast.makeText(view.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();


        fstore.collection("data/"+fUser.getUid()+"/myFood").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    calories=0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("date").equals(dateFormat.format(date)))
                        calories+=document.get("Calories",Double.class);
                    }
                    totalCal-=calories;
                    if (calories > 999) {
                        calories = (double) Math.round(calories / 1000d * 100d) / 100d;
                        totalCalories.setText(calories+" Kcal");
                    } else{
                        calories = (double) Math.round(calories * 100d) / 100d;
                        totalCalories.setText(calories + " Cal");
                    }
                } else {
                    Toast.makeText(view.getContext(),"No".toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), breakfast_page.class);
                intent.putExtra("type","BreakFast");
                startActivity(intent);
            }
        });

        lunch = view.findViewById(R.id.Lunch);
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), breakfast_page.class);
                intent.putExtra("type","Lunch");
                startActivity(intent);
            }
        });

        dinner = view.findViewById(R.id.Dinner);
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), breakfast_page.class);
                intent.putExtra("type","Dinner");
                startActivity(intent);
            }
        });

        suggest = view.findViewById(R.id.Suggestions);
        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), Suggest.class);
                                intent.putExtra("totalCalories",String.valueOf(totalCal));
                                startActivity(intent);

            }
        });

        return view;
    }


}
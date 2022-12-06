package com.ai_factory.calorieapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ai_factory.calorieapp.MainActivity;
import com.ai_factory.calorieapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText rUserName,rUserEmail,rUserPass,rUserConfPass, rUserAge, rUserHeight, rUserWeight;
    Button syncAccount;
    TextView loginAct;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String gender;
    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner dropdown = findViewById(R.id.spinner1);
        gender="0";
        String[] items = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        fstore= FirebaseFirestore.getInstance();

        rUserName = findViewById(R.id.userName);
        rUserAge = findViewById(R.id.userAge);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPass = findViewById(R.id.password);
        rUserHeight = findViewById(R.id.userHeight);
        rUserWeight = findViewById(R.id.userWeight);

        rUserConfPass = findViewById(R.id.passwordConfirm);

        syncAccount = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);

        loginAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        fAuth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        syncAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uUsername = rUserName.getText().toString();
                String uUserEmail = rUserEmail.getText().toString();
                String uUserHeight = rUserHeight.getText().toString();
                String uUserWeight = rUserWeight.getText().toString();
                String uUserPass = rUserPass.getText().toString();
                String uConfPass = rUserConfPass.getText().toString();
                String Age = rUserAge.getText().toString();

                if(uUserHeight.isEmpty() ||uUserWeight.isEmpty() ||uUserEmail.isEmpty() ||
                        uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()){
                    Toast.makeText(Register.this, "All Fields Are Required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(uUserPass.length()<6){
                    rUserConfPass.setError("Atleast 6 chars");
                    return;
                }

                if(!uUserPass.equals(uConfPass)){
                    rUserConfPass.setError("Password Do not Match.");
                    return;
                }
                fAuth.createUserWithEmailAndPassword(uUserEmail,uUserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            DocumentReference docref=fstore
                                    .document("data/"+fAuth.getCurrentUser().getUid()+"/UserDetails/User");
                            HashMap<String,Object> user=new HashMap<>();
                            user.put("fname",uUsername);
                            user.put("age",Age);
                            user.put("gender",gender);
                            user.put("mail",uUserEmail);
                            Uri uri=null;
                            user.put("ProfileImage",uri);
                            double calories=getCalories(uUserHeight,uUserWeight,Age,gender);
                            user.put("calories",calories);
                            docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "Successfully Login.", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(Register.this, "Data Not Uploaded Login.", Toast.LENGTH_SHORT).show();

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(Register.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private double getCalories(String uUserHeight, String uUserWeight, String age, String gender) {
        double H=Double.valueOf(uUserHeight);
        double W=Double.valueOf(uUserWeight);
        double A=Double.valueOf(age);
        double calories=10*W + 6.25*H - 5*A + 5;
        if(gender=="1") calories = 10*W + 6.25*H - 5*A - 161;
        return calories;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                gender="0";
                break;
            case 1:
                gender="1";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
//    For men:
//    BMR = 10W + 6.25H - 5A + 5
//    For women:
//    BMR = 10W + 6.25H - 5A - 161

//    https://www.calculator.net/calorie-calculator.html-> Mifflin-St Jeor Equation:

}
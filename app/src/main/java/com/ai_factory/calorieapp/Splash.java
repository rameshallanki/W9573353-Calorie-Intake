package com.ai_factory.calorieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.ai_factory.calorieapp.auth.Login;
import com.ai_factory.calorieapp.auth.Register;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fAuth=FirebaseAuth.getInstance();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fAuth.getCurrentUser()!=null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        },2000);
    }
}
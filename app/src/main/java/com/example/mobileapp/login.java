package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobileapp.databinding.ActivitySigninBinding;

public class login extends AppCompatActivity {


    ActivitySigninBinding binding;
    BDController bdController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_signin);
        setContentView(binding.getRoot());

        bdController = new BDController(this);

        //Variable de l'id


        //Fonction associer

    }
}
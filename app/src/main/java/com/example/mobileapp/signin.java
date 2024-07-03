package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobileapp.databinding.ActivitySigninBinding;

public class signin extends AppCompatActivity {

    //private Button btnAnnuler;
    private ActivitySigninBinding binding;
    private BDController bdController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_signin);
        setContentView(binding.getRoot());

        //btnAnnuler = findViewById(R.id.btnannuler);

        binding.btnannuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), viewlog.class);
                startActivity(login);
                finish();
            }
        });

        //il faut toujours l'initialiser si non vou ne pouver pas ajouter votre element
        bdController = new BDController(this);

        binding.btninscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = binding.nom.getText().toString();
                String prenom = binding.prenom.getText().toString();
                String email = binding.email.getText().toString();
                String motdepasse = binding.motdepasse.getText().toString();
                String confirmer = binding.confirmermdp.getText().toString();


                try {

                    if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motdepasse.isEmpty()) {
                        Toast.makeText(signin.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                    } else {
                        if (motdepasse.equals(confirmer)) {
                            try {
                                boolean verifyEmail = bdController.verifyemail(email);

                                if (!verifyEmail) {
                                    boolean ajoutUser = bdController.dataInsert(nom, prenom, email, motdepasse);

                                    if (ajoutUser) {
                                        Toast.makeText(signin.this, "Utilisateur ajouté avec succès", Toast.LENGTH_SHORT).show();
                                        Intent login = new Intent(getApplicationContext(), viewlog.class);
                                        startActivity(login);
                                        finish();
                                    } else {
                                        Toast.makeText(signin.this, "Inscription invalide !!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(signin.this, "Cet utilisateur existe déjà !!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(signin.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(signin.this, "Votre mot de passe ne correspond pas", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(signin.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.databinding.ActivitySigninBinding;

public class viewlog extends AppCompatActivity {

    private ActivitySigninBinding Atsb;
    private BDController bdcontroller;
    private EditText email, motdepasse, motdepasseconf;
    private Button btnconnexion;
    private TextView toInscritption;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Atsb = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_viewlog);

        email = findViewById(R.id.txtemail);
        motdepasse = findViewById(R.id.txtmotdepasse);
        btnconnexion = findViewById(R.id.connexion);
        motdepasseconf = findViewById(R.id.txtmotdepasseconf);
        toInscritption = findViewById(R.id.txtinscription);

        bdcontroller = new BDController(this);

        btnconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emmail = email.getText().toString().trim();
                String pass = motdepasse.getText().toString().trim();
                String passconf = motdepasseconf.getText().toString().trim();
                String emailAdmin = "admin@gmail.com";
                String motdepasseAdmin = "admin";
                boolean verificationpwdEmail = bdcontroller.verifyEmailAndPassword(emmail, pass);

                try {
                    if (emmail.isEmpty() || pass.isEmpty() || passconf.isEmpty()) {
                        Toast.makeText(viewlog.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                    } else if (verificationpwdEmail) {
                        int userId = bdcontroller.getUserId(emmail);
                        Log.d("Login", "Email saisi: " + emmail + " Mot de passe: " + pass + " Verification: " + verificationpwdEmail);
                        if (emmail.equals(emailAdmin) && pass.equals(motdepasseAdmin)) {
                            saveUserRole("super_admin", -1);
                        } else {
                            saveUserRole("simple_user", userId);
                        }
                        Toast.makeText(viewlog.this, "Connexion avec succès", Toast.LENGTH_SHORT).show();
                        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(menu);
                        finish();
                    } else {
                        Toast.makeText(viewlog.this, "Utilisateur non existant !!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(viewlog.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });


        toInscritption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(getApplicationContext(), signin.class);
                startActivity(signin);
                finish();
            }
        });
    }

    //pour verifier le role de l'user
    // Méthode pour enregistrer le rôle de l'utilisateur et éventuellement son ID
    private void saveUserRole(String role, int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_role", role);
        if ("simple_user".equals(role)) {
            editor.putInt("user_id", userId);
        }
        editor.apply();
        Log.d("SaveUserRole", "Saved user_role: " + role + ", user_id: " + userId);
    }


    private void saveUserId(int id) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", String.valueOf(id));
        editor.apply();
    }
}

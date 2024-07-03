package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class popUp extends Dialog {
    //Ici on va declarer les elem qui va caracteriser notre popup

    private Button btnannul;
    private Button btnajout;
    private String title;
    private TextView title1;
    private EditText model, marque, prix, description, image, carburant, kilometrage;
    private ImageView carimage;

    //Creer notre popup

    //@SuppressLint("MissingInflatedId")
    //@SuppressLint("MissingInflatedId")
    public popUp(Activity activity) {
        super(activity, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);
        setContentView(R.layout.popup_add_car);
        btnajout = findViewById(R.id.btnannuler);
        btnannul = findViewById(R.id.btnajouter);
        model = findViewById(R.id.modele);
        marque = findViewById(R.id.marque);
        prix = findViewById(R.id.prix);
        description = findViewById(R.id.description);
        //image = findViewById(R.id.imgaddcar);
        carburant = findViewById(R.id.carburant);
        kilometrage = findViewById(R.id.kilometrage);
        title1 = findViewById(R.id.titre);
        carimage = findViewById(R.id.imgaddcar);
        title = "Ajout d'une voiture";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Button getBtnannul() {
        return btnannul;
    }

    public EditText getCarburant() {
        return carburant;
    }

    public EditText getDescription() {
        return description;
    }

    public EditText getModel() {
        return model;
    }

    public EditText getKilometrage() {
        return kilometrage;
    }

    public EditText getMarque() {
        return marque;
    }

    public EditText getPrix() {
        return prix;
    }

    public ImageView getCarimage() {
        return carimage;
    }

    public Button getBtnajout() {
        return btnajout;
    }
    public void build() {
        show();
        title1.setText(title);
    }
}

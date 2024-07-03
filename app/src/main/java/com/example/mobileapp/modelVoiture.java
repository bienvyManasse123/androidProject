package com.example.mobileapp;

public class modelVoiture {
    private int IdVoiture;
    private String marque;
    private String modele;
    private String description;
    private String prix;
    private String carburant;
    private String kilometrage;
    private byte[] image;
    private String couleur;
    private int nbrsiege;


    //constructor
    public modelVoiture(int idVoiture, String marque, String modele, String description, String prix, String carburant,String kilometrage, byte[] image, String couleur, int nbrsiege) {
        IdVoiture = idVoiture;
        this.marque = marque;
        this.modele = modele;
        this.description = description;
        this.prix = prix;
        this.carburant = carburant;
        this.kilometrage = kilometrage;
        this.image = image;
        this.couleur = couleur;
        this.nbrsiege = nbrsiege;
    }
    //nos get and set

    public int getIdVoiture() {
        return IdVoiture;
    }

    public void setIdVoiture(int idVoiture) {
        IdVoiture = idVoiture;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }
    public String getCarburant() {
        return carburant;
    }

    public void setCarburant(String carburant) {
        this.carburant = carburant;
    }

    public String getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(String kilometrage) {
        this.kilometrage = kilometrage;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    public int getNbrsiege() {
        return nbrsiege;
    }

    public void setNbrsiege(int nbrsiege) {
        this.nbrsiege = nbrsiege;
    }
}

package com.example.mobileapp;

public class modelCommande {

    private int idCommande;
    private int idUser;
    private int idVoiture;
    private byte[] image;
    private String marque;
    private String modele;
    private String prix;
    private String dateCommande;
    private String dateLivraison;
    private String etatCommande;
    private String modePaiement;
    private String modeLivraison;
    private String adresseLivraison;
    private String causeAnnulation;
    private String statusLivraison;
    private String causeRetard;

    public modelCommande() {
    }

    public modelCommande(int idCommande, int idUser, int idVoiture, byte[] image, String modele, String marque, String prix, String dateCommande, String dateLivraison, String etatCommande, String modePaiement, String modeLivraison, String adresseLivraison, String causeAnnulation, String statusLivraison, String causeRetard) {
        this.idCommande = idCommande;
        this.idUser = idUser;
        this.idVoiture = idVoiture;
        this.image = image;
        this.modele = modele;
        this.marque = marque;
        this.prix = prix;
        this.dateCommande = dateCommande;
        this.dateLivraison = dateLivraison;
        this.etatCommande = etatCommande;
        this.modePaiement = modePaiement;
        this.modeLivraison = modeLivraison;
        this.adresseLivraison = adresseLivraison;
        this.causeAnnulation = causeAnnulation;
        this.statusLivraison = statusLivraison;
        this.causeRetard = causeRetard;
    }



    public int getIdCommande() {
        return idCommande;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdVoiture() {
        return idVoiture;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getDateCommande() {
        return dateCommande;
    }

    public String getDateLivraison() {
        return dateLivraison;
    }

    public String getEtatCommande() {
        return etatCommande;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public String getModeLivraison() {
        return modeLivraison;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }
    public String getCauseAnnulation() {return causeAnnulation;}

    public void setStatusLivraison(String statusLivraison) {
        this.statusLivraison = statusLivraison;
    }

    public void setCauseRetard(String causeRetard) {
        this.causeRetard = causeRetard;
    }

    public String getStatusLivraison() {
        return statusLivraison;
    }

    public String getCauseRetard() {
        return causeRetard;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setIdVoiture(int idVoiture) {
        this.idVoiture = idVoiture;
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setDateLivraison(String dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public void setEtatCommande(String etatCommande) {
        this.etatCommande = etatCommande;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public void setModeLivraison(String modeLivraison) {
        this.modeLivraison = modeLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public void setCauseAnnulation(String causeAnnulation) {
        this.causeAnnulation = causeAnnulation;
    }
}

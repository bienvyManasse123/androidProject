package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BDController extends SQLiteOpenHelper {

    public static final String DBName = "carManagement.db";
    public BDController(@Nullable Context context) {
        super(context, DBName, null ,1);
    }


    //Pour exécuter notre requéte de création de table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (idUser INTEGER PRIMARY KEY AUTOINCREMENT, nom VARCHAR NOT NULL, prenom VARCHAR NOT NULL,  email NOT NULL, password NOT NULL)");
        db.execSQL("CREATE TABLE voiture (idVoiture INTEGER PRIMARY KEY AUTOINCREMENT, marque VARCHAR, modele VARCHAR, description VARCHAR, image BLOB, carburant VARCHAR, kilometrage VARCHAR, prix VARCHAR, couleur VARCHAR, nbrsiege INT)");
        db.execSQL("CREATE TABLE commande (idCommande INTEGER PRIMARY KEY AUTOINCREMENT, idUser INTEGER, idVoiture INTEGER," +
                " dateCommande DATE, dateLivraison DATE,etatCommande VARCHAR, modePaiement VARCHAR, modeLivraison VARCHAR, " +
                "adresseLivraison VARCHAR, causeAnnulation TEXT DEFAULT null, statusLivraison VARCHAR DEFAULT null, " +
                "causeRetard VARCHAR DEFAULT null, FOREIGN KEY (idUser) REFERENCES users(idUser)," +
                " FOREIGN KEY (idVoiture) REFERENCES Voiture(idVoiture))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS voiture");
        db.execSQL("DROP TABLE IF EXISTS commande");
        onCreate(db);
    }


    //INsertion des user dans la table users
    public boolean dataInsert (String nom, String prenom, String email, String password) {
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nom", nom);
        contentValues.put("prenom", prenom);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = sqlDB.insert("users", null, contentValues);
        if (result == -1) {
            return false;//failed to insert the data
        } else {
            return true;//data insert successfully
        }
    }

    //Insertion de voiture dans la table voiture
    public boolean insertCar(String marque, String modele, String kilometrage, String carburant, String description, String prix, byte[] image, String couleur, Integer nbrsiege) {
        SQLiteDatabase sqlDB = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("marque", marque);
        contentValues.put("modele", modele);
        contentValues.put("kilometrage", kilometrage);
        contentValues.put("carburant", carburant);
        contentValues.put("description", description);
        contentValues.put("prix", prix);
        contentValues.put("image", image);
        contentValues.put("couleur", couleur);
        contentValues.put("nbrsiege", nbrsiege);
        long result = sqlDB.insert("voiture", null, contentValues);
        if (result == -1) {
            return false;//failed to insert the data
        } else {
            return true;//data insert successfully
        }
    }

    //get car
    @SuppressLint("Range")
    public modelVoiture getCarById(int idVoiture) {
        SQLiteDatabase sqldb = getReadableDatabase();
        String query = "SELECT * FROM voiture WHERE idVoiture = ?";
        Cursor cursor = sqldb.rawQuery(query, new String[]{String.valueOf(idVoiture)});

        modelVoiture voiture = null;
        if (cursor.moveToFirst()) {
            voiture = new modelVoiture(
                    cursor.getInt(cursor.getColumnIndex("idVoiture")),
                    cursor.getString(cursor.getColumnIndex("marque")),
                    cursor.getString(cursor.getColumnIndex("modele")),
                    cursor.getString(cursor.getColumnIndex("kilometrage")),
                    cursor.getString(cursor.getColumnIndex("carburant")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("prix")),
                    cursor.getBlob(cursor.getColumnIndex("image")),
                    cursor.getString(cursor.getColumnIndex("couleur")),
                    cursor.getInt(cursor.getColumnIndex("nbrsiege"))
            );
        }
        cursor.close();
        return voiture;
    }
    //get commande
    @SuppressLint("Range")
    public modelCommande getCommandeById(int idCommande) {
        SQLiteDatabase sqldb = getReadableDatabase();
        String query = "SELECT c.*, v.image, v.modele, v.marque, v.prix FROM commande c JOIN voiture v ON c.idVoiture = v.idVoiture  WHERE idCommande = ?";
        Cursor cursor = sqldb.rawQuery(query, new String[]{String.valueOf(idCommande)});
        modelCommande commande = null;
        if (cursor.moveToFirst()) {
            commande = new modelCommande(
                    cursor.getInt(cursor.getColumnIndex("idCommande")),
                    cursor.getInt(cursor.getColumnIndex("idUser")),
                    cursor.getInt(cursor.getColumnIndex("idVoiture")),
                    cursor.getBlob(cursor.getColumnIndex("image")),
                    cursor.getString(cursor.getColumnIndex("modele")),
                    cursor.getString(cursor.getColumnIndex("marque")),
                    cursor.getString(cursor.getColumnIndex("prix")),
                    cursor.getString(cursor.getColumnIndex("dateCommande")),
                    cursor.getString(cursor.getColumnIndex("dateLivraison")),
                    cursor.getString(cursor.getColumnIndex("etatCommande")),
                    cursor.getString(cursor.getColumnIndex("modePaiement")),
                    cursor.getString(cursor.getColumnIndex("modeLivraison")),
                    cursor.getString(cursor.getColumnIndex("adresseLivraison")),
                    cursor.getString(cursor.getColumnIndex("causeAnnulation")),
                    cursor.getString(cursor.getColumnIndex("statusLivraison")),
                    cursor.getString(cursor.getColumnIndex("causeRetard"))
            );
        }
        cursor.close();
        return commande;
    }

    //get id user pour afficher ses commande
    @SuppressLint("Range")
    public List<modelCommande> getCommandesByUserId(int userId) {
        List<modelCommande> commandes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("commande", null, "idUser = ?", new String[]{String.valueOf(userId)}, null, null, "dateCommande DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idCommande = cursor.getInt(cursor.getColumnIndex("idCommande"));
                int idUser = cursor.getInt(cursor.getColumnIndex("idUser"));
                int idVoiture = cursor.getInt(cursor.getColumnIndex("idVoiture"));
                String dateCommande = cursor.getString(cursor.getColumnIndex("dateCommande"));
                String dateLivraison = cursor.getString(cursor.getColumnIndex("dateLivraison"));
                String etatCommande = cursor.getString(cursor.getColumnIndex("etatCommande"));
                String modePaiement = cursor.getString(cursor.getColumnIndex("modePaiement"));
                String modeLivraison = cursor.getString(cursor.getColumnIndex("modeLivraison"));
                String adresseLivraison = cursor.getString(cursor.getColumnIndex("adresseLivraison"));
                String causeAnnulation = cursor.getString(cursor.getColumnIndex("causeAnnulation"));
                String statusLivraison = cursor.getString(cursor.getColumnIndex("statusLivraison"));
                String casueRetard = cursor.getString(cursor.getColumnIndex("causeRetard"));
                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                String marque = cursor.getString(cursor.getColumnIndex("marque"));
                String modele = cursor.getString(cursor.getColumnIndex("modele"));
                String prix = cursor.getString(cursor.getColumnIndex("prix"));

                modelCommande commande = new modelCommande(idCommande, idUser, idVoiture,image, modele, marque, prix, dateCommande, dateLivraison, etatCommande, modePaiement, modeLivraison,adresseLivraison, causeAnnulation, statusLivraison, casueRetard);
                commandes.add(commande);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return commandes;
    }

    //get all commande in bd
    @SuppressLint("Range")
    public List<modelCommande> getCommandes(int userId, String userRole) {
        List<modelCommande> commandes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
//        if ("super_admin".equals(userRole)) {
//            cursor = db.rawQuery("SELECT * FROM commande", null);
//        } else {
//            cursor = db.rawQuery("SELECT * FROM commande WHERE idUser = ?", new String[]{String.valueOf(userId)});
//        }
        if ("super_admin".equals(userRole)) {
            cursor = db.rawQuery("SELECT c.*, v.image, v.marque, v.modele, v.prix FROM commande c JOIN voiture v ON c.idVoiture = v.idVoiture", null);
            //cursor = db.rawQuery("SELECT * FROM commande", null);
        } else {
            cursor = db.rawQuery("SELECT c.*, v.image, v.marque, v.modele, v.prix FROM commande c JOIN voiture v ON c.idVoiture = v.idVoiture WHERE c.idUser = ?", new String[]{String.valueOf(userId)});
        }

        if (cursor.moveToFirst()) {
            do {
                int idCommande = cursor.getInt(cursor.getColumnIndex("idCommande"));
                int idUser = cursor.getInt(cursor.getColumnIndex("idUser"));
                int idVoiture = cursor.getInt(cursor.getColumnIndex("idVoiture"));
                String dateCommande = cursor.getString(cursor.getColumnIndex("dateCommande"));
                String dateLivraison = cursor.getString(cursor.getColumnIndex("dateLivraison"));
                String etatCommande = cursor.getString(cursor.getColumnIndex("etatCommande"));
                String modePaiement = cursor.getString(cursor.getColumnIndex("modePaiement"));
                String modeLivraison = cursor.getString(cursor.getColumnIndex("modeLivraison"));
                String adresseLivraison = cursor.getString(cursor.getColumnIndex("adresseLivraison"));
                String causeAnnulation = cursor.getString(cursor.getColumnIndex("causeAnnulation"));
                String statusLivraison = cursor.getString(cursor.getColumnIndex("statusLivraison"));
                String casueRetard = cursor.getString(cursor.getColumnIndex("causeRetard"));
                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                String marque = cursor.getString(cursor.getColumnIndex("marque"));
                String modele = cursor.getString(cursor.getColumnIndex("modele"));
                String prix = cursor.getString(cursor.getColumnIndex("prix"));

                modelCommande commande = new modelCommande(idCommande, idUser, idVoiture,image,modele, marque, prix, dateCommande, dateLivraison, etatCommande, modePaiement, modeLivraison,adresseLivraison, causeAnnulation, statusLivraison, casueRetard);
                commandes.add(commande);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return commandes;
    }


    //update commande lors du changement status de livraison
    public boolean updateStatusLivraison(int idCommande, String statusLivraison, String causeRetard) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("statusLivraison", statusLivraison);
        values.put("causeRetard", causeRetard);

        int result = db.update("commande", values, "idCommande = ?", new String[]{String.valueOf(idCommande)});
        return result > 0;
    }




    //Update car in table voiture
    public boolean updateCar(int idVoiture, String marque, String modele, String kilometrage, String carburant, String description, String prix, byte[] image, String couleur, int nbrsiege) {
        SQLiteDatabase sqldb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("marque", marque);
        contentValues.put("modele", modele);
        contentValues.put("kilometrage", kilometrage);
        contentValues.put("carburant", carburant);
        contentValues.put("description", description);
        contentValues.put("prix", prix);
        contentValues.put("image", image);
        contentValues.put("couleur", couleur);
        contentValues.put("nbrsiege", nbrsiege);

        int rowGet = sqldb.update("voiture", contentValues, "idVoiture = ?", new String[] { String.valueOf(idVoiture) });
        return rowGet > 0;
    }
    //Delete car in table voiture
    public boolean deleteCar(int idVoiture) {
        SQLiteDatabase sqlDB = getWritableDatabase();

        // Vérifier si l'ID existe
        Cursor cursor = sqlDB.rawQuery("SELECT 1 FROM voiture WHERE idVoiture = ?", new String[]{String.valueOf(idVoiture)});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        // Si l'ID existe, le supprimer
        if (exists) {
            int rowsAffected = sqlDB.delete("voiture", "idVoiture = ?", new String[]{String.valueOf(idVoiture)});
            return rowsAffected > 0;
        } else {
            return false; // L'ID n'existe pas
        }
    }

    //Ajout commande
    public boolean addCommande(int idUser, int idVoiture, String dateCommande, String dateLivraison, String modeLivraison, String adresseLivraison, String etatCommande, String modePaiement) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Récupérer la date actuelle
        contentValues.put("idUser", idUser);
        contentValues.put("idVoiture", idVoiture);
        contentValues.put("dateCommande", dateCommande);
        contentValues.put("dateLivraison", dateLivraison);
        contentValues.put("modeLivraison", modeLivraison);
        contentValues.put("adresseLivraison", adresseLivraison);
        contentValues.put("etatCommande", etatCommande);
        contentValues.put("modePaiement", modePaiement);
        long result = -1;
        try {
            result = sqLiteDatabase.insert("commande", null, contentValues);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Erreur lors de l'insertion de la commande : " + e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }

        return result != -1;
    }

    //Update commande
    public boolean updateCommande(int idCommande, int idUser, int idVoiture, String dateCommande, String dateLivraison, String etatCommande, String modePaiement, String modeLivraison, String adresseLivraison, String statusLivraison) {
        SQLiteDatabase sqldb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idUser", idUser);
        contentValues.put("idVoiture", idVoiture);
        contentValues.put("dateCommande", dateCommande);
        contentValues.put("dateLivraison", dateLivraison);
        contentValues.put("etatCommande", etatCommande);
        contentValues.put("modePaiement", modePaiement);
        contentValues.put("modeLivraison", modeLivraison);
        contentValues.put("adresseLivraison", adresseLivraison);
        contentValues.put("statusLivraison", statusLivraison);

        int rowGet = sqldb.update("commande", contentValues, "idCommande = ?", new String[] { String.valueOf(idCommande) });
        return rowGet > 0;
    }
    //annuler commande
    public boolean annulerCommande(int idCommande, String causeAnnulation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("etatCommande", "Annuler");
        values.put("causeAnnulation", causeAnnulation);

        int rows = db.update("commande", values, "idCommande = ?", new String[]{String.valueOf(idCommande)});
        db.close();

        return rows > 0;
    }


    //get all car in SQLite db
    public Cursor getData(String sql) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql, null);
    }

    // Méthode dans la classe BdController pour récupérer l'ID utilisateur par email
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // Valeur par défaut si l'utilisateur n'est pas trouvé

        Cursor cursor = null;
        try {
            String query = "SELECT idUser FROM users WHERE email = ?";
            Log.d("getUserId", "Requête SQL: " + query);
            cursor = db.rawQuery(query, new String[]{email});
            Log.d("getUserId", "Nombre de résultats: " + cursor.getCount());

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("idUser"));
                Log.d("getUserId", "User ID trouvé: " + userId + " pour l'email: " + email);
            } else {
                Log.d("getUserId", "Aucun utilisateur trouvé pour l'email: " + email);
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Erreur lors de la récupération de l'ID utilisateur : " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userId;
    }

    public boolean verifyemail (String email) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verifyEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;

        try {
            String query = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
            cursor = db.rawQuery(query, new String[]{email, password});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    isValid = true; // Email et mot de passe correspondent à une entrée dans la base de données
                }
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Erreur lors de la vérification des informations de connexion : " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isValid;
    }

    //get all commande
    @SuppressLint("Range")
    public List<modelCommande> getAllCommandes() {
        List<modelCommande> commandes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM commande", null);

        if (cursor.moveToFirst()) {
            do {
                modelCommande commande = new modelCommande();
                commande.setIdCommande(cursor.getInt(cursor.getColumnIndex("idCommande")));
                commande.setIdUser(cursor.getInt(cursor.getColumnIndex("idUser")));
                commande.setIdVoiture(cursor.getInt(cursor.getColumnIndex("idVoiture")));
                commande.setDateCommande(cursor.getString(cursor.getColumnIndex("dateCommande")));
                commande.setDateLivraison(cursor.getString(cursor.getColumnIndex("dateLivraison")));
                commande.setEtatCommande(cursor.getString(cursor.getColumnIndex("etatCommande")));
                commande.setModePaiement(cursor.getString(cursor.getColumnIndex("modePaiement")));
                commande.setModeLivraison(cursor.getString(cursor.getColumnIndex("modeLivraison")));
                commande.setAdresseLivraison(cursor.getString(cursor.getColumnIndex("adresseLivraison")));
                commande.setCauseAnnulation(cursor.getString(cursor.getColumnIndex("causeAnnulation")));
                commande.setStatusLivraison(cursor.getString(cursor.getColumnIndex("statusLivraison")));
                commande.setCauseRetard(cursor.getString(cursor.getColumnIndex("causeRetard")));
                commandes.add(commande);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return commandes;
    }

                    //Affichage dans notre dashboard
    public int getNombreClient() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }

    public int getNombreCommande() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM commande", null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }

    public int getNombreCommandeByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM commande WHERE idUser = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }

    public int getNombreVoiture() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM voiture", null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }


}

package com.example.mobileapp;

import static java.util.Locale.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//////////////////Role de l'adapteur pour afficher nos élément dans la listView
public class CommandeFragmentAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<modelCommande> listeCommande; //liste de notre commande
    private ArrayList<modelCommande> mlist; //liste selon la recherche

    public CommandeFragmentAdapter(Context context, int layout, ArrayList<modelCommande> listeCommande) {
        this.context = context;
        this.layout = layout;
        this.listeCommande = listeCommande;
        this.mlist = new ArrayList<>(listeCommande); //copie de notre liste initiale
    }

    //Les fonction qui possède les annotaion @Override sont des fonctions generer automatiquement par BaseAdapter
//    @Override
//    public int getCount() {
//        return listeCommande.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return listeCommande.get(position);
//    }
    @Override
    public int getCount() {
        return listeCommande.size();
    }

    @Override
    public Object getItem(int position) {
        return listeCommande.get(position);
    }

    //get id commande
    public int getCommandeId(int position) {
        return listeCommande.get(position).getIdCommande();
    }

    public void updateCommandes(List<modelCommande> newCommandes) {
        this.listeCommande.clear();
        this.listeCommande.addAll(newCommandes);
        notifyDataSetChanged();
    }

    public List<modelCommande> getData() {
        return mlist; // retourne une copie de la liste filtrée
    }
    public void setData(List<modelCommande> commandes) {
        this.mlist.clear();
        this.mlist.addAll(commandes);
        this.updateCommandes(commandes);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        //manque ici l'image,modele et marque du voiture
        ImageView imageVoiture;
        TextView txtmodele, txtmarque, txtprix, txtiduser, txtidvoiture, txtdatecommande, txtdatelivraison, txtmodepaiement, txtetatcommande, txtcauseannulation, txtstatuslivraison, txtcauseretard;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//        ViewHolder holder = new ViewHolder();
//        if (row == null) {
//            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//            row = inflater.inflate(layout, null);
//            holder.txtiduser = row.findViewById(R.id.txtiduser);
//            holder.txtidvoiture = row.findViewById(R.id.txtidvoiture);
//            holder.txtdatecommande = row.findViewById(R.id.txtdatecommande);
//            holder.txtmodepaiement = row.findViewById(R.id.txtmodepaiement);
//            holder.txtetatcommande = row.findViewById(R.id.txtetatcommande);
//            holder.txtcauseannulation = row.findViewById(R.id.txtcauseannulation);
//            holder.txtstatuslivraison = row.findViewById(R.id.txtstatuslivraison);
//            holder.txtcauseretard = row.findViewById(R.id.txtcauseretard);
//            row.setTag(holder);
//        } else {
//            holder = (CommandeFragmentAdapter.ViewHolder)row.getTag();
//        }
//
//        modelCommande modelcommande = listeCommande.get(position);
//        // Convertir les valeurs numériques en chaînes pour setText
//        holder.txtiduser.setText(String.valueOf(modelcommande.getIdUser()));
//        holder.txtidvoiture.setText(String.valueOf(modelcommande.getIdVoiture()));
//        holder.txtdatecommande.setText(modelcommande.getDateCommande());
//        holder.txtmodepaiement.setText(modelcommande.getModePaiement());
//        holder.txtetatcommande.setText(modelcommande.getEtatCommande());
//        holder.txtcauseannulation.setText(modelcommande.getCauseAnnulation());
//        holder.txtstatuslivraison.setText(modelcommande.getStatusLivraison());
//        holder.txtcauseretard.setText(modelcommande.getCauseRetard());
//
//
//        // Logique de visibilité des champs
//        String etatCommande = modelcommande.getEtatCommande();
//        String statusLivraison = modelcommande.getStatusLivraison();
//
//        if ("En attente".equals(etatCommande)) {
//            holder.txtcauseannulation.setVisibility(View.GONE);
//            holder.txtstatuslivraison.setVisibility(View.GONE);
//            holder.txtcauseretard.setVisibility(View.GONE);
//        } else if ("Confirmer".equals(etatCommande)) {
//            holder.txtcauseannulation.setVisibility(View.GONE);
//            if ("En cours de préparation".equals(statusLivraison)) {
//                holder.txtstatuslivraison.setVisibility(View.VISIBLE);
//                holder.txtcauseretard.setVisibility(View.GONE);
//            } else if ("En retard".equals(statusLivraison)) {
//                holder.txtstatuslivraison.setVisibility(View.VISIBLE);
//                holder.txtcauseretard.setVisibility(View.VISIBLE);
//            } else {
//                holder.txtstatuslivraison.setVisibility(View.VISIBLE);
//                holder.txtcauseretard.setVisibility(View.GONE);
//            }
//        } else if ("Annuler".equals(etatCommande)) {
//            holder.txtcauseannulation.setVisibility(View.VISIBLE);
//            holder.txtstatuslivraison.setVisibility(View.GONE);
//            holder.txtcauseretard.setVisibility(View.GONE);
//        } else {
//            holder.txtcauseannulation.setVisibility(View.GONE);
//            holder.txtstatuslivraison.setVisibility(View.GONE);
//            holder.txtcauseretard.setVisibility(View.GONE);
//        }
//
//        return row;
//    }
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    View row = convertView;
    ViewHolder holder;
    if (row == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(layout, null);
        holder = new ViewHolder();
        //ici on recupère l'id des elem qu'on veut afficher ajoutons ici l'image, modele, et marque
//        holder.txtiduser = row.findViewById(R.id.txtiduser);
//        holder.txtidvoiture = row.findViewById(R.id.txtidvoiture);
        holder.txtmodele = row.findViewById(R.id.txtmodele);
        holder.txtmarque = row.findViewById(R.id.txtmarque);
        holder.txtprix = row.findViewById(R.id.txtprix);
        holder.imageVoiture = row.findViewById(R.id.imageVoiture);
        holder.txtdatecommande = row.findViewById(R.id.txtdatecommande);
        holder.txtdatelivraison = row.findViewById(R.id.txtdatelivraison);
        holder.txtmodepaiement = row.findViewById(R.id.txtmodepaiement);
        holder.txtetatcommande = row.findViewById(R.id.txtetatcommande);
        holder.txtcauseannulation = row.findViewById(R.id.txtcauseannulation);
        holder.txtstatuslivraison = row.findViewById(R.id.txtstatuslivraison);
        holder.txtcauseretard = row.findViewById(R.id.txtcauseretard);
        row.setTag(holder);
    } else {
        holder = (ViewHolder) row.getTag();
    }

    modelCommande modelcommande = listeCommande.get(position);
    // Convertir les valeurs numériques en chaînes pour setText
    // a n'est pas oublier qu'il faut ajouter dans nos modele image, marque, et modele pour n'est avoir d'erreur
//    holder.txtiduser.setText(String.valueOf(modelcommande.getIdUser()));
//    holder.txtidvoiture.setText(String.valueOf(modelcommande.getIdVoiture()));
    byte[] imgvoiture = modelcommande.getImage();
    Bitmap bitmap = BitmapFactory.decodeByteArray(imgvoiture, 0, imgvoiture.length);
    if (bitmap != null && !bitmap.isRecycled()) {
        holder.imageVoiture.setImageBitmap(bitmap);
    } else {
        // Gérer le cas où l'image est nulle ou invalide
        holder.imageVoiture.setImageResource(R.drawable.image_car); // Remplacez par une image par défaut si nécessaire
    }
    holder.txtmodele.setText(modelcommande.getModele());
    holder.txtmarque.setText(modelcommande.getMarque());
    holder.txtprix.setText(modelcommande.getPrix());
    holder.txtdatecommande.setText(modelcommande.getDateCommande());
    holder.txtdatelivraison.setText(modelcommande.getDateLivraison());
    holder.txtmodepaiement.setText(modelcommande.getModePaiement());
    holder.txtetatcommande.setText(modelcommande.getEtatCommande());
    holder.txtcauseannulation.setText(modelcommande.getCauseAnnulation());
    holder.txtstatuslivraison.setText(modelcommande.getStatusLivraison());
    holder.txtcauseretard.setText(modelcommande.getCauseRetard());

    // Logique de visibilité des champs
    String etatCommande = modelcommande.getEtatCommande();
    String statusLivraison = modelcommande.getStatusLivraison();

    if ("En attente".equals(etatCommande)) {
        holder.txtcauseannulation.setVisibility(View.GONE);
        holder.txtstatuslivraison.setVisibility(View.GONE);
        holder.txtcauseretard.setVisibility(View.GONE);
    } else if ("Confirmer".equals(etatCommande)) {
        holder.txtcauseannulation.setVisibility(View.GONE);
        if ("En cours de préparation".equals(statusLivraison)) {
            holder.txtstatuslivraison.setVisibility(View.VISIBLE);
            holder.txtcauseretard.setVisibility(View.GONE);
        } else if ("En retard".equals(statusLivraison)) {
            holder.txtstatuslivraison.setVisibility(View.VISIBLE);
            holder.txtcauseretard.setVisibility(View.VISIBLE);
        } else {
            holder.txtstatuslivraison.setVisibility(View.VISIBLE);
            holder.txtcauseretard.setVisibility(View.GONE);
        }
    } else if ("Annuler".equals(etatCommande)) {
        holder.txtcauseannulation.setVisibility(View.VISIBLE);
        holder.txtstatuslivraison.setVisibility(View.GONE);
        holder.txtcauseretard.setVisibility(View.GONE);
    } else {
        holder.txtcauseannulation.setVisibility(View.GONE);
        holder.txtstatuslivraison.setVisibility(View.GONE);
        holder.txtcauseretard.setVisibility(View.GONE);
    }

    return row;
}

}

//Quelque déscription
/*
*   L'user lance l'app
* 1. Il s'incrit dans l'app s'il n'a pas encore de compte
* 2. Il choisi la voiture dans une liste proposé par l'application, Ilpeut faire des recherches sur les marque du voiture qu'il veux
* 3. Lors d'une long clique sur la voiture de son choix, une option s'ouvre dans laquel il y a une option pour: Voir la détail du voiture
*                                                                                          et un autre pour passer directement la commande du voiture
* 4. Lors du commande du voiture: -il peut choisir la date Livraison, entrer le type de paiement, selectionner l'adresse du livraison
* 5. En validant sont commande: l'etat du commande est "en attente" pour que l'admin puisse confirmer ou annuler son commande
* 6. Si commande valider: un status de Livraison lui sera possible de voir dans son compte pour suivre la livraison
* 7. Si commande annuler: une message lui sera visible dans son compte pour la cause de l'annulation de son commande
*
* */

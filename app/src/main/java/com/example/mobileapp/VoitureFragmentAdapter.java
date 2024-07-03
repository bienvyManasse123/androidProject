package com.example.mobileapp;

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

public class VoitureFragmentAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<modelVoiture> listeVoiture;
    private ArrayList<modelVoiture> mlist;
    //Constructeur
    public VoitureFragmentAdapter(Context context, int layout, ArrayList<modelVoiture> listeVoiture) {
        this.context = context;
        this.layout = layout;
        this.listeVoiture = listeVoiture;
        this.mlist = new ArrayList<>(listeVoiture);//copie de la liste initiale
    }

    @Override
    public int getCount() {
        return listeVoiture.size();
    }

    @Override
    public Object getItem(int position) {
        return listeVoiture.get(position);
    }

//    @Override
//    public int getCount() {
//        return filteredList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return filteredList.get(position);
//    }
    //get id car in her position
    public int getCarId(int position) {
        return listeVoiture.get(position).getIdVoiture();
    }

    //update car in the filter
    public List<modelVoiture> getAllData() {
        return mlist;
    }

    public void updateCar(List<modelVoiture> newVoiture) {
        this.listeVoiture.clear();
        this.listeVoiture.addAll(newVoiture);
        notifyDataSetChanged();
    }
    public void setData(List<modelVoiture> voitures) {
        this.mlist.clear();
        this.mlist.addAll(voitures);
        this.updateCar(voitures);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView marque, modele, description, prix, carburant, kilometrage;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.modele = row.findViewById(R.id.txtmodele);
            holder.marque = row.findViewById(R.id.txtmarque);
            holder.description = row.findViewById(R.id.txtdescription);
            holder.imageView = row.findViewById(R.id.imageVoiture);
            holder.carburant = row.findViewById(R.id.txtcarburant);
            holder.kilometrage = row.findViewById(R.id.txtkilometrage);
            holder.prix = row.findViewById(R.id.txtprix);
            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }
        modelVoiture modelVoiture = listeVoiture.get(position);
        holder.modele.setText(modelVoiture.getModele());
        holder.marque.setText(modelVoiture.getMarque());
        holder.description.setText(modelVoiture.getDescription());
        byte[] imgvoiture = modelVoiture.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgvoiture, 0, imgvoiture.length);
        if (bitmap != null && !bitmap.isRecycled()) {
            holder.imageView.setImageBitmap(bitmap);
        } else {
            // Gérer le cas où l'image est nulle ou invalide
            holder.imageView.setImageResource(R.drawable.image_car); // Remplacez par une image par défaut si nécessaire
        }
        holder.carburant.setText(modelVoiture.getCarburant());
        holder.kilometrage.setText(modelVoiture.getKilometrage());
        holder.prix.setText(modelVoiture.getPrix());
        return row;
    }
}

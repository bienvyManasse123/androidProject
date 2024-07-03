package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;


public class HomeFragment extends Fragment {


    private TextView tvNombreClient, tvListeCommande, tvNombreVoiture;
    private CardView cardNombreClient, cardListeCommande, cardNombreVoiture;
    private BDController bdController;
    private String userRole;
    private int userId;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialisation des TextView
        tvNombreClient = view.findViewById(R.id.tvNombreClient);
        tvListeCommande = view.findViewById(R.id.tvListeCommande);
        tvNombreVoiture = view.findViewById(R.id.tvNombreVoiture);

        // Initialisation des CardView
        cardNombreClient = view.findViewById(R.id.cardNombreClient);
        cardListeCommande = view.findViewById(R.id.cardListeCommande);
        cardNombreVoiture = view.findViewById(R.id.cardNombreVoiture);

        // Initialisation de l'aide à la base de données
        bdController = new BDController(requireContext());

        // Récupération du rôle et de l'ID de l'utilisateur connecté
//        userRole = getArguments().getString("USER_ROLE");
//        userId = getArguments().getInt("USER_ID");
        userId = getUserId();
        userRole = getUserRole();

        // Mise à jour des TextView en fonction du rôle de l'utilisateur
        updateDashboard();

        return view;
    }


    //////////////**************Fonction dans notre onCreate view*************////////////////////

    //prend le role de l'utilisateur
    private String getUserRole() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_role", "simple_user");
    }
    //prend l'id de l'user
    private int getUserId() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("user_id")) {
            return sharedPreferences.getInt("user_id", -1);
        } else {
            return -1;
        }
    }
    private void updateDashboard() {
        int nombreClient = bdController.getNombreClient();
        int nombreCommande;
        int nombreVoiture = bdController.getNombreVoiture();

        if ("super_admin".equals(userRole)) {
            nombreCommande = bdController.getNombreCommande();
        } else {
            nombreCommande = bdController.getNombreCommandeByUser(userId);
            // Masquer le cardNombreClient pour les utilisateurs simples
//            cardNombreClient.setVisibility(View.GONE);
//            // Ajuster la disposition
//            GridLayout.LayoutParams params = (GridLayout.LayoutParams) cardListeCommande.getLayoutParams();
//            params.columnSpec = GridLayout.spec(0, 2);
//            cardListeCommande.setLayoutParams(params);
            // Masquer le cardNombreClient pour les utilisateurs simples
            cardNombreClient.setVisibility(View.GONE);
            // Ajuster la disposition
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) cardListeCommande.getLayoutParams();
            params.columnSpec = GridLayout.spec(0, 2); // Prendre deux colonnes
            params.width = GridLayout.LayoutParams.MATCH_PARENT; // Ajuster la largeur
            cardListeCommande.setLayoutParams(params);
        }

        tvNombreClient.setText(String.valueOf(nombreClient));
        tvListeCommande.setText(String.valueOf(nombreCommande));
        tvNombreVoiture.setText(String.valueOf(nombreVoiture));
    }
}
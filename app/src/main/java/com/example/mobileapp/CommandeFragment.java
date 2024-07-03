package com.example.mobileapp;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommandeFragment extends Fragment {

    //Declaration de nos variable
    BDController bdController;
    ListView commandeListe;
    ArrayList<modelCommande> mlist;
    CommandeFragmentAdapter mAdapter = null;
    EditText searchOrder;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commande, container, false);

        // Chargement asynchrone des voitures depuis la base de données
        new LoadCommandTask().execute();

        bdController = new BDController(requireContext());
        commandeListe = view.findViewById(R.id.listCommande);
        searchOrder = view.findViewById(R.id.recherchecommande);
        //initialisation sans initialisation mlist et filterdList serons null valeur null
        mlist = new ArrayList<>();
        //filteredList = new ArrayList<>();
        mAdapter = new CommandeFragmentAdapter(requireContext(), R.layout.row_commande, mlist);
        commandeListe.setAdapter(mAdapter);


//        // Initialisez SearchView
//        SearchView searchView = view.findViewById(R.id.searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Optionnel: actions à effectuer lors de la soumission de la recherche
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Convertir le texte de la recherche en minuscules pour la comparaison insensible à la casse
//                String query = newText.toLowerCase().trim();
//                ArrayList<modelCommande> filteredList = new ArrayList<>();
//
//                if (query.isEmpty()) {
//                    filteredList.addAll(mlist); // Afficher toutes les données d'origine si la recherche est vide
//                } else {
//                    for (modelCommande commande : mlist) {
//                        if (commande.getMarque().toLowerCase().contains(query)) {
//                            filteredList.add(commande);
//                        }
//                    }
//                }
//
//                if (!filteredList.equals(mAdapter.getData())) {
//                    mAdapter.updateCommandes(filteredList);
//                }
//
//                return true; // Indiquer que la recherche a été traitée avec succès
//            }
//        });

        // Récupération de l'ID de l'utilisateur connecté
        int userId = getUserId();
        String userRole = getUserRole();
        // Debugging: vérifier le rôle de l'utilisateur
        Log.d("UserRole", "User role: " + userRole);
        Toast.makeText(requireContext(), "User role: " + userRole, Toast.LENGTH_SHORT).show();

        // Récupérer toutes les commandes si l'utilisateur est admin, sinon seulement les commandes de l'utilisateur
        List<modelCommande> commandes = bdController.getCommandes(userId, userRole);
        mlist.addAll(commandes);

        // Initialisation de l'adaptateur avec la liste de commandes
        mAdapter.updateCommandes(commandes);

        //mlist = getCommandes(userId, userRole); // Assurez-vous que cette ligne est exécutée avant d'ajouter le TextWatcher
        //mlist = (ArrayList<modelCommande>) bdController.getCommandes(userId, userRole);
        //TextWacther lors du recherche
//        edtRecherche.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // Rien à faire ici
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Mettre à jour le filtre à chaque changement de texte
//                mAdapter.filter(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // Rien à faire ici
//            }
//        });
        //Recherche d'un commande
        searchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //rien à faire ici
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //rien à faire ici
            }
        });

        commandeListe.setAdapter(mAdapter);

        //Au longClick d'un élément dans le listView
        commandeListe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                CommandeFragmentAdapter adapter = (CommandeFragmentAdapter) parent.getAdapter();

                //final int idSelected = adapter.getCarId(position);
                final int idSelected = adapter.getCommandeId(position);
                String userRole = getUserRole();
                int userId = getUserId();
                final modelCommande commande = bdController.getCommandeById(idSelected);
                final CharSequence[] elem;
                if ("super_admin".equals(userRole)) {
                    elem = new CharSequence[]{"Annuler", "Confirmer", "Changer Status de Livraison"};
                } else {
                    elem = new CharSequence[]{"Annuler"};
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setTitle("Choisir une action");
                //Les fonction qui se déclanche au click de nos bouton dans le dialog
                dialog.setItems(elem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("super_admin".equals(userRole)) {
                            switch (which) {
                                case 0:
                                    //Bouton annuler
                                    annulerCommande(commande);
                                    //verifierEtAnnulerCommande(commande);
                                    break;
                                case 1:
                                    //bouton confirmer
                                    confirmerCommande(commande);
                                    break;
                                case 2:
                                    //bouton changer status commande
                                    changerStatusLivraison(commande);
                                    break;
                            }
                        }else if(commande.getIdUser() == userId) {
                            // Utilisateur simple : Afficher les détails ou Commander
                            switch (which) {
                                case 0:
                                    //Bouton detail
                                    verifierEtAnnulerCommande(commande);
                                    break;
                            }
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

        return view;
    }


        /////////////////****************Déclaration de notre Fonction utiliser dans notre onCreate*******************//////////////////////

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

    //Fonction pour verifieretAnnuler la commande
    private void verifierEtAnnulerCommande(final modelCommande commande) {
        String etatCommande = commande.getEtatCommande();
        String statusLivraison = commande.getStatusLivraison();

        if ("En attente".equals(etatCommande)) {
            // Annuler la commande
            annulerCommande(commande);
        } else if ("Confirmer".equals(etatCommande) && "En route".equals(statusLivraison)) {
            // Afficher un message d'erreur
            Toast.makeText(requireContext(), "La commande est déjà en route et ne peut pas être annulée.", Toast.LENGTH_LONG).show();
        } else if ("Confirmer".equals(etatCommande)) {
            // Annuler la commande
            annulerCommande(commande);
        } else {
            // Afficher un message d'erreur
            Toast.makeText(requireContext(), "La commande ne peut pas être annulée.", Toast.LENGTH_LONG).show();
        }
    }

    //Fonction pour annuler la commande
    @SuppressLint("MissingInflatedId")
    private void annulerCommande(final modelCommande modelcommande) {
        //Pour l'annulation on modifi la valeur de l'etat du commande en annuler et le champs causeAnnulation dans bd sera remplie
        //par la popUp avec une edit text pour saisir la cause de l'annulation du commande.
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_causeannulation, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cause de l'annulation");
        builder.setView(dialogView);

        Button retour = dialogView.findViewById(R.id.btnretourannulation);
        Button annulercommande = dialogView.findViewById(R.id.btnconfirmerannulation);
        EditText causeannuler = dialogView.findViewById(R.id.edtcauseannulation);
        final AlertDialog dialog = builder.create();
        //Au clique du bouton annuler
        annulercommande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String causeAnnulation = causeannuler.getText().toString().trim();
                if (!causeAnnulation.isEmpty()) {
                    // Appeler la méthode pour annuler la commande dans la base de données
                    boolean success = bdController.annulerCommande(modelcommande.getIdCommande(), causeAnnulation);

                    if (success) {
                        // Afficher un message de succès
                        Toast.makeText(getContext(), "Commande annulée avec succès", Toast.LENGTH_SHORT).show();
                        //pour actualiser la listView du changement
                        new LoadCommandTask().execute();
                        dialog.dismiss(); // Fermer la boîte de dialogue après annulation réussie

                    } else {
                        // Afficher un message d'erreur
                        Toast.makeText(getContext(), "Erreur lors de l'annulation de la commande", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Afficher un message pour informer l'utilisateur de saisir une cause
                    Toast.makeText(getContext(), "Veuillez saisir la cause de l'annulation", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //au clique du bouton retour
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Confirmer commande
    private void confirmerCommande(final modelCommande modelcommande) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_addcommande, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Déclaration des id
        Button cmdvalider = dialogView.findViewById(R.id.validercmd);
        Button cmdannuler = dialogView.findViewById(R.id.annulercmd);
        Spinner spinnerModePaiement = dialogView.findViewById(R.id.spinnerModePaiement);
        Spinner spinnerModeLivraison = dialogView.findViewById(R.id.spinnerModeLivraison);
        EditText adresseDelivery = dialogView.findViewById(R.id.txtadresse);
        TextView txt = dialogView.findViewById(R.id.txtviewadresse);
        TextView txtDate = dialogView.findViewById(R.id.txtdate);
        CalendarView calendarView = dialogView.findViewById(R.id.calendarView);

        // Pré-remplir les champs avec les données de modelCommande
        if (modelcommande != null) {
            // Sélectionner le mode de paiement
            String[] modePaiementArray = getResources().getStringArray(R.array.mode_paiement_array);
            for (int i = 0; i < modePaiementArray.length; i++) {
                if (modePaiementArray[i].equals(modelcommande.getModePaiement())) {
                    spinnerModePaiement.setSelection(i);
                    break;
                }
            }

            // Sélectionner le mode de livraison
            String[] modeLivraisonArray = getResources().getStringArray(R.array.mode_livraison_array);
            for (int i = 0; i < modeLivraisonArray.length; i++) {
                if (modeLivraisonArray[i].equals(modelcommande.getModeLivraison())) {
                    spinnerModeLivraison.setSelection(i);
                    break;
                }
            }

            // Remplir l'adresse de livraison
            adresseDelivery.setText(modelcommande.getAdresseLivraison());

            // Remplir la date de livraison si elle est valide
            if (modelcommande.getDateLivraison() != null && !modelcommande.getDateLivraison().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date dateLivraison = sdf.parse(modelcommande.getDateLivraison());
                    if (dateLivraison != null) {
                        calendarView.setDate(dateLivraison.getTime(), true, true);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // Remplir la date de commande si elle est valide
            if (modelcommande.getDateCommande() != null && !modelcommande.getDateCommande().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date dateCommande = sdf.parse(modelcommande.getDateCommande());
                    if (dateCommande != null) {
                        txtDate.setText(sdf.format(dateCommande));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // Gérer les clics sur les boutons
        cmdvalider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs des champs
                String selectedModePaiement = spinnerModePaiement.getSelectedItem().toString().trim();
                String selectedModeLivraison = spinnerModeLivraison.getSelectedItem().toString().trim();
                String adresseLivraison = adresseDelivery.getText().toString().trim();
                String dateLivraison = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(calendarView.getDate()));
                String dateCommande = txtDate.getText().toString().trim(); // Utiliser le texte de la TextView pour la date de commande
                String etatCommande = "Confirmer";
                String statusLivraison = "En cours de préparation";

                // Effectuer l'action de validation
                // Appeler la méthode pour mettre à jour la commande dans la base de données
                boolean miseAJourCommande = bdController.updateCommande(modelcommande.getIdCommande(), modelcommande.getIdUser(), modelcommande.getIdVoiture(), dateCommande, dateLivraison, etatCommande, selectedModePaiement, selectedModeLivraison, adresseLivraison, statusLivraison);

                if (miseAJourCommande) {
                    Toast.makeText(getContext(), "Commande confirmée avec succès", Toast.LENGTH_SHORT).show();
                    //celui d'avant était new LoadCommandTask().execute();
                    // Mettre à jour la liste des commandes et notifier l'adaptateur
                    List<modelCommande> updatedCommandes;
                    if ("super_admin".equals(getUserRole())) {
                        updatedCommandes = bdController.getAllCommandes();
                    } else {
                        updatedCommandes = bdController.getCommandesByUserId(getUserId());
                    }
                    mAdapter.updateCommandes(updatedCommandes);

                    dialog.dismiss(); // Fermer le dialogue après mise à jour réussie
                } else {
                    Toast.makeText(getContext(), "Erreur lors de la confirmation de la commande", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cmdannuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Changer status de livraison
    private void changerStatusLivraison(final modelCommande modelcommande) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_changerstatus_livraison, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Changement status de la livraison");
        builder.setView(dialogView);

        Button retour = dialogView.findViewById(R.id.btncancelstatus);
        Button confirmerStatus = dialogView.findViewById(R.id.btnconfirmerstatus);
        Spinner spinnerStatusLivraison = dialogView.findViewById(R.id.spinnerStatusLivraison);
        EditText motSistatusTard = dialogView.findViewById(R.id.edtcauseretard);
        //Pour la création de notre dialog
        final AlertDialog dialog = builder.create();

        //btn valider changement status
        confirmerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statuslivraison = spinnerStatusLivraison.getSelectedItem().toString().trim();
                String wordRetard = motSistatusTard.getText().toString().trim();
                boolean miseAjourStatus = bdController.updateStatusLivraison(modelcommande.getIdCommande(), statuslivraison, wordRetard);

                if (miseAjourStatus) {
                    Toast.makeText(getContext(), "Statut de livraison mis à jour", Toast.LENGTH_SHORT).show();
                    new LoadCommandTask().execute();
                    dialog.dismiss();
                }
            }
        });

        //btn retour
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Recherche par l'état du commande
//    private void filterCommande(String query) {
//        List<modelCommande> filteredList = new ArrayList<>();
//        for (modelCommande commande : mlist) {
//            if (commande.getEtatCommande().toLowerCase().contains(query.toLowerCase())) {
//                filteredList.add(commande);
//            }
//        }
//        //met à jour l'adapteur pour le changement lors du recherche
//        mAdapter.updateCommandes(filteredList);
//    }
    //recherche par marque de voiture
    private void filterCar(String query) {
        List<modelCommande> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // Si la recherche est vide, afficher toutes les voitures
            filteredList.addAll(mAdapter.getData());
        } else {
            // Filtrer la liste des voitures selon la recherche
            for (modelCommande commande : mAdapter.getData()) {
                if (commande.getMarque().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(commande);
                }
            }
        }

        // Mettre à jour l'adaptateur avec la liste filtrée
        mAdapter.updateCommandes(filteredList);
    }

    //Fonction pour le traitement des thread
    private class LoadCommandTask extends AsyncTask<Void, Void, ArrayList<modelCommande>> {

        @SuppressLint("RestrictedApi")
        @Override
        protected ArrayList<modelCommande> doInBackground(Void... voids) {
            ArrayList<modelCommande> order = new ArrayList<>();
            Cursor cursor = null;

            try {
                cursor = bdController.getData("SELECT c.*, v.image, v.modele, v.marque, v.prix FROM commande c JOIN voiture v ON c.idVoiture = v.idVoiture");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int idCommande = cursor.getInt(cursor.getColumnIndexOrThrow("idCommande"));
                        Log.d(TAG, "Idcommande: " + idCommande);
                        int idUser = cursor.getInt(cursor.getColumnIndexOrThrow("idUser"));
                        int idVoiture = cursor.getInt(cursor.getColumnIndexOrThrow("idVoiture"));
                        byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
                        String modele = cursor.getString(cursor.getColumnIndexOrThrow("modele"));
                        String marque = cursor.getString(cursor.getColumnIndexOrThrow("marque"));
                        String prix = cursor.getString(cursor.getColumnIndexOrThrow("prix"));
                        String dateCommande = cursor.getString(cursor.getColumnIndexOrThrow("dateCommande"));
                        String dateLivraison = cursor.getString(cursor.getColumnIndexOrThrow("dateLivraison"));
                        String etatCommande = cursor.getString(cursor.getColumnIndexOrThrow("etatCommande"));
                        String modePaiement = cursor.getString(cursor.getColumnIndexOrThrow("modePaiement"));
                        String modeLivraison = cursor.getString(cursor.getColumnIndexOrThrow("modeLivraison"));
                        String adresseLivraison = cursor.getString(cursor.getColumnIndexOrThrow("adresseLivraison"));
                        String causeAnnulation = cursor.getString(cursor.getColumnIndexOrThrow("causeAnnulation"));
                        String statusLivraison = cursor.getString(cursor.getColumnIndexOrThrow("statusLivraison"));
                        String causeRetard = cursor.getString(cursor.getColumnIndexOrThrow("causeRetard"));

                        order.add(new modelCommande(idCommande, idUser, idVoiture,image, modele, marque, prix, dateCommande, dateLivraison, etatCommande, modePaiement, modeLivraison, adresseLivraison, causeAnnulation, statusLivraison, causeRetard));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des commandes: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return order;
        }

        @Override
        protected void onPostExecute(ArrayList<modelCommande> order) {
            mlist.clear();
            mlist.addAll(order);

            if (mAdapter == null) {
                mAdapter = new CommandeFragmentAdapter(requireContext(), R.layout.row_commande, mlist);
                commandeListe.setAdapter(mAdapter);
            } else {
                //mAdapter.notifyDataSetChanged();
                mAdapter.setData(order);
            }

            if (mlist.isEmpty()) {
                // Utilisation de runOnUiThread pour afficher le Toast
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "Il n'y a pas encore de données", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


}
package com.example.mobileapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VoitureFragment extends Fragment {

    private static final String TAG = "VoitureFragment";
    private FloatingActionButton btnAdd;
    private BDController bdController;
    private ListView voitureListe;
    private ArrayList<modelVoiture> mlist;
    private VoitureFragmentAdapter mAdapter;

    // Autres variables utiles
    private EditText etMarque, etModele, etKilometrage, etCarburant, etDescription, etPrix, etcouleur, etnbrSiege, edtrecherche;
    private ImageView etImage;
    private Button add, cancel, retourDetaille, addCmd;
    private String userRole;
    private String dateLivraison;

    private static final int REQUEST_CODE_GALLERY = 999;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voiture, container, false);

        // Chargement asynchrone des voitures depuis la base de données
        bdController = new BDController(requireContext());
        btnAdd = view.findViewById(R.id.btnaddcar);
        voitureListe = view.findViewById(R.id.listVoiture);
        edtrecherche = view.findViewById(R.id.txtsearchcar);
        mlist = new ArrayList<>();

        // Chargement asynchrone des voitures depuis la base de données
        new LoadCarsTask().execute();

        mAdapter = new VoitureFragmentAdapter(requireContext(), R.layout.row, mlist);
        voitureListe.setAdapter(mAdapter);
        userRole = getUserRole();
        // Si role simple_utilisateur
        if ("simple_user".equals(userRole)) {
            btnAdd.setEnabled(false);
            btnAdd.setVisibility(View.GONE); // Optionally hide the button
        }

        //Au click du bouton plus on exécute la fonction showAddCarDialog
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCarDialog();
            }
        });
        //mlist

        //Recherche par marche de voiture
        edtrecherche.addTextChangedListener(new TextWatcher() {
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

        //Au click d'un des éléments dans notre listView cet fonction va s'exécuter
        voitureListe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
                                                                                            //ici on récupère la position de l'élément séléctionner pour avoir l'id de cet élément
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                VoitureFragmentAdapter adapter = (VoitureFragmentAdapter) parent.getAdapter();
                //final int idSelected = (int) id;
                userRole = getUserRole();
                // Si role simple_utilisateur
                if ("simple_user".equals(userRole)) {
                    btnAdd.setEnabled(false);
                    btnAdd.setVisibility(View.GONE); // Optionally hide the button
                }
                // Debugging: vérifier le rôle de l'utilisateur
                Log.d("UserRole", "User role: " + userRole);
                //Toast.makeText(requireContext(), "User role: " + userRole, Toast.LENGTH_SHORT).show();
                final int idSelected = adapter.getCarId(position);
                final modelVoiture voiture = bdController.getCarById(idSelected);
                final CharSequence[] elem ;
                // Adapter les options du menu contextuel en fonction du rôle de l'utilisateur
                if ("super_admin".equals(userRole)) {
                    elem = new CharSequence[]{"Modifier", "Supprimer"};
                } else {
                    elem = new CharSequence[]{"Détails", "Commander"};
                }
                AlertDialog.Builder popUpAction = new AlertDialog.Builder(requireContext());
                popUpAction.setTitle("Choisir une action");
                popUpAction.setItems(elem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("super_admin".equals(userRole)) {
                            // Administrateur : Modifier ou Supprimer
                            switch (which) {
                                case 0:
                                    //Bouton modif
                                    showUpDateCarDialog(voiture);
                                    break;
                                case 1:
                                    //bouton supprimer
                                    showDeleteConfirmationDialog(idSelected);
                                    break;
                            }
                        } else {
                            // Utilisateur simple : Afficher les détails ou Commander
                            switch (which) {
                                case 0:
                                    //Bouton detail
                                    showCarDetailsDialog(voiture);
                                    break;
                                case 1:
                                    //Bouton commande
                                    showpopUporder(voiture);
//                                    Toast.makeText(requireContext(), "Commande voiture",Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                });
                popUpAction.show();
                return true;
            }
        });
        return view;
    }



                        //////////***********Declaration des fonction utiliser dans notre onCreateView************///////////////////


    //recherche par marque de voiture
    private void filterCar(String query) {
        List<modelVoiture> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // Si la recherche est vide, afficher toutes les voitures
            filteredList.addAll(mAdapter.getAllData());
        } else {
            // Filtrer la liste des voitures selon la recherche
            for (modelVoiture voiture : mAdapter.getAllData()) {
                if (voiture.getMarque().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(voiture);
                }
            }
        }

        // Mettre à jour l'adaptateur avec la liste filtrée
        mAdapter.updateCar(filteredList);
    }



    //popup affihce détail car
    private void showCarDetailsDialog(final modelVoiture voiture) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_detail_car, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        ImageView etImage = dialogView.findViewById(R.id.imageVoiture);
        TextView etMarque = dialogView.findViewById(R.id.txtmarque);
        TextView etModele = dialogView.findViewById(R.id.txtmodele);
        TextView etKilometrage = dialogView.findViewById(R.id.txtkilometrage);
        TextView etDescription = dialogView.findViewById(R.id.txtdescription);
        TextView etPrix = dialogView.findViewById(R.id.txtprix);
        TextView etcouleur = dialogView.findViewById(R.id.txtcouleurcar);
        TextView etCarburant = dialogView.findViewById(R.id.txtcarburant);
        TextView etnbrSiege = dialogView.findViewById(R.id.txtnbrsiege);
        Button retourDetaille = dialogView.findViewById(R.id.btnretour);
        Button addCmd = dialogView.findViewById(R.id.btncmdCar);

        // valeur voiture selectionner
        etMarque.setText(voiture.getMarque());
        etModele.setText(voiture.getModele());
        etKilometrage.setText(voiture.getKilometrage());
        etDescription.setText(voiture.getDescription());
        etPrix.setText(voiture.getPrix());
        etcouleur.setText(voiture.getCouleur());
        etCarburant.setText(voiture.getCarburant());

        //etnbrSiege.setText(voiture.getNbrsiege());
        //ici nbrSiege est un int donc sans le String.value ça va retourner une erreur car le textView attend une String et non un entier
        etnbrSiege.setText(String.valueOf(voiture.getNbrsiege()));

        Bitmap bitmap = BitmapFactory.decodeByteArray(voiture.getImage(), 0, voiture.getImage().length);
        if (bitmap != null && !bitmap.isRecycled()) {
            etImage.setImageBitmap(bitmap);
        } else {
            etImage.setImageResource(R.drawable.image_car); // Remplacez par une image par défaut si nécessaire
        }

        // btn ajouter commande
        addCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopUporder(voiture);
            }
        });

        // btn retour
        retourDetaille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //affiche le popUP du commande
    @SuppressLint("MissingInflatedId")
    private void showpopUporder(final modelVoiture voiture) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_addcommande, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        //Déclaration des id
        Button cmdvalider = dialogView.findViewById(R.id.validercmd);
        Button cmdannuler = dialogView.findViewById(R.id.annulercmd);
        Spinner spinnerModePaiement = dialogView.findViewById(R.id.spinnerModePaiement);
        Spinner spinnerModeLivraison = dialogView.findViewById(R.id.spinnerModeLivraison);
        EditText adresseDelivery = dialogView.findViewById(R.id.txtadresse);
        TextView txt = dialogView.findViewById(R.id.txtviewadresse);
        TextView txtDate = dialogView.findViewById(R.id.txtdate);
        CalendarView calendarView = dialogView.findViewById(R.id.calendarView);
        adresseDelivery.setVisibility(View.GONE);
        txt.setVisibility(View.GONE);

        // Initialiser la date de livraison avec la date actuelle par défaut
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Construire la date sélectionnée en format "yyyy-MM-dd"
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                // Mettre à jour dateLivraison avec la date sélectionnée
                dateLivraison = selectedDate;
                Toast.makeText(requireContext(), "Date sélectionner: " +dateLivraison, Toast.LENGTH_SHORT).show();
            }
        });
        spinnerModeLivraison.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getItemAtPosition(position).toString();
                if (selectedMode.equals("Livraison à Domicile")) {
                    adresseDelivery.setVisibility(View.VISIBLE); // Rendre l'EditText visible
                    txt.setVisibility(View.VISIBLE);
                    txtDate.setText("Choisir une date pour une rendez-vous chez le Concessionnaire");// Rendre l'EditText visible
                } else {
                    adresseDelivery.setVisibility(View.GONE); // Rendre l'EditText invisible
                    txt.setVisibility(View.GONE); // Rendre l'EditText invisible
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adresseDelivery.setVisibility(View.GONE); // Rendre l'EditText invisible si rien n'est sélectionné
                txt.setVisibility(View.GONE); // Rendre l'EditText invisible si rien n'est sélectionné
            }
        });

        //valider commander
        cmdvalider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedModePaiement = spinnerModePaiement.getSelectedItem().toString().trim();
                String selectdModeLivraison = spinnerModeLivraison.getSelectedItem().toString().trim();
                String adresseLivraison = adresseDelivery.getText().toString().trim();
                String etatCommande = "En attente"; // ou toute autre valeur que vous souhaitez

                // Récupérer la date actuelle pour dateCommande
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateCommande = sdf.format(new Date());

                try {
                    // Récupérer l'ID de l'utilisateur actuellement connecté
                    int userId = getUserId();

                    // Vérifier que l'utilisateur est authentifié et récupérer son ID
                    if (userId != -1) {
                        // Vérifier la validité de la date de livraison par rapport à la date actuelle
                        Date currentDate = new Date();
                        Date selectedDeliveryDate = sdf.parse(dateLivraison); // Assurez-vous que dateLivraison est déjà définie

                        if (selectedDeliveryDate != null && !selectedDeliveryDate.before(currentDate)) {
                            // La date de livraison est valide (elle est après ou égale à la date actuelle)
                            // Appeler la méthode pour insérer la commande dans la base de données
                            boolean ajoutCommande = bdController.addCommande(userId, voiture.getIdVoiture(), dateCommande, dateLivraison, selectdModeLivraison, adresseLivraison, etatCommande, selectedModePaiement);

                            if (ajoutCommande) {
                                // Si l'ajout est réussi, afficher un message de succès
                                Toast.makeText(getContext(), "Commande ajoutée avec succès", Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); // Fermer le dialogue après ajout réussi
                            } else {
                                // Sinon, afficher un message d'erreur
                                Toast.makeText(getContext(), "Erreur lors de l'ajout de la commande", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // La date de livraison est invalide (inférieure à la date actuelle)
                            Toast.makeText(getContext(), "La date de livraison doit être après ou égale à la date actuelle", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Gérer le cas où l'ID utilisateur n'est pas valide
                        Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // En cas d'erreur, afficher l'erreur détaillée
                    Toast.makeText(requireContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace(); // Afficher la trace de la pile pour le débogage
                }
            }
        });

        //annulercommande
        cmdannuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //prend le role de 'user
    private String getUserRole() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_role", "simple_user");
    }
    //prend l'id de l'user
    private int getUserId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("user_id")) {
            return sharedPreferences.getInt("user_id", -1); // -1 est une valeur par défaut si la clé n'est pas trouvée
        } else {
            return -1; // ou une autre valeur par défaut appropriée pour votre logique
        }
    }

    //Affiche le popUpAjoutVoiture lors du click sur le bouton ajouter voiture
    @SuppressLint("MissingInflatedId")
    private void showAddCarDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_add_car, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        // Initialisation des EditText et autres vues
        etMarque = dialogView.findViewById(R.id.marque);
        etModele = dialogView.findViewById(R.id.modele);
        etKilometrage = dialogView.findViewById(R.id.kilometrage);
        etCarburant = dialogView.findViewById(R.id.carburant);
        etImage = dialogView.findViewById(R.id.imgaddcar);
        etDescription = dialogView.findViewById(R.id.description);
        etPrix = dialogView.findViewById(R.id.prix);
        add = dialogView.findViewById(R.id.btnajouter);
        cancel = dialogView.findViewById(R.id.btnannuler);
        etcouleur = dialogView.findViewById(R.id.couleur);
        etnbrSiege = dialogView.findViewById(R.id.nbrsiege);

        final AlertDialog dialog = builder.create();

        // Ajouter une voiture dans la base de données SQLite
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String marque = etMarque.getText().toString().trim();
                    String modele = etModele.getText().toString().trim();
                    String kilometrageStr = etKilometrage.getText().toString().trim();
                    String carburant = etCarburant.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String prixStr = etPrix.getText().toString().trim();
                    String couleur = etcouleur.getText().toString().trim();
                    String nbrsiege = etnbrSiege.getText().toString().trim();

                    boolean insertionReussie = bdController.insertCar(marque, modele, kilometrageStr, carburant, description, prixStr, ImageViewToByte(etImage), couleur, Integer.valueOf(nbrsiege));
                    if (insertionReussie) {
                        Toast.makeText(requireContext(), "Voiture insérée avec succès.", Toast.LENGTH_SHORT).show();
                        new LoadCarsTask().execute(); // Recharger les voitures après l'insertion
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Erreur lors de l'insertion de la voiture.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        etImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY);
                } else {
                    ouvrirGalerie();
                }
            }
        });

        dialog.show();
    }

    //PopUp modif Voiture
    private void showUpDateCarDialog(final modelVoiture voiture) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.popup_add_car, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        // Initialisation des EditText et autres vues
        etMarque = dialogView.findViewById(R.id.marque);
        etModele = dialogView.findViewById(R.id.modele);
        etKilometrage = dialogView.findViewById(R.id.kilometrage);
        etCarburant = dialogView.findViewById(R.id.carburant);
        etImage = dialogView.findViewById(R.id.imgaddcar);
        etDescription = dialogView.findViewById(R.id.description);
        etPrix = dialogView.findViewById(R.id.prix);
        etcouleur = dialogView.findViewById(R.id.couleur);
        etnbrSiege = dialogView.findViewById(R.id.nbrsiege);
        add = dialogView.findViewById(R.id.btnajouter);
        cancel = dialogView.findViewById(R.id.btnannuler);

        // Pré-remplir les champs avec les données existantes
        etMarque.setText(voiture.getMarque());
        etModele.setText(voiture.getModele());
        etPrix.setText(voiture.getPrix());
        etDescription.setText(voiture.getDescription());
        etCarburant.setText(voiture.getCarburant());
        etKilometrage.setText(voiture.getKilometrage());
        etcouleur.setText(voiture.getCouleur());
        //etnbrSiege.setText(voiture.getNbrsiege());
        etnbrSiege.setText(String.valueOf(voiture.getNbrsiege()));


        Bitmap bitmap = BitmapFactory.decodeByteArray(voiture.getImage(), 0, voiture.getImage().length);
        if (bitmap != null && !bitmap.isRecycled()) {
            etImage.setImageBitmap(bitmap);
        } else {
            etImage.setImageResource(R.drawable.image_car); // Remplacez par une image par défaut si nécessaire
        }

        final AlertDialog dialog = builder.create();

        // Ajouter une voiture dans la base de données SQLite
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String marque = etMarque.getText().toString().trim();
                    String modele = etModele.getText().toString().trim();
                    String kilometrageStr = etKilometrage.getText().toString().trim();
                    String carburant = etCarburant.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String prixStr = etPrix.getText().toString().trim();
                    String couleur = etcouleur.getText().toString().trim();
                    String nbrsiege = etnbrSiege.getText().toString().trim();

                    boolean modificationReussie = bdController.updateCar(voiture.getIdVoiture(),marque,modele,prixStr,description,  carburant,kilometrageStr, ImageViewToByte(etImage), couleur, Integer.parseInt(nbrsiege));
                    if (modificationReussie) {
                        Toast.makeText(requireContext(), "Voiture modifié avec succès.", Toast.LENGTH_SHORT).show();
                        new LoadCarsTask().execute(); // Recharger les voitures après l'insertion
                        dialog.dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Erreur lors de la modification de la voiture.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        etImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY);
                } else {
                    ouvrirGalerie();
                }
            }
        });

        dialog.show();
    }

    //Popup pour supr voiture
    private void showDeleteConfirmationDialog(int idSelected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmation de suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer cet élément ?")
                .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Appel à la méthode de suppression dans votre base de données ou votre gestionnaire de données
                        boolean deleteSuccessful = bdController.deleteCar(idSelected);
                        if (deleteSuccessful) {
                            // Mettre à jour votre liste après la suppression si nécessaire
                            new LoadCarsTask().execute();
                            Toast.makeText(requireContext(), "Voiture supprimer avec succès.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Échec de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private byte[] ImageViewToByte(ImageView image) {
        Drawable drawable = image.getDrawable();
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            bitmap = getBitmapFromVectorDrawable((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap getBitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private void ouvrirGalerie() {
        Intent galerieIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galerieIntent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            etImage.setImageURI(selectedImageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ouvrirGalerie();
            } else {
                Toast.makeText(requireContext(), "Permission refusée pour accéder au stockage externe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Fonction pour le traitement des thread
    private class LoadCarsTask extends AsyncTask<Void, Void, ArrayList<modelVoiture>> {

        @Override
        protected ArrayList<modelVoiture> doInBackground(Void... voids) {
            ArrayList<modelVoiture> cars = new ArrayList<>();
            Cursor cursor = null;

            try {
                cursor = bdController.getData("SELECT * FROM voiture");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int IdVoiture = cursor.getInt(cursor.getColumnIndexOrThrow("idVoiture"));
                        Log.d(TAG, "Column: " + IdVoiture);
                        String marque = cursor.getString(cursor.getColumnIndexOrThrow("marque"));
                        String modele = cursor.getString(cursor.getColumnIndexOrThrow("modele"));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                        byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
                        String carburant = cursor.getString(cursor.getColumnIndexOrThrow("carburant"));
                        String kilometrage = cursor.getString(cursor.getColumnIndexOrThrow("kilometrage"));
                        String prix = cursor.getString(cursor.getColumnIndexOrThrow("prix"));
                        String couleur = cursor.getString(cursor.getColumnIndexOrThrow("couleur"));
                        int nbrsiege = cursor.getInt(cursor.getColumnIndexOrThrow("nbrsiege"));

                        cars.add(new modelVoiture(IdVoiture, marque, modele, description, prix, carburant,  kilometrage, image, couleur, nbrsiege));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while reading cursor", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return cars;
        }

        @Override
        protected void onPostExecute(ArrayList<modelVoiture> cars) {
            // Mettre à jour les données et l'UI ici
            mlist.clear();
            mlist.addAll(cars);

            if (mAdapter == null) {
                mAdapter = new VoitureFragmentAdapter(requireContext(), R.layout.row, mlist);
                voitureListe.setAdapter(mAdapter);
            } else {
                //mAdapter.notifyDataSetChanged();
                mAdapter.setData(cars);
            }

            if (mlist.isEmpty()) {
                Toast.makeText(requireContext(), "Il n'y a pas encore de données", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

package com.samsung.socketmap;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.samsung.socketmap.models.Place;

import com.google.android.gms.maps.GoogleMap;
import com.samsung.socketmap.models.Rating;


public class MapActivity extends AppCompatActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap myMap;
    private DatabaseReference placesRef;
    private DatabaseReference ratingRef;
    private FirebaseDatabase database;
    private float averageRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        database = FirebaseDatabase.getInstance();
        placesRef = database.getReference("places");
        ratingRef = database.getReference("raiting");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        ImageView ratingIcon = findViewById(R.id.rating_icon);
        ratingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, RatingActivity.class);
                startActivity(intent);
            }
        });

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addPlaceToDatabase(String address, String description, String userId, LatLng latLng) {
        String placeId = placesRef.push().getKey();

        Place place = new Place(placeId, address, description, userId, latLng.latitude, latLng.longitude);

        placesRef.child(placeId).setValue(place, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Log.e(TAG, "Error adding place to database", error.toException());
                } else {
                    Log.d(TAG, "Place added to database");
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        View root = findViewById(R.id.map);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View place_window = inflater.inflate(R.layout.place_window, null);
        dialog.setView(place_window);

        final MaterialEditText address = place_window.findViewById(R.id.address);
        final MaterialEditText description = place_window.findViewById(R.id.description);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TextUtils.isEmpty(address.getText().toString())) {
                    Snackbar.make(root, "Заполните поля", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description.getText().toString())) {
                    Snackbar.make(root, "Заполните поля", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                addPlaceToDatabase(address.getText().toString(),
                        description.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),
                        latLng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                myMap.addMarker(markerOptions);
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });

        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setOnMapClickListener(this);
        myMap.setOnMapLongClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, enable the map to show current location
            myMap.setMyLocationEnabled(true);
            myMap.setOnMyLocationButtonClickListener(this);
            myMap.setOnMyLocationClickListener(this);
        }


        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                        String address = placeSnapshot.child("address").getValue(String.class);
                        Double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = placeSnapshot.child("longitude").getValue(Double.class);
                        if (latitude != null && longitude != null) {
                            myMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(address));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Dialog dialog = new Dialog(MapActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.card_place);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.BOTTOM);

                RatingBar ratingBar = dialog.getWindow().findViewById(R.id.ratingBar);
                TextView tv_address = dialog.getWindow().findViewById(R.id.tv_address);
                TextView tv_description = dialog.getWindow().findViewById(R.id.tv_description);

                LatLng position = marker.getPosition();
                double latitude = position.latitude;
                double longitude = position.longitude;

                final String[] placeIdneed = new String[1];

                Query query = placesRef.orderByChild("latitude").equalTo(latitude);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                if (place.getLongitude() == longitude) {
                                    tv_address.setText(place.getAddress());
                                    tv_description.setText(place.getDescription());

                                    placeIdneed[0] = place.getPlaceId();
                                }
                            }
                        } else {
                            // Место не найдено
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Обработка ошибок доступа к базе данных
                    }
                });

                String placeId = placeIdneed[0];

                Query query2 = ratingRef.orderByChild("placeId").equalTo(placeId);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float sum = 0;
                        int count = 0;
                        for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                            Rating rating = ratingSnapshot.getValue(Rating.class);
                            sum += rating.getGrade();
                            count++;
                        }
                        if (count > 0) {
                            float average = (float) sum / count;
                            RatingBar ratingBar = dialog.getWindow().findViewById(R.id.ratingBar);
                            ratingBar.setIsIndicator(true);
                            ratingBar.setRating(average);
                            ratingBar.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to read value.", databaseError.toException());
                    }
                });


                Button btnRating = dialog.getWindow().findViewById(R.id.btn_rating);
                btnRating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(MapActivity.this);

                        LayoutInflater inflater = LayoutInflater.from(MapActivity.this);
                        View rating_window = inflater.inflate(R.layout.rating_window, null);
                        dialog2.setView(rating_window);

                        RatingBar ratingBarEdit = rating_window.findViewById(R.id.ratingBarEdit);

                        ratingBarEdit.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating,
                                                        boolean fromUser) {
                                ratingBar.setRating(rating);
                                Toast.makeText(MapActivity.this, "рейтинг: " + String.valueOf(rating),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog2.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        });

                        dialog2.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                float rating = ratingBarEdit.getRating();

                                if (placeIdneed[0].isEmpty()) {

                                } else {
                                    String placeId = placeIdneed[0];
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                                    Rating newRating = new Rating(placeId, userId, rating);

                                    Query query3 = ratingRef.orderByChild("userId").equalTo(userId);
                                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            boolean found = false;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Rating oldRating = snapshot.getValue(Rating.class);
                                                if (oldRating.getPlaceId().equals(placeId) && !found) {
                                                    oldRating.setGrade(rating); // Обновляем значение рейтинга
                                                    ratingRef.child(snapshot.getKey()).setValue(oldRating);
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if (!found) {
                                                // Рейтинг пользователя для этого места не найден, создаем новый
                                                ratingRef.child(ratingRef.push().getKey().toString()).setValue(newRating);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Обработка ошибок
                                        }
                                    });
                                }
                            }
                        });
                        dialog2.show();
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
        //        .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
        //        .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

}

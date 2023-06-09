package com.samsung.socketmap;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.rengwuxian.materialedittext.MaterialEditText;


import com.samsung.socketmap.models.Rating;
import com.samsung.socketmap.models.Place;


public class MapActivity extends AppCompatActivity implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap myMap;
    private DatabaseReference placesRef;
    private DatabaseReference ratingRef;
    private FirebaseDatabase database;
    private boolean flag;
    private double placelatitude, placelongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        flag = true;

        database = FirebaseDatabase.getInstance();
        placesRef = database.getReference("places");
        ratingRef = database.getReference("raiting");

        placelatitude = getIntent().getDoubleExtra("LATITUDE", 0.0);
        placelongitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);
        if (placelatitude != 0.0) {
            flag = false;
        }

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

                int vectorResourceId = R.drawable.marker_socket;
                Drawable vectorDrawable = getResources().getDrawable(vectorResourceId);

                Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                        vectorDrawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap));
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
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            myMap.setMyLocationEnabled(true);
            myMap.setOnMyLocationButtonClickListener(this);
            myMap.setOnMyLocationClickListener(this);
        }

        if (flag) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.0f));
                }
            });
        } else {
            LatLng location = new LatLng(placelatitude, placelongitude);
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            flag = true;
        }

        int vectorResourceId = R.drawable.marker_socket;
        Drawable vectorDrawable = getResources().getDrawable(vectorResourceId);

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                        String address = placeSnapshot.child("address").getValue(String.class);
                        Double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = placeSnapshot.child("longitude").getValue(Double.class);
                        if (latitude != null && longitude != null) {
                            myMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                    .title(address).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
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

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String placeId = placeIdneed[0];

                Query query2 = ratingRef.orderByChild("placeId").equalTo(placeId);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = 0;
                        float sum = 0;
                        for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                            Rating rating = ratingSnapshot.getValue(Rating.class);
                            sum += rating.getGrade();
                            count++;
                        }
                        if (count > 0) {
                            float avgRating = sum / count;
                            ratingBar.setRating(avgRating);
                            ratingBar.setIsIndicator(true);
                            ratingBar.setRating(avgRating);
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
                                                    oldRating.setGrade(rating);
                                                    ratingRef.child(snapshot.getKey()).setValue(oldRating);
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if (!found) {
                                                ratingRef.child(ratingRef.push().getKey().toString()).setValue(newRating);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

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

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}

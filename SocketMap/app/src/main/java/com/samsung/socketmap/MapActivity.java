package com.samsung.socketmap;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.samsung.socketmap.models.DBImitation;
import com.samsung.socketmap.models.Place;
import com.samsung.socketmap.models.User;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap myMap;
    private final Place[] places = DBImitation.places;
    private DatabaseReference placesRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        database = FirebaseDatabase.getInstance();
        placesRef = database.getReference("places");

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
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.BOTTOM);

                LatLng position = marker.getPosition();
                double latitude = position.latitude;
                double longitude = position.longitude;


                Query query = placesRef.orderByChild("latitude").equalTo(latitude);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                if (place.getLongitude() == longitude) {
                                    TextView tv_address = dialog.getWindow().findViewById(R.id.tv_address);
                                    TextView tv_description = dialog.getWindow().findViewById(R.id.tv_description);

                                    tv_address.setText(place.getAddress());
                                    tv_description.setText(place.getDescription());
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
                return true;
            }
        });

    }
}

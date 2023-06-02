package com.samsung.socketmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.samsung.socketmap.models.Place;
import com.samsung.socketmap.models.PlaceAdapter;
import com.samsung.socketmap.models.Rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RatingActivity extends AppCompatActivity {
    private boolean sortByRating = true;
    double userLat;
    double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
            }
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference placesRef = databaseRef.child("places");
        ImageButton sortButton = findViewById(R.id.sort_button);
        sortButton.setImageResource(R.drawable.rating);

        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Place> placeList = new ArrayList<>();
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    place.setAvgRating(0);
                    place.setCountRating(0);
                    Query ratingRef = databaseRef.child("raiting").orderByChild("placeId").equalTo(place.getPlaceId());
                    ratingRef.addValueEventListener(new ValueEventListener() {
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
                                place.setAvgRating(avgRating);
                                place.setCountRating(count);
                            }
                            placeList.add(place);
                            Collections.sort(placeList, new Comparator<Place>() {
                                @Override
                                public int compare(Place o1, Place o2) {
                                    return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                }
                            });
                            RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                            rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                            PlaceAdapter adapter = new PlaceAdapter(placeList, RatingActivity.this);
                            rvPlaces.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByRating = !sortByRating;
                if (sortByRating) {
                    sortButton.setImageResource(R.drawable.rating);
                    placesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Place> placeList = new ArrayList<>();
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                Query ratingRef = databaseRef.child("raiting").orderByChild("placeId").equalTo(place.getPlaceId());
                                ratingRef.addValueEventListener(new ValueEventListener() {
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
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList, RatingActivity.this);
                                        rvPlaces.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    sortButton.setImageResource(R.drawable.distance);
                    placesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Place> placeList = new ArrayList<>();
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                Query ratingRef = databaseRef.child("raiting").orderByChild("placeId").equalTo(place.getPlaceId());
                                ratingRef.addValueEventListener(new ValueEventListener() {
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
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                return Double.compare(o1.getDistance(userLat, userLng, o1.getLatitude(), o1.getLongitude()),
                                                        o2.getDistance(userLat, userLng, o2.getLatitude(), o2.getLongitude()));
                                            }
                                        });
                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList, RatingActivity.this);
                                        rvPlaces.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        ImageView searchBtn = findViewById(R.id.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField = findViewById(R.id.search_field);
                String searchText = searchField.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    searchText = searchText.substring(0,1).toUpperCase() + searchText.substring(1).toLowerCase();
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    Query placesRef = databaseRef.child("places").orderByChild("address").startAt(searchText).endAt(searchText + "\uf8ff");
                    placesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Place> placeList = new ArrayList<>();
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                Query ratingRef = databaseRef.child("rating").orderByChild("placeId").equalTo(place.getPlaceId());
                                ratingRef.addValueEventListener(new ValueEventListener() {
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
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList, RatingActivity.this);
                                        rvPlaces.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("mytag", "Cancelled: " + databaseError.getMessage());
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("mytag", "Cancelled: " + databaseError.getMessage());
                        }
                    });

                    searchText = searchText.toLowerCase();
                    Query placesRef2 = databaseRef.child("places").orderByChild("address").startAt(searchText).endAt(searchText + "\uf8ff");
                    placesRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Place> placeList = new ArrayList<>();
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                Query ratingRef = databaseRef.child("rating").orderByChild("placeId").equalTo(place.getPlaceId());
                                ratingRef.addValueEventListener(new ValueEventListener() {
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
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList, RatingActivity.this);
                                        rvPlaces.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("mytag", "Cancelled: " + databaseError.getMessage());
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("mytag", "Cancelled: " + databaseError.getMessage());
                        }
                    });
                } else {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference placesRef = databaseRef.child("places");

                    placesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Place> placeList = new ArrayList<>();
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                Place place = placeSnapshot.getValue(Place.class);
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                Query ratingRef = databaseRef.child("raiting").orderByChild("placeId").equalTo(place.getPlaceId());
                                ratingRef.addValueEventListener(new ValueEventListener() {
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
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList, RatingActivity.this);
                                        rvPlaces.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
            }
        });

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RatingActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageView mapIcon = findViewById(R.id.map_icon);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RatingActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
}

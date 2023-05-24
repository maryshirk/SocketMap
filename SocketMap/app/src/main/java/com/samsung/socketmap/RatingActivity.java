package com.samsung.socketmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.samsung.socketmap.models.Place;
import com.samsung.socketmap.models.PlaceAdapter;
import com.samsung.socketmap.models.Rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RatingActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference placesRef, ratingsRef;
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // database = FirebaseDatabase.getInstance();
        // placesRef = database.getReference("places");
        // ratingsRef = database.getReference("raiting");

        // Создаем экземпляр класса для работы с БД
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
// Получаем ссылку на коллекцию "places"
        DatabaseReference placesRef = databaseRef.child("places");

// Считываем данные из коллекции "places"
        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Создаем список для хранения объектов Place
                List<Place> placeList = new ArrayList<>();
                // Проходимся по всем дочерним узлам в коллекции
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    // Получаем объект Place из текущего узла
                    Place place = placeSnapshot.getValue(Place.class);
                    // Сбрасываем рейтинг до 0
                    place.setAvgRating(0);
                    place.setCountRating(0);
                    // Получаем ссылку на коллекцию "raiting" для текущего места
                    Query ratingRef = databaseRef.child("raiting").orderByChild("placeId").equalTo(place.getPlaceId());
                    // Считываем данные из коллекции "raiting" для текущего места
                    ratingRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Объявляем переменные для подсчета суммы и количества оценок
                            int count = 0;
                            float sum = 0;
                            // Проходимся по всем дочерним узлам в коллекции
                            for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                // Получаем объект Rating из текущего узла
                                Rating rating = ratingSnapshot.getValue(Rating.class);
                                // Суммируем все оценки
                                sum += rating.getGrade();
                                // Увеличиваем счетчик оценок
                                count++;
                            }
                            // Если были оценки, то вычисляем средний рейтинг для текущего места
                            if (count > 0) {
                                float avgRating = sum / count;
                                place.setAvgRating(avgRating);
                                place.setCountRating(count);
                            }
                            // Добавляем текущее место в список
                            placeList.add(place);
                            Collections.sort(placeList, new Comparator<Place>() {
                                @Override
                                public int compare(Place o1, Place o2) {
                                    return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                }
                            });
                            // Создаем адаптер и устанавливаем его для RecyclerView
                            RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                            rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                            PlaceAdapter adapter = new PlaceAdapter(placeList);
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

        ImageView searchBtn = findViewById(R.id.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField = findViewById(R.id.search_field);
                String searchText = searchField.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    searchText = searchText.substring(0,1).toUpperCase() + searchText.substring(1).toLowerCase();
                    // Получаем ссылку на коллекцию "places"
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    Query placesRef = databaseRef.child("places").orderByChild("address").startAt(searchText).endAt(searchText + "\uf8ff");
                    placesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Создаем список для хранения объектов Place
                            List<Place> placeList = new ArrayList<>();
                            // Проходимся по всем дочерним узлам в коллекции
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                // Получаем объект Place из текущего узла
                                Place place = placeSnapshot.getValue(Place.class);
                                // Сбрасываем рейтинг до 0
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                // Получаем ссылку на коллекцию "rating" для текущего места
                                Query ratingRef = databaseRef.child("rating").orderByChild("placeId").equalTo(place.getPlaceId());
                                // Считываем данные из коллекции "rating" для текущего места
                                ratingRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Объявляем переменные для подсчета суммы и количества оценок
                                        int count = 0;                                float sum = 0;
                                        // Проходимся по всем дочерним узлам в коллекции
                                        for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                            // Получаем объект Rating из текущего узла
                                            Rating rating = ratingSnapshot.getValue(Rating.class);
                                            // Суммируем все оценки
                                            sum += rating.getGrade();
                                            // Увеличиваем счетчик оценок
                                            count++;
                                        }
                                        // Если были оценки, то вычисляем средний рейтинг для текущего места
                                        if (count > 0) {
                                            float avgRating = sum / count;
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        // Добавляем текущее место в список
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                // Сортируем по убыванию по среднему рейтингу
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        // Создаем адаптер и передаем список мест в него

                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList);
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
                            // Создаем список для хранения объектов Place
                            List<Place> placeList = new ArrayList<>();
                            // Проходимся по всем дочерним узлам в коллекции
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                // Получаем объект Place из текущего узла
                                Place place = placeSnapshot.getValue(Place.class);
                                // Сбрасываем рейтинг до 0
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                // Получаем ссылку на коллекцию "rating" для текущего места
                                Query ratingRef = databaseRef.child("rating").orderByChild("placeId").equalTo(place.getPlaceId());
                                // Считываем данные из коллекции "rating" для текущего места
                                ratingRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Объявляем переменные для подсчета суммы и количества оценок
                                        int count = 0;                                float sum = 0;
                                        // Проходимся по всем дочерним узлам в коллекции
                                        for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                            // Получаем объект Rating из текущего узла
                                            Rating rating = ratingSnapshot.getValue(Rating.class);
                                            // Суммируем все оценки
                                            sum += rating.getGrade();
                                            // Увеличиваем счетчик оценок
                                            count++;
                                        }
                                        // Если были оценки, то вычисляем средний рейтинг для текущего места
                                        if (count > 0) {
                                            float avgRating = sum / count;
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        // Добавляем текущее место в список
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                // Сортируем по убыванию по среднему рейтингу
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        // Создаем адаптер и передаем список мест в него

                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList);
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
// Получаем ссылку на коллекцию "places"
                    DatabaseReference placesRef = databaseRef.child("places");

// Считываем данные из коллекции "places"
                    placesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Создаем список для хранения объектов Place
                            List<Place> placeList = new ArrayList<>();
                            // Проходимся по всем дочерним узлам в коллекции
                            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                                // Получаем объект Place из текущего узла
                                Place place = placeSnapshot.getValue(Place.class);
                                // Сбрасываем рейтинг до 0
                                place.setAvgRating(0);
                                place.setCountRating(0);
                                // Получаем ссылку на коллекцию "raiting" для текущего места
                                Query ratingRef = databaseRef.child("raiting").orderByChild("placeId").equalTo(place.getPlaceId());
                                // Считываем данные из коллекции "raiting" для текущего места
                                ratingRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Объявляем переменные для подсчета суммы и количества оценок
                                        int count = 0;
                                        float sum = 0;
                                        // Проходимся по всем дочерним узлам в коллекции
                                        for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                            // Получаем объект Rating из текущего узла
                                            Rating rating = ratingSnapshot.getValue(Rating.class);
                                            // Суммируем все оценки
                                            sum += rating.getGrade();
                                            // Увеличиваем счетчик оценок
                                            count++;
                                        }
                                        // Если были оценки, то вычисляем средний рейтинг для текущего места
                                        if (count > 0) {
                                            float avgRating = sum / count;
                                            place.setAvgRating(avgRating);
                                            place.setCountRating(count);
                                        }
                                        // Добавляем текущее место в список
                                        placeList.add(place);
                                        Collections.sort(placeList, new Comparator<Place>() {
                                            @Override
                                            public int compare(Place o1, Place o2) {
                                                return Float.compare(o2.getAvgRating(), o1.getAvgRating());
                                            }
                                        });
                                        // Создаем адаптер и устанавливаем его для RecyclerView
                                        RecyclerView rvPlaces = findViewById(R.id.recyclerView);
                                        rvPlaces.setLayoutManager(new LinearLayoutManager(RatingActivity.this));
                                        PlaceAdapter adapter = new PlaceAdapter(placeList);
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
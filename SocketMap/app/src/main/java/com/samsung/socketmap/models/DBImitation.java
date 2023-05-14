package com.samsung.socketmap.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.samsung.socketmap.models.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class DBImitation {
    public static Place[] places = {new Place("1", "Canada", "Canada",
            "1", 43.7001, 79.4163)};

    public static Place findByName(String address){

        for (Place place: places) {
            if(place.getAddress().equals(address)) return place;
        }
        // stream api - быстрый способ пройтись по коллекции, сделав выборку/обработку/группировку/... с данными
        // ссылка на информацию - https://metanit.com/java/tutorial/10.1.php
//        return Arrays.stream(places).filter(place -> place.getName().equals(name)).findFirst().get();
        return null;
    }
}
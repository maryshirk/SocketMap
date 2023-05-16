package com.samsung.socketmap.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsung.socketmap.R;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private List<Place> placeList;

    public PlaceAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_place_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.tv_address.setText(place.getAddress());
        holder.tv_description.setText(place.getDescription());
        holder.ratingBar.setRating(place.getAvgRating());
        holder.ratingCount.setText(String.valueOf(place.getCountRating()));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address;
        TextView tv_description;
        RatingBar ratingBar;
        TextView ratingCount;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_address = itemView.findViewById(R.id.tv_address_item);
            tv_description = itemView.findViewById(R.id.tv_description_item);
            ratingBar = itemView.findViewById(R.id.ratingBarItem);
            ratingCount = itemView.findViewById(R.id.rating_count);
        }
    }
}
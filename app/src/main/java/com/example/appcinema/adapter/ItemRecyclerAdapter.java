package com.example.appcinema.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appcinema.MovieDetails;
import com.example.appcinema.R;
import com.example.appcinema.model.CategoryItemList;

import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder> {

    Context context;
    List<CategoryItemList> categoryItemList;

    public ItemRecyclerAdapter(Context context, List<CategoryItemList> categoryItemList) {
        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cat_recycler_row_items, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Glide.with(context).load(categoryItemList.get(position).getImageUrl()).into(holder.itemImage);

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("movieId",categoryItemList.get(position).getId());
                intent.putExtra("movieName",categoryItemList.get(position).getMovieName());
                intent.putExtra("movieImageUrl",categoryItemList.get(position).getImageUrl());
                intent.putExtra("movieFile",categoryItemList.get(position).getFileUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}

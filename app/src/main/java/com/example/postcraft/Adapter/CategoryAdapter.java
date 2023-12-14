package com.example.postcraft.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.postcraft.Activities.PostActivity;
import com.example.postcraft.NetworkResponse.CategoryListResponce;
import com.example.postcraft.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    List<CategoryListResponce.Category> categoryListResponceList, Searchlist;
    Context context;

    public CategoryAdapter(Context context, List<CategoryListResponce.Category> categoryListResponceList) {
        this.context = context;
        this.categoryListResponceList = categoryListResponceList;
        Searchlist = new ArrayList<>(categoryListResponceList);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryListResponce.Category categoryListResponce = Searchlist.get(position);
        holder.itemTextView.setText(categoryListResponce.getCategoryName());
        try {
            Glide.with(context)
                    .load(Searchlist.get(position).getCategoryImage())
                    .placeholder(R.drawable.background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.itemImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PostActivity.class);
                i.putExtra("CategoryId", categoryListResponce.getCategoryId());
                v.getContext().startActivity(i);


            }
        });

    }

    @Override
    public int getItemCount() {
        return Searchlist.size();
    }
    public boolean isEmpty() {
        return Searchlist.isEmpty();
    }

    public void updateData(List<CategoryListResponce.Category> newData) {
        categoryListResponceList.clear();
        categoryListResponceList.addAll(newData);
        Searchlist = new ArrayList<>(newData);
        notifyDataSetChanged();
    }
    public void Search(CharSequence charSequence, RecyclerView rcv,TextView textView) {
        try {
            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                Searchlist = categoryListResponceList;
                rcv.setVisibility(View.VISIBLE);
            } else {
                int flag = 0;
                List<CategoryListResponce.Category> filterlist = new ArrayList<>();
                for (CategoryListResponce.Category Row : categoryListResponceList) {
                    if (Row.getCategoryName().toLowerCase().contains(charString.toLowerCase())) {
                        filterlist.add(Row);
                        flag = 1;
                    }
                }
                if (flag == 1) {
                    Searchlist = filterlist;
                    rcv.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else {
                    rcv.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }

}

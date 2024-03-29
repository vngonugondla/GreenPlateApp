package com.example.greenplate.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;

import java.util.ArrayList;

public class RecipeScrollAdapter extends RecyclerView.Adapter<RecipeScrollAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<RecipeModel> list;

    public RecipeScrollAdapter(Context context, ArrayList<RecipeModel> map) {
        this.context = context;
        this.list = map;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recipeitem,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RecipeModel recipeModel = list.get(position);
        holder.recipeName.setText(recipeModel.getRecipeName());
        Map<String,String> ingredients = recipeModel.getIngredients();
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : ingredients.entrySet()) {
            ingredientsBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        holder.ingredients.setText(ingredientsBuilder.toString());
        if (recipeModel.getHasEnoughIngredients()) {
            holder.ingredientCheckmark.setVisibility(View.VISIBLE);
            holder.ingredientCross.setVisibility(View.GONE);
            holder.recipeName.setTextColor(Color.GREEN);
        } else {
            holder.recipeName.setTextColor(Color.RED);
            holder.ingredientCross.setVisibility(View.VISIBLE);
            holder.ingredientCheckmark.setVisibility(View.GONE);

        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView recipeName, ingredients, ingredientCheckmark, ingredientCross;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.textrecipe);
            ingredients = itemView.findViewById(R.id.textingredients);
            ingredientCheckmark = itemView.findViewById(R.id.ingredientCheckmark);
            ingredientCross = itemView.findViewById(R.id.ingredientCross);
        }
    }
}

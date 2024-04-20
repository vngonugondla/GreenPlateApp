package com.example.greenplate.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;

import java.util.ArrayList;
import java.util.Map;

public class RecipeScrollAdapter extends RecyclerView.Adapter<RecipeScrollAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<RecipeModel> list;
    private OnRecipeClickListener onRecipeClickListener;

    public RecipeScrollAdapter(Context context, ArrayList<RecipeModel> list,
                               OnRecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.onRecipeClickListener = listener;
    }
    //code for implementing scrolling mechanism in the Recipe screen
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recipeitem,
                parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RecipeModel recipeModel = list.get(position);
        holder.recipeName.setText(recipeModel.getRecipeName());
        holder.recipeName.setOnClickListener(view -> {
            if (recipeModel.getHasEnoughIngredients()) {
                onRecipeClickListener.onRecipeClick(recipeModel);
            } else {
                Toast.makeText(context,
                        "You don't have enough ingredients to view recipe details",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Map<String, String> ingredients = recipeModel.getIngredients();
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : ingredients.entrySet()) {
            ingredientsBuilder.append(entry.getKey()).append(": ")
                    .append(entry.getValue()).append("\n");
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
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeName;
        private TextView ingredients;
        private TextView ingredientCheckmark;
        private TextView ingredientCross;

        //viewholder for recipiescroll adapter
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.textrecipe);
            ingredients = itemView.findViewById(R.id.textingredients);
            ingredientCheckmark = itemView.findViewById(R.id.ingredientCheckmark);
            ingredientCross = itemView.findViewById(R.id.ingredientCross);
        }
    }
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeModel recipeModel);
    }
}

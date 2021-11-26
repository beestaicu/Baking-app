package com.example.anthonybs.baking.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anthonybs.baking.R;

import java.util.ArrayList;
import java.util.List;

import data.Recipe;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    private static final String TAG = RecipeListAdapter.class.getSimpleName();
    private final RecipeListAdapterOnClickListener mOnClickListener;
    private List<Recipe> mRecipes = new ArrayList<Recipe>();

    public interface RecipeListAdapterOnClickListener {
        void onClick (Recipe selectedRecipe);
    }

    public RecipeListAdapter(RecipeListAdapterOnClickListener itemClickListener) {
        mOnClickListener = itemClickListener;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mRecipeTextView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mRecipeTextView = (TextView) itemView.findViewById(R.id.recipe_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Recipe selectedRecipe = mRecipes.get(clickedPosition);
            mOnClickListener.onClick(selectedRecipe);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);

        holder.mRecipeTextView.setText(recipe.getName());
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null)
            return 0;
        else
            return mRecipes.size();
    }

    public void setRecipeData (List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }
}

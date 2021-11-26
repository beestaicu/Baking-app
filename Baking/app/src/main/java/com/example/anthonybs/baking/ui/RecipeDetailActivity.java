package com.example.anthonybs.baking.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anthonybs.baking.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.Recipe;

public class RecipeDetailActivity extends AppCompatActivity implements StepListAdapter.StepListAdapterOnClickListener{
    public static final String TAG = "RecipeDetailActivity";
    private static final int RECIPE_DETAIL_ID = 50;
    private Recipe recipe;
    private StepListAdapter stepListAdapter;
    SpannableStringBuilder mSpanStringBuilder = new SpannableStringBuilder();
    String mText;

    @BindView(R.id.imageView) ImageView mRecipeImageView;
    @BindView(R.id.tv_serving) TextView mServingsTextView;
    @BindView(R.id.tv_ingredients) TextView mIngredientsTextView;
    @BindView(R.id.rv_steps) RecyclerView mStepListRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ButterKnife.bind(this);

        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            if (parentIntent.hasExtra(Intent.EXTRA_TEXT)) {
                recipe = parentIntent.getParcelableExtra(Intent.EXTRA_TEXT);
                setRecipeDetailView();
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mStepListRecyclerView.setLayoutManager(layoutManager);
        mStepListRecyclerView.setHasFixedSize(true);

        stepListAdapter = new StepListAdapter(this, recipe.getSteps());
        mStepListRecyclerView.setAdapter(stepListAdapter);
    }

    private void setRecipeDetailView() {
        if (!recipe.getImage().equals("")) {
            Uri recipeImageUri = Uri.parse(recipe.getImage());
            Glide.with(this)
                    .load(recipeImageUri)
                    .into(mRecipeImageView);
        }

        setTitle(recipe.getName());
        mServingsTextView.setText(String.valueOf(recipe.getServings()));

        for (int i = 0; i < recipe.getIngredients().size(); i++)
        {
            mText += "\n"+recipe.getIngredients().get(i);
        }
        mSpanStringBuilder = new SpannableStringBuilder(mText);
        for (int j = 0; j < recipe.getIngredients().size(); j++)
        {
            showBullet(recipe.getIngredients().get(j));
        }
        //remove null and new line at the beginning of string
        mSpanStringBuilder.delete(0,5);
        mIngredientsTextView.setText(mSpanStringBuilder);
    }


    protected void showBullet(String textToBullet){
        // Initialize a new BulletSpan
        BulletSpan bulletSpan = new BulletSpan(
                30, Color.BLACK);

        // Apply the bullet to the span
        mSpanStringBuilder.setSpan(bulletSpan, mText.indexOf(textToBullet),
                mText.indexOf(textToBullet) + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onClick(ArrayList<Recipe.Step> steps, int selectedPosition) {
        Class destinationClass = StepDetailActivity.class;
        Intent intentToStartRecipeDetailActivity = new Intent(this, destinationClass);
        intentToStartRecipeDetailActivity.putExtra(Intent.EXTRA_TEXT, selectedPosition);
        intentToStartRecipeDetailActivity.putParcelableArrayListExtra("Steps", steps);
        startActivity(intentToStartRecipeDetailActivity);
    }
}


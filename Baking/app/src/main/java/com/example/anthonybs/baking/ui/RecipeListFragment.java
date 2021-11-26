package com.example.anthonybs.baking.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anthonybs.baking.R;
import com.example.anthonybs.baking.utils.JsonUtils;
import com.example.anthonybs.baking.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.Recipe;

public class RecipeListFragment extends Fragment implements RecipeListAdapter.RecipeListAdapterOnClickListener,
        LoaderManager.LoaderCallbacks<List<Recipe>>{
    private RecipeListAdapter mRecipeAdapter;
    private static final int RECIPE_LOADER_ID = 10;
    private static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    @BindView(R.id.rv_recipes_list) RecyclerView mRecipeListView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mProgressBar;

    public RecipeListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        ButterKnife.bind(this, rootView);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mRecipeListView.setLayoutManager (new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        else
            mRecipeListView.setLayoutManager (new GridLayoutManager(getContext(), 4));

        mRecipeAdapter = new RecipeListAdapter(this);
        mRecipeListView.setAdapter(mRecipeAdapter);
        getLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        Class destinationClass = RecipeDetailActivity.class;
        Intent intentToStartRecipeDetailActivity = new Intent(getContext(), destinationClass);
        intentToStartRecipeDetailActivity.putExtra(Intent.EXTRA_TEXT, selectedRecipe);
        startActivity(intentToStartRecipeDetailActivity);
    }

    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Recipe>>(getContext()) {

            public List<Recipe> recipes;

            @Override
            protected void onStartLoading() {
                if (NetworkUtils.hasNetworkConnection(getContext())) {
                    super.onStartLoading();
                    mProgressBar.setVisibility(View.VISIBLE);
                    if (recipes != null)
                        deliverResult(recipes);
                    else
                        forceLoad();
                }
                else
                    showErrorMessage();
            }

            @Override
            public List<Recipe> loadInBackground() {
                List<Recipe> tempRecipes = new ArrayList<Recipe>();

                if (NetworkUtils.hasNetworkConnection(getContext())) {
                        try {
                            URL recipeListUrl = new URL(RECIPE_URL);
                            String response = NetworkUtils.getResponseFromHttpUrl(recipeListUrl);
                            recipes = JsonUtils.parseRecipeJson(response);

                            return recipes;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                }
                else
                    return null;
            }

            @Override
            public void deliverResult(List<Recipe> data) {
                recipes = data;
                super.deliverResult(data);
            }
        };
    }

    private void showRecipeDataView() {
        mErrorMessageView.setVisibility(View.INVISIBLE);
        mRecipeListView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecipeListView.setVisibility(View.INVISIBLE);
        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            showRecipeDataView();
            mRecipeAdapter.setRecipeData(data);
        }
        else
            showErrorMessage();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {

    }
}

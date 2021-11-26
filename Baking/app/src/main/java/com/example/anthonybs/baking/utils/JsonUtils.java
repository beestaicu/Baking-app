package com.example.anthonybs.baking.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import data.Recipe;

public class JsonUtils {

    public static List<Recipe> parseRecipeJson(String json) throws JSONException{
        List<Recipe> recipes = new ArrayList<Recipe>();
        Recipe recipe;

        if (json != null) {
            try {
                JSONArray jsonRecipesArray = new JSONArray(json);

                for (int i = 0; i < jsonRecipesArray.length(); i++) {
                    JSONObject recipeJSON = jsonRecipesArray.getJSONObject(i);
                    String recipeName = recipeJSON.getString("name");
                    List<String> ingredients = new ArrayList<String>();
                    JSONArray ingredientsJsonArray = recipeJSON.getJSONArray("ingredients");
                    for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                        JSONObject ingredientJsonObject = ingredientsJsonArray.getJSONObject(j);
                        String ingredient = ingredientJsonObject.getString("quantity") + " " +
                                ingredientJsonObject.getString("measure") + " " +
                                ingredientJsonObject.getString("ingredient");

                        ingredients.add(ingredient);
                    }

                    JSONArray stepsJsonArray = recipeJSON.getJSONArray("steps");
                    ArrayList<Recipe.Step> steps = new ArrayList<Recipe.Step>();
                    for (int k = 0; k < stepsJsonArray.length(); k++) {
                        JSONObject stepJsonObject = stepsJsonArray.getJSONObject(k);
                        Recipe.Step step = new Recipe.Step();
                        step.setShortDescription(stepJsonObject.getString("shortDescription"));
                        step.setDescription(stepJsonObject.getString("description"));
                        step.setVideoUrl(stepJsonObject.getString("videoURL"));
                        step.setThumbnailUrl(stepJsonObject.getString("thumbnailURL"));

                        steps.add(step);
                    }

                    int servings = recipeJSON.getInt("servings");
                    String image = recipeJSON.getString("image");

                    recipe = new Recipe(recipeName, ingredients, steps, servings, image);
                    recipes.add(recipe);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return recipes;
    }
}

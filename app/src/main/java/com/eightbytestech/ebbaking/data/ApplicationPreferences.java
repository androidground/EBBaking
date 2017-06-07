package com.eightbytestech.ebbaking.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.eightbytestech.ebbaking.R;


public class ApplicationPreferences {

    private static final String RECIPE_ID = "recipe_id";
    private static final String RECIPE_NAME = "recipe_name";
    private static final String RECIPE_INSTRUCTIONS = "instructions";
    private static final String RECIPE_IMAGE = "recipe_image";

    /**
     * Method to return the default shared preferences
     *
     * @param context - used to access default shared preference
     * @return - SharedPreferences
     */
    private static SharedPreferences getDefaultPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Method to return shared preferences editor
     *
     * @param context - Used to access the shared prefernces
     * @return - SharedPreferences editor
     */
    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = getDefaultPreferences(context);
        return sharedPreferences.edit();
    }

    /**
     * Method to save the ID of the recipe
     *
     * @param context  - Used to fetch the Shared preference editor
     * @param recipeId - Recipe ID to be stored
     */
    public static void setRecipeId(Context context, int recipeId) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(RECIPE_ID, recipeId);
        editor.commit();
    }

    /**
     * Method to return the Recipe ID
     *
     * @param context - Used to access the SharedPreference Editor
     * @return - returns the Recipe ID stored in the preference
     */
    public static int getRecipeId(Context context) {
        return getDefaultPreferences(context).getInt(RECIPE_ID, 0);
    }

    /**
     * Method to store the Recipe name in SharedPreference
     *
     * @param context    - Used to access the SharedPreference Editor
     * @param recipeName - Recipe name to be stored in SharedPreference
     */
    public static void setRecipeName(Context context, String recipeName) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(RECIPE_NAME, recipeName);
        editor.commit();
    }

    /**
     * Method to return the Recipe Name stored in the SharedPreference
     *
     * @param context - Used to access the SharedPreference Editor
     * @return - returns the Recipe Name in SharedPreference
     */
    public static String getRecipeName(Context context) {
        return getDefaultPreferences(context).getString(RECIPE_NAME, context.getString(R.string.app_name));
    }

    /**
     * Method to store the total instructions for a recipe
     *
     * @param context      - Used to access the SharedPreference Editor
     * @param instructions - The total instructions count to be stored
     */
    public static void setTotalInstructions(Context context, int instructions) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(RECIPE_INSTRUCTIONS, instructions - 1);
        editor.commit();
    }

    /**
     * Method to return the total instructions for a particular recipe
     *
     * @param context - Used to access the SharedPreference Editor
     * @return - Returns the total instructions for a particular recipe
     */
    public static int getTotalInstructions(Context context) {
        return getDefaultPreferences(context).getInt(RECIPE_INSTRUCTIONS, 0);
    }

    /**
     * Method to store the Recipe image URL
     *
     * @param context      - Used to access the SharedPreference Editor
     * @param image - The image URL for the recipe
     */
    public static void setRecipeImage(Context context, String image) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(RECIPE_IMAGE, image);
        editor.commit();
    }

    /**
     * Method to return the Recipe image URL
     *
     * @param context - Used to access the SharedPreference Editor
     * @return - Returns the image URL for the recipe
     */
    public static String getRecipeImage(Context context) {
        return getDefaultPreferences(context).getString(RECIPE_IMAGE, "");
    }
}

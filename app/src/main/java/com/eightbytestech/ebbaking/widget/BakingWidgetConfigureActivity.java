package com.eightbytestech.ebbaking.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eightbytestech.ebbaking.R;
import com.eightbytestech.ebbaking.adapters.RecipesAdapter;
import com.eightbytestech.ebbaking.data.BakingProvider;
import com.eightbytestech.ebbaking.models.Ingredient;
import com.eightbytestech.ebbaking.models.Recipe;
import com.eightbytestech.ebbaking.models.Step;
import com.eightbytestech.ebbaking.utils.BakingApiInterface;
import com.eightbytestech.ebbaking.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * The configuration screen for the {@link BakingWidget BakingWidget} AppWidget.
 */
public class BakingWidgetConfigureActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String PREFS_NAME = "com.eightbytestech.mywidgets.NewAppWidget";

    private static final String PREF_RECIPE_ID = "ID_";
    private static final String PREF_RECIPE_NAME = "NAME_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.recipesGridView)
    GridView mRecipesGrid;

    @BindView(R.id.errorTextView)
    TextView mErrorText;

    @BindView(R.id.loadingDataProgress)
    ProgressBar mLoadingProgress;

    CompositeDisposable mCompositeDisposable;
    RecipesAdapter mAdapter;
    private ArrayList<Recipe> mRecipeItems = new ArrayList<>();

    public BakingWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId, String recipeName, int recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_RECIPE_ID + appWidgetId, recipeId);
        prefs.putString(PREF_RECIPE_NAME + appWidgetId, recipeName);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadRecipeName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String recipeName = prefs.getString(PREF_RECIPE_NAME + appWidgetId, null);
        if (recipeName != null) {
            return recipeName;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static int loadRecipeId(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int recipeId = prefs.getInt(PREF_RECIPE_ID + appWidgetId, 0);
        if ( recipeId > 0 ) {
            return recipeId;
        } else {
            return Integer.parseInt(context.getString(R.string.appwidget_recipeid));
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_RECIPE_ID + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_widget_configure);

        mCompositeDisposable = new CompositeDisposable();
        ButterKnife.bind(this);

        mLoadingProgress.setVisibility(View.GONE);

        BakingApiInterface bakingInterface = NetworkUtils.buildRetrofit().create(BakingApiInterface.class);

        mCompositeDisposable.add(bakingInterface.getRecipes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::apiResponse, this::apiError));

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    /**
     * Method to read json response and parse the same
     * by passing the objects to the adapter
     *
     * @param recipes - Main GSON Object class
     */
    private void apiResponse(ArrayList<Recipe> recipes) {
        mLoadingProgress.setVisibility(View.GONE);

        getContentResolver().delete(BakingProvider.BakingTable.CONTENT_URI, null, null);
        getContentResolver().delete(BakingProvider.IngredientsTable.CONTENT_URI, null, null);
        getContentResolver().delete(BakingProvider.StepsTable.CONTENT_URI, null, null);

        for (Recipe recipe : recipes) {
            mRecipeItems.add(recipe);

            ContentValues cvRecipe = new ContentValues();
            cvRecipe.put("recipe_id", recipe.getId());
            cvRecipe.put("name", recipe.getName());
            cvRecipe.put("servings", recipe.getServings());
            cvRecipe.put("image", recipe.getImage());
            getContentResolver().insert(BakingProvider.BakingTable.CONTENT_URI, cvRecipe);

            for (Ingredient ingredient : recipe.getIngredients()) {

                ContentValues cvIngredient = new ContentValues();
                cvIngredient.put("recipe_id", recipe.getId());
                cvIngredient.put("ingredient", ingredient.getIngredient());
                cvIngredient.put("quantity", ingredient.getQuantity());
                cvIngredient.put("measure", ingredient.getMeasure());

                getContentResolver().insert(BakingProvider.IngredientsTable.CONTENT_URI, cvIngredient);
            }

            for (Step step : recipe.getSteps()) {

                ContentValues cvSteps = new ContentValues();
                cvSteps.put("recipe_id", recipe.getId());
                cvSteps.put("step", step.getId());
                cvSteps.put("short_description", step.getShortDescription());
                cvSteps.put("long_description", step.getDescription());
                cvSteps.put("video_url", step.getVideoURL());
                cvSteps.put("thumnail_url", step.getThumbnailURL());

                getContentResolver().insert(BakingProvider.StepsTable.CONTENT_URI, cvSteps);
            }
        }

        mAdapter = new RecipesAdapter(this, mRecipeItems);
        mRecipesGrid.setAdapter(mAdapter);
        mRecipesGrid.setOnItemClickListener(this);
    }

    /**
     * Method to display the error during network operations
     *
     * @param error -Throwable to access the localized message
     */
    private void apiError(Throwable error) {
        mLoadingProgress.setVisibility(View.GONE);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(error.getLocalizedMessage());
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Context context = BakingWidgetConfigureActivity.this;

        Recipe recipe = mRecipeItems.get(position);

        createWidget(context, recipe.getName(), recipe.getId());
    }

    private void createWidget(Context context, String recipeName, int recipeId) {
        // Store the string locally
        saveRecipePref(context, mAppWidgetId, recipeName, recipeId);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        BakingWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}


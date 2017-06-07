package com.eightbytestech.ebbaking;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eightbytestech.ebbaking.adapters.RecipesAdapter;
import com.eightbytestech.ebbaking.data.ApplicationPreferences;
import com.eightbytestech.ebbaking.data.BakingProvider;
import com.eightbytestech.ebbaking.models.Ingredient;
import com.eightbytestech.ebbaking.models.Recipe;
import com.eightbytestech.ebbaking.models.Step;
import com.eightbytestech.ebbaking.utils.BakingApiInterface;
import com.eightbytestech.ebbaking.utils.NetworkUtils;
import com.eightbytestech.ebbaking.utils.ShareUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RecipesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.recipesGridView)
    GridView mRecipesGrid;

    @BindView(R.id.errorTextView)
    TextView mErrorText;

    @BindView(R.id.loadingDataProgress)
    ProgressBar mLoadingProgress;

    CompositeDisposable mCompositeDisposable;
    RecipesAdapter mAdapter;
    private ArrayList<Recipe> mRecipeItems = new ArrayList<>();

    public static final String RECIPE_ID = "recipe_id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPES = "recipes";
    public static final String RECIPE_POSITION = "recipe_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        mCompositeDisposable = new CompositeDisposable();
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            BakingApiInterface bakingInterface = NetworkUtils.buildRetrofit().create(BakingApiInterface.class);

            mCompositeDisposable.add(bakingInterface.getRecipes()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::apiResponse, this::apiError));
        } else {

            mLoadingProgress.setVisibility(View.GONE);

            mRecipeItems = savedInstanceState.getParcelableArrayList(RECIPES);
            mAdapter = new RecipesAdapter(this, mRecipeItems);
            mRecipesGrid.setAdapter(mAdapter);
            mRecipesGrid.setOnItemClickListener(this);

            int index = savedInstanceState.getInt(RECIPE_POSITION);
            mRecipesGrid.smoothScrollToPosition(index);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_share:
                new ShareUtils().shareApp(this);
                break;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int index = mRecipesGrid.getFirstVisiblePosition();
        outState.putInt(RECIPE_POSITION, index);
        outState.putParcelableArrayList(RECIPES, mRecipeItems);
        super.onSaveInstanceState(outState);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Recipe recipe = mRecipeItems.get(position);
        ApplicationPreferences.setRecipeId(this,recipe.getId());
        ApplicationPreferences.setRecipeName(this,recipe.getName());
        ApplicationPreferences.setRecipeImage(this,recipe.getImage());

        Intent recipesInstructionsIntent = new Intent(this, RecipeInstructionsListActivity.class);
        startActivity(recipesInstructionsIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}

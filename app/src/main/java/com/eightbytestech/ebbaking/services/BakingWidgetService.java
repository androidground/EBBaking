package com.eightbytestech.ebbaking.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.eightbytestech.ebbaking.R;
import com.eightbytestech.ebbaking.data.BakingProvider;
import com.eightbytestech.ebbaking.data.IngredientsColumns;
import com.eightbytestech.ebbaking.models.Ingredient;
import com.eightbytestech.ebbaking.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishalsaxena on 05/06/17.
 */

public class BakingWidgetService extends RemoteViewsService {
    public static final String RECIPE_KEY = "recipe";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingWidgetFactory(this.getApplicationContext(), intent.getIntExtra(RECIPE_KEY,0));
    }

    private class BakingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private int mRecipeId;
        private List<Ingredient> mIngredientsList;

        public BakingWidgetFactory(Context context, int recipeId) {
            this.mContext = context;
            this.mRecipeId = recipeId;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mIngredientsList = new ArrayList<Ingredient>();
            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(BakingProvider.IngredientsTable.CONTENT_URI
                            , null
                            , IngredientsColumns.RECIPE_ID + "=" + this.mRecipeId
                            , null
                            , null);
            if (cursor != null) {
                int i = 1;
                if (cursor.moveToFirst()) {
                    do {
                        Ingredient tempIngredient = new Ingredient();
                        tempIngredient.setIngredient(cursor.getString(
                                cursor.getColumnIndex(IngredientsColumns.INGREDIENT)));
                        tempIngredient.setMeasure(cursor.getString(
                                cursor.getColumnIndex(IngredientsColumns.MEASURE)));
                        tempIngredient.setQuantity(cursor.getFloat(
                                cursor.getColumnIndex(IngredientsColumns.QUANTITY)));

                        mIngredientsList.add(tempIngredient);
                        i++;
                    } while (cursor.moveToNext());
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mIngredientsList == null ? 0 : mIngredientsList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(mIngredientsList != null && mIngredientsList.size() > 0) {
                Ingredient ingredient = mIngredientsList.get(position);

                RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_ingredients);
                views.setTextViewText(R.id.item_widget_ingredient, ViewUtils.getIngredientString(mContext, ingredient));

                return views;
            }

            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}



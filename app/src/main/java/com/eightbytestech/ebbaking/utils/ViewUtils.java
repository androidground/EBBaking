package com.eightbytestech.ebbaking.utils;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.eightbytestech.ebbaking.R;
import com.eightbytestech.ebbaking.models.Ingredient;

import java.util.List;

/**
 * Created by mou on 25/05/17.
 */

public class ViewUtils {

    public static String getIngredientString(Context context, Ingredient ingredient) {
        String format = context.getString(R.string.ingredients_item);

        return String.format(format,
                String.valueOf(ingredient.getQuantity()),
                ingredient.getMeasure(),
                ingredient.getIngredient());
    }
}

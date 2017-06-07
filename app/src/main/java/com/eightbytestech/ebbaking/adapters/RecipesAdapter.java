package com.eightbytestech.ebbaking.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eightbytestech.ebbaking.R;
import com.eightbytestech.ebbaking.models.Recipe;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishalsaxena on 31/05/17.
 */

public class RecipesAdapter extends BaseAdapter {

    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private Context mContext;

    @BindView(R.id.recipeTitleText)
    TextView mRecipeTitleText;

    @BindView(R.id.recipeServesText)
    TextView mRecipeServesText;

    @BindView(R.id.recipeRelativeLayout)
    RelativeLayout mRecipeContainer;

    @BindView(R.id.recipeImageView)
    ImageView mRecipeImage;

    public RecipesAdapter(Context context, ArrayList<Recipe> recipes) {
        this.mContext = context;
        this.mRecipeList = recipes;
    }

    @Override
    public int getCount() {
        return mRecipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if ( convertView == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recipe_items, viewGroup, false);
            ButterKnife.bind(this, convertView);
        }

        Recipe item = (Recipe) getItem(position);
        String servesText =
                mContext.getString(R.string.recipe_serves_text) + " : " + item.getServings();

        mRecipeTitleText.setText(item.getName());
        mRecipeServesText.setText(servesText);

        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        mRecipeContainer.setBackgroundColor(Color.rgb(r, g, b));

        String url = item.getImage();

        if (url != null) {
            if (!url.isEmpty()) {
                Glide.with(mContext).load(url).centerCrop().into(mRecipeImage);
            } else {
                Glide.with(mContext).load("").placeholder(R.drawable.ebbaking_food_cake).fitCenter().into(mRecipeImage);
            }
        }

        return convertView;
    }
}

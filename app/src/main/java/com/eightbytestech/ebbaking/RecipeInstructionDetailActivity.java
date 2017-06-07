package com.eightbytestech.ebbaking;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.eightbytestech.ebbaking.data.ApplicationPreferences;
import com.eightbytestech.ebbaking.data.BakingProvider;
import com.eightbytestech.ebbaking.data.StepsColumns;
import com.eightbytestech.ebbaking.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.ButterKnife;

/**
 * An activity representing a single RecipeItem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeInstructionsListActivity}.
 */
public class RecipeInstructionDetailActivity extends AppCompatActivity {

    int mInstructionStep;
    SimpleExoPlayer mExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_instruction_detail);

        String name = ApplicationPreferences.getRecipeName(this);
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(name);
        }

        Bundle arguments = new Bundle();

        if (savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.containsKey(RecipeInstructionsListActivity.INSTRUCTION_STEP)) {
                    mInstructionStep
                            = extras.getInt(RecipeInstructionsListActivity.INSTRUCTION_STEP) - 1;
                    arguments.putInt(RecipeInstructionsListActivity.INSTRUCTION_STEP,
                            extras.getInt(RecipeInstructionsListActivity.INSTRUCTION_STEP));
                }
            }
        } else {
            mInstructionStep
                    = savedInstanceState.getInt(RecipeInstructionsListActivity.INSTRUCTION_STEP);
        }

        checkOrientationAndLoad(arguments, savedInstanceState);
    }

    private void checkOrientationAndLoad(Bundle arguments, Bundle savedInstanceState) {

        if ((getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) || (mInstructionStep == -1)) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            ImageView recipeImage = ButterKnife.findById(this, R.id.recipeImageView);

            String imageUrl = ApplicationPreferences.getRecipeImage(this);
            if (!imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ebbaking_food_cake)
                        .error(R.drawable.ebbaking_food_cake)
                        .into(recipeImage);
            }

            if (savedInstanceState == null) {
                RecipeInstructionDetailFragment fragment = new RecipeInstructionDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipeItemDetailContainer, fragment)
                        .commit();
            }
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            SimpleExoPlayerView simpleExoPlayer =
                    ButterKnife.findById(this, R.id.instruction_player);
            simpleExoPlayer.setVisibility(View.VISIBLE);

            Step step = getInstruction(this, mInstructionStep);
            if (step != null) {
                loadMediaPlayer(step.getVideoURL());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RecipeInstructionsListActivity.INSTRUCTION_STEP, mInstructionStep);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RecipeInstructionsListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMediaPlayer(String videoUrl) {
        TrackSelector trackSelector = new DefaultTrackSelector();

        SimpleExoPlayerView simpleExoPlayer
                = ButterKnife.findById(this, R.id.instruction_player);

        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this
                , trackSelector, loadControl);
        simpleExoPlayer.setPlayer(mExoPlayer);

        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));

        if (videoUrl != null) {
            if (!videoUrl.isEmpty()) {
                Log.d("check url", videoUrl);
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videoUrl)
                        , new DefaultDataSourceFactory(
                        this, userAgent), new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
            } else {
                simpleExoPlayer.setVisibility(View.GONE);
            }
        } else {
            simpleExoPlayer.setVisibility(View.GONE);
        }
    }

    public static Step getInstruction(Activity activity, int instructionCount) {
        Step step = null;

        int recipeId = ApplicationPreferences.getRecipeId(activity);

        Cursor cursor = activity.getContentResolver()
                .query(BakingProvider.StepsTable.CONTENT_URI
                        , null
                        , StepsColumns.RECIPE_ID + "=" + recipeId
                                + " AND " + StepsColumns.STEPS + "=" + instructionCount
                        , null
                        , null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                step = new Step();
                step.setShortDescription(cursor.getString(
                        cursor.getColumnIndex(StepsColumns.SHORT_DESCRIPTION)));
                step.setDescription(cursor.getString(
                        cursor.getColumnIndex(StepsColumns.LONG_DESCRIPTION)));
                step.setVideoURL(cursor.getString(
                        cursor.getColumnIndex(StepsColumns.VIDEO_URL)));
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return step;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
}

package com.eightbytestech.ebbaking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.eightbytestech.ebbaking.R;
import com.eightbytestech.ebbaking.services.BakingWidgetService;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakingWidgetConfigureActivity BakingWidgetConfigureActivity}
 */
public class BakingWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String recipeName = BakingWidgetConfigureActivity.loadRecipeName(context, appWidgetId);
        int recipeId = BakingWidgetConfigureActivity.loadRecipeId(context, appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        configWidgetViews(context, views, recipeName, recipeId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void configWidgetViews(Context context, RemoteViews views, String recipeName, int recipeId) {
        views.setTextViewText(R.id.appwidget_title, recipeName);
        Intent intent = new Intent(context, BakingWidgetService.class);
        intent.putExtra(BakingWidgetService.RECIPE_KEY, recipeId);
        views.setRemoteAdapter(R.id.appwidget_steps, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BakingWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


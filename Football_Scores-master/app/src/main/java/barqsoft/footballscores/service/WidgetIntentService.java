package barqsoft.footballscores.service;

import android.app.IntentService;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.FootballScoresWidgetProvider;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresDBHelper;
import barqsoft.footballscores.ScoresProvider;

/**
 * Created by Douglas on 10/23/2015.
 */
public class WidgetIntentService extends IntentService implements Loader.OnLoadCompleteListener<Cursor> {

    public static final int WIDGET_LOADER = 0;
    public CursorLoader mCursorLoader;

    public WidgetIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data from the incoming Intent
        String dataString = intent.getDataString();

        String[] projection = {
                DatabaseContract.scores_table._ID,
                DatabaseContract.scores_table.DATE_COL,
                DatabaseContract.scores_table.HOME_COL,
                DatabaseContract.scores_table.HOME_GOALS_COL,
                DatabaseContract.scores_table.AWAY_COL,
                DatabaseContract.scores_table.AWAY_GOALS_COL,
        };
        String selectionclause = null; //for example WHERE or LIKE
        String[] mSelectionArgs = {""}; //For example ID or Name

        String[] date = new String[1];
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        date[0] = mformat.format(now);

        // Get today's data from the ContentProvider
        mCursorLoader = new CursorLoader(getApplicationContext(), DatabaseContract.scores_table.buildScoreWithDate(), null, null, date, DatabaseContract.scores_table.DATE_COL);
        mCursorLoader.registerListener(WIDGET_LOADER, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onDestroy() {

        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FootballScoresWidgetProvider.class));

        // Extract the weather data from the Cursor
        boolean loadertestdata = data.moveToFirst();
        String homeName = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        String homeScore = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
        String awayName = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        String awayScore = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.appwidget_provider_layout);

            //Assign our widget to have the latest football score
            views.setTextViewText(R.id.home_name,homeName);
            if(!homeScore.equalsIgnoreCase("-1"))
                views.setTextViewText(R.id.home_score,homeScore);
            else
                views.setTextViewText(R.id.home_score,getString(R.string.home_widget_score_text_placeholder));

            views.setTextViewText(R.id.away_name,awayName);
            if(!awayScore.equalsIgnoreCase("-1"))
                views.setTextViewText(R.id.away_score,awayScore);
            else
                views.setTextViewText(R.id.away_score,getString(R.string.away_widget_score_text_placeholder));

            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


}

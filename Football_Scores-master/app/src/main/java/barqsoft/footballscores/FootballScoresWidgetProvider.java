package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.service.WidgetIntentService;

/**
 * Created by Douglas on 10/18/2015.
 */
public class FootballScoresWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, WidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, WidgetIntentService.class));
        super.onReceive(context, intent);
    }
}

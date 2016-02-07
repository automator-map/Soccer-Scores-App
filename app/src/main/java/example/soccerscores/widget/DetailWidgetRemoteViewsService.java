package example.soccerscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Date;

import example.soccerscores.DatabaseContract;
import example.soccerscores.MainScreenFragment;
import example.soccerscores.R;
import example.soccerscores.ScoresProvider;
import example.soccerscores.Utilities;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    private String[] mWidgetDate = new String[1];

    private static final String[] MATCH_COLUMNS = {
            DatabaseContract.ScoresTableEntry.TABLE_NAME + "." + DatabaseContract.ScoresTableEntry._ID,
            DatabaseContract.ScoresTableEntry.DATE_COL,
            DatabaseContract.ScoresTableEntry.TIME_COL,
            DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_NAME,
            DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_NAME,
            DatabaseContract.ScoresTableEntry.LEAGUE_COL,
            DatabaseContract.ScoresTableEntry.HOME_GOALS_COL,
            DatabaseContract.ScoresTableEntry.AWAY_GOALS_COL,
            DatabaseContract.ScoresTableEntry.MATCH_ID,
            DatabaseContract.ScoresTableEntry.MATCH_DAY,
            ScoresProvider.COLUMN_JOINED_TABLE_HOME_TEAM_IMAGE_URL,
            ScoresProvider.COLUMN_JOINED_TABLE_AWAY_TEAM_IMAGE_URL

    };

    public static final int COL_SCORE_ENTRY_ID = 0;
    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME_TEAM_NAME = 3;
    public static final int COL_AWAY_TEAM_NAME = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_HOME_TEAM_IMAGE = 10;
    public static final int COL_AWAY_TEAM_IMAGE = 11;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                Date fragmentDate = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = dateFormat.format(fragmentDate);
                mWidgetDate[0] = date;

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                Uri matchInfoWithDateUri = DatabaseContract.ScoresTableEntry.buildScoreWithDate();

                data = getContentResolver().query(matchInfoWithDateUri,
                        MATCH_COLUMNS,
                        null,
                        mWidgetDate,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                String homeTeamName = data.getString(COL_HOME_TEAM_NAME);
                String awayTeamName = data.getString(COL_AWAY_TEAM_NAME);

                String homeCrestUrl = data.getString(MainScreenFragment.COL_HOME_TEAM_IMAGE);
                String awayCrestUrl = data.getString(MainScreenFragment.COL_AWAY_TEAM_IMAGE);

                String matchTime = data.getString(MainScreenFragment.COL_MATCHTIME);
                String matchDate = data.getString(MainScreenFragment.COL_DATE);

                String matchScore = Utilities.getScores(data.getInt(MainScreenFragment.COL_HOME_GOALS), data.getInt(MainScreenFragment.COL_AWAY_GOALS));

                int homeTeamCrest = Utilities.getTeamCrestByTeamName(data.getString(MainScreenFragment.COL_HOME));
                int awayTeamCrest = Utilities.getTeamCrestByTeamName(data.getString(MainScreenFragment.COL_AWAY));

                String matchDescription = Utilities.getMatchDescription(getApplicationContext(), matchScore, homeTeamName, awayTeamName, matchTime, matchDate);


                Bitmap homeTeamImage = null;
                Bitmap awayTeamImage = null;

                try {

                    if (homeTeamCrest != -1) {
                        homeTeamImage = Picasso.with(DetailWidgetRemoteViewsService.this).load(homeTeamCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).get();
                    } else {
                        homeTeamImage = Picasso.with(DetailWidgetRemoteViewsService.this).load(homeCrestUrl).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).get();
                    }

                    if (awayTeamCrest != -1) {
                        awayTeamImage = Picasso.with(DetailWidgetRemoteViewsService.this).load(awayTeamCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).get();
                    } else {
                        awayTeamImage = Picasso.with(DetailWidgetRemoteViewsService.this).load(awayCrestUrl).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).get();
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error retrieving team image from resource", e);
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, matchDescription);
                }

                if (homeTeamImage != null) {
                    views.setImageViewBitmap(R.id.widget_home_crest, homeTeamImage);
                } else {
                    views.setImageViewResource(R.id.widget_home_crest, R.drawable.no_icon);
                }

                if (awayTeamImage != null) {
                    views.setImageViewBitmap(R.id.widget_away_crest, awayTeamImage);
                } else {
                    views.setImageViewResource(R.id.widget_away_crest, R.drawable.no_icon);
                }

                views.setTextViewText(R.id.widget_home_name, homeTeamName);
                views.setTextViewText(R.id.widget_away_name, awayTeamName);
                views.setTextViewText(R.id.widget_date_textview, matchTime);
                views.setTextViewText(R.id.widget_score_textview, matchScore);

                final Intent fillInIntent = new Intent();

                Uri matchUri = DatabaseContract.ScoresTableEntry.buildScoreWithDate();
                fillInIntent.setData(matchUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_list_item, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(COL_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

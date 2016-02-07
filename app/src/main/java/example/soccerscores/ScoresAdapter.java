package example.soccerscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class ScoresAdapter extends CursorAdapter {

    public static final String LOG_TAG = ScoresAdapter.class.getSimpleName();

    public double DETAIL_MATCH_ID = 0;

//    public static final int COL_DATE = 1;
//    public static final int COL_MATCHTIME = 2;
//    public static final int COL_HOME_TEAM_NAME = 3;
//    public static final int COL_AWAY_TEAM_NAME = 4;
//    public static final int COL_LEAGUE = 5;
//    public static final int COL_HOME_GOALS = 6;
//    public static final int COL_AWAY_GOALS = 7;
//    public static final int COL_ID = 8;
//    public static final int COL_MATCHDAY = 9;
//    public static final int COL_HOME_SHORT_NAME = 10;
//    public static final int COL_AWAY_SHORT_NAME = 11;


    private static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();

        String homeTeamName = cursor.getString(MainScreenFragment.COL_HOME);
        String awayTeamName = cursor.getString(MainScreenFragment.COL_AWAY);
        String matchScore = Utilities.getScores(cursor.getInt(MainScreenFragment.COL_HOME_GOALS), cursor.getInt(MainScreenFragment.COL_AWAY_GOALS));
        String matchTime = cursor.getString(MainScreenFragment.COL_MATCHTIME);
        String matchDate = cursor.getString(MainScreenFragment.COL_DATE);

        mHolder.home_name.setText(homeTeamName);
        mHolder.away_name.setText(awayTeamName);
        mHolder.date.setText(matchTime);
        mHolder.score.setText(matchScore);
        mHolder.match_id = cursor.getDouble(MainScreenFragment.COL_ID);


        // Student comment:
        // TODO: Picasso doesn't work with .svg files (Scalable Vector Graphics). This graphics need to be converted
        // to a standard image format so Picasso or Glide can display them.

        // All Team Image URLs are stored in a 2nd database table. Maybe in the future, the .svg
        // files could be downloaded, and converted to a format that Picasso can display. For now they
        // will display the "no icon" image.

        String homeCrestUrl = cursor.getString(MainScreenFragment.COL_HOME_TEAM_IMAGE);
        String awayCrestUrl = cursor.getString(MainScreenFragment.COL_AWAY_TEAM_IMAGE);


        int homeTeamCrest = Utilities.getTeamCrestByTeamName(cursor.getString(MainScreenFragment.COL_HOME));
        int awayTeamCrest = Utilities.getTeamCrestByTeamName(cursor.getString(MainScreenFragment.COL_AWAY));

        if (homeTeamCrest != -1) {
            Picasso.with(context).load(homeTeamCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(mHolder.home_crest);
        } else {
            Picasso.with(context).load(homeCrestUrl).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(mHolder.home_crest);
        }

        if (awayTeamCrest != -1) {
            Picasso.with(context).load(awayTeamCrest).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(mHolder.away_crest);
        } else {
            Picasso.with(context).load(awayCrestUrl).placeholder(R.drawable.no_icon).error(R.drawable.no_icon).into(mHolder.away_crest);
        }

        // Content descriptions are added for the visually impaired.
        String matchDescription = Utilities.getMatchDescription(mContext, matchScore, homeTeamName, awayTeamName, matchTime, matchDate);
        view.setContentDescription(matchDescription);

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        if (mHolder.match_id == DETAIL_MATCH_ID) {

            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);
            matchDay.setText(Utilities.getMatchDay(mContext, cursor.getInt(MainScreenFragment.COL_MATCHDAY),
                    cursor.getInt(MainScreenFragment.COL_LEAGUE)));

            TextView league = (TextView) v.findViewById(R.id.league_textview);

            league.setText(Utilities.getLeague(mContext, cursor.getInt(MainScreenFragment.COL_LEAGUE)));

            Button shareButton = (Button) v.findViewById(R.id.share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });

            shareButton.setContentDescription(mContext.getString(R.string.share_text));
        }
        else {
            container.removeAllViews();
        }

    }
    public Intent createShareForecastIntent(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }


}

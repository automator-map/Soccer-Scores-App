package example.soccerscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


/**
 * A placeholder fragment containing a simple view.
 */

public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public ScoresAdapter mAdapter;

    public static final int SCORES_LOADER = 0;
    public static final int IMAGES_LOADER = 1;

    private String[] mFragmentDate = new String[1];
    private int mLastSelectedItem = -1;


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
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_HOME_TEAM_IMAGE = 10;
    public static final int COL_AWAY_TEAM_IMAGE = 11;

    public MainScreenFragment() {
    }


    public void setFragmentDate(String date) {
        mFragmentDate[0] = date;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // It's not necessary to fetch data from the server with each new page. updateScores() removed from here.
        // Moved to MainActivity.class
        // updateScores();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(),null,0);
        scoreList.setAdapter(mAdapter);

        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        getLoaderManager().initLoader(IMAGES_LOADER, null, this);

        mAdapter.DETAIL_MATCH_ID = MainActivity.sSelectedMatchId;

        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.DETAIL_MATCH_ID = selected.match_id;
                MainActivity.sSelectedMatchId = (int) selected.match_id;

                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {

        return new CursorLoader(getActivity(), DatabaseContract.ScoresTableEntry.buildScoreWithDate(),
                MATCH_COLUMNS, null, mFragmentDate, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {

        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


}

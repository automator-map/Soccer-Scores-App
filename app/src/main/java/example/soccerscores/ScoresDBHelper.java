package example.soccerscores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;

    public ScoresDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_SCORES_TABLE = "CREATE TABLE " + DatabaseContract.ScoresTableEntry.TABLE_NAME + " ("
                + DatabaseContract.ScoresTableEntry._ID + " INTEGER PRIMARY KEY,"
                + DatabaseContract.ScoresTableEntry.DATE_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTableEntry.TIME_COL + " INTEGER NOT NULL,"
                + DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_NAME + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_NAME + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTableEntry.LEAGUE_COL + " INTEGER NOT NULL,"
                + DatabaseContract.ScoresTableEntry.HOME_GOALS_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTableEntry.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTableEntry.MATCH_ID + " INTEGER NOT NULL,"
                + DatabaseContract.ScoresTableEntry.MATCH_DAY + " INTEGER NOT NULL,"
                + DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_ID + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_ID + " TEXT NOT NULL,"
                + " UNIQUE ("+ DatabaseContract.ScoresTableEntry.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";


        final String SQL_CREATE_IMAGES_TABLE = "CREATE TABLE " + DatabaseContract.TeamImagesEntry.TABLE_NAME + " (" +
                DatabaseContract.TeamImagesEntry._ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME + " TEXT UNIQUE NOT NULL, " +
                DatabaseContract.TeamImagesEntry.COLUMN_TEAM_SHORT_NAME + " TEXT UNIQUE NOT NULL, " +
                DatabaseContract.TeamImagesEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_SCORES_TABLE);
        db.execSQL(SQL_CREATE_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ScoresTableEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TeamImagesEntry.TABLE_NAME);
        onCreate(db);
    }
}

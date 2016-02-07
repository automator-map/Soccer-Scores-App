package example.soccerscores;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


public class ScoresProvider extends ContentProvider
{
    private static ScoresDBHelper mOpenHelper;
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder ScoreQuery =
            new SQLiteQueryBuilder();
    private static final String SCORES_BY_LEAGUE = DatabaseContract.ScoresTableEntry.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE =
            DatabaseContract.ScoresTableEntry.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID =
            DatabaseContract.ScoresTableEntry.MATCH_ID + " = ?";

    // Added for SuperDuo Project:
    private static final int TEAM_IMAGES = 200;
    private static final int SINGLE_TEAM_IMAGE = 201;
    private static final String IMAGE_BY_TEAM_NAME_SELECTION = DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME + " = ?";

    public static final String LOG_TAG = ScoresProvider.class.getSimpleName();

    public static final String HOME_TEAM_IMAGE_TABLE_ALIAS = "images_home";
    public static final String AWAY_TEAM_IMAGE_TABLE_ALIAS = "images_away";

    public static final String COLUMN_JOINED_TABLE_HOME_TEAM_IMAGE_URL = HOME_TEAM_IMAGE_TABLE_ALIAS + "." + DatabaseContract.TeamImagesEntry.COLUMN_IMAGE_URL;
    public static final String COLUMN_JOINED_TABLE_AWAY_TEAM_IMAGE_URL = AWAY_TEAM_IMAGE_TABLE_ALIAS + "." + DatabaseContract.TeamImagesEntry.COLUMN_IMAGE_URL;


    private static final SQLiteQueryBuilder sTeamsWithImagesQueryBuilder;

    static{
        sTeamsWithImagesQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sTeamsWithImagesQueryBuilder.setTables(DatabaseContract.ScoresTableEntry.TABLE_NAME +
                        " LEFT OUTER JOIN " +
                        DatabaseContract.TeamImagesEntry.TABLE_NAME + " AS " + HOME_TEAM_IMAGE_TABLE_ALIAS +
                        " ON " + DatabaseContract.ScoresTableEntry.TABLE_NAME + "." + DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_NAME +
                        " = " + HOME_TEAM_IMAGE_TABLE_ALIAS + "." + DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME +
                        " LEFT OUTER JOIN " +
                        DatabaseContract.TeamImagesEntry.TABLE_NAME + " AS " + AWAY_TEAM_IMAGE_TABLE_ALIAS +
                        " ON " + DatabaseContract.ScoresTableEntry.TABLE_NAME + "." + DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_NAME +
                        " = " + AWAY_TEAM_IMAGE_TABLE_ALIAS + "." + DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME
        );

//        sTeamsWithImagesQueryBuilder.setTables(DatabaseContract.ScoresTableEntry.TABLE_NAME +
//                        " LEFT OUTER JOIN " +
//                        DatabaseContract.TeamImagesEntry.TABLE_NAME +
//                        " ON (" + DatabaseContract.ScoresTableEntry.TABLE_NAME + "." + DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_NAME +
//                        " = " + DatabaseContract.TeamImagesEntry.TABLE_NAME + "." + DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME +
//                        " OR " + DatabaseContract.ScoresTableEntry.TABLE_NAME + "." + DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_NAME +
//                        " = " + DatabaseContract.TeamImagesEntry.TABLE_NAME + "." + DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME + ")"
//        );

        //Log.i(LOG_TAG, "sTeamsWithImagesQueryBuilder: " + sTeamsWithImagesQueryBuilder);
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_SCORES, MATCHES);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/date", MATCHES_WITH_DATE);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/league", MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, DatabaseContract.PATH_SCORES + "/id", MATCHES_WITH_ID);

        //Log.w(LOG_TAG, "DatabaseContract.PATH_SCORES + \"/date\": " + DatabaseContract.PATH_SCORES + "/date");

        // To retrieve the teams image from URL.
        matcher.addURI(authority, DatabaseContract.PATH_IMAGES, TEAM_IMAGES);
        matcher.addURI(authority, DatabaseContract.PATH_IMAGES + "/*", SINGLE_TEAM_IMAGE);
        return matcher;
    }

    // Futile, to delete:

//    private int matchUri(Uri uri)
//    {
//        String link = uri.toString();
//        {
//           if(link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString()))
//           {
//               return MATCHES;
//           }
//           else if(link.contentEquals(DatabaseContract.ScoresTableEntry.buildScoreWithDate().toString()))
//           {
//               return MATCHES_WITH_DATE;
//           }
//           else if(link.contentEquals(DatabaseContract.ScoresTableEntry.buildScoreWithId().toString()))
//           {
//               return MATCHES_WITH_ID;
//           }
//           else if(link.contentEquals(DatabaseContract.ScoresTableEntry.buildScoreWithLeague().toString()))
//           {
//               return MATCHES_WITH_LEAGUE;
//           }
//        }
//        return -1;
//    }

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    private Cursor getSingleTeamImage(Uri uri, String[] projection) {

        String teamName = DatabaseContract.TeamImagesEntry.getTeamShortNameFromUri(uri);

        String[] selectionArgs = new String[] {teamName};

        return sTeamsWithImagesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection, IMAGE_BY_TEAM_NAME_SELECTION, selectionArgs, null, null, null);
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.ScoresTableEntry.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.ScoresTableEntry.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.ScoresTableEntry.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.ScoresTableEntry.CONTENT_TYPE;
            case TEAM_IMAGES:
                return DatabaseContract.ScoresTableEntry.CONTENT_TYPE;
            case SINGLE_TEAM_IMAGE:
                return DatabaseContract.ScoresTableEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI :" + uri );
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        //Log.v(FetchScoreTask.LOG_TAG,uri.getPathSegments().toString());
        //int match = matchUri(uri);
        //Log.v(FetchScoreTask.LOG_TAG,SCORES_BY_LEAGUE);
        //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[0]);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(match));

        Log.w(LOG_TAG, "sUriMatcher.match(uri): " + sUriMatcher.match(uri));

        switch (sUriMatcher.match(uri))
        {
            case MATCHES:
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        DatabaseContract.ScoresTableEntry.TABLE_NAME,
//                        projection,null,null,null,null,sortOrder);

                retCursor = sTeamsWithImagesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection, null, null, null, null, sortOrder);

                break;

            case MATCHES_WITH_DATE:
                //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[1]);
                //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[2]);
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        DatabaseContract.ScoresTableEntry.TABLE_NAME,
//                        projection,SCORES_BY_DATE,selectionArgs,null,null,sortOrder);

                retCursor = sTeamsWithImagesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection, SCORES_BY_DATE, selectionArgs, null, null, sortOrder);

                break;

            case MATCHES_WITH_ID:
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        DatabaseContract.ScoresTableEntry.TABLE_NAME,
//                        projection,SCORES_BY_ID,selectionArgs,null,null,sortOrder);

                retCursor = sTeamsWithImagesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection, SCORES_BY_ID, selectionArgs, null, null, sortOrder);

                break;

            case MATCHES_WITH_LEAGUE:
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        DatabaseContract.ScoresTableEntry.TABLE_NAME,
//                        projection,SCORES_BY_LEAGUE,selectionArgs,null,null,sortOrder);

                retCursor = sTeamsWithImagesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection, SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);

                break;

            case TEAM_IMAGES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.TeamImagesEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;

            case SINGLE_TEAM_IMAGE:
                retCursor = getSingleTeamImage(uri, projection);
                break;

            default: throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        Log.w(LOG_TAG, "on bulkInsert");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //db.delete(DatabaseContract.TABLE_NAME,null,null);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(sUriMatcher.match(uri)));

        int returnCount;

        switch (sUriMatcher.match(uri)) {
            case MATCHES:
                db.beginTransaction();
                returnCount = 0;
                try {

                    for(ContentValues value : values)  {

                        long _id = db.insertWithOnConflict(DatabaseContract.ScoresTableEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;

            case TEAM_IMAGES:
                db.beginTransaction();
                returnCount = 0;

                try {
                    for (ContentValues value : values) {

                        // Log.w(LOG_TAG, "bulkInsert value: " + value.toString());

                        long _id = db.insertWithOnConflict(DatabaseContract.TeamImagesEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}

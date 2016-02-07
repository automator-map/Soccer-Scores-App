package example.soccerscores;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class DatabaseContract {


    public static final String PATH_SCORES = "scores";
    public static final String PATH_IMAGES = "images";

    //URI data
    public static final String CONTENT_AUTHORITY = "example.soccerscores";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class ScoresTableEntry implements BaseColumns {

        public static final String TABLE_NAME = "scores";

        //Table data
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String COLUMN_HOME_TEAM_NAME = "home_team_name";
        public static final String COLUMN_AWAY_TEAM_NAME = "away_team_name";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";

        public static final String COLUMN_HOME_TEAM_ID = "home_team_id";
        public static final String COLUMN_AWAY_TEAM_ID = "away_team_id";


        //Types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORES;


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCORES).build();

        public static Uri buildScoreWithLeague() {
            return CONTENT_URI.buildUpon().appendPath("league").build();
        }

        public static Uri buildScoreWithId() {
            return CONTENT_URI.buildUpon().appendPath("id").build();
        }

        public static Uri buildScoreWithDate() {
            return CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }


    public static final class TeamImagesEntry implements BaseColumns {

        public static final String TABLE_NAME = "images";
        public static final String COLUMN_TEAM_NAME = "team_name";
        public static final String COLUMN_TEAM_SHORT_NAME = "team_short_name";
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        public static Uri buildTeamImageUri(String shortName) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_TEAM_SHORT_NAME, shortName).build();
        }

        public static String getTeamShortNameFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_TEAM_SHORT_NAME);
        }

    }

}

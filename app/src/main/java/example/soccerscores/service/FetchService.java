package example.soccerscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import example.soccerscores.DatabaseContract;
import example.soccerscores.R;


public class FetchService extends IntentService {


    public static final String LOG_TAG = FetchService.class.getSimpleName();

    public static final String ACTION_DATA_UPDATED = "ACTION_DATA_UPDATED";

    public static final String FUTURE_TIME_FRAME = "n4";
    public static final String PAST_TIME_FRAME = "p3";

    public static final String LEAGUE_CODE = "league_code";

    //JSON data
    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    final static String BUNDESLIGA1 = "394";
    final static String BUNDESLIGA2 = "395";
    final static String LIGUE1 = "396";
    final static String LIGUE2 = "397";
    final static String PREMIER_LEAGUE = "398";
    final static String PRIMERA_DIVISION = "399";
    final static String SEGUNDA_DIVISION = "400";
    final static String SERIE_A = "401";
    final static String PRIMERA_LIGA = "402";
    final static String BUNDESLIGA3 = "403";
    final static String EREDIVISIE = "404";

    // Added the Champions League:
    final static String CHAMPIONS_LEAGUE = "405";


    // The leagues that will be fetched are stored in this list
    private static final List<String> sLeaguesList = Arrays.asList(
            PREMIER_LEAGUE,
            SERIE_A,
            BUNDESLIGA1,
            BUNDESLIGA2,
            PRIMERA_DIVISION,
            CHAMPIONS_LEAGUE
    );


    public FetchService()
    {
        super("FetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        getData(FUTURE_TIME_FRAME);
        getData(PAST_TIME_FRAME);
        getImageData();
    }

    private void getData (String timeFrame)
    {

        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            connection = (HttpURLConnection) fetch.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));

            Log.d(LOG_TAG, fetch_build.toString());

            connection.connect();

            // Read the input stream into a String
            InputStream inputStream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Exception here" + e.getMessage());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
//                    processJsonData(getString(R.string.dummy_data), getApplicationContext(), false);

                    Log.d(LOG_TAG, "Off season, no match data to display");
                    return;
                }

                processJsonData(JSON_data, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }


    private void processJsonData(String JSONdata, Context mContext, boolean isReal) {

        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        final String HOME_TEAM_LINK = "homeTeam";
        final String AWAY_TEAM_LINK = "awayTeam";
        final String TEAM_PATH_LINK = "http://api.football-data.org/alpha/teams/";

        //Match data
        String league;
        String date;
        String time;
        String team_home;
        String team_away;
        String team_home_goals;
        String team_away_goals;
        String matchId;
        String match_day;

        // Added to get the team images:
        String homeTeamId;
        String awayTeamId;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());


            for(int i = 0;i < matches.length();i++) {

                JSONObject matchData = matches.getJSONObject(i);

                JSONObject matchLinks = matchData.getJSONObject(LINKS);

                league = matchLinks.getJSONObject(SOCCER_SEASON).
                        getString("href");
                league = league.replace(SEASON_LINK,"");

                // This if statement controls which leagues we're interested in the data from.
                // add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if (sLeaguesList.contains(league)) {

                    matchId = matchLinks.getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");

                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        matchId = matchId + Integer.toString(i);
                    }

                    homeTeamId = matchLinks.getJSONObject(HOME_TEAM_LINK).getString("href").replace(TEAM_PATH_LINK, "");
                    awayTeamId = matchLinks.getJSONObject(AWAY_TEAM_LINK).getString("href").replace(TEAM_PATH_LINK, "");

                    date = matchData.getString(MATCH_DATE);

                    time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));

                    date = date.substring(0,date.indexOf("T"));

                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");

                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));

                    try {
                        Date parsedDate = match_date.parse(date+time);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        date = new_date.format(parsedDate);
                        time = date.substring(date.indexOf(":") + 1);
                        date = date.substring(0,date.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentDate = new Date(System.currentTimeMillis() + ((i-2) * 86400000));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            date = dateFormat.format(fragmentDate);
                        }
                    }
                    catch (Exception e) {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG,e.getMessage());
                    }

                    team_home = matchData.getString(HOME_TEAM);
                    team_away = matchData.getString(AWAY_TEAM);
                    team_home_goals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    team_away_goals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = matchData.getString(MATCH_DAY);
                    ContentValues matchValues = new ContentValues();
                    matchValues.put(DatabaseContract.ScoresTableEntry.MATCH_ID, matchId);
                    matchValues.put(DatabaseContract.ScoresTableEntry.DATE_COL, date);
                    matchValues.put(DatabaseContract.ScoresTableEntry.TIME_COL, time);
                    matchValues.put(DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_NAME, team_home);
                    matchValues.put(DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_NAME, team_away);
                    matchValues.put(DatabaseContract.ScoresTableEntry.HOME_GOALS_COL, team_home_goals);
                    matchValues.put(DatabaseContract.ScoresTableEntry.AWAY_GOALS_COL, team_away_goals);
                    matchValues.put(DatabaseContract.ScoresTableEntry.LEAGUE_COL, league);
                    matchValues.put(DatabaseContract.ScoresTableEntry.MATCH_DAY, match_day);

                    // Store the team ids:
                    matchValues.put(DatabaseContract.ScoresTableEntry.COLUMN_HOME_TEAM_ID, homeTeamId);
                    matchValues.put(DatabaseContract.ScoresTableEntry.COLUMN_AWAY_TEAM_ID, awayTeamId);

                    values.add(matchValues);
                }
            }

            int inserted_data = 0;
            ContentValues[] cVValues = new ContentValues[values.size()];
            values.toArray(cVValues);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.ScoresTableEntry.CONTENT_URI, cVValues);

            updateWidgets();

        }
        catch (JSONException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    public void getImageData() {
        for (String leagueStr : sLeaguesList) {
            Log.w(LOG_TAG, "leagueStr: " + leagueStr);
            fetchTeamImage(leagueStr);
        }
    }

    public void fetchTeamImage(String league) {
        Log.w(LOG_TAG, "on fetchTeamImage");

        Intent intent = new Intent(this, TeamImageService.class);
        intent.putExtra(LEAGUE_CODE, league);
        startService(intent);

    }

    private void updateWidgets() {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(this.getPackageName());
        this.sendBroadcast(dataUpdatedIntent);
    }

}


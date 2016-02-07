package example.soccerscores.service;

import android.app.IntentService;
import android.content.ContentValues;
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
import java.util.Vector;

import example.soccerscores.DatabaseContract;
import example.soccerscores.R;


public class TeamImageService extends IntentService {

    private static final String LOG_TAG = TeamImageService.class.getSimpleName();

    private String mLeagueCode;

    public TeamImageService()
    {
        super("TeamImageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mLeagueCode = intent.getExtras().getString(FetchService.LEAGUE_CODE);

        final String BASE_URL = "http://api.football-data.org/alpha/soccerseasons/";

        final String TEAM_PATH = "teams";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String teamJsonStr;

        try {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendPath(mLeagueCode).appendPath(TEAM_PATH).build();

            // Log.w(LOG_TAG, "TeamImageService builtUri: " + builtUri);

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
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
            teamJsonStr = buffer.toString();

            getImageUrlFromJson(teamJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the team data, there's no point in attempting
            // to parse it.

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }


    private void getImageUrlFromJson(String teamJsonStr) throws JSONException {

        final String TEAMS_ARRAY = "teams";
        final String TEAM_NAME = "name";
        final String TEAM_SHORT_NAME = "shortName";
        final String IMAGE_URL = "crestUrl";

        try {

            JSONObject teamJson = new JSONObject(teamJsonStr);

            JSONArray teamsJsonArray = teamJson.getJSONArray(TEAMS_ARRAY);


            Vector<ContentValues> cVVector = new Vector<ContentValues>(teamsJsonArray.length());

            for(int i = 0; i < teamsJsonArray.length(); i++) {

                JSONObject teamInfo = teamsJsonArray.getJSONObject(i);

                String imageUrl = teamInfo.getString(IMAGE_URL);
                String teamName = teamInfo.getString(TEAM_NAME);
                String teamShortName = teamInfo.getString(TEAM_SHORT_NAME);

                ContentValues imageValues = new ContentValues();

                imageValues.put(DatabaseContract.TeamImagesEntry.COLUMN_TEAM_NAME, teamName);
                imageValues.put(DatabaseContract.TeamImagesEntry.COLUMN_TEAM_SHORT_NAME, teamShortName);
                imageValues.put(DatabaseContract.TeamImagesEntry.COLUMN_IMAGE_URL, imageUrl);

                // Log.w(LOG_TAG, "imageUrl retrieved: " + imageUrl);

                cVVector.add(imageValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {

                ContentValues[] cVArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cVArray);

                // Log.w(LOG_TAG, "Downloaded Data:\ncVArray: \n" + Arrays.toString(cVArray));

                inserted = getContentResolver().bulkInsert(DatabaseContract.TeamImagesEntry.CONTENT_URI, cVArray);
            }

            Log.d(LOG_TAG, "Image Sync Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

//    private void downloadImageFromUrl(URL url) {
//
//        try {
//
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            InputStream inputStream = urlConnection.getInputStream();
//            SVG svg = SVGParser. getSVGFromInputStream(inputStream);
//            Drawable drawable = svg.createPictureDrawable();
//            return drawable;
//        } catch (Exception e) {
//            Log.e("MainActivity", e.getMessage(), e);
//        }
//
//        return null;
//
//    }

}

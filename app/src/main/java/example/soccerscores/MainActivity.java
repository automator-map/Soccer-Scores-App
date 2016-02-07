package example.soccerscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import example.soccerscores.service.FetchService;

public class MainActivity extends ActionBarActivity
{
    public static int sSelectedMatchId;
    public static int sCurrentFragment = 3;
    public static String LOG_TAG = MainActivity.class.getSimpleName();
    private final String SAVE_TAG = "Save Test";
    private PagerFragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");

        // Scores are updated:
        updateScores();
        Log.d(LOG_TAG, "Scores updated from server");

        if (savedInstanceState == null) {
            mFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("Pager_Current", mFragment.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match", sSelectedMatchId);
        getSupportFragmentManager().putFragment(outState,"mFragment", mFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        sCurrentFragment = savedInstanceState.getInt("Pager_Current");
        sSelectedMatchId = savedInstanceState.getInt("Selected_match");
        mFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"mFragment");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void updateScores()
    {
        Intent service_start = new Intent(this, FetchService.class);
        startService(service_start);
    }
}

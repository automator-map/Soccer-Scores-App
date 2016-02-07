package example.soccerscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewHolder
{
    public TextView home_name;
    public TextView away_name;
    public TextView score;
    public TextView date;
    public ImageView home_crest;
    public ImageView away_crest;
    public double match_id;
    public ViewHolder(View view)
    {
        home_name = (TextView) view.findViewById(R.id.home_name);
        away_name = (TextView) view.findViewById(R.id.away_name);
        score     = (TextView) view.findViewById(R.id.score_textview);
        date      = (TextView) view.findViewById(R.id.date_textview);
        home_crest = (ImageView) view.findViewById(R.id.home_crest);
        away_crest = (ImageView) view.findViewById(R.id.away_crest);

        // To disable unnecessary content descriptions.
        home_name.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        away_name.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        score.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        date.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }


}

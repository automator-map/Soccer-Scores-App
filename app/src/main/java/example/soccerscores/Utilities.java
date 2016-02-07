package example.soccerscores;

import android.content.Context;
import android.content.res.Resources;


public class Utilities
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEAGUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;


    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return context.getString(R.string.league_name_serie_a);
            case PREMIER_LEAGUE: return context.getString(R.string.league_name_premier_league);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.league_name_champions_league);
            case PRIMERA_DIVISION : return context.getString(R.string.league_name_primera_division);
            case BUNDESLIGA : return context.getString(R.string.league_name_bundesliga);
            default: return context.getString(R.string.league_name_not_known);
        }
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.match_day_group_stages);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.match_day_first_knockout);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.match_day_quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.match_day_semifinal);
            }
            else
            {
                return context.getString(R.string.match_day_final);
            }
        }
        else
        {
            return context.getString(R.string.match_day_default) + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
//        if (teamname==null){
//            return R.drawable.no_icon;
//        }

        switch (teamname) {
            // This is the set of icons that are currently in the app. Feel free to find and add more
            // as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            //default: break;

        }
        return -1;
    }

    public static String getMatchDescription(Context context, String matchScore, String homeTeamName, String awayTeamName, String matchTime, String matchDate) {

        String matchDescription;

        Resources res = context.getResources();

        if (!matchScore.equals(" - ")) {
            matchDescription = String.format(res.getString(R.string.past_match_description), homeTeamName, matchScore, awayTeamName, matchTime, matchDate);

        } else {
            matchDescription = String.format(res.getString(R.string.upcoming_match_description), homeTeamName, awayTeamName, matchTime, matchDate);

        }

        return matchDescription;

    }

}

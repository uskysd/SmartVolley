package uskysd.smartvolley;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.Point;
import uskysd.smartvolley.data.Set;
import uskysd.smartvolley.scores.PlayScore;
import uskysd.smartvolley.scores.ServicePointRate;

public class DataAnalyzer extends OrmLiteObject {

    private static final String TAG = DataAnalyzer.class.getSimpleName();

    public enum TargetDataKey {
        MATCH_ID,
        SET_ID,
        YEARS,
        MONTHS,
        WEEKS,
    }

    private Context context;
    private List<PlayScore> playScores;

    // For database access
    private DatabaseHelper helper;
    private Dao<Match, Integer> matchDao;
    private Dao<Set, Integer> setDao;
    private Dao<Point, Integer> pointDao;
    private Dao<Play, Integer> playDao;
    private Dao<Player, Integer> playerDao;



    public DataAnalyzer(Context context) {
        this.context = context;
        this.playScores = new ArrayList<PlayScore>();
        this.playScores.add(new ServicePointRate());

        try {
            this.helper = getDatabaseHelper(context);
            this.matchDao = helper.getMatchDao();
            this.setDao = helper.getSetDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Context getContext() {
        return context;
    }

    public List<PlayScore> getPlayScores() {
        return playScores;
    }

    public List<Play> getTargetPlays(TargetDataKey key, Integer value) throws SQLException {
        switch (key) {
            case MATCH_ID:
                Match match = matchDao.queryForId(value);
                return match.getAllPlays();
            case SET_ID:
                Set set = setDao.queryForId(value);
                return set.getAllPlays();
            case YEARS:
                break;
            case MONTHS:
                break;
            case WEEKS:
                break;
                default:
                    break;
        }
        return null;

    }

    public HashMap<String, String> getPlayerScore(PlayScore playScore, Player player,
                                                  TargetDataKey key, Integer value)
            throws SQLException {
        List<Play> plays = new ArrayList<Play>();
        List<Play> allPlays = getTargetPlays(key, value);
        Log.d(TAG, "All play count: "+Integer.toString(allPlays.size()));
        for (Play play: allPlays) {
            if (play.getPlayer().getId()==player.getId()) {
                plays.add(play);
            }
        }
        Log.d(TAG, "Target player's play count: "+Integer.toString(plays.size()));
        return playScore.calculate(plays);
    }





}

package uskysd.smartvolley;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uskysd.smartvolley.data.Event;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.Point;
import uskysd.smartvolley.data.Set;

/**
 * Created by yusukeyohishida on 3/4/18.
 */

public class MatchDataManager extends OrmLiteObject {

    private static final String TAG = MatchDataManager.class.getSimpleName();

    // Team flags
    private static final boolean TEAM_A = true;
    private static final boolean TEAM_B = false;

    private Context context;
    private Match match;
    private Set set;
    private Point point;
    private Player player;
    private Play.PlayType playType;
    private Play.PlayEvaluation playEvaluation;
    private Integer pointCount;

    public static final Integer POINT_COUNT = 25;
    public static final Integer FINAL_SET_POINT_COUNT = 15;
    public static final Integer SET_COUNT = 3;

    public MatchDataManager(Context context, Match match) {
        this.context = context;
        this.match = match;

        try {
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setMatch(Match match) {
        this.match = match;
        try {
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() throws SQLException {
        Log.d(TAG, "initializing");
        Dao<Set, Integer> setDao = getDatabaseHelper(this.context).getSetDao();
        Dao<Point, Integer> pointDao = getDatabaseHelper(this.context).getPointDao();

        set = this.match.getOnGoingSet();
        if (set==null) {
            set = new Set(this.match);
            setDao.create(set);
        }
        point = set.getOnGoingPoint();
        if (point==null) {
            point = new Point(set);
            pointDao.create(this.point);
        }

        if (set.getSetNumber()==SET_COUNT) {
            this.pointCount = FINAL_SET_POINT_COUNT;
        } else {
            this.pointCount = POINT_COUNT;
        }



    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayType(Play.PlayType playType) {
        this.playType = playType;
    }

    public void setPlayEvaluation(Play.PlayEvaluation playEvaluation) {
        this.playEvaluation = playEvaluation;
    }

    public void createPlay() throws SQLException {


        if ((this.player==null)||(this.playType==null)) {
            throw new IllegalArgumentException("Player and PlayType must be set to create Play");
        }

        //Dao<Match, Integer> matchDao = getDatabaseHelper(this.context).getMatchDao();
        Dao<Play, Integer> playDao = getDatabaseHelper(this.context).getPlayDao();
        //Dao<Player, Integer> playerDao = getDatabaseHelper(this.context).getPlayerDao();

        Play play = new Play(point, player, playType);
        if (playEvaluation!=null) {
            play.setEvaluation(playEvaluation);
        }
        playDao.create(play);
        Log.d(TAG, "Created new play: "+play.toString());

    }

    public void setPointWinner(boolean teamflag) throws SQLException {
        Integer winnerPoints = null;
        Integer otherPoints = null;
        if (teamflag==TEAM_A) {
            this.point.setTeamAWon();
            winnerPoints = this.set.getPointsWonByTeamA().size();
            otherPoints = this.set.getPointsWonByTeamB().size();
        } else {
            this.point.setTeamBWon();
            winnerPoints = this.set.getPointsWonByTeamB().size();
            otherPoints = this.set.getPointsWonByTeamA().size();
        }

        Dao<Point, Integer> playDao = getDatabaseHelper(this.context).getPointDao();
        Integer countA = this.set.getPointsWonByTeamA().size();
        Integer countB = this.set.getPointsWonByTeamB().size();
        if (winnerPoints>=this.pointCount) {
            if (winnerPoints-otherPoints>=2) {
                // Current Set won by the point winner
                this.setSetWinner(teamflag);
            } else {
                // Advantage point winner team
                this.point = new Point(this.set);
                playDao.create(this.point);

            }
        } else {
            this.point = new Point(this.set);
            playDao.create(this.point);
        }
    }

    public void setSetWinner(boolean teamflag) throws SQLException {
        if (teamflag == TEAM_A) {
            this.set.setTeamAWon();
        } else {
            this.set.setTeamBWon();
        }


        if (this.match.getSetsWonByTeamA().size() >= SET_COUNT) {
            this.match.setTeamAWon();
            this.match = null;
            // TODO Should notify the Match is over
        } else {
            Dao<Set, Integer> setDao = getDatabaseHelper(this.context).getSetDao();
            Dao<Point, Integer> pointDao = getDatabaseHelper(this.context).getPointDao();

            // Create new Set
            this.set = new Set(this.match);
            setDao.create(this.set);

            if (set.getSetNumber() == SET_COUNT) {
                this.pointCount = FINAL_SET_POINT_COUNT;
            } else {
                this.pointCount = POINT_COUNT;
            }

            // Create new Point
            this.point = new Point(this.set);
            pointDao.create(this.point);

        }

    }

    public Integer getPointCount(boolean teamflag) {
        if (teamflag==TEAM_A) {
            return this.set.getPointsWonByTeamA().size();
        } else {
            return this.set.getPointsWonByTeamB().size();
        }
    }

    public Integer getSetCount(boolean teamflag) {
        if (teamflag==TEAM_A) {
            return this.match.getSetsWonByTeamA().size();
        } else {
            return this.match.getSetsWonByTeamB().size();
        }
    }

    public List<Event> getEvents() throws SQLException {
        Dao<Play, Integer> playDao = getDatabaseHelper(this.context).getPlayDao();
        Dao<Player, Integer> playerDao = getDatabaseHelper(this.context).getPlayerDao();
        List<Event> events = new ArrayList<Event>();
        for (Play p: playDao.queryForAll()) {
            // Restore player info since foreign objects have only id.

            //p.setPlayer(playerDao.queryForId(p.getPlayer().getId()));
            events.add(p);
        }
        //TODO add other events such as player change and timeout.
        Collections.sort(events);
        return events;
    }

}

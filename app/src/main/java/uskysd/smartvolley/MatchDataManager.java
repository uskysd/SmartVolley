package uskysd.smartvolley;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

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

    public void initialize() throws SQLException {
        Dao<Set, Integer> setDao = getDatabaseHelper(this.context).getSetDao();
        Dao<Point, Integer> pointDao = getDatabaseHelper(this.context).getPointDao();

        Set set = this.match.getOnGoingSet();
        if (set==null) {
            set = new Set(this.match);
            setDao.create(set);
        }
        Point point = set.getOnGoingPoint();
        if (point==null) {
            point = new Point(set);
            pointDao.create(point);
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

        Play play = new Play(point, player, playType);
        if (playEvaluation!=null) {
            play.setEvaluation(playEvaluation);
        }
        playDao.create(play);

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
        if (teamflag==TEAM_A) {
            this.set.setTeamAWon();
        } else {
            this.set.setTeamBWon();
        }


        if (this.match.getSetsWonByTeamA().size()>=SET_COUNT) {
            this.match.setTeamAWon();
            this.match = null;
            // TODO Should notify the Match is over
        } else {
            Dao<Set, Integer> setDao = getDatabaseHelper(this.context).getSetDao();
            Dao<Point, Integer> pointDao = getDatabaseHelper(this.context).getPointDao();

            // Create new Set
            this.set = new Set(this.match);
            setDao.create(this.set);

            // Create new Point
            this.point = new Point(this.set);
            pointDao.create(this.point);


        }
    }


}

package uskysd.smartvolley;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final boolean TEAM_A = true;
    public static final boolean TEAM_B = false;

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

    public enum PointScenario {

        SERVICE_POINT("Service Point"),
        SERVICE_FAILURE("Service Failure"),
        RECEPTION_FAILURE("Reception Failure"),
        ATTACK_POINT("Attack Point"),
        ATTACK_FAILURE("Attack Failure"),
        BLOCK_POINT("Block Point"),
        RECEIVE_FAILURE("Receive Failure"),
        PASS_POINT("Pass Point"),
        PASS_FAILURE("Pass Failure"),
        VIOLATION("Violation");

        private final String str;
        private PointScenario(String str) {
            this.str = str;
        }
        public String toString() {return this.str;}
    }


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
        Log.d(TAG, "Play count in the current point: "+Integer.toString(point.getPlays().size()));

        clearEmptyPlays();

    }

    public void setPointWinner(boolean teamflag) throws SQLException {

        Dao<Point, Integer> pointDao = getDatabaseHelper(this.context).getPointDao();
        Integer winnerPoints = null;
        Integer otherPoints = null;
        if (teamflag==TEAM_A) {
            Log.d(TAG, "TEAM A won a point");
            this.point.setTeamAWon();
            pointDao.update(this.point);
            winnerPoints = this.set.getPointsWonByTeamA().size();
            otherPoints = this.set.getPointsWonByTeamB().size();
        } else {
            Log.d(TAG, "TEAM B won a point");
            this.point.setTeamBWon();
            pointDao.update(this.point);
            winnerPoints = this.set.getPointsWonByTeamB().size();
            otherPoints = this.set.getPointsWonByTeamA().size();
        }

        Integer countA = this.set.getPointsWonByTeamA().size();
        Integer countB = this.set.getPointsWonByTeamB().size();
        if (winnerPoints>=this.pointCount) {
            if (winnerPoints-otherPoints>=2) {
                // Current Set won by the point winner
                this.setSetWinner(teamflag);

            } else {
                // Advantage point winner team
                Log.d(TAG, "Set point");
                this.point = new Point(this.set);
                pointDao.create(this.point);

            }
        } else {
            this.point = new Point(this.set);
            pointDao.create(this.point);
        }
    }

    public void setSetWinner(boolean teamflag) throws SQLException {

        Dao<Set, Integer> setDao = getDatabaseHelper(this.context).getSetDao();
        if (teamflag == TEAM_A) {
            Log.d(TAG, "TEAM A won a set");
            this.set.setTeamAWon();
        } else {
            Log.d(TAG, "TEAM B won a set");
            this.set.setTeamBWon();
        }
        setDao.update(this.set);


        if (this.match.getSetsWonByTeamA().size() >= SET_COUNT) {
            this.match.setTeamAWon();
            this.match = null;
            // TODO Should notify the Match is over
        } else {
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

        Dao<Player, Integer> playerDao = getDatabaseHelper(this.context).getPlayerDao();
        List<Event> events = new ArrayList<Event>();
        for (Play p: match.getPlays()) {
            // Restore player info since foreign objects have only id.
            if (p.getPlayer()!=null) {
                p.setPlayer(playerDao.queryForId(p.getPlayer().getId()));
                events.add(p);
            } else {
                Log.d(TAG, "No player assigned to the play: "+ p.toString());
            }

        }
        //TODO add other events such as player change and timeout.
        Collections.sort(events);
        return events;
    }
    public void clearEmptyPlays() throws SQLException {
        Dao<Play, Integer> playDao = getDatabaseHelper(this.context).getPlayDao();
        for (Play p: this.match.getPlays()) {
            if ((p.getPlayer()==null)||(p.getPlayType()==null)) {
                playDao.delete(p);

            }
        }
    }

    public Play getLastPlay() {
        return this.point.getLastPlay();
    }

    public List<PointScenario> getPointScenarioCandidates(boolean winner_teamflag) {
        Play lastPlay = point.getLastPlay();
        if (lastPlay==null) {
            return Arrays.asList(PointScenario.VIOLATION);
        }
        if (playOf(lastPlay)==winner_teamflag) {
            // Last play is by the point winner
            switch (lastPlay.getPlayType()) {
                case ATTACK:
                    return Arrays.asList(PointScenario.ATTACK_POINT, PointScenario.VIOLATION);
                case BLOCK:
                    return Arrays.asList(PointScenario.BLOCK_POINT, PointScenario.VIOLATION);
                case SERVICE:
                    return Arrays.asList(PointScenario.SERVICE_POINT, PointScenario.VIOLATION);
                case PASS:
                    return Arrays.asList(PointScenario.PASS_POINT, PointScenario.VIOLATION);
                default:
                    return Arrays.asList(PointScenario.VIOLATION);

            }
        } else {
            // Last play is by the point loser
            switch (lastPlay.getPlayType()) {
                case SERVICE:
                    return Arrays.asList(PointScenario.SERVICE_FAILURE, PointScenario.VIOLATION);
                case ATTACK:
                    return Arrays.asList(PointScenario.ATTACK_FAILURE, PointScenario.VIOLATION);
                case BLOCK:
                    return Arrays.asList(PointScenario.ATTACK_POINT, PointScenario.VIOLATION);
                case RECEPTION:
                    return Arrays.asList(PointScenario.RECEPTION_FAILURE, PointScenario.VIOLATION);
                case RECEIVE:
                    return Arrays.asList(PointScenario.RECEIVE_FAILURE,
                            PointScenario.ATTACK_POINT, PointScenario.VIOLATION);
                case PASS:
                    return Arrays.asList(PointScenario.PASS_FAILURE, PointScenario.VIOLATION);
                default:
                    return Arrays.asList(PointScenario.VIOLATION);
            }

        }
    }

    public void setPlayResultsFromScenario(boolean teamflag, PointScenario scenario) {
        // Set play results based on the point scenario
        Play lastPlay = point.getLastPlay();
        //Log.d(TAG, "Last play: "+lastPlay.toString());
        if (lastPlay==null) {
            // Do nothing
        } else if (playOf(lastPlay)==teamflag) {
            // Last play is by the point winner
            switch (scenario) {
                case SERVICE_POINT:
                    if (lastPlay.getPlayType()== Play.PlayType.SERVICE) {
                        lastPlay.setPlayResult(Play.PlayResult.POINT);
                    } else {
                        throw new RuntimeException("Unexpected Play Type");
                    }
                    break;
                case ATTACK_POINT:
                    if (lastPlay.getPlayType()== Play.PlayType.ATTACK) {
                        lastPlay.setPlayResult(Play.PlayResult.POINT);
                    } else {
                        throw new RuntimeException("Unexpected Play Type");
                    }
                    break;
                case BLOCK_POINT:
                    if (lastPlay.getPlayType()== Play.PlayType.BLOCK) {
                        lastPlay.setPlayResult(Play.PlayResult.POINT);
                    } else {
                        throw new RuntimeException("Unexpected Play Type");
                    }
                    break;
                case PASS_POINT:
                    if (lastPlay.getPlayType()== Play.PlayType.PASS) {
                        lastPlay.setPlayResult(Play.PlayResult.POINT);
                    } else {
                        throw new RuntimeException("Unexpected Play Type");
                    }
                    break;
                case VIOLATION:
                    break;
                default:
                    //

            }
        } else {
            // Last play is by the point loser
            List<Play> plays = point.getPlays();
            Play previousPlay = plays.get(plays.size()-2);
            switch (scenario) {
                case SERVICE_FAILURE:
                    if (lastPlay.getPlayType()== Play.PlayType.SERVICE) {
                        lastPlay.setPlayResult(Play.PlayResult.FAILURE);
                    } else {
                        throw new RuntimeException("Unexpected play type");
                    }
                    break;
                case ATTACK_FAILURE:
                    if (lastPlay.getPlayType()== Play.PlayType.ATTACK) {
                        lastPlay.setPlayResult(Play.PlayResult.FAILURE);
                    } else {
                        throw new RuntimeException("Unexpected play type");
                    }
                    break;
                case ATTACK_POINT:
                    // Receive challenged
                    if ((lastPlay.getPlayType()== Play.PlayType.RECEIVE) &&
                            (previousPlay.getPlayType()== Play.PlayType.ATTACK)) {
                        lastPlay.setPlayResult(Play.PlayResult.CHALLENGED);
                        previousPlay.setPlayResult(Play.PlayResult.POINT);
                    } else {
                        throw new RuntimeException("Unexpected play type");
                    }
                    break;
                case RECEPTION_FAILURE:
                    if (lastPlay.getPlayType()== Play.PlayType.RECEPTION) {
                        lastPlay.setPlayResult(Play.PlayResult.FAILURE);
                    } else {
                        throw new RuntimeException("Unexpected play type");
                    }
                    break;
                case RECEIVE_FAILURE:
                    if (lastPlay.getPlayType()== Play.PlayType.RECEIVE) {
                        lastPlay.setPlayResult(Play.PlayResult.FAILURE);
                    } else {
                        throw new RuntimeException("Unexpected play type");
                    }
                    break;
                case PASS_FAILURE:
                    if (lastPlay.getPlayType()== Play.PlayType.PASS) {
                        lastPlay.setPlayResult(Play.PlayResult.FAILURE);
                    } else {
                        throw new RuntimeException("Unexpected play type");
                    }
                    break;
                case VIOLATION:
                    break;
                default:
                    // Do nothing

            }

        }

    }

    private Boolean playOf(Play play) {
        if (match.getPlayersFromTeamA().contains(play.getPlayer())) {
            return TEAM_A;
        } else if (match.getPlayersFromTeamB().contains(play.getPlayer())) {
            return TEAM_B;
        } else {
            return null;
        }
    }

    public int[] getRotaions() throws SQLException {
        // Returns current rotation numbers for both teams
        // The first item is for Team A and the sencond is for Team B
        Log.d(TAG, "Calculating rotations");
        int[] rotations = {1, 1};
        List<Point> points = this.set.getPoints();

        Dao<Point, Integer> pointDao = getDatabaseHelper(this.context).getPointDao();
        Play firstPlay;
        Boolean service;

        int count=0;
        for (Point p: points) {
            count+=1;
            Log.d(TAG, "Analyzing point #"+Integer.toString(count));
            // Restore point data
            p = pointDao.queryForId(p.getId());
            if (p.isOnGoing()) {
                Log.d(TAG, "The point is ongoing. Current rotations: "
                        +Integer.toString(rotations[0])+"-"+Integer.toString(rotations[1]));
                return rotations;
            }
            firstPlay = point.getPlays().get(0);
            if (firstPlay.getPlayType()!= Play.PlayType.SERVICE) {
                // Illegal state
            }
            service = playOf(firstPlay);
            String serviceTeam = service?"Team A":"Team B";
            Log.d(TAG, "Service by" + serviceTeam);
            if (service==null) {
                throw new RuntimeException("Player is not set to the play: "+ player.toString());
            }
            if (p.wonByTeamA()==service) {
                // Point won by service team-> No rotation
                Log.d(TAG, "Point won by service team. No rotation");
            } else if (service==TEAM_A) {
                // Rotate team B
                //rotations[1] = (rotations[1]==6)?1:rotations[1]+1;
                if (rotations[1]==6) {
                    rotations[1] = 1;
                } else {
                    rotations[1] += 1;
                }
                Log.d(TAG, "Team B rotation: "+Integer.toString(rotations[1]));

            } else {
                // Rotate team A
                //rotations[0] = (rotations[0]==6)?1:rotations[0]+1;
                if (rotations[0]==6) {
                    rotations[0] = 1;
                } else {
                    rotations[0] += 1;
                }
                Log.d(TAG, "Team A rotation: "+Integer.toString(rotations[0]));
            }



        }
        return rotations;

    }


}

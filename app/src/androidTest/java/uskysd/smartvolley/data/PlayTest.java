package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by 112886 on 12/25/2015.
 */
public class PlayTest extends OrmLiteAndroidTestCase {

    Dao<Match, Integer> matchDao;
    Dao<Team, Integer> teamDao;
    Dao<Player, Integer> playerDao;
    Dao<Set, Integer> setDao;
    Dao<Point, Integer> pointDao;
    Dao<Rally, Integer> rallyDao;
    Dao<Play, Integer> playDao;
    Match match;
    Team teamA;
    Team teamB;
    Player player1;
    Player player2;
    Set set;
    Point point;


    public void setUp() throws Exception {
        DatabaseHelper helper = getDatabaseHelper(getContext());
        matchDao = helper.getMatchDao();
        teamDao = helper.getTeamDao();
        playerDao = helper.getPlayerDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        rallyDao = helper.getRallyDao();
        playDao = helper.getPlayDao();
        teamA = new Team("Team A");
        teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);
        match = new Match("Test Match", teamA, teamB);
        matchDao.create(match);
        player1 = new Player("Taro", "Volley");
        player2 = new Player("Jiro", "Ball");
        playerDao.create(player1);
        playerDao.create(player2);
        set = new Set(match);
        setDao.create(set);
        point = new Point(set);
        pointDao.create(point);
    }


    public void testCreatingPlay() throws Exception {
        //TODO
        



    }


}

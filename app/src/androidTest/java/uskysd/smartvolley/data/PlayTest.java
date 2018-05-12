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
    //Dao<Rally, Integer> rallyDao;
    Dao<Play, Integer> playDao;
    Match match;
    Team teamA;
    Team teamB;
    Player player1;
    Player player2;
    Player player3;
    Set set;
    Point point;


    public void setUp() throws Exception {
        DatabaseHelper helper = getDatabaseHelper(getContext());
        matchDao = helper.getMatchDao();
        teamDao = helper.getTeamDao();
        playerDao = helper.getPlayerDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        //rallyDao = helper.getRallyDao();
        playDao = helper.getPlayDao();
        teamA = new Team("Team A");
        teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);
        match = new Match("Test Match", teamA, teamB);
        matchDao.create(match);
        player1 = new Player("Taro", "Volley");
        player2 = new Player("Jiro", "Ball");
        player3 = new Player("Yusuke", "Yoshida");
        player1.setTeam(teamA);
        player2.setTeam(teamB);
        player3.setTeam(teamB);
        playerDao.create(player1);
        playerDao.create(player2);
        playerDao.create(player3);
        set = new Set(match);
        setDao.create(set);
        point = new Point(set);
        pointDao.create(point);
    }


    public void testCreatingPlay() throws Exception {

        // Setup
        setUp();

        // Exercise
        Play sut1 = new Play(point, player1, Play.PlayType.ATTACK);
        Play sut2 = new Play(point, player2, Play.PlayType.RECEIVE);
        Play sut3 = new Play(point, player3, Play.PlayType.BLOCK);

        // Verify
        assertEquals(point, sut1.getPoint());
        assertEquals(point, sut2.getPoint());
        assertEquals(match, sut1.getMatch());
        assertEquals(match, sut2.getMatch());
        assertEquals(player1, sut1.getPlayer());
        assertEquals(player2, sut2.getPlayer());
        assertEquals(Play.PlayType.ATTACK, sut1.getPlayType());
        assertEquals(Play.PlayType.RECEIVE, sut2.getPlayType());

        // TearDown
        tearDown();

    }

    public void testSettingPlayResult() throws Exception {
        // Setup
        setUp();
        Play sut = new Play(point, player1, Play.PlayType.SERVICE);

        // Exercise
        sut.setPlayResult(Play.PlayResult.POINT);

        // Verify
        assertEquals(Play.PlayResult.POINT, sut.getPlayResult());

        // Tear Down
        tearDown();

    }

    public void testAddingToDabase() throws Exception {

        // Setup
        setUp();

        // Exercise
        Play sut1 = new Play(point, player1, Play.PlayType.SERVICE);
        sut1.setPlayResult(Play.PlayResult.FAILURE);
        playDao.create(sut1);
        playerDao.update(player1);

        // Verify
        Play queried = playDao.queryForAll().get(0);

        assertEquals(sut1, queried);
        assertEquals(point, queried.getPoint());
        assertEquals(player1, queried.getPlayer());
        assertEquals(Play.PlayType.SERVICE, queried.getPlayType());
        assertEquals("Taro Volley", player1.getFullName());
        assertEquals(Play.PlayResult.FAILURE, queried.getPlayResult());


        // By default, foreign object only have id
        //assertEquals("Taro Volley", queried.getPlayer().getFullName());

        //Tear Down
        tearDown();

    }


}

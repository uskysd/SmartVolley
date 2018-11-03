package uskysd.smartvolley;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Event;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.PlayerEntry;
import uskysd.smartvolley.data.Point;
import uskysd.smartvolley.data.Set;
import uskysd.smartvolley.data.Team;

/**
 * Created by yusukeyohishida on 3/9/18.
 */

public class MatchDataManagerTest extends OrmLiteAndroidTestCase {


    private MatchDataManager dataManager;
    private Match match;
    private Team teamA;
    private Team teamB;
    private Player player1;
    private Player player2;

    // DAOs
    private Dao<Team, Integer> teamDao;
    private Dao<Match, Integer> matchDao;
    private Dao<Player, Integer> playerDao;
    private Dao<PlayerEntry, Integer> playerEntryDao;
    private Dao<Set, Integer> setDao;
    private Dao<Point, Integer> pointDao;
    private Dao<Play, Integer> playDao;

    @Override
    public void setUp() throws Exception {

        super.setUp();

        // Setup DAOs
        DatabaseHelper helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        matchDao = helper.getMatchDao();
        playerDao = helper.getPlayerDao();
        playerEntryDao = helper.getPlayerEntryDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        playDao = helper.getPlayDao();

        // Create test data set
        teamA  = new Team("Team A");
        teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);

        match = new Match("Test Match", teamA, teamB);
        matchDao.create(match);

        player1 = new Player("Yusuke", "Yoshida");
        player2 = new Player("Jennifer", "Lent");
        playerDao.create(player1);
        playerDao.create(player2);

        player1.setTeam(teamA);
        player2.setTeam(teamB);
        playerDao.update(player1);
        playerDao.update(player2);

        PlayerEntry entry1 = new PlayerEntry(match, player1, 10, PlayerEntry.TEAM_A, Position.BACK_LEFT);
        PlayerEntry entry2 = new PlayerEntry(match, player2, 7, PlayerEntry.TEAM_B, Position.FRONT_RIGHT);
        
        playerEntryDao.create(entry1);
        playerEntryDao.create(entry2);

        // Initialize MatchDataManager
        dataManager = new MatchDataManager(getContext(), match);


    }

    public void testCreatePlay() throws Exception {
        //SetUp
        setUp();

        // Exercise
        dataManager.setPlayer(player1);
        dataManager.setPlayType(Play.PlayType.SERVICE);
        dataManager.setPlayResult(Play.PlayResult.POINT);
        dataManager.createPlay();

        // Verify
        ArrayList<Play> plays = (ArrayList<Play>) playDao.queryForAll();
        assertEquals(1, plays.size());
        assertEquals(1, (int) plays.get(0).getId());
        assertEquals(player1, plays.get(0).getPlayer());
        assertEquals(Play.PlayType.SERVICE, plays.get(0).getPlayType());
        assertEquals(Play.PlayResult.POINT, plays.get(0).getPlayResult());

        // Teardown
        tearDown();

    }

    public void testGetEvents() throws Exception {
        //Setup
        setUp();

        dataManager.setPlayer(player1);
        dataManager.setPlayType(Play.PlayType.ATTACK);
        dataManager.setPlayResult(Play.PlayResult.CONTINUE);
        dataManager.createPlay();
        dataManager.setPlayer(player2);
        dataManager.setPlayType(Play.PlayType.BLOCK);
        dataManager.setPlayEvaluation(Play.PlayEvaluation.GOOD);
        dataManager.setPlayResult(Play.PlayResult.POINT);
        dataManager.createPlay();

        // Exercise
        List<Event> events = dataManager.getEvents();

        // Verify
        assertEquals(2, events.size());
        Play play1 = (Play) events.get(0);
        Play play2 = (Play) events.get(1);
        assertEquals(player1, play1.getPlayer());
        assertEquals(Play.PlayType.ATTACK, play1.getPlayType());
        assertEquals(null, play1.getEvaluation());
        assertEquals(Play.PlayResult.CONTINUE, play1.getPlayResult());
        assertEquals(player2, play2.getPlayer());
        assertEquals(Play.PlayType.BLOCK, play2.getPlayType());
        assertEquals(Play.PlayEvaluation.GOOD, play2.getEvaluation());
        assertEquals(Play.PlayResult.POINT, play2.getPlayResult());

        // Tear down
        tearDown();
    }




}





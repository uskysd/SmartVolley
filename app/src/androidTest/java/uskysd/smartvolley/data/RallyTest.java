package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by 112886 on 1/25/2016.
 */
public class RallyTest extends OrmLiteAndroidTestCase {

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
        player1.setTeam(teamA);
        player2.setTeam(teamB);
        playerDao.create(player1);
        playerDao.create(player2);
        set = new Set(match);
        setDao.create(set);
        point = new Point(set);
        pointDao.create(point);
    }

    public void testCreateRally() throws Exception {
        //Setup
        setUp();

        //Exercise
        Rally sut = new Rally(point, Rally.TEAM_A);
        Rally sut2 = new Rally(point, Rally.TEAM_B);
        rallyDao.create(sut);
        rallyDao.create(sut2);

        //Verify
        assertEquals(point, sut.getPoint());
        assertEquals(1, sut.getNumber());
        assertEquals(true, point.getRallies().contains(sut));
        assertEquals(0, sut.getPlayCount());
        assertEquals(2, sut2.getNumber());
        assertEquals(true, sut.isForTeamA());
        assertEquals(false, sut.isForTeamB());
        assertEquals(true, sut2.isForTeamB());
        assertEquals(false, sut2.isForTeamA());

        Rally queried = rallyDao.queryForAll().get(0);
        assertEquals(sut, queried);
        assertEquals(1, queried.getNumber());
        assertEquals(point, queried.getPoint());
        assertEquals(0, queried.getPlayCount());
        assertEquals(true, queried.isForTeamA());
        assertEquals(false, queried.isForTeamB());

        //TearDown
        tearDown();
    }

    public void testSetNumber() throws Exception {
        //Setup
        setUp();
        Rally sut = new Rally(point, Rally.TEAM_A);
        rallyDao.create(sut);

        //Exercise
        sut.setNumber(3);
        rallyDao.update(sut);

        //Verify
        assertEquals(3, sut.getNumber());
        Rally queried = rallyDao.queryForAll().get(0);
        assertEquals(3, queried.getNumber());

        //Tear Down
        tearDown();

    }


    public void testAddPlays() throws Exception {
        //Setup
        setUp();
        Rally sut = new Rally(point, Rally.TEAM_A);
        rallyDao.create(sut);

        //Exercise
        Play p1 = new Play(sut, player1, Play.PlayType.SERVICE, Play.PlayResult.GOOD);
        Play p2 = new Play(sut, player2, Play.PlayType.RECEPTION, Play.PlayResult.BAD);
        playDao.create(p1);
        playDao.create(p2);
        rallyDao.update(sut);

        //Verify
        assertEquals(2, sut.getPlayCount());
        assertEquals(true, sut.getPlays().contains(p1));
        assertEquals(true, sut.getPlays().contains(p2));
        assertEquals(sut, p1.getRally());
        assertEquals(sut, p2.getRally());

        Rally queried = rallyDao.queryForAll().get(0);
        assertEquals(2, queried.getPlayCount());
        assertEquals(true, queried.getPlays().contains(p1));
        assertEquals(true, queried.getPlays().contains(p2));

        //Tear Down
        tearDown();

    }

    public void testSortRallies() throws Exception {
        //Setup
        setUp();
        Rally r1 = new Rally(point, Rally.TEAM_A);
        Rally r2 = new Rally(point, Rally.TEAM_B);
        Rally r3 = new Rally(point, Rally.TEAM_A);
        Rally r4 = new Rally(point, Rally.TEAM_B);
        List<Rally> rallies = Arrays.asList(r3, r1, r4, r2);

        //Exercise
        Collections.sort(rallies);

        //Verify
        assertEquals(r1, rallies.get(0));
        assertEquals(r2, rallies.get(1));
        assertEquals(r3, rallies.get(2));
        assertEquals(r4, rallies.get(3));

        //Tear Down
        tearDown();

    }

    public void testRenumberPlays() throws Exception {
        //Setup
        setUp();
        Rally sut = new Rally(point, Rally.TEAM_A);
        rallyDao.create(sut);

        //Create plays
        Play p1 = new Play(sut, player1, Play.PlayType.ATTACK, Play.PlayResult.GOOD);
        Play p2 = new Play(sut, player1, Play.PlayType.RECEPTION, Play.PlayResult.GOOD);
        Play p3 = new Play(sut, player2, Play.PlayType.TOSS, Play.PlayResult.BAD);
        p1.setNumber(2);
        p2.setNumber(5);
        p3.setNumber(8);
        playDao.create(p1);
        playDao.create(p2);
        playDao.create(p3);
        rallyDao.update(sut);

        //Exercise
        sut.renumberPlay();
        playDao.update(p1);
        playDao.update(p2);
        playDao.update(p3);

        //Verify
        assertEquals(1, p1.getNumber());
        assertEquals(2, p2.getNumber());
        assertEquals(3, p3.getNumber());

        Rally queried = rallyDao.queryForAll().get(0);
        List<Play> plays = new ArrayList<Play>(queried.getPlays());
        Collections.sort(plays);
        assertEquals(1, plays.get(0).getNumber());
        assertEquals(2, plays.get(1).getNumber());
        assertEquals(3, plays.get(2).getNumber());

        //Tear Down
        tearDown();
    }

    public void testRemovePlay() throws Exception {
        //Setup
        setUp();
        Rally sut = new Rally(point, Rally.TEAM_A);
        rallyDao.create(sut);

        //Create plays
        Play p1 = new Play(sut, player1, Play.PlayType.ATTACK, Play.PlayResult.NORMAL);
        Play p2 = new Play(sut, player1, Play.PlayType.RECEPTION, Play.PlayResult.NORMAL);
        Play p3 = new Play(sut, player2, Play.PlayType.TOSS, Play.PlayResult.NORMAL);
        p1.setNumber(2);
        p2.setNumber(5);
        p3.setNumber(8);
        playDao.create(p1);
        playDao.create(p2);
        playDao.create(p3);
        rallyDao.update(sut);

        //Exercise
        sut.removePlay(p2);
        playDao.delete(p2);
        rallyDao.update(sut);
        playDao.update(p1);
        playDao.update(p3);

        //Verify
        assertEquals(2, sut.getPlayCount());
        assertEquals(1, p1.getNumber());
        assertEquals(2, p3.getNumber());
        assertEquals(2, playDao.queryForAll().size());
        Rally queried = rallyDao.queryForAll().get(0);
        assertEquals(2, queried.getPlayCount());
        List<Play> plays = new ArrayList<Play>(queried.getPlays());
        Collections.sort(plays);
        assertEquals(1, plays.get(0).getNumber());
        assertEquals(2, plays.get(1).getNumber());

        //Tear Down
        tearDown();

    }






}

package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by 112886 on 12/30/2015.
 */
public class PointTest extends OrmLiteAndroidTestCase {

    Dao<Team, Integer> teamDao;
    Dao<Match, Integer> matchDao;
    Dao<Set, Integer> setDao;
    Dao<Point, Integer> pointDao;
    Dao<Play, Integer> playDao;
    Dao<Player, Integer> playerDao;
    Team team1;
    Team team2;
    Player player1;
    Player player2;
    Match match;
    Set set;

    public void setUp() throws SQLException {
        DatabaseHelper helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        matchDao = helper.getMatchDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        playDao = helper.getPlayDao();
        playerDao = helper.getPlayerDao();
        team1 = new Team("Test team 1");
        team2 = new Team("Test team 2");
        player1 = new Player("Yusuke", "Yoshida");
        player2 = new Player("Taro", "Volley");
        teamDao.create(team1);
        teamDao.create(team2);
        player1.setTeam(team1);
        player2.setTeam(team2);
        playerDao.create(player1);
        playerDao.create(player2);
        match = new Match("Test match", team1, team2);
        matchDao.create(match);
        set = new Set(match, 1);
        setDao.create(set);
        matchDao.refresh(match);
        setDao.refresh(set);
    }

    public void testCreatePoint() throws Exception {
        //Setup
        setUp();

        //Exercise
        Point sut = new Point(set);
        set.getPoints().add(sut);
        Point sut2 = new Point(set);
        set.getPoints().add(sut2);
        Point sut3 = new Point(set);
        set.getPoints().add(sut3);

        List<Point> queriedPoints = pointDao.queryForAll();
        Collections.sort(queriedPoints);

        //Verify
        assertEquals(1, sut.getNumber());
        assertEquals(2, sut2.getNumber());
        assertEquals(3, sut3.getNumber());
        assertEquals(3, set.getPoints().size());
        assertEquals(true, set.getPoints().contains(sut));
        assertEquals(true, set.getPoints().contains(sut2));
        assertEquals(true, set.getPoints().contains(sut3));
        assertEquals(sut, queriedPoints.get(0));
        assertEquals(sut2, queriedPoints.get(1));
        assertEquals(sut3, queriedPoints.get(2));
        assertEquals(1, queriedPoints.get(0).getNumber());
        assertEquals(2, queriedPoints.get(1).getNumber());
        assertEquals(3, queriedPoints.get(2).getNumber());
        for (Point p: queriedPoints) {
            assertEquals(set, p.getSet());
        }

        //TearDown
        tearDown();

    }

    public void testSetTeamWon() throws Exception {
        //Setup
        setUp();
        Point sut = new Point(set);
        set.addPoint(sut);

        //Exercise & Verify
        assertEquals(true, sut.isOnGoing());
        assertEquals(false, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());

        sut.setTeamAWon();
        assertEquals(false, sut.isOnGoing());
        assertEquals(true, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());

        sut.setTeamBWon();
        assertEquals(false, sut.isOnGoing());
        assertEquals(false, sut.wonByTeamA());
        assertEquals(true, sut.wonByTeamB());

        sut.resetTeamWon();
        assertEquals(true, sut.isOnGoing());
        assertEquals(false, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());

        //TearDown
        tearDown();

    }

    public void testSetNumber() throws Exception {
        //Setup
        setUp();
        Point sut = new Point(set);

        //Exercise & Verify
        assertEquals(1, sut.getNumber());
        pointDao.create(sut);
        Point queried = pointDao.queryForAll().get(0);
        assertEquals(1, queried.getNumber());

        //Set new number
        sut.setNumber(3);
        assertEquals(3, sut.getNumber());
        pointDao.update(sut);
        queried = pointDao.queryForAll().get(0);
        assertEquals(3, queried.getNumber());

        //TearDown
        tearDown();
    }

    public void testSortPoints() throws Exception {
        //Setup
        setUp();
        Point p1 = new Point(set);
        set.getPoints().add(p1);
        Point p2 = new Point(set);
        set.getPoints().add(p2);
        Point p3 = new Point(set);
        set.getPoints().add(p3);
        Point p4 = new Point(set);
        set.getPoints().add(p4);
        Point p5 = new Point(set);
        set.getPoints().add(p5);
        List<Point> points = Arrays.asList(p3, p1, p5, p4, p2);

        //Exercise
        Collections.sort(points);

        //Verify
        assertEquals(p1, points.get(0));
        assertEquals(p2, points.get(1));
        assertEquals(p3, points.get(2));
        assertEquals(p4, points.get(3));
        assertEquals(p5, points.get(4));

        //TearDown
        tearDown();

    }

    public void testAddPlay() throws Exception {
        //Setup
        setUp();
        Point sut = new Point(set);
        pointDao.create(sut);
        setDao.refresh(set);

        // Exercise
        Play p1 = new Play(sut, player1, Play.PlayType.SERVICE);
        Play p2 = new Play(sut, player2, Play.PlayType.RECEPTION);
        Play p3 = new Play(sut, player1, Play.PlayType.TOSS);


        // Create on database
        playDao.create(p1);
        playDao.create(p2);
        playDao.create(p3);
        pointDao.refresh(sut);


        // Verify
        assertTrue(sut.getPlays().contains(p1));
        assertTrue(sut.getPlays().contains(p2));
        assertTrue(sut.getPlays().contains(p3));
        assertEquals(sut, p1.getPoint());
        assertEquals(sut, p2.getPoint());
        assertEquals(sut, p3.getPoint());

        Point queried = pointDao.queryForAll().get(0);
        assertEquals(sut, queried);
        assertTrue(queried.getPlays().contains(p1));
        assertTrue(queried.getPlays().contains(p2));
        assertTrue(queried.getPlays().contains(p3));
        for (Play p: playDao.queryForAll()) {
            assertTrue(queried.getPlays().contains(p));
        }

        // TearDown
        tearDown();

    }

    public void testRemovePlay() throws Exception {
        // Setup
        setUp();
        Point sut = new Point(set);
        pointDao.create(sut);
        setDao.refresh(set);
        pointDao.refresh(sut);

        // Add Plays
        Play p1 = new Play(sut, player1, Play.PlayType.SERVICE);
        sut.getPlays().add(p1);
        Play p2 = new Play(sut, player2, Play.PlayType.RECEPTION);
        sut.getPlays().add(p2);
        Play p3 = new Play(sut, player1, Play.PlayType.TOSS);
        sut.getPlays().add(p3);
        pointDao.update(sut);

        // Create on database
        //playDao.create(p1);
        //playDao.create(p2);
        //playDao.create(p3);
        //pointDao.refresh(sut);

        // Exercise
        sut.removePlay(p1);
        sut.removePlay(p3);
        pointDao.update(sut);
        playDao.delete(p1);
        playDao.delete(p3);

        // Verify
        assertFalse(sut.getPlays().contains(p1));
        assertTrue(sut.getPlays().contains(p2));
        assertFalse(sut.getPlays().contains(p3));
        Point queried = pointDao.queryForAll().get(0);
        assertFalse(sut.getPlays().contains(p1));
        assertTrue(sut.getPlays().contains(p2));
        assertFalse(sut.getPlays().contains(p3));

        // Tear Down
        tearDown();
    }

    public void testAddingPlayToQueriedPoint() throws Exception {

        // Setup
        setUp();
        Point point = new Point(set);
        pointDao.create(point);
        Point qpoint = pointDao.queryForId(point.getId());
        setDao.refresh(qpoint.getSet());
        matchDao.refresh(qpoint.getSet().getMatch());

        // Exercise
        Play p1 = new Play(qpoint, player1, Play.PlayType.ATTACK);
        Play p2 = new Play(qpoint, player2, Play.PlayType.SERVICE);

        playDao.create(p1);
        playDao.create(p2);
        pointDao.refresh(qpoint);


        // Verify
        assertEquals(2, playDao.countOf());
        assertEquals(qpoint, p1.getPoint());
        assertEquals(qpoint, p2.getPoint());
        assertTrue(qpoint.getPlays().contains(p1));
        assertTrue(qpoint.getPlays().contains(p2));

        // Tear Down
        tearDown();



    }

}

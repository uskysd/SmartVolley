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
    Dao<Rally, Integer> rallyDao;
    Team team1;
    Team team2;
    Match match;
    Set set;

    public void setUp() throws SQLException {
        DatabaseHelper helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        matchDao = helper.getMatchDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        rallyDao = helper.getRallyDao();
        team1 = new Team("Test team 1");
        team2 = new Team("Test team 2");
        teamDao.create(team1);
        teamDao.create(team2);
        match = new Match("Test match", team1, team2);
        matchDao.create(match);
        set = new Set(match);
        setDao.create(set);
    }

    public void testCreatePoint() throws Exception {
        //Setup
        setUp();

        //Exercise
        Point sut = new Point(set);
        Point sut2 = new Point(set);
        Point sut3 = new Point(set);
        pointDao.create(sut);
        pointDao.create(sut2);
        pointDao.create(sut3);

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
        Point p2 = new Point(set);
        Point p3 = new Point(set);
        Point p4 = new Point(set);
        Point p5 = new Point(set);
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



}

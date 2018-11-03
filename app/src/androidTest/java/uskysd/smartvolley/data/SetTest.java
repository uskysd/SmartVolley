package uskysd.smartvolley.data;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by 112886 on 12/25/2015.
 */
public class SetTest extends OrmLiteAndroidTestCase {

    Dao<Team, Integer> teamDao;
    Dao<Match, Integer> matchDao;
    Dao<Set, Integer> setDao;
    Dao<Point, Integer> pointDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DatabaseHelper helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        matchDao = helper.getMatchDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
    }

    public void testCreatingSet() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);

        //Exercise
        Set sut = new Set(match, 1);
        setDao.create(sut);

        //Verify
        Set queried = setDao.queryForAll().get(0);
        assertEquals(match, sut.getMatch());
        assertEquals(sut.getId(), queried.getId());
        assertEquals(match, queried.getMatch());

        //TearDown
        tearDown();

    }

    public void testSettingSetNumber() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);

        //Create Set
        Set sut = new Set(match, 1);
        setDao.create(sut);

        //Exrecise & Verify
        assertEquals(1, sut.getSetNumber());
        sut.setSetNumber(2);
        assertEquals(2, sut.getSetNumber());

        //TearDown
        tearDown();

    }

    public void testSettingTeamWon() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);

        //Create Set
        Set sut = new Set(match, 1);
        setDao.create(sut);

        //Exercise & Verify
        assertEquals(false, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());
        assertEquals(true, sut.isOnGoing());

        sut.setTeamAWon();
        assertEquals(true, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());
        assertEquals(false, sut.isOnGoing());

        sut.setTeamBWon();
        assertEquals(false, sut.wonByTeamA());
        assertEquals(true, sut.wonByTeamB());
        assertEquals(false, sut.isOnGoing());

        sut.resetTeamWon();
        assertEquals(false, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());
        assertEquals(true, sut.isOnGoing());

        //TearDown
        tearDown();


    }

    public void testAddingPoints() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);

        //Create Set
        Set sut = new Set(match, 1);
        setDao.create(sut);
        matchDao.refresh(match);//update Match.sets
        setDao.refresh(sut); //update Set.points

        //Exercise
        Point p1 = new Point(sut);
        sut.getPoints().add(p1);
        Point p2 = new Point(sut);
        sut.getPoints().add(p2);
        Point p3 = new Point(sut);
        sut.getPoints().add(p3);
        Point p4 = new Point(sut);
        sut.getPoints().add(p4);
        p1.setTeamAWon();
        p2.setTeamAWon();
        p3.setTeamBWon();
        p4.setTeamBWon();
        List<Point> points = Arrays.asList(p1, p2, p3, p4);
        for (Point p: points) {
            pointDao.update(p);
        }
        setDao.refresh(sut);


        //Verify
        Set queried = setDao.queryForAll().get(0);
        for (Point p: points) {
            assertEquals(queried, p.getSet());
            assertEquals(true, sut.getPoints().contains(p));
            assertEquals(true, queried.getPoints().contains(p));
        }
        assertEquals(2, sut.getPointsWonByTeamA().size());
        assertEquals(2, sut.getPointsWonByTeamB().size());
        assertEquals(true, sut.getPointsWonByTeamA().contains(p1));
        assertEquals(true, sut.getPointsWonByTeamA().contains(p2));
        assertEquals(true, sut.getPointsWonByTeamB().contains(p3));
        assertEquals(true, sut.getPointsWonByTeamB().contains(p4));

        //TearDown
        tearDown();

    }

    public void testUpdateTeamWon() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);

        //Create Set
        Set sut = new Set(match, 1);
        setDao.create(sut);
        matchDao.refresh(match);//update Match.sets
        setDao.refresh(sut); // update Set.points

        //Exercise and Verify
        sut.updateTeamWon();
        assertEquals(true, sut.isOnGoing());

        List<Point> pointsA = new ArrayList<Point>();
        List<Point> pointsB = new ArrayList<Point>();
        for (int i=0;i<24;i++) {
            Point p = new Point(sut);
            p.setTeamAWon();
            sut.getPoints().add(p);
            pointsA.add(p);
        }
        for (int i=0;i<24;i++) {
            Point p = new Point(sut);
            p.setTeamBWon();
            sut.getPoints().add(p);
            pointsB.add(p);
        }

        sut.updateTeamWon();
        assertEquals(true, sut.isOnGoing());
        assertEquals(false, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());
        assertEquals(24, sut.getPointsWonByTeamA().size());
        assertEquals(24, sut.getPointsWonByTeamB().size());

        Point p1 = new Point(sut);
        p1.setTeamAWon();
        sut.getPoints().add(p1);
        sut.updateTeamWon();

        assertEquals(25, sut.getPointsWonByTeamA().size());
        assertEquals(true, sut.isOnGoing());

        Point p2 = new Point(sut);;
        p2.setTeamAWon();
        sut.getPoints().add(p2);
        sut.updateTeamWon();

        assertEquals(26, sut.getPointsWonByTeamA().size());
        assertEquals(true, sut.wonByTeamA());

    }

    public void testRenumberPoints() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);

        //Create Set
        Set sut = new Set(match, 1);
        setDao.create(sut);
        matchDao.refresh(match);//update Match.sets
        setDao.refresh(sut); //update Set.points

        //Create points
        Point p1 = new Point(sut);
        sut.getPoints().add(p1);
        Point p2 = new Point(sut);
        sut.getPoints().add(p2);
        Point p3 = new Point(sut);
        sut.getPoints().add(p3);
        Point p4 = new Point(sut);
        sut.getPoints().add(p4);
        Point p5 = new Point(sut);
        sut.getPoints().add(p5);

        setDao.refresh(sut);

        List<Point> points = Arrays.asList(p1, p2, p3, p4, p5);

        //Change number of points
        p1.setNumber(3);
        p2.setNumber(5);
        p3.setNumber(1);
        p4.setNumber(8);
        p5.setNumber(10);
        for (Point p: points) {
            pointDao.update(p);
        }
        for (Point p: sut.getPoints()) {
            pointDao.refresh(p);
        }

        //Renumber points
        sut.renumberPoints();
        for (Point p: sut.getPoints()) {
            pointDao.update(p);
        }

        //Verify
        assertEquals(1, pointDao.queryForId(p3.getId()).getNumber());
        assertEquals(2, pointDao.queryForId(p1.getId()).getNumber());
        assertEquals(3, pointDao.queryForId(p2.getId()).getNumber());
        assertEquals(4, pointDao.queryForId(p4.getId()).getNumber());
        assertEquals(5, pointDao.queryForId(p5.getId()).getNumber());

        Set queried = setDao.queryForAll().get(0);
        List<Point> queriedPoints = new ArrayList<Point>(queried.getPoints());
        Collections.sort(queriedPoints);

        assertEquals(p3, queriedPoints.get(0));
        assertEquals(p1, queriedPoints.get(1));
        assertEquals(p2, queriedPoints.get(2));
        assertEquals(p4, queriedPoints.get(3));
        assertEquals(p5, queriedPoints.get(4));
        
        for (int i=0; i<queriedPoints.size(); i++) {
            Point p = queriedPoints.get(i);
            assertEquals(i+1, p.getNumber());
        }

        //TearDown
        tearDown();

    }

    public void testRemoveSet() throws Exception {
        //Setup
        setUp();

        //Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        //Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);
        matchDao.refresh(match);

        //Create set
        Set sut = new Set(match, 1);
        match.getSets().add(sut);
        setDao.refresh(sut);//update Set.points

        //Create points
        Point p1 = new Point(sut);
        sut.getPoints().add(p1);
        Point p2 = new Point(sut);
        sut.getPoints().add(p2);
        Point p3 = new Point(sut);
        sut.getPoints().add(p3);


        //Exercise
        sut.removePoint(p2);
        pointDao.delete(p2);
        setDao.update(sut);
        pointDao.update(p1);
        pointDao.update(p3);

        //Verify
        assertEquals(2, sut.getPoints().size());
        List<Point> points = new ArrayList<Point>(sut.getPoints());
        Collections.sort(points);
        assertEquals(1, points.get(0).getNumber());
        assertEquals(2, points.get(1).getNumber());

        assertEquals(2, pointDao.queryForAll().size());
        Set queried = setDao.queryForAll().get(0);
        assertEquals(2, queried.getPoints().size());
        points = new ArrayList<Point>(queried.getPoints());
        assertEquals(1, points.get(0).getNumber());
        assertEquals(2, points.get(1).getNumber());

        //Tear Down
        tearDown();
    }

    public void testAddSetToQueriedMatch() throws Exception {
        // Setup
        setUp();

        // Create team
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        // Create match
        Match match = new Match("TestMatch", team1, team2);
        matchDao.create(match);
        Match qmatch = matchDao.queryForId(match.getId());

        Log.d("SetTest", "Original match"+match.toString());
        Log.d("SetTest", "Queried match: "+qmatch.toString());



        // Exercise
        Set set1 = new Set(qmatch, 1);
        setDao.create(set1);
        matchDao.refresh(qmatch);
        Set set2 = new Set(qmatch, 2);
        qmatch.getSets().add(set2);//Set can also be created by adding into Match.sets
        Set set3 = new Set(qmatch, 3);
        qmatch.getSets().add(set3);
        /*
        for (Set s: Arrays.asList(set1, set2, set3)) {
            setDao.create(s);
        }

        for (Set s: Arrays.asList(set1, set2, set3)) {
            qmatch.addSet(s);
        }
        */


        Log.d("SetTest", "Set Count: " +Integer.toString((int) setDao.countOf()));
        for (Set s: setDao.queryForAll()) {
            Log.d("SetTest", "Queried Sets: "+s.toString());
        }


        // Verify
        assertEquals(match.getName(), qmatch.getName());
        assertEquals(3, setDao.countOf());
        assertEquals(qmatch, set1.getMatch());
        assertEquals(qmatch, set1.getMatch());
        assertEquals(qmatch, set3.getMatch());
        assertTrue(qmatch.getSets().contains(set1));
        assertTrue(qmatch.getSets().contains(set2));
        assertTrue(qmatch.getSets().contains(set3));
        assertEquals(1, set1.getSetNumber());
        assertEquals(2, set2.getSetNumber());
        assertEquals(3, set3.getSetNumber());

        // Tear Down
        tearDown();



    }

    // TODO Add member change testing



}


package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by 112886 on 12/14/2015.
 */

public class MatchTest extends OrmLiteAndroidTestCase {

    Dao<Match, Integer> matchDao;
    Dao<Team, Integer> teamDao;
    Dao<Player, Integer> playerDao;
    Dao<Set, Integer> setDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DatabaseHelper helper = getDatabaseHelper(getContext());
        matchDao = helper.getMatchDao();
        teamDao = helper.getTeamDao();
        playerDao = helper.getPlayerDao();
        setDao = helper.getSetDao();
    }

    public void testCreatingMatch() throws Exception {
        //Setup
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        // Team should be create on db before referred from match
        teamDao.create(teamA);
        teamDao.create(teamB);
        DateTime today = DateTime.now();

        //Exercise
        Match sut = new Match("Test match", teamA, teamB);

        //Verify
        assertEquals("Test match", sut.getName());
        assertEquals(teamA, sut.getTeamA());
        assertEquals(teamB, sut.getTeamB());
        assertEquals(today.getDayOfMonth(), sut.getStartDateTime().getDayOfMonth());
        assertEquals(today.getMonthOfYear(), sut.getStartDateTime().getMonthOfYear());
        assertEquals(today.getYear(), sut.getStartDateTime().getYear());

        //TearDown
        tearDown();
    }

    public void testCreatingMatchWithStartingDateTime() throws Exception {
        //Setup
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        // Team should be create on db before referred from match
        teamDao.create(teamA);
        teamDao.create(teamB);

        // Team not in db
        Team teamC = new Team("Team C");

        DateTime startDateTime = new DateTime(2015, 12, 14, 13, 00, 00);

        //Exercise
        Match sut = new Match("Test match", teamA, teamB, startDateTime);


        //Verify
        assertEquals("Test match", sut.getName());
        assertEquals(teamA, sut.getTeamA());
        assertEquals(teamB, sut.getTeamB());
        assertEquals(startDateTime, sut.getStartDateTime());

        //TearDown
        tearDown();
    }

    public void testCannotCreateMatchFromTeamNotOnDatabase() throws Exception {
        //Setup
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao teamDao = helper.getTeamDao();
        //Only team1 is created on database
        teamDao.create(team1);
        boolean thrown = false;

        //Exercise
        try {
            Match sut = new Match("Test Match", team1, team2);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        //Verify
        assertEquals(true, thrown);

        //Tear Down
        tearDown();
    }

    public void testCannotSetMatchNotOnDatabase() throws Exception {
        //Setup
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        // Team should be create on db before referred from match
        teamDao.create(teamA);
        teamDao.create(teamB);
        Match sut = new Match("Test match", teamA, teamB);
        //Team not created on database
        Team teamC = new Team("Team C");
        boolean thrownForTeamA = false;
        boolean thrownForTeamB = false;

        //Exercise
        try {
            sut.setTeamA(teamC);
        } catch (IllegalArgumentException e) {
            thrownForTeamA = true;
        }
        try {
            sut.setTeamB(teamC);
        } catch (IllegalArgumentException e) {
            thrownForTeamB = true;
        }

        //Verify
        assertEquals(true, thrownForTeamA);
        assertEquals(true, thrownForTeamB);
    }

    public void testDifferentTeamsMustBeRegistered() throws Exception {
        //Setup
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        // Team should be create on db before referred from match
        teamDao.create(team1);
        teamDao.create(team2);
        boolean thrownForConstructor = false;
        boolean thrownForTestASetter = false;
        boolean thrownForTestBSetter = false;
        Match sut;

        //Exercise
        try {
            sut = new Match("Test match", team1, team1);
        } catch (IllegalArgumentException e) {
            thrownForConstructor = true;
        }
        sut = new Match("Test match", team1, team2);
        try {
            sut.setTeamA(team2);

        } catch (IllegalArgumentException e) {
            thrownForTestASetter = true;
        }
        try {
            sut.setTeamB(team1);
        } catch (IllegalArgumentException e) {
            thrownForTestBSetter = true;
        }

        //Verify
        assertEquals(true, thrownForConstructor);
        assertEquals(true, thrownForTestASetter);
        assertEquals(true, thrownForTestBSetter);

        //Tear Down
        tearDown();

    }

    public void testSettingName() throws Exception {
        //Setup
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        // Team should be create on db before referred from match
        teamDao.create(teamA);
        teamDao.create(teamB);
        Match sut = new Match("Test match", teamA, teamB);
        String newName = "New match name";

        //Exercise
        sut.setName(newName);

        //Verify
        assertEquals(newName, sut.getName());

        //TearDown
        tearDown();

    }

    public void testSettingLocation() throws Exception {
        //Setup
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Match, Integer> matchDao = helper.getMatchDao();
        // Team should be create on db before referred from match
        teamDao.create(teamA);
        teamDao.create(teamB);
        Match sut = new Match("Test match", teamA, teamB);

        //Exercise
        sut.setLocation("Japan");
        matchDao.create(sut);

        //Verify
        Match queried = matchDao.queryForAll().get(0);
        assertEquals("Japan", sut.getLocation());
        assertEquals("Japan", queried.getLocation());

        //TearDown
        tearDown();

    }


    public void testSettingStartDateTime() throws Exception {
        //Setup
        setUp();
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        Match sut = new Match("Test much", team1, team2);
        DateTime dt = new DateTime(2015, 12, 14, 13, 00, 00);


        //Exercise
        sut.setStartDateTime(dt);

        //Verify
        assertEquals(dt, sut.getStartDateTime());

        //TearDown
        tearDown();

    }

    public void testSettingTeams() throws Exception {
        //Setup
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        // Team should be create on db before referred from match
        teamDao.create(teamA);
        teamDao.create(teamB);
        Match sut = new Match("Test match", teamA, teamB);
        Team newTeamA = new Team("New Team A");
        Team newTeamB = new Team("New Team B");
        teamDao.create(newTeamA);
        teamDao.create(newTeamB);

        //Exercise
        sut.setTeamA(newTeamA);
        sut.setTeamB(newTeamB);

        //Verify
        assertEquals(newTeamA, sut.getTeamA());
        assertEquals(newTeamB, sut.getTeamB());

        //Tear Down
        tearDown();

    }

    public void testRegisterPlayerEntries() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Match, Integer> matchDao = helper.getMatchDao();
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        Dao<PlayerEntry, Integer> playerEntryDao = helper.getPlayerEntryDao();

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamDao.create(teamA);
        teamDao.create(teamB);

        Match sut = new Match("Test Match", teamA, teamB);
        matchDao.create(sut);

        Player player1 = new Player("Taro", "Volley");
        Player player2 = new Player("Jiro", "Ball");
        playerDao.create(player1);
        playerDao.create(player2);

        //Exercise
        PlayerEntry playerEntry1 = new PlayerEntry(sut, player1, 10, PlayerEntry.TEAM_A, Position.BACK_LEFT);
        PlayerEntry playerEntry2 = new PlayerEntry(sut, player2, 5, PlayerEntry.TEAM_B, Position.LIBERO);
        playerEntryDao.create(playerEntry1);
        playerEntryDao.create(playerEntry2);
        matchDao.update(sut);

        //Verify
        Match queried = matchDao.queryForAll().get(0);

        assertEquals(true, queried.getPlayerEntries().contains(playerEntry1));
        assertEquals(true, queried.getPlayerEntries().contains(playerEntry2));
        assertEquals(true, queried.getPlayerEntriesToTeamA().contains(playerEntry1));
        assertEquals(false, queried.getPlayerEntriesToTeamA().contains(playerEntry2));
        assertEquals(true, queried.getPlayerEntriesToTeamB().contains(playerEntry2));
        assertEquals(false, queried.getPlayerEntriesToTeamB().contains(playerEntry1));

        //TearDown
        tearDown();


    }

    public void testSettingTeamWon() throws Exception {
        //Setup
        setUp();
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);

        Match sut = new Match("Test match", team1, team2);
        matchDao.create(sut);

        //Exercise and Verify
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



    public void testAddingSets() throws Exception {
        //Setup
        setUp();
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);
        Match sut = new Match("Test match", team1, team2);
        matchDao.create(sut);

        //Exercise
        Set set1 = new Set(sut);
        Set set2 = new Set(sut);
        setDao.create(set1);
        setDao.create(set2);

        //Verify
        Match queried = matchDao.queryForAll().get(0);
        assertEquals(false, queried.getSets()==null);
        assertEquals(true, queried.getSets().contains(set1));
        assertEquals(true, queried.getSets().contains(set2));

        //TearDown
        tearDown();

    }

    public void testGetSetsWonByTeam() throws Exception {
        //Setup
        setUp();
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);
        Match sut = new Match("Test match", team1, team2);
        matchDao.create(sut);

        //Create & add sets
        Set set1 = new Set(sut);
        Set set2 = new Set(sut);
        Set set3 = new Set(sut);
        Set set4 = new Set(sut);
        Set set5 = new Set(sut);
        set1.setTeamAWon();
        set2.setTeamAWon();
        set3.setTeamBWon();
        set4.setTeamBWon();
        set5.setTeamAWon();
        List<Set> sets = Arrays.asList(set1, set2, set3, set4, set5);
        for (Set s: sets) {
            setDao.create(s);
        }
        matchDao.update(sut);
        Match queried = matchDao.queryForAll().get(0);

        // Exercise & Verify
        for (Set s: queried.getSets()) {
            assertEquals(false, s.isOnGoing());
        }
        assertEquals(false, queried.getSetsWonByTeamA()==null);
        assertEquals(false, queried.getSetsWonByTeamB()==null);
        assertEquals(5, queried.getSets().size());
        assertEquals(3, queried.getSetsWonByTeamA().size());
        assertEquals(2, queried.getSetsWonByTeamB().size());
        assertEquals(true, queried.getSetsWonByTeamA().contains(set1));
        assertEquals(true, queried.getSetsWonByTeamA().contains(set2));
        assertEquals(false, queried.getSetsWonByTeamA().contains(set3));
        assertEquals(false, queried.getSetsWonByTeamA().contains(set4));
        assertEquals(true, queried.getSetsWonByTeamA().contains(set5));
        assertEquals(false, queried.getSetsWonByTeamB().contains(set1));
        assertEquals(false, queried.getSetsWonByTeamB().contains(set2));
        assertEquals(true, queried.getSetsWonByTeamB().contains(set3));
        assertEquals(true, queried.getSetsWonByTeamB().contains(set4));
        assertEquals(false, queried.getSetsWonByTeamB().contains(set5));

        assertEquals(5, sut.getSets().size());
        assertEquals(3, sut.getSetsWonByTeamA().size());
        assertEquals(2, sut.getSetsWonByTeamB().size());
        assertEquals(true, sut.getSetsWonByTeamA().contains(set1));
        assertEquals(true, sut.getSetsWonByTeamA().contains(set2));
        assertEquals(true, sut.getSetsWonByTeamB().contains(set3));
        assertEquals(true, sut.getSetsWonByTeamB().contains(set4));
        assertEquals(true, sut.getSetsWonByTeamA().contains(set5));

        //TearDown
        tearDown();

    }

    public void testUpdateTeamWon() throws Exception {
        //Setup
        setUp();
        Team team1 = new Team("Team 1");
        Team team2 = new Team("Team 2");
        teamDao.create(team1);
        teamDao.create(team2);
        Match sut = new Match("Test match", team1, team2);
        Match sut2 = new Match("Test match 2", team1, team2);
        matchDao.create(sut);
        matchDao.create(sut2);

        //Exercise & Verify
        //Team A wins
        Set set1 = new Set(sut);
        set1.setTeamAWon();
        Set set2 = new Set(sut);
        set2.setTeamAWon();
        Set set3 = new Set(sut);
        set3.setTeamBWon();
        setDao.create(set1);
        setDao.create(set2);
        setDao.create(set3);
        sut.updateTeamWon();

        assertEquals(true, sut.isOnGoing());

        Set set4 = new Set(sut);
        set4.setTeamAWon();
        setDao.create(set4);
        sut.updateTeamWon();

        assertEquals(4, sut.getSets().size());
        assertEquals(3, sut.getSetsWonByTeamA().size());
        assertEquals(true, sut.wonByTeamA());
        assertEquals(false, sut.wonByTeamB());
        assertEquals(false, sut.isOnGoing());

        //Team B wins
        Set s1 = new Set(sut2);
        Set s2 = new Set(sut2);
        Set s3 = new Set(sut2);
        s1.setTeamBWon();
        s2.setTeamBWon();
        s3.setTeamBWon();
        setDao.create(s1);
        setDao.create(s2);
        setDao.create(s3);
        sut2.updateTeamWon();

        assertEquals(true, sut2.wonByTeamB());
        assertEquals(false, sut2.wonByTeamA());
        assertEquals(false, sut2.isOnGoing());

        //TearDown
        tearDown();

    }


    public void testRemoveSet() throws Exception {
        //Setup
        setUp();
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);
        Match sut = new Match("Test Match", teamA, teamB);
        matchDao.create(sut);
        Set s1 = new Set(sut);
        Set s2 = new Set(sut);
        Set s3 = new Set(sut);
        setDao.create(s1);
        setDao.create(s2);
        setDao.create(s3);

        //Exercise
        sut.removeSet(s2);
        setDao.delete(s2);
        matchDao.update(sut);
        setDao.update(s1);
        setDao.update(s3);

        //Verify
        assertEquals(2, sut.getSetCount());
        List<Set> sets = new ArrayList<Set>(sut.getSets());
        Collections.sort(sets);
        assertEquals(1, sets.get(0).getSetNumber());
        assertEquals(2, sets.get(1).getSetNumber());

        assertEquals(2, setDao.queryForAll().size());
        Match queried = matchDao.queryForAll().get(0);
        assertEquals(2, queried.getSets().size());
        sets = new ArrayList<Set>(queried.getSets());
        Collections.sort(sets);
        assertEquals(1, sets.get(0).getSetNumber());
        assertEquals(2, sets.get(1).getSetNumber());

        //Tear Down
        tearDown();

    }



}

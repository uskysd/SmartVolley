package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by 112886 on 12/24/2015.
 */
public class PlayerEntryTest extends OrmLiteAndroidTestCase {

    public void testCreatingPlayerEntry() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Match, Integer> matchDao = helper.getMatchDao();
        Dao<PlayerEntry, Integer> playerEntryDao = helper.getPlayerEntryDao();

        Player player = new Player("Taro", "Volley");
        playerDao.create(player);

        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);

        Match match = new Match("Test match", teamA, teamB);
        matchDao.create(match);
        Integer number = 10;

        //Exercise
        PlayerEntry sut = new PlayerEntry(match, player, number, PlayerEntry.TEAM_A, Position.BACK_CENTER);
        playerEntryDao.create(sut);

        //Verify
        assertEquals(player, sut.getPlayer());
        assertEquals(match, sut.getMatch());
        assertEquals(number, sut.getNumber());
        assertEquals(true, sut.isForTeamA());
        assertEquals(false, sut.isForTeamB());
        assertEquals(Position.BACK_CENTER, sut.getStartingPosition());
        PlayerEntry queried = playerEntryDao.queryForAll().get(0);
        assertEquals(queried.getPlayer(), sut.getPlayer());
        assertEquals(queried.getNumber(), sut.getNumber());
        assertEquals(true, queried.isForTeamA());
        assertEquals(false, queried.isForTeamB());
        assertEquals(queried.getStartingPosition(), sut.getStartingPosition());

        //TearDown
        tearDown();

    }

    public void testThrowExceptionIfMatchIsNotOnDatabase() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Match, Integer> matchDao = helper.getMatchDao();
        Dao<PlayerEntry, Integer> playerEntryDao = helper.getPlayerEntryDao();

        Player player = new Player("Taro", "Volley");
        playerDao.create(player);

        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);

        Match match = new Match("Test match", teamA, teamB);
        Integer number = 10;
        boolean thrown = false;

        //Exercise
        try {
            PlayerEntry sut = new PlayerEntry(match, player, number, PlayerEntry.TEAM_A, Position.BACK_LEFT);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        //Verify
        assertEquals(true, thrown);

        //TearDown
        tearDown();

    }

    public void testSetTeam() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Match, Integer> matchDao = helper.getMatchDao();
        Dao<PlayerEntry, Integer> playerEntryDao = helper.getPlayerEntryDao();

        Player player = new Player("Taro", "Volley");
        playerDao.create(player);

        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);

        Match match = new Match("Test match", teamA, teamB);
        matchDao.create(match);
        Integer number = 10;

        PlayerEntry sut = new PlayerEntry(match, player, number, PlayerEntry.TEAM_A, Position.BACK_CENTER);
        playerEntryDao.create(sut);

        //Exercise & Verify
        assertEquals(true, sut.isForTeamA());
        assertEquals(false, sut.isForTeamB());
        sut.setForTeamB();
        assertEquals(true, sut.isForTeamB());
        assertEquals(false, sut.isForTeamA());
        sut.setForTeamA();
        assertEquals(true, sut.isForTeamA());
        assertEquals(false, sut.isForTeamB());

        //TearDown
        tearDown();

    }

    public void testSetNumber() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Match, Integer> matchDao = helper.getMatchDao();
        Dao<PlayerEntry, Integer> playerEntryDao = helper.getPlayerEntryDao();

        Player player = new Player("Taro", "Volley");
        playerDao.create(player);

        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);

        Match match = new Match("Test match", teamA, teamB);
        matchDao.create(match);
        Integer number = 10;
        Integer newNumber = 20;

        PlayerEntry sut = new PlayerEntry(match, player, number, PlayerEntry.TEAM_A, Position.BACK_CENTER);
        playerEntryDao.create(sut);

        //Exercise
        sut.setNumber(newNumber);

        //Verify
        assertEquals(newNumber, sut.getNumber());

        //TearDown
        tearDown();


    }







}

package uskysd.smartvolley.data;


import com.j256.ormlite.dao.Dao;

import java.util.Collection;
import java.util.Iterator;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by uskysd on 11/2/2015.
 */
public class TeamTest extends OrmLiteAndroidTestCase {

    public void testSettingNameWithConstructor() throws Exception {
        //Setup and Exercise
        Team sut = new Team("TeamName");
        String expected = "TeamName";
        String actual = sut.getName();

        //Verify
        assertEquals(expected, actual);

    }

    public void testCallingDao() throws Exception {
        //Setup
        getDatabaseHelper(getContext()).clearTables();

        //Exercise & Verify
        Dao<Team, Integer> dao = getDatabaseHelper(getContext()).getTeamDao();

        //Tear down
        tearDown();
    }

    public void testEntryToDatabase() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        helper.clearTables();
        Dao<Team, Integer> dao = helper.getTeamDao();
        Team sut = new Team("TeamName");

        //Exercise
        dao.create(sut);

        //Verify
        Team queried = dao.queryForAll().get(0);
        assertEquals(sut.getId(), queried.getId());

        //Tear Down
        tearDown();

    }

    public void testAddingPlayers() throws Exception {
        //Setup
        Team sut = new Team("Test Team");
        Player p1 = new Player("Taro", "Volley");
        Player p2 = new Player("Jiro", "Ball");
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Player, Integer> playerDao = helper.getPlayerDao();

        //Exercise & Verify
        try {
            sut.addPlayer(p1);
        } catch (IllegalArgumentException e) {
            // Team should be created before referred from player
        }
        teamDao.create(sut);
        p1.setTeam(sut);
        p2.setTeam(sut);
        //sut.addPlayer(p1);
        //sut.addPlayer(p2);
        playerDao.create(p1);
        playerDao.create(p2);

        assertEquals(2, teamDao.queryForId(sut.getId()).getPlayers().size());


    }


    public void testPlayersNeedToBeSeparatelyAddedToDatabase() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Team sut = new Team("Test Team");
        Player p1 = new Player("Taro", "Volley");
        Player p2 = new Player ("Jiro", "Ball");
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Player, Integer> playerDao = helper.getPlayerDao();

        //Exercise
        // Need to create team before adding player
        teamDao.create(sut);
        p1.setTeam(sut);
        p2.setTeam(sut);

        //Verify
        Collection<Team> queriedTeams = teamDao.queryForAll();
        assertEquals(1, queriedTeams.size());
        Collection<Player> queriedPlayers = playerDao.queryForAll();
        assertEquals(0, queriedPlayers.size());

        //Tear Down
        tearDown();

    }

    public void testAddingPlayersToTeam() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Team sut = new Team("Test Team");
        Player p1 = new Player("Taro", "Volley");
        Player p2 = new Player("Jiro", "Ball");
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        //playerDao.create(p1);
        //playerDao.create(p2);

        // Exercise
        teamDao.create(sut);
        Collection<Player> players = teamDao.queryForId(sut.getId()).getPlayers();

        // Added player will be created on database when added to foreign collection.
        players.add(p1);
        players.add(p2);
        playerDao.update(p1);
        playerDao.update(p2);

        //Verify

        Team queriedTeam = teamDao.queryForAll().get(0);
        Collection<Player> teamplayers = queriedTeam.getPlayers();
        Collection<Player> queriedPlayers = playerDao.queryForAll();
        //assertEquals(2, sut.getPlayers().size());
        assertEquals(2, queriedPlayers.size());
        assertEquals(2, teamplayers.size());

        for (Player p: playerDao.queryForAll()) {
            assertEquals(true, players.contains(p));
            assertEquals(sut, p.getTeam());
        }

        //TearDown
        tearDown();

    }

    public void testAddingPlayersToQueriedTeam() throws Exception {

        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        Dao<Team, Integer> teamDao = helper.getTeamDao();
        Dao<Player, Integer> playerDao = helper.getPlayerDao();
        Team sut = new Team("Test Team");
        teamDao.create(sut);

        Team qteam = teamDao.queryForId(sut.getId());

        Player p1 = new Player("Taro", "Volley");
        Player p2 = new Player("Jiro", "Ball");
        playerDao.create(p1);
        playerDao.create(p2);

        // Exercise

        p1.setTeam(qteam);
        //qteam.addPlayer(p1);
        p2.setTeam(qteam);
        //qteam.addPlayer(p2);

        playerDao.update(p1);
        playerDao.update(p2);

        // Verify
        assertEquals(1, teamDao.countOf());
        assertEquals(2, playerDao.countOf());
        assertTrue(qteam.getPlayers().contains(p1));
        assertTrue(qteam.getPlayers().contains(p2));
        Iterator<Player> iterator = qteam.getPlayers().iterator();
        assertEquals(p1.getFullName(), iterator.next().getFullName());
        assertEquals(p2.getFullName(), iterator.next().getFullName());

        // Tear down
        tearDown();




    }







}

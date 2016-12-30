package uskysd.smartvolley.data;



import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        sut.addPlayer(p1);
        sut.addPlayer(p2);
        playerDao.create(p1);
        playerDao.create(p2);

        assertEquals(2, sut.getPlayers().size());


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

        // Exercise
        teamDao.create(sut);
        p1.setTeam(sut);
        p2.setTeam(sut);
        playerDao.create(p1);
        playerDao.create(p2);

        //Verify

        Team queriedTeam = teamDao.queryForAll().get(0);
        Collection<Player> players = queriedTeam.getPlayers();
        Collection<Player> queriedPlayers = playerDao.queryForAll();
        assertEquals(2, sut.getPlayers().size());
        assertEquals(2, queriedPlayers.size());
        assertEquals(2, players.size());

        for (Player p: playerDao.queryForAll()) {
            assertEquals(true, players.contains(p));
            assertEquals(sut, p.getTeam());
        }

        //TearDown
        tearDown();

    }







}

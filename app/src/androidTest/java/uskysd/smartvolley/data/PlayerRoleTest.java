package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import uskysd.smartvolley.OrmLiteAndroidTestCase;

/**
 * Created by usky04da on 12/10/17.
 */

public class PlayerRoleTest extends OrmLiteAndroidTestCase {

    Dao<Team, Integer> teamDao;
    Dao<Player, Integer> playerDao;
    Dao<PlayerRole, Integer> playerRoleDao;
    Team team;
    Player player1;
    Player player2;

    public void setUp() throws SQLException {
        DatabaseHelper helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        playerDao = helper.getPlayerDao();
        playerRoleDao = helper.getPlayerRoleDao();
        team = new Team("Test Team");
        player1  = new Player("Yusuke", "Yoshida");
        player2 = new Player("Taro", "Volley");
        teamDao.create(team);
        player1.setTeam(team);
        player2.setTeam(team);
        playerDao.create(player1);
        playerDao.create(player2);

    }

    public void testCreatePlayerRole() throws Exception {
        // Setup
        setUp();

        // Exercise
        PlayerRole role1 = new PlayerRole(player1, Role.CAPTAIN);
        PlayerRole role2 = new PlayerRole(player1, Role.SETTER);
        PlayerRole role3 = new PlayerRole(player2, Role.CENTER);

        playerRoleDao.create(role1);
        playerRoleDao.create(role2);
        playerRoleDao.create(role3);

        List<PlayerRole> queried = playerRoleDao.queryForAll();

        // Verify
        assertEquals(player1, role1.getPlayer());
        assertEquals(Role.CAPTAIN, role1.getRole());
        assertEquals(player1, role2.getPlayer());
        assertEquals(Role.SETTER, role2.getRole());
        assertEquals(player2, role3.getPlayer());
        assertEquals(Role.CENTER, role3.getRole());

        assertEquals(3, queried.size());
        assertEquals(role1, queried.get(0));
        assertEquals(player1, queried.get(0).getPlayer());
        assertEquals(Role.CAPTAIN, queried.get(0).getRole());
        assertEquals(role2, queried.get(1));
        assertEquals(player1, queried.get(1).getPlayer());
        assertEquals(Role.SETTER, queried.get(1).getRole());
        assertEquals(role3, queried.get(2));
        assertEquals(player2, queried.get(2).getPlayer());
        assertEquals(Role.CENTER, queried.get(2).getRole());

        // Teardown
        tearDown();
    }


}

package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import java.util.Arrays;
import java.util.List;

import uskysd.smartvolley.OrmLiteAndroidTestCase;


/**
 * Created by uskysd on 11/1/2015.
 */
public class PlayerTest extends OrmLiteAndroidTestCase {

    DatabaseHelper helper;
    Dao<Player, Integer> playerDao;
    Dao<Team, Integer> teamDao;
    Team team1;
    Team team2;



    public void setUp() throws Exception {
        helper = getDatabaseHelper(getContext());
        playerDao = helper.getPlayerDao();
        teamDao = helper.getTeamDao();
        team1 = new Team("Team A");
        team2 = new Team("Team B");
        teamDao.create(team1);
        teamDao.create(team2);
    }

    public void testGettingFullname() {
        //Setup
        Player sut = new Player("Firstname", "Lastname");
        String expected = "Firstname Lastname";

        //Exercise
        String actual = sut.getFullName();

        //Verify
        assertEquals(expected, actual);

    }

    public void testSettingAsMale() {
        //Setup
        Player sut = new Player();

        //Exercise
        sut.setAsMale();

        //Verify
        assertEquals(true, sut.isMale());
        assertEquals(false, sut.isFemale());

    }

    public void testSettingAsFemale() {
        //Setup
        Player sut = new Player();

        //Exercise
        sut.setAsFemale();

        //Verify
        assertEquals(true, sut.isFemale());
        assertEquals(false, sut.isMale());
    }

    public void testCannotSetNegativeValueForHeight() {
        //Setup
        Player sut = new Player();
        float expected = (float)190.0;
        sut.setHeight(expected);

        //Exercise
        try {
            sut.setHeight((float)-10.0);
        } catch (IllegalArgumentException e) {
            //do nothing
        }
        float actual = sut.getHeight();

        //Verify

        assertEquals(expected, actual);
    }

    public void testCannotSetNegativeValueForWeight(){
        //Setup
        Player sut = new Player();
        float expected = (float)80.0;
        sut.setWeight(expected);

        //Exercise
        try {
            sut.setWeight((float)-10.0);
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        float actual = sut.getWeight();

        //Verify
        assertEquals(expected, actual);

    }

    public void testCallingDao() throws Exception {

        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        helper.clearTables();

        //Exercise
        Dao<Player, Integer> dao =helper.getPlayerDao();

        //Tear Down
        tearDown();

    }


    public void testEntryToDatabase() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        helper.clearTables();
        Dao<Player, Integer> dao = helper.getPlayerDao();

        //Exercise
        Player sut = new Player("Firstname", "Lastname");
        dao.create(sut);

        //Verify
        Player queried = dao.queryForAll().get(0);
        assertEquals(queried.getId(), sut.getId());
        assertEquals("Firstname", queried.getFirstName());
        assertEquals("Lastname", queried.getLastName());
        assertEquals("Firstname Lastname", queried.getFullName());

        //Tear Down
        tearDown();
    }

    public void testEquals() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        helper.clearTables();
        Dao<Player, Integer> dao = helper.getPlayerDao();
        Player sut1 = new Player("Taro", "Volley");
        Player sut2 = new Player("Jiro", "Ball");

        // Exercise and Verify
        assertEquals(false, sut1==sut2);
        System.out.println(sut1.getId());
        assertEquals(true, sut1.getId()==null);
        assertEquals(true, sut1.equals(sut1));
        assertEquals(false, sut1.equals(sut2));

        dao.create(sut1);
        Player queried1 = dao.queryForAll().get(0);
        Player queried2 = dao.queryForAll().get(0);
        assertEquals(true, queried1.getId()==1);
        assertEquals(true, sut1.equals(queried1));
        assertEquals(true, queried1.equals(queried2));

        //TearDown
        tearDown();

    }

    public void testSettingTeam() throws Exception {
        // Setup
        setUp();
        Player player1 = new Player("Cristiano", "Ronaldo");
        Player player2 = new Player("Lionel", "Messi");
        Player player3 = new Player("Romelu", "Lukaku");
        Player player4 = new Player("Luka", "Modric");

        Team qteam1 = teamDao.queryForId(team1.getId());
        Team qteam2 = teamDao.queryForId(team2.getId());

        for (Player p: Arrays.asList(player1, player2, player3, player4)) {
            playerDao.create(p);
        }

        // Exercise


        player1.setTeam(qteam1);
        //playerDao.create(player1);
        player2.setTeam(qteam1);
        //playerDao.create(player2);
        player3.setTeam(qteam2);
        //playerDao.create(player3);
        player4.setTeam(qteam2);
        //playerDao.create(player4);

        for (Player p: Arrays.asList(player1, player2, player3, player4)) {
            playerDao.update(p);
        }

        List<Player> queriedPlayers = playerDao.queryForAll();


        // Verify
        assertEquals(4, queriedPlayers.size());
        assertEquals(team1, player1.getTeam());
        assertEquals(team1.getName(), player1.getTeam().getName());
        Player queried = playerDao.queryForAll().get(0);
        assertEquals(player1, queried);
        assertEquals(team1, queried.getTeam());
        assertEquals(4, playerDao.countOf());

        // Tear Down
        tearDown();


    }


}

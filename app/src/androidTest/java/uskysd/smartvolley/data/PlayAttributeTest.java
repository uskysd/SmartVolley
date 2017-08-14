package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import uskysd.smartvolley.OrmLiteAndroidTestCase;
import uskysd.smartvolley.data.Play.PlayType;

/**
 * Created by usky04da on 12/19/16.
 */

public class PlayAttributeTest extends OrmLiteAndroidTestCase {


    public void testConstructor() throws Exception {

        // Setup & Exercise
        PlayAttribute sut = new PlayAttribute("Super Duper Quick", PlayType.ATTACK);

        // Verify
        assertEquals("Super Duper Quick", sut.getName());
        assertEquals(PlayType.ATTACK, sut.getPlayType());

    }

    public void testCallingDao() throws Exception {

        // Setup
        getDatabaseHelper(getContext()).clearTables();

        // Exercise and Verify
        Dao<PlayAttribute, Integer> dao = getDatabaseHelper(getContext()).getPlayAttributeDao();

        // Tear Down
        tearDown();
    }

    public void testEntryToDatabase() throws Exception {
        // Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        helper.clearTables();
        Dao<PlayAttribute, Integer> dao = helper.getPlayAttributeDao();
        PlayAttribute sut = new PlayAttribute("Back Attack", PlayType.ATTACK);

        // Exercise
        dao.create(sut);

        // Verify
        PlayAttribute queried = dao.queryForAll().get(0);
        assertEquals(sut.getId(), queried.getId());
        assertEquals("Back Attack", queried.getName());
        assertEquals(PlayType.ATTACK, queried.getPlayType());
    }

    public void testAddingPlays() throws Exception {
        // Setup

        // Setup Database helper and Dao
        DatabaseHelper helper = getDatabaseHelper(getContext());
        helper.clearTables();
        Dao<PlayAttribute, Integer> attrDao = helper.getPlayAttributeDao();
        Dao<Play, Integer> playDao = helper.getPlayDao();
        Dao<Player, Integer> playerDao = helper.getPlayerDao();

        PlayAttribute sut = new PlayAttribute("Super Block", PlayType.BLOCK);
        Player player1 = new Player("Yusuke", "Yoshida");
        playerDao.create(player1);
        Play p1 = new Play(player1, PlayType.BLOCK);
        attrDao.create(sut);

        // Exercise
        p1.setAttribute(sut);

        // Verify
        assertEquals(sut, p1.getAttribute());
        assertTrue(sut.getPlays().contains(p1));

        // Create on database
        playDao.create(p1);

        // Verify queried data
        Play qp1 = playDao.queryForAll().get(0);
        PlayAttribute qattr = attrDao.queryForAll().get(0);
        assertEquals(qattr, sut);
        assertEquals(qp1, p1);
        assertEquals(qattr, qp1.getAttribute());
        assertTrue(qattr.getPlays().contains(qp1));

    }


}

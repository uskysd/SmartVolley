package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import uskysd.smartvolley.OrmLiteAndroidTestCase;
import uskysd.smartvolley.data.Play.PlayType;

/**
 * Created by usky04da on 12/19/16.
 */

public class PlayAttributeTest extends OrmLiteAndroidTestCase {

    public void testConstractor() throws Exception {

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
        PlayAttribute sut = new PlayAttribute("Super Block", PlayType.BLOCK);
        Play p1 = Play

    }


}

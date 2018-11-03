package uskysd.smartvolley.data;

import android.util.Log;

import com.j256.ormlite.dao.Dao;

import uskysd.smartvolley.OrmLiteAndroidTestCase;
import uskysd.smartvolley.TestDataGenerator;

public class TestTestDataGenerator extends OrmLiteAndroidTestCase {

    DatabaseHelper helper;
    Dao<Team, Integer> teamDao;
    Dao<Player, Integer> playerDao;
    Dao<Match, Integer> matchDao;
    Dao<Set, Integer> setDao;
    Dao<Point, Integer> pointDao;
    Dao<Play, Integer> playDao;
    Dao<MemberChange, Integer> memberChangeDao;

    int TEAM_COUNT = 2;
    int PLAYER_COUNT = 30;

    public void setUp() throws Exception {
        helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        playerDao = helper.getPlayerDao();
        matchDao = helper.getMatchDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        playDao = helper.getPlayDao();
        memberChangeDao = helper.getMemberChangeDao();
    }

    public void testLoadTestDataFromCsv() throws Exception {

        // Setup
        setUp();
        TestDataGenerator sut = new TestDataGenerator(getContext());

        // Exercise
        sut.loadTestDataFromCsv();

        // Verify
        for (Player p: playerDao.queryForAll()) {
            Log.d("Test", p.toString());
        }
        assertEquals(TEAM_COUNT, teamDao.countOf());
        assertEquals(PLAYER_COUNT, playerDao.countOf());



        // Tear Down
        tearDown();

    }



}

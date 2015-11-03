package uskysd.smartvolley.data;



import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;


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

        //Excerise & Verify
        Dao<Team, Integer> dao = getDatabaseHelper(getContext()).getTeamDao();

        //Tear down
        tearDown();
    }

    public void testEntryToDatabase() throws Exception {
        //Setup
        DatabaseHelper helper = getDatabaseHelper(getContext());
        DatabaseConnection connection = (DatabaseConnection) helper.getConnectionSource();
        helper.clearTables();
        Dao<Team, Integer> dao = helper.getTeamDao();
        Team sut = new Team("TeamName");

        //Exercise
        dao.create(sut);
        dao.commit(connection);

        //Verify
        Team queried = dao.queryForAll().get(0);
        assertEquals(sut.getId(), queried.getId());

    }






}

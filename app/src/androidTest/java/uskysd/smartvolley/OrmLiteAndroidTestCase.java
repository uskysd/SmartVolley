package uskysd.smartvolley;

import android.content.Context;
import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import uskysd.smartvolley.data.DatabaseHelper;

/**
 * Created by uskysd on 11/2/2015.
 */
public class OrmLiteAndroidTestCase extends AndroidTestCase {

    private DatabaseHelper databaseHelper = null;

    protected DatabaseHelper getDatabaseHelper(Context context) {
        if (databaseHelper==null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // clear database
        tearDown();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //Clear tables
        try {
            getDatabaseHelper(getContext()).clearTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Release helper
        if (databaseHelper!=null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }
}

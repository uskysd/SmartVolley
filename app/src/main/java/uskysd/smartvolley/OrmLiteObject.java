package uskysd.smartvolley;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import uskysd.smartvolley.data.DatabaseHelper;

/**
 * Created by yusukeyohishida on 1/17/18.
 */

public class OrmLiteObject {

    private DatabaseHelper databaseHelper = null;

    protected DatabaseHelper getDatabaseHelper(Context context) {
        if (databaseHelper==null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    protected void clearDatabase(Context context) throws SQLException {
        // Clear tables
        getDatabaseHelper(context).clearTables();

        // Release helper
        if (databaseHelper!=null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

}

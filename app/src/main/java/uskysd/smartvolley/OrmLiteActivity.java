package uskysd.smartvolley;

import android.app.Activity;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import uskysd.smartvolley.data.DatabaseHelper;

/**
 * Created by yusukeyohishida on 4/7/18.
 */

public class OrmLiteActivity extends Activity {

        private DatabaseHelper databaseHelper;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper!=null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper==null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}

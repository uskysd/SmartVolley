package uskysd.smartvolley.data;



import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    //private static final String PATH_TO_CONFIG = "ormlite_config.txt";
    private static final String PATH_TO_CONFIG = "src/main/res/raw/ormlite_config.txt";
    private static final Class<?>[] classes = new Class[]{
            Match.class, Play.class, PlayAttribute.class, Player.class, PlayerEntry.class,
            PlayerRole.class, Point.class, Set.class, Team.class, MemberChange.class,
    };


	public static void main(String[] args) throws Exception {

	    writeConfigFile(new File(PATH_TO_CONFIG), classes);
	}
}

package uskysd.smartvolley.data;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.PlayAttribute;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.PlayerEntry;
import uskysd.smartvolley.data.PlayerRole;
import uskysd.smartvolley.data.Point;
import uskysd.smartvolley.data.Rally;
import uskysd.smartvolley.data.Role;
import uskysd.smartvolley.data.Set;
import uskysd.smartvolley.data.Team;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final String PATH_TO_CONFIG = "ormlite_config.txt";
    private static final String PATH = "C:\\Users\\112886\\AndroidStudioProjects\\SmartVolley\\app\\src\\main\\res\\raw\\ormlite_config.txt";
    private static final Class<?>[] classes = new Class[]{
            Match.class, Play.class, PlayAttribute.class, Player.class, PlayerEntry.class,
            PlayerRole.class, Point.class, Rally.class, Set.class, Team.class,
    };

	public static void main(String[] args) throws Exception {
		writeConfigFile(new File(PATH), classes);
	}
}

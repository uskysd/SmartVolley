package uskysd.smartvolley.data;

import java.sql.SQLException;

import android.app.ActionBar;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import uskysd.smartvolley.R;

/**

 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "smartvolley.db";
	private static final int DATABASE_VERSION = 1;

	private Dao<Team, Integer> teamDao = null;
	private Dao<Player, Integer> playerDao = null;
	//private Dao<Role, Integer> roleDao = null;
	private Dao<PlayerRole, Integer> playerRoleDao = null;
	private Dao<Match, Integer> matchDao = null;
	private Dao<PlayerEntry, Integer> playerEntryDao = null;
	private Dao<Set, Integer> setDao = null;
	private Dao<Point, Integer> pointDao = null;
	private Dao<Rally, Integer> rallyDao = null;
	private Dao<Play, Integer> playDao = null;
	private Dao<PlayAttribute, Integer> playAttributeDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Team.class);
			TableUtils.createTable(connectionSource, Player.class);
			//ableUtils.createTable(connectionSource, Role.class);
			//TableUtils.createTable(connectionSource, PlayerRole.class);
			TableUtils.createTable(connectionSource, Match.class);
			TableUtils.createTable(connectionSource, Play.class);
			TableUtils.createTable(connectionSource, PlayerEntry.class);
			TableUtils.createTable(connectionSource, PlayAttribute.class);
			TableUtils.createTable(connectionSource, Point.class);
			TableUtils.createTable(connectionSource, Rally.class);
			TableUtils.createTable(connectionSource, Set.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Team.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
					+ newVer, e);
		}
	}

	public Dao<Team, Integer> getTeamDao() throws SQLException {
		if (teamDao == null) {
			teamDao = getDao(Team.class);
		}
		return teamDao;
	}
	
	public Dao<Player, Integer> getPlayerDao() throws SQLException {
		if (playerDao == null) {
			playerDao = getDao(Player.class);
		}
		return playerDao;
	}
	

	public Dao<Match, Integer> getMatchDao() throws SQLException {
		if (matchDao == null) {
			matchDao = getDao(Match.class);
		}
		return matchDao;
	}
	
	public Dao<PlayerEntry, Integer> getPlayerEntryDao() throws SQLException {
		if (playerEntryDao == null) {
			playerEntryDao = getDao(PlayerEntry.class);
		}
		return playerEntryDao;
	}
	
	public Dao<Set, Integer> getSetDao() throws SQLException {
		if (setDao == null) {
			setDao = getDao(Set.class);
		}
		return setDao;
	}
	
	public Dao<Point, Integer> getPointDao() throws SQLException {
		if (pointDao == null) {
			pointDao = getDao(Point.class);
		}
		return pointDao;
	}
	
	public Dao<Rally, Integer> getRallyDao() throws SQLException {
		if (rallyDao == null) {
			rallyDao = getDao(Rally.class);
		}
		return rallyDao;
	}
	
	public Dao<Play, Integer> getPlayDao() throws SQLException {
		if (playDao == null) {
			playDao = getDao(Play.class);
		}
		return playDao;
	}
	
	public Dao<PlayAttribute, Integer> getPlayAttributeDao() throws SQLException {
		if (playAttributeDao == null) {
			playAttributeDao = getDao(PlayAttribute.class);
		}
		return playAttributeDao;
	}
	
	
	@Override
	public void close() {
		super.close();
		teamDao = null;
		playerDao = null;
		//roleDao = null;
		//playerRoleDao = null;
		matchDao = null;
		playerEntryDao = null;
		setDao = null;
		pointDao = null;
		rallyDao = null;
		playDao = null;
		playAttributeDao = null;
	}

	public void clearTables() throws SQLException {
		// Drop tables
		TableUtils.dropTable(getConnectionSource(), Team.class, true);
		TableUtils.dropTable(getConnectionSource(), Player.class, true);
		//TableUtils.dropTable(getConnectionSource(), Role.class, true);
		//TableUtils.dropTable(getConnectionSource(), PlayerRole.class, true);
		TableUtils.dropTable(getConnectionSource(), Match.class, true);
		TableUtils.dropTable(getConnectionSource(), PlayerEntry.class, true);
		TableUtils.dropTable(getConnectionSource(), Play.class, true);
		TableUtils.dropTable(getConnectionSource(), Set.class, true);
		TableUtils.dropTable(getConnectionSource(), Point.class, true);
		TableUtils.dropTable(getConnectionSource(), Rally.class, true);
		TableUtils.dropTable(getConnectionSource(), PlayAttribute.class, true);

		// Recreate tables
		TableUtils.createTable(connectionSource, Team.class);
		TableUtils.createTable(connectionSource, Player.class);
		//TableUtils.createTable(connectionSource, Role.class);
		//TableUtils.createTable(connectionSource, PlayerRole.class);
		TableUtils.createTable(connectionSource, Match.class);
		TableUtils.createTable(connectionSource, Play.class);
		TableUtils.createTable(connectionSource, PlayerEntry.class);
		TableUtils.createTable(connectionSource, PlayAttribute.class);
		TableUtils.createTable(connectionSource, Point.class);
		TableUtils.createTable(connectionSource, Rally.class);
		TableUtils.createTable(connectionSource, Set.class);
	}
	
}

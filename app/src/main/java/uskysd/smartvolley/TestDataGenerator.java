package uskysd.smartvolley;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.PlayAttribute;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.PlayerEntry;
import uskysd.smartvolley.data.PlayerRole;
import uskysd.smartvolley.data.Point;
import uskysd.smartvolley.data.Set;
import uskysd.smartvolley.data.Team;

import static uskysd.smartvolley.data.PlayerEntry.TEAM_A;
import static uskysd.smartvolley.data.PlayerEntry.TEAM_B;

/**
 * Created by yusukeyohishida on 1/20/18.
 */

public class TestDataGenerator extends OrmLiteObject {

    private Dao<Player, Integer> playerDao;
    private Dao<Team, Integer> teamDao;
    private Dao<Match, Integer> matchDao;
    private Dao<Set, Integer> setDao;
    private Dao<Point, Integer> pointDao;
    private Dao<PlayerEntry, Integer> playerEntryDao;
    private Dao<Play, Integer> playDao;
    private Dao<PlayAttribute, Integer> playAttributeDao;
    private Dao<PlayerRole, Integer> playerRoleDao;
    private Context context;


    public TestDataGenerator(Context context) throws SQLException {
        this.context = context;
        DatabaseHelper helper = getDatabaseHelper(context);
        playerDao = helper.getPlayerDao();
        teamDao = helper.getTeamDao();
        matchDao = helper.getMatchDao();
        playerEntryDao = helper.getPlayerEntryDao();
        playerRoleDao = helper.getPlayerRoleDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        playDao = helper.getPlayDao();

        // clear database
        helper.clearTables();

    }

    public void createTestDataCase001() throws SQLException {

        // Create players
        Player yusuke = new Player("Yusuke", "Yoshida", Position.SUB);
        Player nobunaga = new Player("Nobunaga", "Oda", Position.FRONT_LEFT);
        Player hideyoshi = new Player("Hideyoshi", "Toyotomi", Position.FRONT_CENTER);
        Player masamune = new Player("Masamune", "Date", Position.FRONT_CENTER);
        Player mitsuhide = new Player("Mitsuhide", "Akechi", Position.BACK_RIGHT);
        Player nobushige = new Player("Nobushige", "Sanada", Position.BACK_CENTER);
        Player toshiie = new Player("Toshiie", "Maeda", Position.SUB);
        Player hanzo = new Player("Hanzo", "Hattori", Position.SUB);
        Player kojiro = new Player("Kojiro", "Sasaki", Position.BACK_LEFT);

        Player ieyasu = new Player("Ieyasu", "Tokugawa", Position.FRONT_LEFT);
        Player kenshin = new Player("Kenshin", "Uesugi", Position.FRONT_CENTER);
        Player shingen = new Player("Shingen", "Takeda", Position.FRONT_RIGHT);
        Player nagamasa = new Player("Nagamasa", "Asai", Position.BACK_RIGHT);
        Player yoshimoto = new Player("Yoshimoto", "Imagawa", Position.SUB);
        Player mitsunari = new Player("Mitsunari", "Ishida", Position.BACK_CENTER);
        Player goemon = new Player("Goemon", "Ishikawa", Position.SUB);
        Player musashi = new Player("Musashi", "Miyamoto", Position.BACK_LEFT);
        Player motonari = new Player("Motonari", "Mouri", Position.SUB);

        List<Player> players = Arrays.asList(yusuke, nobunaga, hideyoshi, ieyasu, kenshin,
                shingen, masamune, mitsuhide, nagamasa, yoshimoto, mitsunari, nobushige,
                toshiie, goemon, hanzo, musashi, kojiro, motonari);

        List<Player> playersA = Arrays.asList(yusuke, nobunaga, hideyoshi, masamune, mitsuhide,
                nobushige, toshiie, hanzo, kojiro);
        List<Player> playersB = Arrays.asList(ieyasu, kenshin, shingen, nagamasa, yoshimoto,
                mitsunari, goemon, musashi, motonari);

        for (Player p: players) {
            playerDao.create(p);
        }

        // Create teams
        Team bushosA = new Team("Bushos A");
        Team bushosB = new Team("Bushos B");

        teamDao.create(bushosA);
        teamDao.create(bushosB);

        // Assign players to team
        for (Player p: playersA) {
            bushosA.addPlayer(p);
            playerDao.update(p);
        }
        for (Player p: playersB) {
            bushosB.addPlayer(p);
            playerDao.update(p);
        }

        teamDao.update(bushosA);
        teamDao.update(bushosB);

        // Create match
        Match match = new Match("Test Match", bushosA, bushosB);
        matchDao.create(match);

        // Player entry
        int number = 1;
        for (Player p: playersA) {
            PlayerEntry entry = new PlayerEntry(match, p, number, TEAM_A, p.getStartingPosition());
            playerEntryDao.create(entry);
            number+=1;
        }
        for (Player p: playersB) {
            PlayerEntry entry = new PlayerEntry(match, p, number, TEAM_B, p.getStartingPosition());
            playerEntryDao.create(entry);
            number+=1;
        }
        matchDao.update(match);


        // Add some plays
        Set set1 = new Set(match);
        setDao.create(set1);

        Point point1 = new Point(set1);
        pointDao.create(point1);

        Play play01 = new Play(point1, nobunaga, Play.PlayType.SERVICE);
        Play play02 = new Play(point1, ieyasu, Play.PlayType.RECEPTION);

        playDao.create(play01);
        playDao.create(play02);
        playerDao.update(nobunaga);
        playerDao.update(ieyasu);



    }


}

package uskysd.smartvolley;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.MemberChange;
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

    private static final String TAG = TestDataGenerator.class.getSimpleName();

    private Dao<Player, Integer> playerDao;
    private Dao<Team, Integer> teamDao;
    private Dao<Match, Integer> matchDao;
    private Dao<Set, Integer> setDao;
    private Dao<Point, Integer> pointDao;
    private Dao<PlayerEntry, Integer> playerEntryDao;
    private Dao<Play, Integer> playDao;
    private Dao<PlayAttribute, Integer> playAttributeDao;
    private Dao<PlayerRole, Integer> playerRoleDao;
    private Dao<MemberChange, Integer> memberChangeDao;
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
        memberChangeDao = helper.getMemberChangeDao();

        // clear database
        helper.clearTables();

    }


    public void loadTestDataFromCsv() throws SQLException, IOException {

        Log.d(TAG, "Loading test data");

        BufferedReader br = null;
        String line = "";

        InputStream assetInStream = null;

        try {
            assetInStream = context.getAssets().open("testdata.csv");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }


        try {
            Integer id = 0;

            // String position -> Position
            Map<String, Position> str2position = new HashMap<String, Position>();
            str2position.put("Front Left", Position.FRONT_LEFT);
            str2position.put("Front Center", Position.FRONT_CENTER);
            str2position.put("Front Right", Position.FRONT_RIGHT);
            str2position.put("Back Right", Position.BACK_RIGHT);
            str2position.put("Back Center", Position.BACK_CENTER);
            str2position.put("BACK_LEFT", Position.BACK_LEFT);

            br = new BufferedReader(new InputStreamReader(assetInStream, Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                Log.d(TAG, line);
                String[] row = line.split(",");
                if (row.length==0) {
                    continue;
                }
                if (row[0]=="#Team") {
                    // Read team data
                    List<String> labels = Arrays.asList(br.readLine().split(","));
                    Integer indexId = labels.indexOf("ID");
                    Integer indexName = labels.indexOf("Team Name");
                    Integer indexLocation = labels.indexOf("Location");
                    Integer indexDescription = labels.indexOf("Description");
                    row = br.readLine().split(",");
                    while ((row.length!=0)&&(row[indexId]!="")) {
                        try {
                            id = Integer.parseInt(row[indexId]);
                        } catch (ClassCastException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        Team team = new Team(row[indexName]);
                        team.setId(id);
                        team.setLocation(row[indexLocation]);
                        team.setDescription(row[indexDescription]);
                        teamDao.create(team);

                        // Next row
                        row = br.readLine().split(",");

                    }
                } else if (row[0]=="#Player") {
                    // Read player data

                    List<String> labels = Arrays.asList(br.readLine().split(","));
                    Integer indexId = labels.indexOf("ID");
                    Integer indexFirstName = labels.indexOf("First Name");
                    Integer indexLastName = labels.indexOf("Last Name");
                    Integer indexTeamId = labels.indexOf("Team ID");
                    Integer indexStartingPosition = labels.indexOf("Starting Position");
                    Integer indexHeight = labels.indexOf("Height");
                    Integer indexWeight = labels.indexOf("Weight");
                    Integer indexAge = labels.indexOf("Age");
                    Integer indexDescription = labels.indexOf("Description");

                    row = br.readLine().split(",");
                    while ((row.length!=0)&&(row[indexId]!="")) {
                        try {
                            id = Integer.parseInt(row[indexId]);
                        } catch (ClassCastException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        Player player = new Player(row[indexFirstName], row[indexLastName]);
                        player.setId(Integer.parseInt(row[indexId]));
                        player.setTeam(teamDao.queryForId(Integer.parseInt(row[indexTeamId])));
                        if (str2position.keySet().contains(row[indexStartingPosition])) {
                            player.setStartingPosition(str2position.get(row[indexStartingPosition]));
                        }
                        if (row[indexHeight]!="") {
                            player.setHeight(Float.parseFloat(row[indexHeight]));
                        }
                        if (row[indexWeight]!="") {
                            player.setWeight(Float.parseFloat(row[indexWeight]));
                        }
                        if (row[indexAge]!="") {
                            player.setAge(Integer.parseInt(row[indexAge]));
                        }
                        playerDao.create(player);

                        //Next row
                        row = br.readLine().split(",");

                    }
                } else if (row[0]=="#Match") {
                    // Load match
                    row = br.readLine().split(",");
                    Match match = new Match();
                    List<String> labels = null;

                    while (row.length!=0) {
                        switch (row[0]) {
                            case "ID":
                                match.setId(Integer.parseInt(row[1]));
                            case "Match Name":
                                match.setName(row[1]);
                                break;
                            case "Match Date Time":
                                String[] strdate = row[1].split("/");
                                String[] strtime = row[2].split(";");
                                match.setStartDateTime(
                                        new DateTime(
                                                Integer.parseInt(strdate[0]),
                                                Integer.parseInt(strdate[1]),
                                                Integer.parseInt(strdate[2]),
                                                Integer.parseInt(strtime[0]),
                                                Integer.parseInt(strtime[1]),
                                                0, 0));
                                break;
                            case "Team A ID":
                                match.setTeamA(teamDao.queryForId(Integer.parseInt(row[1])));
                                break;
                            case "Team B iD":
                                match.setTeamB(teamDao.queryForId(Integer.parseInt(row[1])));
                                // Create match on database
                                matchDao.create(match);
                                break;
                            case "#Player Entry":
                                // Create player entry
                                Log.d(TAG, "Start loading player entries");
                                labels = Arrays.asList(
                                        br.readLine().split(",")
                                );
                                Integer indexPlayer = labels.indexOf("Player ID");
                                Integer indexTeam = labels.indexOf("Team");
                                Integer indexNumber = labels.indexOf("Number");
                                Integer indexStartPos = labels.indexOf("Starting Position");

                                row = br.readLine().split(",");
                                while ((row.length!=0)&&(row[0]!="")) {
                                    Player player = playerDao.queryForId(Integer.parseInt(row[indexPlayer]));
                                    Integer number = Integer.parseInt(row[indexNumber]);
                                    Boolean teamflag = null;
                                    switch (row[indexTeam]){
                                        case "A":
                                            teamflag = TEAM_A;
                                            break;
                                        case "B":
                                            teamflag = TEAM_B;
                                            break;
                                        default:
                                            throw new IOException("Should select A or B for team");
                                    }
                                    Position startPos = Position.NONE;
                                    if (row[indexStartPos]!="") {
                                        if (str2position.keySet().contains(row[indexStartPos])) {
                                            startPos = str2position.get(row[indexStartPos]);
                                        } else {
                                            throw new IOException("Unexpected position input: " + row[indexStartPos]);
                                        }
                                    }

                                    // Create player entry
                                    PlayerEntry entry = new PlayerEntry(match, player, number, teamflag, startPos);
                                    playerEntryDao.create(entry);

                                    // Next row
                                    row = br.readLine().split(",");

                                }
                                break;
                            case "#Event":
                                Log.d(TAG, "Start loading match events");
                                labels = Arrays.asList(
                                        br.readLine().split(",")
                                );
                                Integer indexEventType = labels.indexOf("Event Type");
                                Integer indexPlayer2 = labels.indexOf("Player ID");
                                Integer indexPlayType = labels.indexOf("Play Type");
                                Integer indexPlayResult = labels.indexOf("Play Result");
                                Integer indexPlayAttr = labels.indexOf("Play Attribute");
                                Integer indexPlayPosX = labels.indexOf("Play Position X");
                                Integer indexPlayPosY = labels.indexOf("Play Position Y");
                                Integer indexPlayerIn = labels.indexOf("Player IN ID");
                                Integer indexPlayerOut = labels.indexOf("Player OUT ID");

                                // Read first row
                                row = br.readLine().split(",");
                                while ((row.length!=0)&&(row[0]!="")) {
                                    switch (row[indexEventType]) {
                                        case "Play":
                                            Player player = playerDao.queryForId(Integer.parseInt(row[indexPlayer2]));
                                            Play.PlayType playType = Play.PlayType.fromString(row[indexPlayType]);
                                            Play play = new Play(
                                                    match.getOnGoingSet().getOnGoingPoint(),
                                                    player,
                                                    playType);
                                            if (row[indexPlayAttr]=="") {
                                                String attrName = row[indexPlayAttr];
                                                QueryBuilder<PlayAttribute, Integer> qb = playAttributeDao.queryBuilder();
                                                qb.where().eq(PlayAttribute.NAME_FIELD_NAME, attrName);

                                                if (qb.countOf()==0) {
                                                    // Create new PlayAttribute
                                                    PlayAttribute attr = new PlayAttribute(attrName, playType);
                                                    playAttributeDao.create(attr);
                                                    play.setAttribute(attr);
                                                } else {
                                                    play.setAttribute(qb.queryForFirst());
                                                }
                                            }
                                            playDao.create(play);

                                            // New line
                                            row = br.readLine().split(",");

                                            break;
                                        case "MemberChange":
                                            Player playerIn = playerDao.queryForId(Integer.parseInt(row[indexPlayerIn]));
                                            Player playerOut = playerDao.queryForId(Integer.parseInt(row[indexPlayerOut]));
                                            MemberChange memberChange = new MemberChange(match.getOnGoingSet(), playerIn, playerOut);
                                            memberChangeDao.create(memberChange);

                                            //New line
                                            row = br.readLine().split(",");

                                            break;
                                        default:
                                            break;
                                    }
                                }

                                break;
                            default:
                                break;
                        }
                        // Next row
                        row = br.readLine().split(",");
                    }
                    // Create Match on the database
                    matchDao.create(match);



                }

            }

        } catch (FileNotFoundException e) {
            throw new SQLException("file not found");
        } catch (IOException e) {
            e.printStackTrace();
            throw new SQLException("IO exception");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


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

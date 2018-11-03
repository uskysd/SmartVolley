package uskysd.smartvolley;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

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
    private MatchDataManager matchDataManager;



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
        Log.d(TAG, "Clear database");
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
            throw e;
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
            str2position.put("Back Left", Position.BACK_LEFT);
            Match match = null;



            br = new BufferedReader(new InputStreamReader(assetInStream, Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                Log.d(TAG, line);
                String[] row = line.split(",");
                if (row.length == 0) {
                    continue;
                }
                Log.d(TAG, "First item: " + row[0]);
                if (row[0].trim().matches("#Team")) {
                    // Read team data
                    Log.d(TAG, "Team data detected. Start loading...");
                    List<String> labels = Arrays.asList(br.readLine().split(","));
                    Integer indexId = labels.indexOf("ID");
                    Integer indexName = labels.indexOf("Team Name");
                    Integer indexLocation = labels.indexOf("Location");
                    Integer indexDescription = labels.indexOf("Description");

                    Log.d(TAG, "Detecting indexes form label");
                    Log.d(TAG, "ID: " + indexId.toString()
                            + ", Name: " + indexName.toString()
                            + ", Location: " + indexLocation.toString()
                            + ", Description: " + indexDescription.toString());

                    row = br.readLine().split(",");
                    while ((row.length != 0) && (row[indexId] != "")) {
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
                        Log.d(TAG, "Created Team: " + team.toString());

                        // Next row
                        row = br.readLine().split(",");

                    }

                } else if (row[0].trim().matches("#Player")) {
                    // Read player data
                    Log.d(TAG, "Player data detected. Start loading...");

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

                    Log.d(TAG, "Detecting indexes from label");
                    Log.d(TAG, "ID: " + indexId.toString()
                            + ", First Name: " + indexFirstName.toString()
                            + ", Last Name: " + indexLastName.toString()
                            + ", Team ID: " + indexTeamId.toString()
                            + ", Starting Position: " + indexStartingPosition.toString()
                            + ", Height: " + indexHeight.toString()
                            + ", Weight: " + indexWeight.toString()
                            + ", Age: " + indexAge.toString()
                            + ", Description: " + indexDescription.toString());

                    row = br.readLine().split(",");
                    while ((row.length != 0) && (row[indexId] != "")) {
                        /*
                        try {
                            id = Integer.parseInt(row[indexId]);
                        } catch (ClassCastException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        */
                        Player player = new Player(row[indexFirstName], row[indexLastName]);
                        player.setId(Integer.parseInt(row[indexId]));
                        player.setTeam(teamDao.queryForId(Integer.parseInt(row[indexTeamId])));

                        if (str2position.keySet().contains(row[indexStartingPosition])) {
                            player.setStartingPosition(str2position.get(row[indexStartingPosition]));
                        }
                        if (row[indexHeight] != "") {
                            player.setHeight(Float.parseFloat(row[indexHeight]));
                        }
                        if (row[indexWeight] != "") {
                            player.setWeight(Float.parseFloat(row[indexWeight]));
                        }
                        if (row[indexAge] != "") {
                            player.setAge(Integer.parseInt(row[indexAge]));
                        }
                        playerDao.create(player);
                        Log.d(TAG, "Created Player:" + player.toString());

                        //Next row
                        row = br.readLine().split(",");

                    }
                } else if (row[0].trim().matches("#Match")) {
                    // Load match
                    Log.d(TAG, "Match data detected. Start loading...");
                    row = br.readLine().split(",");
                    match = new Match("default");
                    //matchDao.create(match);

                    while (row.length != 0) {
                        Log.d(TAG, "Searching Match Info: " + row[0]);
                        switch (row[0].trim()) {
                            case "ID":
                                Log.d(TAG, "Match ID: " + row[1]);
                                match.setId(Integer.parseInt(row[1]));
                            case "Match Name":
                                Log.d(TAG, "Match Name: " + row[1]);
                                match.setName(row[1]);
                                break;
                            case "Match Date Time":
                                String[] strdate = row[1].split("/");
                                String[] strtime = row[2].split(":");
                                DateTime dt = new DateTime(
                                        Integer.parseInt(strdate[0]),
                                        Integer.parseInt(strdate[1]),
                                        Integer.parseInt(strdate[2]),
                                        Integer.parseInt(strtime[0]),
                                        Integer.parseInt(strtime[1]),
                                        0, 0);
                                Log.d(TAG, "Match Start DateTime: " + dt.toString());
                                match.setStartDateTime(dt);
                                break;
                            case "Team A ID":
                                Log.d(TAG, "Team A ID: " + row[1]);
                                Team teamA = teamDao.queryForId(Integer.parseInt(row[1]));
                                Log.d(TAG, "Team A: "+teamA.toString());
                                match.setTeamA(teamA);
                                break;
                            case "Team B ID":
                                Log.d(TAG, "Team B ID: " + row[1]);
                                Team teamB = teamDao.queryForId(Integer.parseInt(row[1]));
                                Log.d(TAG, "Team B: " + teamB.toString());
                                match.setTeamB(teamB);
                                break;

                            default:
                                break;
                        }
                        // Next row
                        row = br.readLine().split(",");
                    }
                    // Create match on database
                    Log.d(TAG, "Create a new match: "+match.toString());
                    matchDao.create(match);
                    matchDataManager = new MatchDataManager(context, match);
                    Log.d(TAG, "Created match: " + match.toString());
                } else if (row[0].trim().equals("#Player Entry")) {
                    // Create player entry
                    Log.d(TAG, "Start loading player entries");
                    List<String> labels = Arrays.asList(
                            br.readLine().split(",")
                    );
                    Integer indexPlayer = labels.indexOf("Player ID");
                    Integer indexTeam = labels.indexOf("Team");
                    Integer indexNumber = labels.indexOf("Number");
                    Integer indexStartPos = labels.indexOf("Starting Position");

                    Log.d(TAG, "Detecting indexes from label");
                    Log.d(TAG, "Player: "+indexPlayer.toString()
                                    +", Team: "+indexTeam.toString()
                                    +", Number: "+indexNumber.toString()
                                    +", Starting Position: "+indexStartPos.toString());

                    row = br.readLine().split(",");
                    while ((row.length != 0) && (row[0] != "")) {
                        Player player = playerDao.queryForId(Integer.parseInt(row[indexPlayer]));
                        Integer number = Integer.parseInt(row[indexNumber]);
                        Boolean teamflag = null;
                        switch (row[indexTeam]) {
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
                        try {
                            if (row[indexStartPos] != "") {
                                if (str2position.keySet().contains(row[indexStartPos])) {
                                    startPos = str2position.get(row[indexStartPos]);
                                } else {
                                    throw new IOException("Unexpected position input: " + row[indexStartPos]);
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            // Empty starting position
                            // Do nothing
                        }


                        // Create player entry
                        PlayerEntry entry = new PlayerEntry(match, player, number, teamflag, startPos);
                        playerEntryDao.create(entry);
                        Log.d(TAG, "Created Player Entry: " + entry.toString());

                        // Next row
                        row = br.readLine().split(",");

                    }
                } else if (row[0].trim().equals("#Event")) {
                    Log.d(TAG, "Start loading match events");
                    List<String> labels = Arrays.asList(
                            br.readLine().split(",")
                    );
                    Integer indexEventType = labels.indexOf("Event Type");
                    Integer indexPlayer2 = labels.indexOf("Player ID");
                    Integer indexPlayType = labels.indexOf("Play Type");
                    Integer indexPlayResult = labels.indexOf("Play Result");
                    Integer indexPlayEval = labels.indexOf("Play Evaluation");
                    Integer indexPlayAttr = labels.indexOf("Play Attribute");
                    Integer indexPlayPosX = labels.indexOf("Play Position X");
                    Integer indexPlayPosY = labels.indexOf("Play Position Y");
                    Integer indexPlayerIn = labels.indexOf("Player IN ID");
                    Integer indexPlayerOut = labels.indexOf("Player OUT ID");

                    // Read first row
                    row = br.readLine().split(",");
                    while ((row.length != 0) && (row[0] != "")) {
                        switch (row[indexEventType].trim()) {
                            case "Play":
                                matchDataManager.setPlayer(
                                        playerDao.queryForId(Integer.parseInt(row[indexPlayer2])));
                                Play.PlayType playType = Play.PlayType.fromString(row[indexPlayType]);
                                matchDataManager.setPlayType(playType);
                                matchDataManager.setPlayResult(
                                        Play.PlayResult.fromString(row[indexPlayResult]));

                                if ((row.length>indexPlayEval)&&(row[indexPlayEval].trim()!="")) {
                                    matchDataManager.setPlayEvaluation(
                                            Play.PlayEvaluation.fromString(row[indexPlayEval]));
                                }

                                if ((row.length>indexPlayAttr)&&(row[indexPlayAttr].trim()!="")) {
                                    PlayAttribute attr = matchDataManager
                                            .findPlayAttribute(playType, row[indexPlayAttr].trim());
                                    matchDataManager.setPlayAttribute(attr);
                                }

                                matchDataManager.createPlay();
                                /*
                                Point point = null;
                                Set set = match.getOnGoingSet();
                                if (set==null) {
                                    set = new Set(match);
                                    setDao.create(set);
                                    point = new Point(set);
                                    pointDao.create(point);
                                } else {
                                    point = set.getOnGoingPoint();
                                    if (point==null) {
                                        point = new Point(set);
                                        pointDao.create(point);
                                    }
                                }

                                Play play = new Play(point, player, playType);


                                //boolean teamflag = match.getPlayerEntryTeamFlag(player);

                                if ((row.length>indexPlayAttr)&&(row[indexPlayAttr] == "")) {
                                    String attrName = row[indexPlayAttr];
                                    QueryBuilder<PlayAttribute, Integer> qb = playAttributeDao.queryBuilder();
                                    qb.where().eq(PlayAttribute.NAME_FIELD_NAME, attrName);

                                    if (qb.countOf() == 0) {
                                        // Create new PlayAttribute
                                        PlayAttribute attr = new PlayAttribute(attrName, playType);
                                        playAttributeDao.create(attr);
                                        play.setAttribute(attr);
                                    } else {
                                        play.setAttribute(qb.queryForFirst());
                                    }
                                }
                                playDao.create(play);
                                */
                                //Log.d(TAG, "Created Play: "+play.toString());

                                // New line
                                row = br.readLine().split(",");

                                break;
                            case "Member Change":
                                Player playerIn = playerDao.queryForId(Integer.parseInt(row[indexPlayerIn]));
                                Player playerOut = playerDao.queryForId(Integer.parseInt(row[indexPlayerOut]));
                                matchDataManager.createMemberChange(playerIn, playerOut);
                                /*
                                MemberChange memberChange = new MemberChange(match.getOnGoingSet(), playerIn, playerOut);
                                memberChangeDao.create(memberChange);
                                Log.d(TAG, "Created MemberChange: "+memberChange.toString());
                                */
                                //New line
                                row = br.readLine().split(",");

                                break;
                            default:
                                break;
                        }
                    }
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
        Set set1 = new Set(match, 1);
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

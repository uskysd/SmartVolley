package uskysd.smartvolley.data;

import com.j256.ormlite.dao.Dao;

import uskysd.smartvolley.OrmLiteAndroidTestCase;
import uskysd.smartvolley.Position;

public class MemberChangeTest extends OrmLiteAndroidTestCase {

    Dao<Team, Integer> teamDao;
    Dao<Player, Integer> playerDao;
    Dao<Match, Integer> matchDao;
    Dao<PlayerEntry, Integer> playerEntryDao;
    Dao<Set, Integer> setDao;
    Dao<Point, Integer> pointDao;
    Dao<Play, Integer> playDao;
    Dao<MemberChange, Integer> memberChangeDao;
    private Team teamA;
    private Team teamB;
    private Player playerA1;
    private Player playerA2;
    private Player playerB1;
    private Player playerB2;
    private Match match;
    private Set set;
    private Point point;



    public void setUp() throws Exception {
        DatabaseHelper helper = getDatabaseHelper(getContext());
        teamDao = helper.getTeamDao();
        playerDao = helper.getPlayerDao();
        matchDao = helper.getMatchDao();
        setDao = helper.getSetDao();
        pointDao = helper.getPointDao();
        playDao = helper.getPlayDao();
        playerEntryDao = helper.getPlayerEntryDao();
        memberChangeDao = helper.getMemberChangeDao();

        teamA = new Team("Team A");
        teamB = new Team("Team B");
        teamDao.create(teamA);
        teamDao.create(teamB);

        playerA1 = new Player("Yusuke", "Yoshida");
        playerA2 = new Player("Nobunaga", "Oda");
        playerB1 = new Player("Toyotomi", "Hideyoshi");
        playerB2 = new Player("Ieyasu", "Tokugawa");
        playerA1.setTeam(teamA);
        playerA2.setTeam(teamA);
        playerB1.setTeam(teamB);
        playerB2.setTeam(teamB);
        playerDao.create(playerA1);
        playerDao.create(playerA2);
        playerDao.create(playerB1);
        playerDao.create(playerB2);

        match = new Match("Test Match", teamA, teamB);
        matchDao.create(match);

        // Player Entries
        PlayerEntry pe;
        pe = new PlayerEntry(match, playerA1, 1, PlayerEntry.TEAM_A, Position.BACK_CENTER);
        playerEntryDao.create(pe);
        pe = new PlayerEntry(match, playerA2, 2, PlayerEntry.TEAM_A, Position.FRONT_CENTER);
        playerEntryDao.create(pe);
        pe = new PlayerEntry(match, playerB1, 3, PlayerEntry.TEAM_B, Position.FRONT_CENTER);
        playerEntryDao.create(pe);
        pe = new PlayerEntry(match, playerB2, 4, PlayerEntry.TEAM_B, Position.BACK_LEFT);
        playerEntryDao.create(pe);

        set = new Set(match, 1);
        setDao.create(set);
        matchDao.refresh(match);


    }

    public void testConstructor() throws Exception {

        // Setup
        setUp();

        // Exercise
        MemberChange sut = new MemberChange(set, playerA1, playerA2);
        sut.setEventOrder(1);
        setDao.refresh(set);

        // Verify
        assertEquals(set, sut.getSet());
        assertEquals(playerA1, sut.getPlayerIn());
        assertEquals(playerA2, sut.getPlayerOut());
        assertEquals(1, sut.getEventOrder());


        // TearDown
        tearDown();


    }

    public void testAddingIntoDatabase() throws Exception {

        // Setup
        setUp();

        // Exercise
        MemberChange sut = new MemberChange(set, playerA1, playerA2);
        sut.setEventOrder(2);
        memberChangeDao.create(sut);
        MemberChange queried = memberChangeDao.queryForAll().get(0);


        // Verify
        assertEquals(sut.getId(), queried.getId());
        assertEquals(playerA1.getId(), queried.getPlayerIn().getId());
        assertEquals(playerA2.getId(), queried.getPlayerOut().getId());
        assertEquals(2, queried.getEventOrder());

        // TearDown
        tearDown();
    }

    public void testEventOrder() throws Exception {
        // Setup
        setUp();

        // Exercise
        MemberChange mc1 = new MemberChange(set, playerA1, playerA2);
        memberChangeDao.create(mc1);
        setDao.refresh(set);
        MemberChange mc2 = new MemberChange(set, playerB1, playerB2);
        memberChangeDao.create(mc2);
        setDao.refresh(set);
        assertEquals(2, set.getMemberChanges().size());

        Point point = set.getOnGoingPoint();
        if (point==null) {
            point = new Point(set);
            pointDao.create(point);
        }

        setDao.refresh(set);
        Play p1 = new Play(point, playerA2, Play.PlayType.SERVICE);
        playDao.create(p1);
        pointDao.refresh(point);
        MemberChange mc3 = new MemberChange(set, playerB2, playerB1);
        set.getMemberChanges().add(mc3);
        Play p2 = new Play(point, playerB1, Play.PlayType.ATTACK);
        playDao.create(p2);
        pointDao.refresh(point);
        matchDao.refresh(match);

        /*
        for (Set s: match.getSets()) {
            setDao.refresh(s);
            for (MemberChange mc: s.getMemberChanges()) {
                memberChangeDao.refresh(mc);
            }
            for (Point p: s.getPoints()) {
                pointDao.refresh(p);
                for (Play play: p.getPlays()) {
                    pointDao.refresh(p);
                }
            }
        }
        */


        // Verify
        assertEquals(5, match.getAllEvents().size());
        assertEquals(1, mc1.getEventOrder());
        assertEquals(2, mc2.getEventOrder());
        assertEquals(3, p1.getEventOrder());
        assertEquals(4, mc3.getEventOrder());
        assertEquals(5, p2.getEventOrder());

        // TearDown
        tearDown();
    }





}

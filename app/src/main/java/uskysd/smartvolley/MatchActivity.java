package uskysd.smartvolley;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Event;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.PlayerEntry;
import uskysd.smartvolley.data.Set;
import uskysd.smartvolley.data.Team;
import uskysd.smartvolley.graphics.PlayerToken;
import uskysd.smartvolley.scores.PlayScore;
import uskysd.smartvolley.scores.ReceptionScore;
import uskysd.smartvolley.scores.ServiceEffectiveness;

//import uskysd.smartvolley.data.Point;

public class MatchActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
	private static final String TAG = MatchActivity.class.getSimpleName();
    private static final String MATCH_ID = "match_id";
    private static final String INSTANCE_STATE_KEY = "MATCH";
    private static MatchView matchView;
    private ListView mListView;
    private Match match;
    private InputListener mInputListener;
    private MatchDataManager dataManager;
    private DataAnalyzer analyzer;
    private PlayScore playScore;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//matchView = new MatchView(this);

		//setContentView(matchView);
        setContentView(R.layout.activity_match);
        matchView = (MatchView) findViewById(R.id.match_view);
        mListView = (ListView) findViewById(R.id.event_list_view);
        mInputListener = new NormalModeInputListener();
        matchView.setInputListener(mInputListener);

        // Data Analyzer
        analyzer = new DataAnalyzer(getBaseContext());
        playScore = new ServiceEffectiveness();

        reInit(savedInstanceState);
        Log.d(TAG, "View added");



	}

    @Override
    protected void onResume() {
        super.onResume();

        try {
            listEvents();
        } catch (SQLException e) {
            Log.d(TAG, e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.match, menu);
		return true;
	}
	
	public static void callMe(Context context) {
		context.startActivity(new Intent(context, MatchActivity.class));
	}

    public static void callMe(Context context, int matchId) {
        Intent intent = new Intent(context, MatchActivity.class);
        intent.putExtra(MATCH_ID, matchId);
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INSTANCE_STATE_KEY, match);
    }

    public int getMatchIdFromIntent() {
        return getIntent().getIntExtra(MATCH_ID, -1);
    }

    public void reInit(Bundle savedInstanceState) {

        // Make MatchView transparent
        /*
        matchView.setZOrderOnTop(true);
        SurfaceHolder holder = matchView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        */

	    try {
            Dao<Match, Integer> dao = getHelper().getMatchDao();

            if (savedInstanceState != null) {
                //Restore match from saved instance state
                this.match = (Match) savedInstanceState.get(INSTANCE_STATE_KEY);
                loadMatchInfo();
            } else {
                int matchId = getMatchIdFromIntent();
                if (matchId > 0) {
                    this.match = dao.queryForId(matchId);
                    if (this.match!=null) {
                        this.dataManager = new MatchDataManager(this, this.match);
                        loadMatchInfo();
                    }
                } else {
                    //throw new RuntimeException("Match data not found");
                    // Generate dummy data set for testing

                    Log.d(TAG, "Loading dummy data");
                    TestDataGenerator dgen = new TestDataGenerator(this);
                    //dgen.createTestDataCase001();
                    dgen.loadTestDataFromCsv();

                    //createNewMatch();
                    DatabaseHelper helper = getHelper();
                    Dao<Match, Integer> matchDao = helper.getMatchDao();
                    Dao<Team, Integer> teamDao = helper.getTeamDao();
                    for (Match m: matchDao.queryForAll()) {

                        Log.d(TAG, "Match Found: " + m.toString());
                        if (m!=null) {
                            Team teamA = m.getTeamA();
                            Team teamB = m.getTeamB();
                            m.setTeamA(teamDao.queryForId(teamA.getId()));
                            m.setTeamB(teamDao.queryForId(teamB.getId()));
                            Log.d(TAG, "Restore teams: "+m.toString());

                        }
                    }
                    this.match = matchDao.queryForAll().get(0);
                    matchDao.refresh(this.match);
                    Log.d(TAG, "Check player entries");
                    Dao<PlayerEntry, Integer> playerEntryDao = helper.getPlayerEntryDao();
                    this.dataManager = new MatchDataManager(this, this.match);
                    loadMatchInfo();

                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
	        throw new RuntimeException(e);
        }

    }

    public void createNewMatch() throws SQLException {
	    // Create dummy match data for experiments
        Log.d(TAG, "Creating new dummy match data for testing");

        getHelper().clearTables();

        Dao<Team, Integer> teamDao = getHelper().getTeamDao();
        Dao<Player, Integer> playerDao = getHelper().getPlayerDao();
        Dao<PlayerEntry, Integer> playerEntryDao = getHelper().getPlayerEntryDao();
        Dao<Play, Integer> playDao = getHelper().getPlayDao();
        Dao<Match, Integer> matchDao = getHelper().getMatchDao();
        Dao<Set, Integer> setDao = getHelper().getSetDao();
        Dao<uskysd.smartvolley.data.Point, Integer> pointDao = getHelper().getPointDao();

        Log.d(TAG, "Start creating teams");
        Team teamA = new Team("Team A");
	    Team teamB = new Team("Team B");
	    teamDao.create(teamA);
	    teamDao.create(teamB);

        Log.d(TAG, "Start creating players");
        //List<Position> positions = new ArrayList<Position>();
        List<Position> positions = Arrays.asList(
                Position.BACK_CENTER,
                Position.BACK_LEFT,
                Position.BACK_RIGHT,
                Position.FRONT_CENTER,
                Position.FRONT_LEFT,
                Position.FRONT_RIGHT,
                Position.LIBERO);
        List<Player> playersA = new ArrayList<Player>();
        List<Player> playersB = new ArrayList<Player>();


	    for (int i=0; i<positions.size(); i++) {
	        Player playerA = new Player("Dummy Player A", "No. "+Integer.toString(i),
                    positions.get(i));
	        playerA.setTeam(teamA);
	        playerA.setUniformNumber(i);

	        Player playerB = new Player("Dummy Player B", "No. "+ Integer.toString(i),
                    positions.get(i));
	        playerB.setTeam(teamB);
	        playerB.setUniformNumber(i);

	        playerDao.create(playerA);
	        playerDao.create(playerB);
	        playersA.add(playerA);
	        playersB.add(playerB);
        }

        Log.d(TAG, "Start creating match");

        Match newmatch = new Match("Test Match", teamA, teamB);
	    matchDao.create(newmatch);

        Log.d(TAG, "Start creating player entries");
	    for (Player p: playersA) {
	        PlayerEntry entry = new PlayerEntry(newmatch, p, p.getUniformNumber(),
                    PlayerEntry.TEAM_A, p.getStartingPosition());
	        playerEntryDao.create(entry);
        }

        for (Player p: playersB) {
	        PlayerEntry entry = new PlayerEntry(newmatch, p, p.getUniformNumber(),
                    PlayerEntry.TEAM_B, p.getStartingPosition());
	        playerEntryDao.create(entry);
        }

        Log.d(TAG, "Start creating set, point and plays");

        Set set1 = new Set(newmatch, 1);
        setDao.create(set1);

        uskysd.smartvolley.data.Point point = new uskysd.smartvolley.data.Point(set1);
        pointDao.create(point);

        Play p1 = new Play(point, playersA.get(0), Play.PlayType.SERVICE);
	    Play p2 = new Play(point, playersB.get(0), Play.PlayType.RECEPTION);
	    playDao.create(p1);
	    playDao.create(p2);


    }

    public void loadMatchInfo() throws SQLException {
        //TODO load match info to the match view
        Log.d(TAG, "Loading match info: "+match.toString());

        //TODO need to be changed to load current positions
        matchView.loadStartingPositions(match);

        // Set team names
        Dao<Team, Integer> teamDao = getHelper().getTeamDao();
        Team teamA = match.getTeamA();
        Team teamB = match.getTeamB();
        teamDao.refresh(teamA);
        teamDao.refresh(teamB);

        matchView.setTeamA("A: "+teamA.getName());
        matchView.setTeamB("B: "+teamB.getName());

        // Update Scoreboard
        updateScore();

        // load events
        listEvents();

        // Show player score
        showPlayerScore(playScore);

        //Define view size requirements

        int width = 0;
        int height = 0;
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size); //Only for API LEVEL >= 13
            width = (int) (size.x * 0.75);
            height = size.y;
        } else {
            width = (int) (display.getWidth()*0.75); //deprecated
            height = (int) (display.getHeight()*0.75); //deprecated
        }
        matchView.measure(width, height);


    }



	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		super.onStop();
	}

    public void listEvents() throws SQLException {
	    Log.i(TAG, "Show event list");
        /*
	    Dao<Play, Integer> playDao = getHelper().getPlayDao();
	    Dao<Player, Integer> playerDao = getHelper().getPlayerDao();
	    Log.d(TAG, "Play DAO is called");
	    List<Event> list = new ArrayList<Event>();

        for (Play p: playDao.queryForAll()) {
            // Restore player data. Foreign object only has id by default.
            if (p.getPlayer()!=null) {
                p.setPlayer(playerDao.queryForId(p.getPlayer().getId()));
                list.add(p);
            } else {
                Log.d(TAG, "Emtpy Play object is detected.");
                playDao.delete(p);
            }

        }
        */

	    List<Event> list = dataManager.getEvents();
        ArrayAdapter<Event> arrayAdapter = new EventAdapter(this, R.layout.event_row, list);
        mListView.setAdapter(arrayAdapter);
        // Scroll to the bottom
        mListView.setSelection(arrayAdapter.getCount()-1);
    }

    public void listPlayTypes() {
	    Log.i(TAG, "Show play types in the list");

	    //List<Play.PlayType> playTypes = new ArrayList<Play.PlayType>();
        List<String> playTypes = Arrays.asList("Service", "Reception", "Toss", "Attack", "Block", "Receive");
	    ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                                                android.R.layout.simple_list_item_1,
                                                playTypes);
	    mListView.setAdapter(adapter);

	    // Set listener for the play type list
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                //dataManager.setPlayType(Play.PlayType.SERVICE); // default
                switch(value) {
                    case "Service":
                        // Service
                        dataManager.setPlayType(Play.PlayType.SERVICE);
                        break;
                    case "Reception":
                        // Reception
                        dataManager.setPlayType(Play.PlayType.RECEPTION);
                        break;
                    case "Receive":
                        // Receive
                        dataManager.setPlayType(Play.PlayType.RECEIVE);
                        break;
                    case "Attack":
                        //Attack
                        dataManager.setPlayType(Play.PlayType.ATTACK);
                        break;
                    case "Block":
                        //Block
                        dataManager.setPlayType(Play.PlayType.BLOCK);
                        break;
                    case "Toss":
                        //Toss
                        dataManager.setPlayType(Play.PlayType.TOSS);
                        break;
                    default:
                        Log.d(TAG, "Unexpected play type: "+value);
                        dataManager.setPlayType(Play.PlayType.SERVICE);
                }
                try {
                    // Set previous play result "Continue"
                    Play lastPlay = dataManager.getLastPlay();
                    if (lastPlay!=null) {
                        lastPlay.setPlayResult(Play.PlayResult.CONTINUE);
                    }

                    // List play results to let user select
                    listPlayResults();

                    // Create new play
                    //dataManager.createPlay();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                /*
                try {
                    listEvents();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                */

            }
            });

    }

    public void listPlayResults() {
	    Log.d(TAG, "List candidates for play results");
	    List<Play.PlayResult> results = Arrays.asList(Play.PlayResult.values());
	    ArrayAdapter<Play.PlayResult> adapter = new ArrayAdapter<Play.PlayResult>(this,
                android.R.layout.simple_list_item_1,
                results);
	    mListView.setAdapter(adapter);

	    // Set listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Play.PlayResult value = (Play.PlayResult) adapterView.getItemAtPosition(i);
                dataManager.setPlayResult(value);

                // Create play
                try {
                    dataManager.createPlay();

                    // Update score
                    updateScore();
                    showPlayerScore(playScore);

                    // List events again
                    listEvents();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    public void listPointScenarios(final boolean teamflag) {
	    // Show list of point scenarios for the current point
        final boolean winner = teamflag;
        final List<MatchDataManager.PointScenario> scenarios =
                dataManager.getPointScenarioCandidates(teamflag);
        List<String> strScenarios = new ArrayList<String>();
        for (MatchDataManager.PointScenario s: scenarios) {
            strScenarios.add(s.toString());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        strScenarios);
        mListView.setAdapter(adapter);

        // Set listener for the point scenarios
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MatchDataManager.PointScenario selected = scenarios.get(i);
                dataManager.setPlayResultsFromScenario(winner, selected);

                try {
                    dataManager.setPointWinner(winner);
                    updateScore();
                    updateRotation();
                    listEvents();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }



    public void updateScore() {

        matchView.setLeftSetCount(dataManager.getSetCount(MatchDataManager.TEAM_A));
        matchView.setLeftPointCount(dataManager.getPointCount(MatchDataManager.TEAM_A));
        matchView.setRightSetCount(dataManager.getSetCount(MatchDataManager.TEAM_B));
        matchView.setRightPointCount(dataManager.getPointCount(MatchDataManager.TEAM_B));


    }

    public void addPointToTeamA() throws SQLException {
	    // Add point to Team A (Left)

        //let user input how the team won the point

        //listPointScenarios(MatchDataManager.TEAM_A);
        dataManager.setPointWinner(MatchDataManager.TEAM_A);
        updateScore();

    }

    public void addPointToTeamB() throws SQLException {
	    // Add pint to Team B (Right)

        //let user input how the team won the point
        //listPointScenarios(MatchDataManager.TEAM_B);
        dataManager.setPointWinner(MatchDataManager.TEAM_B);
        updateScore();

    }

    public void updateRotation() throws SQLException {
	    Log.d(TAG, "Updating rotation");
	    int[] rotations = dataManager.getRotaions();
	    matchView.setLeftRotation(rotations[0]);
	    matchView.setRightRotation(rotations[1]);
    }

    public void rotateTeamA() {
        matchView.rotateLeft();

    }

    public void rotateTeamB() {
	    matchView.rotateRight();
    }



    public void createPlayer() throws SQLException {
        // Just for debugging
	    Player player = new Player("Yusuke", "Yoshida");
	    getHelper().getPlayerDao().create(player);


        Log.d(TAG, "showing players");
	    for (Player p: getHelper().getPlayerDao().queryForAll()) {
	        Log.d(TAG, p.toString());
        }
    }

    public void showPlayerScore(PlayScore playScore) throws SQLException {
	    //TODO Show play score for each player
        Dao<Player, Integer> playerDao = getHelper().getPlayerDao();
        PlayScore serviceScore = new ServiceEffectiveness();
        PlayScore receptionScore = new ReceptionScore();

        for (PlayerToken token: matchView.getPlayerTokens()) {

            // Clear comments
            token.clearComments();

            Player player = playerDao.queryForId(token.getPlayerId());
            // Service Score
            HashMap<String, String> score =
                    analyzer.getPlayerScore(serviceScore, player,
                            DataAnalyzer.TargetDataKey.MATCH_ID, match.getId());
            token.addComment("SRV:"+score.get(PlayScore.SUMMARY), Color.RED);
            // Reception Score
            score = analyzer.getPlayerScore(receptionScore, player,
                    DataAnalyzer.TargetDataKey.MATCH_ID, match.getId());
            token.addComment("RCP:"+score.get(PlayScore.SUMMARY), Color.BLUE);
        }


    }



    public class NormalModeInputListener extends InputListener {

        public NormalModeInputListener() {
            super();
        }

        @Override
        public void onPlayerTouched(int playerId, int x, int y) {
            Log.d(TAG, "onPlayerTouched called, player id: "+ Integer.toString(playerId));
            super.onPlayerTouched(playerId, x, y);
            if (playerId>0) {
                try {

                    Player player = getHelper().getPlayerDao().queryForId(playerId);
                    Log.d(TAG, "Player selected: "+player.toString());
                    dataManager.setPlayer(player);

                } catch (SQLException e) {
                    Log.d(TAG, "Failed to set player for data input");
                    throw new RuntimeException(e);
                }
                listPlayTypes();
            } else {
                Log.d(TAG, "Invalid player ID");
            }


        }

        @Override
        public void onScoreBoardLeftTouched(int x, int y) {
            Log.d(TAG, "Detected scoreboard left touched");
            super.onScoreBoardLeftTouched(x, y);

            try {
                addPointToTeamA();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }


        @Override
        public void onScoreBoardRightTouched(int x, int y) {
            Log.d(TAG, "Detected scoreboard right touched");
            super.onScoreBoardRightTouched(x, y);
            try {
                addPointToTeamB();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onRotationBoardLeftTouched(int x, int y) {
            super.onRotationBoardLeftTouched(x, y);
            // Rotate team A
            rotateTeamA();
        }

        @Override
        public void onRotationBoardRightTouched(int x, int y) {
            super.onRotationBoardRightTouched(x, y);
            // Rotate team B
            rotateTeamB();
        }

        @Override
        public void onLeftSideBackLeftSwipedToSeviceArea(int playerId, int x, int y) {
            super.onLeftSideBackLeftSwipedToSeviceArea(playerId, x, y);

//            startService(playerId, x, y);

        }

        @Override
        public void onRightSideBackLeftSwipeToServiceArea(int playerId, int x, int y) {
            super.onRightSideBackLeftSwipeToServiceArea(playerId, x, y);
//            startService(playerId, x, y);
        }

        @Override
        public void onAnalysisButtonTouched(int x, int y) {
            super.onAnalysisButtonTouched(x, y);

            matchView.deactivate();

            //MatchAnalysisActivity.callMe(getBaseContext());
            StartMenuActivity.callMe(getBaseContext());
        }
    }

    public class PlayTargetInputListenener extends InputListener {

        public PlayTargetInputListenener() {
            super();
        }

//        onPlayerSwiped
    }


    private class EventAdapter extends ArrayAdapter<Event> {

	    public EventAdapter(Context context, int textViewResourceId, List<Event> items) {
	        super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v==null) {
	            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.event_row, null);
	        }
	        Event event = getItem(position);

	        // Color settings
            switch (event.getEventType()) {
                case PLAY:
                    v.setBackgroundColor(Color.GRAY);
                    Play play = (Play) event;
                    String strteam = (play.getTeamFlag()==Match.TEAM_A)? "A":"B";
                    PlayerEntry pe = match.getPlayerEntry(play.getPlayer());
                    String num = (pe!=null)? Integer.toString(pe.getNumber()):"--";
                    fillText(v, R.id.event_row_title,
                            strteam+"#"+num+": "+play.getPlayType().toString()
                                    +">"+play.getPlayResult().toString());
                    break;
                case MEMBERCHANGE:
                    v.setBackgroundColor(Color.YELLOW);
                    fillText(v, R.id.event_row_title, event.getEventTitle());
                    break;
                    default:
                        break;
            }
	        return v;
        }

        public void fillText(View v, int id, String text) {
	        TextView textView = (TextView) v.findViewById(id);
	        textView.setText(text==null ? "": text);
        }
    }




}

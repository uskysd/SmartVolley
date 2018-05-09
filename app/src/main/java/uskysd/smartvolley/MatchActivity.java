package uskysd.smartvolley;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Event;
import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Play;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.PlayerEntry;
import uskysd.smartvolley.data.Set;
import uskysd.smartvolley.data.Team;

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
        try {
            Dao<Match, Integer> dao = getHelper().getMatchDao();

            // Make MatchView transparent
            matchView.setZOrderOnTop(true);
            SurfaceHolder holder = matchView.getHolder();
            holder.setFormat(PixelFormat.TRANSPARENT);

            if (savedInstanceState != null) {
                //Restore match from saved instance state
                this.match = (Match) savedInstanceState.get(INSTANCE_STATE_KEY);
                loadMatchInfo();
            } else {
                int matchId = getMatchIdFromIntent();
                if (matchId > 0) {
                    this.match = dao.queryForId(matchId);
                    if (this.match!=null) {

                        loadMatchInfo();
                    }
                } else {
                    //throw new RuntimeException("Match data not found");
                    // Generate dummy data set for testing


                    //TestDataGenerator dgen = new TestDataGenerator(this);
                    //dgen.createTestDataCase001();
                    createNewMatch();
                    DatabaseHelper helper = getHelper();
                    Dao<Match, Integer> matchDao = helper.getMatchDao();
                    match = matchDao.queryForAll().get(0);
                    loadMatchInfo();

                }

            }
        } catch (SQLException e) {
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

        Set set1 = new Set(newmatch);
        setDao.create(set1);

        uskysd.smartvolley.data.Point point = new uskysd.smartvolley.data.Point(set1);
        pointDao.create(point);

        Play p1 = new Play(point, playersA.get(0), Play.PlayType.SERVICE);
	    Play p2 = new Play(point, playersB.get(0), Play.PlayType.RECEPTION);
	    playDao.create(p1);
	    playDao.create(p2);


    }

    public void loadMatchInfo() {
        //TODO load match info to the match view

        this.dataManager = new MatchDataManager(this, this.match);
        matchView.loadMatchInfo(match);

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

	    //List<Event> list = dataManager.getEvents();
        ArrayAdapter<Event> arrayAdapter = new EventAdapter(this, R.layout.event_row, list);
        mListView.setAdapter(arrayAdapter);
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
                    dataManager.createPlay();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    listEvents();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
            });

    }

    public void createPlay(Player player) throws SQLException {
        Log.d(TAG, "createPlay called");
	    dataManager.setPlayer(player);
	    dataManager.createPlay();


        /* Just for debugging
        Dao<Play, Integer> playDao = getHelper().getPlayDao();

        Log.d(TAG, "Showing all Plays in the database: before creating new play");
        for (Play p: playDao.queryForAll()) {
            Log.d(TAG, p.toString());
        }

        Play newplay = new Play(match.getOnGoingSet().getOnGoingPoint(), player, Play.PlayType.ATTACK);

	    playDao.create(newplay);


	    Log.d(TAG, "Showing all Plays in the database after creating new play");
	    for (Play p: playDao.queryForAll()) {
	        Log.d(TAG, p.toString());
        }
        */
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
        public void onLeftSideBackLeftSwipedToSeviceArea(int playerId, int x, int y) {
            super.onLeftSideBackLeftSwipedToSeviceArea(playerId, x, y);

//            startService(playerId, x, y);

        }

        @Override
        public void onRightSideBackLeftSwipeToServiceArea(int playerId, int x, int y) {
            super.onRightSideBackLeftSwipeToServiceArea(playerId, x, y);
//            startService(playerId, x, y);
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
	        fillText(v, R.id.event_row_title, event.getEventTitle());
	        return v;
        }

        public void fillText(View v, int id, String text) {
	        TextView textView = (TextView) v.findViewById(id);
	        textView.setText(text==null ? "": text);
        }
    }




}

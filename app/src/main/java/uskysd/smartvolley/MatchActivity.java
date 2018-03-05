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
import uskysd.smartvolley.data.Set;

public class MatchActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
	private static final String TAG = MatchActivity.class.getSimpleName();
    private static final String MATCH_ID = "match_id";
    private static final String INSTANCE_STATE_KEY = "MATCH";
    private static MatchView matchView;
    private ListView mListView;
    private Match match;
    private MatchDataManager manager;
    private InputListener mInputListener;



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
                    TestDataGenerator dgen = new TestDataGenerator(getBaseContext());
                    dgen.createTestDataCase001();
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

    public void loadMatchInfo() {
        //TODO load match info to the match view

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

    public static class MatchDataManager {

        public static enum Mode {TEAM_A_SERVICE, TEAM_B_SERVICE, TEAM_A_RALLY, TEAM_B_RALLY}
        private static Mode mode;
        private Match match;

        public MatchDataManager(Match match) {
            this.match = match;
            this.mode = Mode.TEAM_A_RALLY;
        }






    }

    public void listEvents() throws SQLException {
	    Log.i(TAG, "Show event list");
	    Dao<Play, Integer> playDao = getHelper().getPlayDao();
	    Dao<Player, Integer> playerDao = getHelper().getPlayerDao();
	    Log.d(TAG, "Play DAO is called");
	    List<Event> list = new ArrayList<Event>();
        for (Play p: playDao.queryForAll()) {
            // Restore player data. Foreign object only has id by default.
            p.setPlayer(playerDao.queryForId(p.getPlayer().getId()));
            list.add(p);
        }
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
                switch(value) {
                    case "Service":
                        // Service
                    }
                }
            });

    }

    private class MatchDataInputManager {

	    private Match match;
	    private Set set;
	    private Point point;
	    private Player player;
	    private Play.PlayType playType;
	    private Play.PlayEvaluation playEvaluation;

	    public MatchDataInputManager(Match match) {
	        this.match = match;


        }


    }

    public class NormalModeInputListener extends InputListener {

        public NormalModeInputListener() {
            super();
        }

        @Override
        public void onPlayerTouched(int playerId, int x, int y) {
            Log.d(TAG, "onPlayerTouched called");
            super.onPlayerTouched(playerId, x, y);
            listPlayTypes();

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

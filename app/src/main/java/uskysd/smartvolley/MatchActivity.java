package uskysd.smartvolley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import uskysd.smartvolley.data.DatabaseHelper;
import uskysd.smartvolley.data.Match;

public class MatchActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
	private static final String TAG = MatchActivity.class.getSimpleName();
    private static final String MATCH_ID = "match_id";
    private static final String INSTANCE_STATE_KEY = "MATCH";
    MatchView matchView;
    private Match match;
    private MatchDataManager manager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        matchView = new MatchView(this);
        reInit(savedInstanceState);
		setContentView(matchView);
		Log.d(TAG, "View added");
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
                    throw new RuntimeException("Match data not found");
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadMatchInfo() {
        //TODO load match info to the match view
        matchView.loadMatchInfo(match);
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

    public static class NormalModeInputListener extends MatchView.InputListener {

        public NormalModeInputListener() {
            super();
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

    public static class PlayTargetInputListenener extends MatchView.InputListener {

        public PlayTargetInputListenener() {
            super();
        }

//        onPlayerSwiped
    }



}

package uskysd.smartvolley.data;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@DatabaseTable(tableName="matches")
public class Match implements Serializable, Comparable<Match> {

    /**
     *
     */
    private static final long serialVersionUID = 403945017757332672L;
    private static final Boolean TEAM_A = true;
    private static final Boolean TEAM_B = false;


    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String name;

    @DatabaseField
    private DateTime startDateTime;

    @DatabaseField
    private String location;

    @DatabaseField
    private Boolean teamWonFlag;

    @DatabaseField(foreign = true)
    private Team teamA;

    @DatabaseField(foreign = true)
    private Team teamB;

    @ForeignCollectionField
    Collection<PlayerEntry> playerEntries;

    @ForeignCollectionField(eager = true)
    Collection<Set> sets;


    public Match() {
        //needed by ormlite
    }

    public Match(String name) {
        this.name = name;
        this.startDateTime = DateTime.now();
        //initCollections();
    }

    public Match(String name, Team teamA, Team teamB) {
        this.name = name;
        this.setTeamA(teamA);
        this.setTeamB(teamB);
        this.startDateTime = DateTime.now();
        //initCollections();
    }

    public Match(String name, Team teamA, Team teamB, DateTime dateTime) {
        this.name = name;
        this.setTeamA(teamA);
        this.setTeamB(teamB);
        this.startDateTime = dateTime;
        //initCollections();
    }

    private void initCollections() {
        if (this.sets == null) {
            this.sets = new ArrayList<Set>();
        }
        if (this.playerEntries == null) {
            this.playerEntries = new ArrayList<PlayerEntry>();
        }

    }


    public Collection<PlayerEntry> getPlayerEntries() {
        return playerEntries;
    }

    public Collection<PlayerEntry> getPlayerEntriesToTeamA() {
        Collection<PlayerEntry> result = new ArrayList<PlayerEntry>();
        for (PlayerEntry entry : playerEntries) {
            if (entry.isForTeamA()) {
                result.add(entry);
            }
        }
        return result;
    }

    public Collection<PlayerEntry> getPlayerEntriesToTeamB() {
        Collection<PlayerEntry> result = new ArrayList<PlayerEntry>();
        for (PlayerEntry entry : playerEntries) {
            if (entry.isForTeamB()) {
                result.add(entry);
            }
        }
        return result;
    }

    public List<Player> getPlayersFromTeamA() {
        List<Player> players = new ArrayList<Player>();
        for (PlayerEntry entry : playerEntries) {
            if (entry.isForTeamA()) {
                players.add(entry.getPlayer());
            }
        }
        return players;
    }

    public List<Player> getPlayersFromTeamB() {
        List<Player> players = new ArrayList<Player>();
        for (PlayerEntry entry : playerEntries) {
            if (entry.isForTeamB()) {
                players.add(entry.getPlayer());
            }
        }
        return players;
    }


    public List<Play> getPlays() {
        ArrayList<Play> plays = new ArrayList<Play>();
        for (Set set : getSets()) {
            for (Point point : set.getPoints()) {
                plays.addAll(point.getPlays());
            }
        }
        return plays;
    }

    public Collection<Set> getSets() {
        return sets;
    }

    public List<Set> getSetsWonByTeamA() {
        List<Set> result = new ArrayList<Set>();
        for (Set set : sets) {
            if (set.wonByTeamA() == true) {
                result.add(set);
            }
        }
        return result;
    }

    public List<Set> getSetsWonByTeamB() {
        List<Set> result = new ArrayList<Set>();
        for (Set set : sets) {
            if (set.wonByTeamB() == true) {
                result.add(set);
            }
        }
        return result;
    }

    public int getSetCount() {
        if (this.id == 0 || this.getId() == null) {
            throw new IllegalStateException("Match must be created on db before counting sets");
        } else if (getSets() == null) {
            throw new IllegalStateException("Match must be queried object");
        }

        return getSets().size();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team team) throws IllegalArgumentException {
        if ((team.getId() == null) || (team.getId() == 0)) {
            throw new IllegalArgumentException("Team should be created on database before assigned to match");
        }
        if (team.equals(this.teamB)) {
            throw new IllegalArgumentException("Team A & B cannot be the same.");
        }
        this.teamA = team;
        /*
        if (!(team.getMatches().contains(this))) {
            team.addMatch(this);
        }
        */
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team team) {
        if ((team.getId() == null) || (team.getId() == 0)) {
            throw new IllegalArgumentException("Team should be created on database before assigned to match");
        }
        if (team.equals(this.teamA)) {
            throw new IllegalArgumentException("Team A & B cannot be the same.");
        }
        this.teamB = team;
        /*
        if (!(team.getMatches().contains(this))) {
            team.addMatch(this);
        }
        */
    }

    public void setTeamAWon() {
        this.teamWonFlag = TEAM_A;
    }

    public void setTeamBWon() {
        this.teamWonFlag = TEAM_B;
    }

    public void resetTeamWon() {
        this.teamWonFlag = null;
    }

    public void updateTeamWon() {
        //
        int setNumA = getSetsWonByTeamA().size();
        int setNumB = getSetsWonByTeamB().size();
        if ((setNumA < 3) && (setNumB < 3)) {
            this.teamWonFlag = null;
        } else if (setNumA == 3) {
            this.teamWonFlag = TEAM_A;
        } else if (setNumB == 3) {
            this.teamWonFlag = TEAM_B;
        } else {
            throw new IllegalStateException("Illegal set count state found in the match.");
        }

    }

    public boolean wonByTeamA() {
        if (this.teamWonFlag == TEAM_A) {
            return true;
        } else {
            return false;
        }
    }

    public boolean wonByTeamB() {
        if (this.teamWonFlag == TEAM_B) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isOnGoing() {
        if (this.teamWonFlag == null) {
            return true;
        } else {
            return false;
        }
    }

    public void addSet(Set set) {

        this.sets.add(set);
        set.setSetNumber(this.sets.size());

    }

    public void renumberSets() {
        //Renumber set
        Log.d("Match", "Renumber Set numbers");
        List<Set> sets = new ArrayList<Set>(this.sets);
        Collections.sort(sets);
        for (int i = 0; i < sets.size(); i++) {

            Set s = sets.get(i);
            s.setSetNumber(i + 1);

        }
    }

    public void removeSet(Set set) {
        this.sets.remove(set);
        this.renumberSets();
    }

    public Set getOnGoingSet() {
        if ((this.sets==null)||(this.sets.size()==0)) {
            return null;
        }
        ArrayList<Set> setList = new ArrayList<Set>(this.sets);
        Collections.sort(setList);
        Set last = setList.get(setList.size() - 1);
        if (last.isOnGoing()) {
            return last;
        } else {
            return null;
        }
    }

    /*
    public void addPlayerEntry(PlayerEntry entry) {
        this.playerEntries.add(entry);
    }
    */

    public boolean checkPlayerEntry(Player player) {
        // Returns true if the player is registered to the match
        if ((this.teamA.getPlayers().contains(player)) || (this.teamB.getPlayers().contains(player))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getPlayerEntryTeamFlag(Player player) {
        //TODO
        return true;
    }

    public List<Event> getAllEvents() {
        // Return all Play & MemberChange events in the match
        List<Event> events = new ArrayList<Event>();
        for (Set s : this.getSets()) {
            // Add events from each set
            events.addAll(s.getAllEvents());
        }
        // Sort by event order
        Collections.sort(events);
        return events;
    }

    public int getNextEventOrder() {
        // Returns next event order: the last event order +1
        List<Event> events = this.getAllEvents();
        if (events.size() == 0) {
            return 1;
        } else {
            return events.get(events.size() - 1).getEventOrder() + 1;
        }
	}



	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof Match)) return false;
		Match match = (Match) o;
		if ((match.getId()==null)||(match.getId()==0)) {
			return false;
		} else if (match.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Match another) {
		if (this.startDateTime.isBefore(another.getStartDateTime())) {
			return 1;
		} else if (this.startDateTime.isAfter(another.getStartDateTime())) {
			return -1;
		} else {
			return this.name.compareTo(another.name);
		}
	}

    @Override
    public String toString() {
        String strTeamA;
        String strTeamB;
        if (teamA==null) {
            strTeamA = "None";
        } else {
            strTeamA = teamA.toString();
        }
        if (teamB==null) {
            strTeamB = "None";
        } else {
            strTeamB = teamB.toString();
        }
        return "Match: "+this.name+" - " +strTeamA+" vs. "+strTeamB;
    }
}

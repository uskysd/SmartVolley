package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="matches")
public class Match implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 403945017757332672L;
	
	
	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private DateTime startDateTime;
	
	@DatabaseField
	private String location;
	
	@DatabaseField(foreign=true)
	private Team teamA;
	
	@DatabaseField(foreign=true)
	private Team teamB;
	
	@ForeignCollectionField
	Collection<PlayerEntry> playerEntries;
	
	@ForeignCollectionField
	Collection<Set> sets;
	
	
	public Match() {
		//needed by ormlite
	}
	
	public Match(String name, Team teamA, Team teamB) {
		this.name = name;
		this.teamA = teamA;
		this.teamB = teamB;
	}
	
	
	public Collection<PlayerEntry> getPlayerEntries() {
		return playerEntries;
	}
	
	public Collection<PlayerEntry> getPlayerEntriesToTeamA() {
		Collection<PlayerEntry> result = new ArrayList<PlayerEntry>();
		for (PlayerEntry entry: playerEntries) {
			if (entry.isForTeamA()) {
				result.add(entry);
			}
		}
		return result;
	}
	
	public Collection<PlayerEntry> getPlayerEntriesToTeamB() {
		Collection<PlayerEntry> result = new ArrayList<PlayerEntry>();
		for (PlayerEntry entry: playerEntries) {
			if (entry.isForTeamB()) {
				result.add(entry);
			}
		}
		return result;
	}
	
	public Collection<Set> getSets() {
		return sets;
	}
	
	public Collection<Set> getSetsWonByTeamA() {
		Collection<Set> result = new ArrayList<Set>();
		for (Set set: sets) {
			if (set.wonByTeamA()) {
				result.add(set);
			}
		}
		return result;
	}
	
	public Collection<Set> getSetsWonByTeamB() {
		Collection<Set> result = new ArrayList<Set>();
		for (Set set: sets) {
			if (set.wonByTeamB()) {
				result.add(set);
			}
		}
		return result;
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

	public void setTeamA(Team teamA) {
		this.teamA = teamA;
	}

	public Team getTeamB() {
		return teamB;
	}

	public void setTeamB(Team teamB) {
		this.teamB = teamB;
	}

    public void addPlayerEntry(PlayerEntry entry) {
        this.playerEntries.add(entry);
    }

    public void addPlayerEntries(List<PlayerEntry> entries) {
        this.playerEntries.addAll(entries);
    }

	
	
	
	

}

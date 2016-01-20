package uskysd.smartvolley.data;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="teams")
public class Team implements Serializable, Comparable<Team> {
	
	/**
	 * Team information object saved to the database
	 */
	private static final long serialVersionUID = 1456834982199628946L;
	
	public static final String TEAM_ID_FIELD_NAME = "team_name";

	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField(columnName = TEAM_ID_FIELD_NAME)
	private String name;
	
	@DatabaseField
	private String location;
	
	@DatabaseField
	private String description;
	
	@DatabaseField
	private Integer score;
	
	@ForeignCollectionField
	Collection<Player> players;
	
	@ForeignCollectionField
	Collection<Match> matches;
	
	
	public Team() {
		//Empty constructor for ormlite
	}
	
	public Team(String name) {
		this.name = name;
		initCollections();

	}

	private void initCollections() {
		if (this.players==null) {
			this.players = new ArrayList<Player>();
		}
		if (this.matches==null) {
			this.matches = new ArrayList<Match>();
		}
	}

	public void addPlayer(Player player) {
		if ((this.getId()==null)||this.getId()==0) {
			throw new IllegalArgumentException("Team must be created on db before referred from player.");
		}
		this.players.add(player);
		if (player.getTeam()!=this) {
			player.setTeam(this);
		}
	}

	public void addMatch(Match match) {

		if (match.getTeamA()==this||match.getTeamB()==this) {
			this.matches.add(match);
		} else {
			throw new IllegalArgumentException("Team must be registered as Team A or B to the match");
		}
	}

	public Collection<Player> getPlayers() {
		return players;
	}
	
	public Collection<Match> getMatches() {
		return matches;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	
	
	@Override
	public String toString() {
		return name + " @" + location;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (!(o instanceof Team)) return false;
		Team team = (Team) o;
		if ((team.getId() == null) || (team.getId() == 0)) {
			return false;
		} else if (team.getId() == this.id) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public int compareTo(Team another) {
		return this.name.compareTo(another.getName());
	}
}

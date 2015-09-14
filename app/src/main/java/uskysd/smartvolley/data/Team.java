package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="teams")
public class Team implements Serializable {
	
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
	private Integer score = null;
	
	@ForeignCollectionField
	Collection<Player> players;
	
	@ForeignCollectionField
	Collection<Match> matches;
	
	
	public Team() {
		//Empty constructor for ormlite
	}
	
	public Team(String name) {
		this.name = name;
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

}

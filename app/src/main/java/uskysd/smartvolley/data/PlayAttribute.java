package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import uskysd.smartvolley.data.Play.PlayType;

/**
 * Additional information of Play object, 
 * such as "quick", "faint", "back attack" ... for "Attack" type Play object
 * 
 *@author USK
 */


@DatabaseTable(tableName="playAttributes")
public class PlayAttribute implements Serializable {

	private static final long serialVersionUID = -3163568683842983603L;
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField
	private PlayType playType;
	
	@DatabaseField
	private String name;
	
	@ForeignCollectionField
	Collection<Play> plays;
	
	public PlayAttribute() {
		//needed by ormlite
	}
	
	public PlayAttribute(String name, PlayType playTypeFlag) {
		this.name = name;
		this.playType = playTypeFlag;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PlayType getPlayType() {
		return playType;
	}

	public void setPlayType(PlayType playType) {
		this.playType = playType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Play> getPlays() {
		return plays;
	}

	
	
	

}

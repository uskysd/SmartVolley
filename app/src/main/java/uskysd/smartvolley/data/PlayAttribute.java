package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Additional information of Play object, 
 * such as "quick", "faint", "back attack" ... for "Attack" type Play object
 * 
 *@author USK
 */


@DatabaseTable(tableName="playAttributes")
public class PlayAttribute implements Serializable {

	private static final long serialVersionUID = -3163568683842983603L;

	public static final String NAME_FIELD_NAME = "name";
	public static final String PLAY_TYPE_FIELD_NAME = "play type";

	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(columnName = PLAY_TYPE_FIELD_NAME)
	private Play.PlayType playType;
	
	@DatabaseField(columnName = NAME_FIELD_NAME)
	private String name;
	
	@ForeignCollectionField
	Collection<Play> plays;
	
	public PlayAttribute() {
		//needed by ormlite
	}
	
	public PlayAttribute(String name, Play.PlayType playTypeFlag) {
		this.name = name;
		this.playType = playTypeFlag;
		this.plays = new ArrayList<Play>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Play.PlayType getPlayType() {
		return playType;
	}

	public void setPlayType(Play.PlayType playType) {
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

	public void addPlay(Play play) {
		if (!this.plays.contains(play)) {
			this.plays.add(play);
		}
		if (play.getAttribute()!=this) {
			play.setAttribute(this);
		}
	}

	public void removePlay(Play play) {
		this.plays.remove(play);
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof PlayAttribute)) return false;
		PlayAttribute pa = (PlayAttribute) o;
		if (pa.getId()==null||pa.getId()==0) {
			return false;
		} else if (pa.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}
	}

	
	
	

}

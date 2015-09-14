package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="points")
public class Point implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1184422808510707583L;
	
	public static final boolean TEAM_A = true;
	public static final boolean TEAM_B = false;
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(foreign=true)
	private Set set;
	
	@DatabaseField
	private boolean teamFlag;
	
	@ForeignCollectionField
	Collection<Rally> rallies;
	
	public Point() {
		//needed by ormlite
	}
	
	public Point(Set set) {
		this.set = set;
	}
	
	public boolean wonByTeamA() {
		return teamFlag;
	}
	
	public boolean wonByTeamB() {
		return !teamFlag;
	}
	
	public void setWinner(boolean teamFlag) {
		//team A: true, team B: false
		this.teamFlag = teamFlag;
	}

}

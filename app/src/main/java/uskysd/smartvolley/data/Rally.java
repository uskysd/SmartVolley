package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="rallies")
public class Rally implements Serializable, Comparable<Rally> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2828054531557992141L;
	public static boolean TEAM_A = true;
	public static boolean TEAM_B = false;
	
	@DatabaseField(generatedId=true)
	private Integer id;

	@DatabaseField
	private Integer number;
	
	@DatabaseField(foreign=true)
	private Point point;

	@DatabaseField
	private Boolean teamFlag;
	
	@ForeignCollectionField
	Collection<Play> plays;
	
	public Rally() {
		//needed by ormlite
	}
	
	public Rally(Point point, boolean teamFlag) {
		if (point.getId()==null||point.getId()==0) {
			throw new IllegalArgumentException("Point must be created on db before referred from rally");
		}
		if (!(point.isOnGoing())) {
			throw new IllegalArgumentException("Cannot add rally to point already ended");
		}
		point.addRally(this);
		this.point = point;
		this.number = point.getRallyCount();
		if(this.plays==null) {
			this.plays = new ArrayList<Play>();
		}
		this.teamFlag = teamFlag;

	}

	public Integer getId() {
		return id;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Collection<Play> getPlays() {
		return plays;
	}


	public boolean isForTeamA() {
		return this.teamFlag;
	}

	public boolean isForTeamB() {
		return !this.teamFlag;
	}

	public int getPlayCount() {
		//renumberPlay();
		return getPlays().size();
	}

	public void addPlay(Play play) {
		this.plays.add(play);
	}

	public void removePlay(Play play) {
		this.plays.remove(play);
		//renumberPlay();
	}


	public boolean checkPlayerEntry(Player player) {
		// Return true if player is registered to the match
		return this.getPoint().checkPlayerEntry(player);
	}

	public void onGetPoint() {
		// Called when getting point with a play
		if (this.isForTeamA()) {
			this.getPoint().setTeamAWon();
		} else {
			this.getPoint().setTeamBWon();
		}
	}

	public void onLoosePoint() {
		// Called when loosing point with a play
		if (this.isForTeamA()) {
			this.getPoint().setTeamBWon();
		} else {
			this.getPoint().setTeamAWon();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof Rally)) return false;
		Rally rally = (Rally) o;
		if (rally.getId()==null||rally.getId()==0) {
			return false;
		}
		if (rally.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public int compareTo(Rally another) {
		return this.number - another.getNumber();
	}
}

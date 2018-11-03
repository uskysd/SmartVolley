package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


@DatabaseTable(tableName="points")
public class Point implements Serializable, Comparable<Point> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1184422808510707583L;
	
	public static final Boolean TEAM_A = true;
	public static final Boolean TEAM_B = false;
	
	@DatabaseField(generatedId=true)
	private Integer id;

	@DatabaseField
	private int number;
	
	@DatabaseField(foreign=true)
	private Set set;
	
	@DatabaseField
	private Boolean teamFlag;
	
	@ForeignCollectionField (eager=true)
	Collection<Play> plays;
	
	public Point() {
		//needed by ormlite
	}
	
	public Point(Set set) {
		if (set.getId()==null||set.getId()==0) {
			throw new IllegalArgumentException("Set must be created on db before referred from point");
		}
		if (!(set.isOnGoing())) {
			throw new IllegalArgumentException("Cannot add point to set already ended");
		}
		this.set = set;
		this.number = set.getPointCount()+1;
	}

	public Collection<Play> getPlays() {
		return plays;
	}

    /*
	public List<Play> getPlayList() {
		// Return sorted list of plays
		List<Play> plays = new ArrayList<Play>(this.plays);
		Collections.sort(plays);
		return plays;
	}
	*/

	public Play getLastPlay() {
		ArrayList<Play> plays = new ArrayList<Play>(this.plays);
		if (plays.size()==0) {
			return null;
		}
		Collections.sort(plays);
		return plays.get(plays.size()-1);

	}



	public int getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public boolean wonByTeamA() {
		if (teamFlag==TEAM_A) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean wonByTeamB() {
		if (teamFlag == TEAM_B) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOnGoing() {
		if (teamFlag==null) {
			return true;
		} else {
			return false;
		}
	}

	public void setWinner(Boolean teamFlag) {
		this.teamFlag = teamFlag;
		// true: Team A
		// false: Team B
	}
	
	public void setTeamAWon() {
		this.teamFlag = TEAM_A;
	}

	public void setTeamBWon() {
		this.teamFlag = TEAM_B;
	}

	public void resetTeamWon() {
		this.teamFlag = null;
	}

	public Integer getId() {
		return id;
	}

	public Set getSet() {
		return set;
	}

	public int getNextEventOrder() {
		return this.set.getNextEventOrder();
	}

	public void addPlay(Play play) {
		if (!this.plays.contains(play)) {
			// Set event order
			play.setEventOrder(this.getNextEventOrder());

			// Then add to the list
			this.plays.add(play);
		}
		if (play.getPoint()!=this) {
			play.setPoint(this);
		}
	}

	public void removePlay(Play play) {
		this.plays.remove(play);
	}

	public boolean checkPlayerEntry(Player player) {
		// Returns true if the player is registered to the match
		return this.getSet().checkPlayerEntry(player);
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof Point)) return false;
		Point p = (Point) o;
		if (p.getId()==null||p.getId()==0) {
			return false;
		} else if (p.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Point another) {
		return this.number - another.getNumber();

	}
}

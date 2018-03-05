package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@DatabaseTable(tableName="sets")
public class Set implements Serializable, Comparable<Set> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1944228499971534900L;
	private static Boolean TEAM_A = true;
	private static Boolean TEAM_B = false;
	private static int SET_POINT = 25;
	private static int FINAL_SET_POINT = 15;


	@DatabaseField(generatedId=true)
	private Integer id;

	@DatabaseField
	private Integer setNumber;

	@DatabaseField
	private Boolean teamWonFlag;
	
	@DatabaseField(foreign=true)
	private Match match;
	
	@ForeignCollectionField
	Collection<Point> points;
	
	public Set() {
		//needed by ormlite
	}
	
	public Set(Match match) {
		this.setMatch(match);

		if (this.points==null) {
			this.points = new ArrayList<Point>();
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getSetNumber() {
		return this.setNumber;
	}

	public void setSetNumber(int setNum) {
		this.setNumber = setNum;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		if (match.getId()==0||match.getId()==null) {
			throw new IllegalArgumentException("Match should be created on db before adding sets");
		}
		if (!(match.isOnGoing())) {
			throw new IllegalArgumentException("Cannot add set to match already ended.");
		}
		this.match = match;
		match.addSet(this);
		
		//Setting set number
		this.setSetNumber(match.getSetCount());
	}

	public Collection<Point> getPoints() {
		return points;
	}
	
	public Collection<Point> getPointsWonByTeamA() {
		Collection<Point> result = new ArrayList<Point>();
		for (Point p: points) {
			if (p.wonByTeamA()) {
				result.add(p);
			}
		}
		return result;
	}

	public void addPoint(Point point) {
		if (!(point.getSet()==this)) {
			throw new IllegalArgumentException("Point should have reference to the set.");
		}
		if (!(this.points.contains(point))) {
			this.points.add(point);
		}

	}

	public void removePoint(Point point) {
		this.points.remove(point);
		this.renumberPoints();
	}

	public Point getOnGoingPoint() {
		ArrayList<Point> pointList = new ArrayList<Point>(this.points);
		Collections.sort(pointList);
		Point last = pointList.get(pointList.size()-1);
		if (last.isOnGoing()) {
			return last;
		} else {
			return null;
		}
	}
	
	public Collection<Point> getPointsWonByTeamB() {
		Collection<Point> result = new ArrayList<Point>();
		for (Point p: points) {
			if (p.wonByTeamB()) {
				result.add(p);
			}
		}
		return result;
	}
	
	public void updateTeamWon() {
		int pointNumA = getPointsWonByTeamA().size();
		int pointNumB = getPointsWonByTeamB().size();
		if (this.setNumber<5) {
			judgeWithSetPoint(pointNumA, pointNumB, SET_POINT);
		} else {
			// final set
			judgeWithSetPoint(pointNumA, pointNumB, FINAL_SET_POINT);
		}
	}

	private void judgeWithSetPoint(int pointNumA, int pointNumB, int setPoint) {
		if ((pointNumA<setPoint)&&(pointNumB<setPoint)) {
			this.teamWonFlag = null;
		} else if (pointNumA - pointNumB >=2) {
			this.teamWonFlag = TEAM_A;
		} else if (pointNumB - pointNumA >=2) {
			this.teamWonFlag = TEAM_B;
		} else {
			this.teamWonFlag = null;
		}
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

	public boolean wonByTeamA() {
		if (this.teamWonFlag==TEAM_A) {
			return true;
		} else {
			return false;
		}
	}

	public boolean wonByTeamB() {
		if (this.teamWonFlag==TEAM_B) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOnGoing() {
		if (this.teamWonFlag==null) {
			return true;
		} else {
			return false;
		}
	}

	public Integer getPointCount() {
		//TODO
		return this.getPoints().size();
	}



	public void renumberPoints() {
		List<Point> points = new ArrayList<Point>(this.getPoints());
		Collections.sort(points);
		for (int i=0; i<points.size(); i++) {
			Point p = points.get(i);
			p.setNumber(i+1);
			//p.renumberRally();
		}
	}

	public boolean checkPlayerEntry(Player player) {
		return this.getMatch().checkPlayerEntry(player);
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof Set)) return false;
		Set set = (Set) o;
		if (set.getId()==0||set.getId()==null) return false;
		if (set.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public int compareTo(Set another) {
		return this.getSetNumber()-another.getSetNumber();
	}


}

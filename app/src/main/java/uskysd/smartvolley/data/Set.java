package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="sets")
public class 	Set implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1944228499971534900L;
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(foreign=true)
	private Match match;
	
	@ForeignCollectionField
	Collection<Point> points;
	
	public Set() {
		//needed by ormlite
	}
	
	public Set(Match match) {
		this.match = match;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
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
	
	public Collection<Point> getPointsWonByTeamB() {
		Collection<Point> result = new ArrayList<Point>();
		for (Point p: points) {
			if (p.wonByTeamB()) {
				result.add(p);
			}
		}
		return result;
	}
	
	public boolean wonByTeamA() throws RuntimeException {
		Collection<Point> pointA = new ArrayList<Point>();
		Collection<Point> pointB = new ArrayList<Point>();
		for (Point p: points) {
			if (p.wonByTeamA()) {
				pointA.add(p);
			} else if (p.wonByTeamB()) {
				pointB.add(p);
			}
		}
		if (pointA.size() > pointB.size()) {
			return true;
		} else if (pointA.size() < pointB.size()) {
			return false;
		} else {
			throw new RuntimeException("Team A & B has the same points");
		}
	}
	
	public boolean wonByTeamB() throws RuntimeException {
		return !this.wonByTeamA();
	}
			
	
	



	

}

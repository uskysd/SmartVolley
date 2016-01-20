package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="rallies")
public class Rally implements Serializable, Comparable<Rally> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2828054531557992141L;
	
	@DatabaseField(generatedId=true)
	private Integer id;

	@DatabaseField
	private Integer number;
	
	@DatabaseField(foreign=true)
	private Point point;
	
	@ForeignCollectionField
	Collection<Play> plays;
	
	public Rally() {
		//needed by ormlite
	}
	
	public Rally(Point point) {
		if (point.getId()==null||point.getId()==0) {
			throw new IllegalArgumentException("Point must be created on db before referred from rally");
		}
		if (!(point.isOnGoing())) {
			throw new IllegalArgumentException("Cannot add rally to point already ended");
		}
		this.point = point;
		this.number = point.getRallyCount();

	}

	public Integer getId() {
		return id;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getNumber() {
		return number;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public void renumberPlay() {
		//TODO
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

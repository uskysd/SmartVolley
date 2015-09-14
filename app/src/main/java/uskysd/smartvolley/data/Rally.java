package uskysd.smartvolley.data;

import java.io.Serializable;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="rallies")
public class Rally implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2828054531557992141L;
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(foreign=true)
	private Point point;
	
	@ForeignCollectionField
	Collection<Play> plays;
	
	public Rally() {
		//needed by ormlite
	}
	
	public Rally(Point point) {
		this.point = point;
	}

	public Integer getId() {
		return id;
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
	
	
	

}

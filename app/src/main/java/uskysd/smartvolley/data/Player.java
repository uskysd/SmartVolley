package uskysd.smartvolley.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName="players")
public class Player implements Serializable, Comparable<Player> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7727930092478303517L;
	//for  QueryBuilder
	public static final String FIRSTNAME_FIELD_NAME = "first_name";
	public static final String LASTNAME_FIELD_NAME = "last_name";
	public static final String ID_FIELD_NAME = "id";
    public static final String ORDER_IN_TEAM = "order_in_team";
	public enum StartingPosition {
		BACK_LEFT, FRONT_LEFT, FRONT_CENTER, FRONT_RIGHT, BACK_RIGHT, BACK_CENTER, LIBERO,
		SUB, NONE
	}
	
	@DatabaseField(generatedId=true, columnName=ID_FIELD_NAME)
	private int id;
	
	@DatabaseField(columnName=FIRSTNAME_FIELD_NAME)
	private String firstName;
	
	@DatabaseField(columnName=LASTNAME_FIELD_NAME)
	private String lastName;

    @DatabaseField(columnName=ORDER_IN_TEAM)
    private int order;
	
	@DatabaseField
	private Date birthday;
	
	@DatabaseField(canBeNull=true)
	private Boolean isMale;

	@DatabaseField
	private float height;
	
	@DatabaseField
	private float weight;
	
	@DatabaseField(canBeNull = true, foreign = true)
	private Team team;
	
	@DatabaseField(canBeNull = false)
	private Integer uniformNumber = -1;
	
	@DatabaseField(canBeNull = true)
	private String description;
	
	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] imageBytes;
	
	@DatabaseField(canBeNull=false, defaultValue="NONE", unknownEnumName="NONE")
	private StartingPosition startingPosition;

	
	public Player() {
		//needed by ormlite
	}
	
	public Player(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setFirstName(String name) {
		this.firstName = name;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setLastName(String name){
		this.lastName = name;
	}
		
	public String getLastName() {
		return lastName;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

    public Integer getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


	public Date getBirthday() {
		return birthday;
	}
	
	public void setBirthday(Date date) {
		this.birthday = date;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setAsMale() {
		this.isMale = true;
	}
	
	public void setAsFemale() {
		this.isMale = false;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isMale() {
		if (this.isMale != null) {
			return this.isMale;
		} else {
			return false;
		}
	}
	
	public boolean isFemale() {
		if (this.isMale != null) {
			return !this.isMale;
		} else {
			return false;
		}
	}
	
	public void setAge(Integer age) {
		Integer currentYear = new DateTime().getYear();
		DateTime birthday = new DateTime(currentYear-age, 1, 1, 0, 0);
		setBirthday(birthday.toDate());
	}
	
	public Integer getAge() {
		DateTime today = new DateTime();
		DateTime birthday = new DateTime(this.getBirthday());
		Integer age = Years.yearsBetween(birthday, today).getYears();
		return age;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public StartingPosition getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;
	}

	public Integer getUniformNumber() {
		return uniformNumber;
	}

	public void setUniformNumber(Integer uniformNumber) {
		this.uniformNumber = uniformNumber;
	}

	@Override
	public int compareTo(Player another) {
		// TODO Auto-generated method stub
		int startingPosDiff = this.getStartingPosition().ordinal()
				- another.getStartingPosition().ordinal();
		return startingPosDiff;
	}


	

}

	

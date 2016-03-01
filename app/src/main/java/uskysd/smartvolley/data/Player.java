package uskysd.smartvolley.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.IllegalFormatCodePointException;

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
	
	@DatabaseField(generatedId=true, columnName=ID_FIELD_NAME)
	private Integer id;
	
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
	private Position startingPosition;

	@DatabaseField(canBeNull=false, defaultValue="NONE", unknownEnumName="NONE")
	private Role role;

	
	public Player() {
		//needed by ormlite
	}
	
	public Player(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = Role.NONE;
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
		if (height > 0) {
			this.height = height;
		} else {
			throw new IllegalArgumentException("Height must be positive value.");
		}
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setWeight(float weight) {
		if (weight > 0) {
			this.weight = weight;
		} else {
			throw new IllegalArgumentException("Weight must be positive value.");
		}
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
		if (team.getId()==0) {
			throw new IllegalArgumentException("Team should be created on db before referred from player");
		}
		this.team = team;
		if (!(team.getPlayers().contains(this))) {
			team.addPlayer(this);
		}
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

	public Position getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(Position startingPosition) {
		this.startingPosition = startingPosition;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Role getRole() {
		return role;
	}

	public Integer getUniformNumber() {
		return uniformNumber;
	}

	public void setUniformNumber(Integer uniformNumber) {
		this.uniformNumber = uniformNumber;
	}

	@Override
	public int compareTo(Player another) {
		return this.getFullName().compareTo(another.getFullName());

		//int startingPosDiff = this.getStartingPosition().ordinal()
		//		- another.getStartingPosition().ordinal();
		//return startingPosDiff;
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof Player)) return false;
		Player p = (Player) o;
		if ((p.getId()==null)||(p.getId()==0)){
			return false;
		} else if (p.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}


	}


	

}

	

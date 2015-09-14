package uskysd.smartvolley.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="playerRolls")
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4633501049234529557L;
	
	private enum RoleName {
		SUPER_ACE, ACE, CENTER, SETTER, LIBERO, CAPTAIN
	}
	
	public static final String ID_FIELD_NAME = "id";
	
	@DatabaseField(generatedId=true, columnName=ID_FIELD_NAME)
	private Integer id;
	
	
	@DatabaseField(defaultValue="SUB")
	private RoleName roleName;
	
	
	public Role() {
		//for ormlite
	}
	
	public Role(RoleName roleName) {
		this.roleName = roleName;
	}

	public RoleName getRoleName() {
		return roleName;
	}

	public void setRoleName(RoleName roleName ) {
		this.roleName = roleName;
	}


}

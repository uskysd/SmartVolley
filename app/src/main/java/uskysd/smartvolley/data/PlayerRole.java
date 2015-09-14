package uskysd.smartvolley.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class PlayerRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4633501049234529557L;
	
	public static final String PLAYER_ID_FIELD_NAME =  "player_id";
	public static final String ROLE_ID_FIELD_NAME = "role_id";
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(foreign=true, columnName=PLAYER_ID_FIELD_NAME)
	private Player player;
	
	@DatabaseField(foreign=true, columnName=ROLE_ID_FIELD_NAME)
	private Role role;
	
	public PlayerRole() {
		// for OrmLite
	}
	
	public PlayerRole(Player player, Role role) {
		this.player = player;
		this.role = role;
	}
	
	

}

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

	@DatabaseField(columnName=ROLE_ID_FIELD_NAME,
			canBeNull=false, defaultValue="NONE", unknownEnumName="NONE")
	private Role role;
	

	public PlayerRole(Player player, Role role) {
		this.player = player;
		this.role = role;
	}

	public PlayerRole() {
		// needed by ormlite
	}

	public Integer getId() {
		return this.id;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Role getRole() {
		return this.role;
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof PlayerRole)) {
			return false;
		}
		PlayerRole pr = (PlayerRole) o;
		if ((pr.getId()==0)||(pr.getId()==null)) {
			return false;
		} else if (pr.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}
	}




	
	

}

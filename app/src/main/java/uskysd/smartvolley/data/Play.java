package uskysd.smartvolley.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="plays")
public class Play implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054422602195971927L;
	
	public static final Integer SERVICE = 0;
	public static final Integer RECIEVE = 1;
	public static final Integer TOSS = 2;
	public static final Integer ATTACK = 3;
	public static final Integer BLOCK = 4;
	public enum PlayType {SERVICE, RECIEVE, TOSS, ATTACK, BLOCK};
	
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(foreign=true)
	private Rally rally;
	
	@DatabaseField(foreign=true)
	private Player player;
	
	@DatabaseField
	private PlayType playType;
	
	@DatabaseField(canBeNull=true)
	private Float positionX;
	
	@DatabaseField(canBeNull=true)
	private Float positionY;
	
	@DatabaseField(canBeNull=true)
	private Float targetX;
	
	@DatabaseField(canBeNull=true)
	private Float targetY;
	
	@DatabaseField(foreign=true, canBeNull=true)
	private PlayAttribute attribute;
	
	public Play() {
		//needed by ormlite
	}
	
	public Play(Rally rally, Player player, PlayType playType) {
		this.rally = rally;
		this.player = player;
		this.playType = playType;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Rally getRally() {
		return rally;
	}

	public void setRally(Rally rally) {
		this.rally = rally;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public PlayType getTypeFlag() {
		return playType;
	}

	public void setTypeFlag(PlayType playType) {
		this.playType = playType;
	}

	public Float getPositionX() {
		return positionX;
	}

	public void setPositionX(Float positionX) {
		this.positionX = positionX;
	}

	public Float getPositionY() {
		return positionY;
	}

	public void setPositionY(Float positionY) {
		this.positionY = positionY;
	}

	public Float getTargetX() {
		return targetX;
	}

	public void setTargetX(Float targetX) {
		this.targetX = targetX;
	}

	public Float getTargetY() {
		return targetY;
	}

	public void setTargetY(Float targetY) {
		this.targetY = targetY;
	}

	public PlayAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(PlayAttribute attribute) {
		this.attribute = attribute;
	}

	
	
	
	
	
	
}

package uskysd.smartvolley.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="plays")
public class Play implements Serializable, Comparable<Play> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054422602195971927L;
	
	public enum PlayType {SERVICE, RECEPTION, RECEIVE, TOSS, ATTACK, BLOCK};
	public enum PlayResult {POINT, GOOD, NORMAL, BAD, MISTAKE, NONE}
	
	
	@DatabaseField(generatedId=true)
	private Integer id;

	@DatabaseField
	private int number;
	
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

	@DatabaseField
	private PlayResult playResult;
	
	public Play() {
		//needed by ormlite
	}
	
	public Play(Rally rally, Player player, PlayType playType, PlayResult result) {
		this.setRally(rally);
		this.setPlayer(player);
		this.playType = playType;
		this.number = rally.getPlayCount();
		this.setResult(result);

	}
	
	public Integer getId() {
		return id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Rally getRally() {
		return rally;
	}

	public void setRally(Rally rally) {
		if (rally.getId()==null||rally.getId()==0) {
			throw new IllegalArgumentException("Rally should be created on db before assigning play");
		}

		this.rally = rally;
		rally.addPlay(this);

	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		if ((player.getId()==null)||(player.getId()==0)) {
			throw new IllegalArgumentException("Player must be created on db before registering play");
		}
		if (this.checkPlayerEntry(player)==true) {
			this.player = player;
		} else {
			throw new IllegalArgumentException("Player is not registered to the match");
		}
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

	public PlayResult getResult() {
		return playResult;
	}

	public void setResult(PlayResult result) {
		this.playResult = result;
		switch (result) {
			case POINT:
				this.getRally().onGetPoint();
				break;
			case MISTAKE:
				this.getRally().onLoosePoint();
				break;
			default:
				break;

		}

	}

	public boolean checkPlayerEntry(Player player) {
		//Return true if player is registered to the match
		return this.getRally().checkPlayerEntry(player);
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof Play)) return false;
		Play play = (Play) o;
		if (play.getId()==null||play.getId()==0) {
			return false;
		} else if (play.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Play another) {
		return this.getNumber()-another.getNumber();

	}

	
	
	
	
	
	
}

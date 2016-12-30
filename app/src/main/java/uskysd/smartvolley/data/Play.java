package uskysd.smartvolley.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

@DatabaseTable(tableName="plays")
public class Play extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054422602195971927L;
	
	public enum PlayType {
		SERVICE("Service"), RECEPTION("Reception"), RECEIVE("Receive"), TOSS("Toss"),
		ATTACK("Attack"), BLOCK("Block");
		private final String playTypeName;

		private PlayType(String name) {
			this.playTypeName = name;
		}

		public String toString() {
			return this.playTypeName;
		}

	};

	public enum PlayResult {
        POINT("Point"), GOOD("Good"), NORMAL("Normal"), BAD("Bad"),
        MISTAKE("Mistake"), NONE("None");
        private final String str;
        private PlayResult(String str) {
            this.str = str;
        }

        public String toString() {
            return this.str;
        }

    }
	
	
	@DatabaseField(generatedId=true)
	private Integer id;

	@DatabaseField
	private int eventOrder;

    @DatabaseField
    private DateTime dateTime;
	
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
	
	public Play(Player player, PlayType playType) {
		this.setPlayer(player);
		this.playType = playType;
        this.dateTime = DateTime.now();
	}
	
	public Integer getId() {
		return id;
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

	public String getEventTitle() {
		return "Play."+this.playType.toString()+" by "+this.player.toString();
	}

	public DateTime getTimeStamp() {
		return DateTime.now();
	}

    @Override
    public void setTimeStamp(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
	public int getEventOrder() {
		return this.eventOrder;
	}

    @Override
    public void setEventOrder(int eventOrder) {
        this.eventOrder = eventOrder;
    }
}

package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;

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

	public enum PlayEvaluation {
        GOOD("Good"), NORMAL("Normal"), BAD("Bad"), NONE("None");
        private final String str;
        private PlayEvaluation(String str) {
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
	private Point point;
	
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
	private PlayEvaluation evaluation;
	
	public Play() {
		//needed by ormlite
	}
	
	public Play(Player player, PlayType playType) {
		this.setPlayer(player);
		this.playType = playType;
        this.dateTime = DateTime.now();
	}

	public Play(Point point, Player player, PlayType playType) {
		this.setPoint(point);
		this.setPlayer(player);
		this.playType = playType;
		this.dateTime = DateTime.now();
	}
	
	public Integer getId() {
		return id;
	}

	public Point getPoint() { return point; }

	public void setPoint(Point point) {
		if (point.getId()==0||point.getId()==null) {
			throw new IllegalArgumentException("Point is not on db");
		} else {
			this.point = point;
			if (!point.getPlays().contains(this)) {
				point.addPlay(this);
			}

		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player player) {
		if ((player.getId()==null)||(player.getId()==0)) {
			throw new IllegalArgumentException("Player must be created on db before registering play");
		}
		this.player = player;
		if (!player.getPlays().contains(this)) {
			player.addPlay(this);
		}
	}

	public PlayType getPlayType() {
		return playType;
	}

	public void setPlayType(PlayType playType) {
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
		if (attribute.getId()==0||attribute.getId()==null) {
			throw new IllegalArgumentException("The PlayAttribute is not on database.");
		}

		if (this.attribute!=null && this.attribute!=attribute) {
			this.attribute.removePlay(this);
		}
		this.attribute = attribute;
		attribute.addPlay(this);

		/*
		if (!attribute.getPlays().contains(this)) {
			attribute.addPlay(this);
		}
		*/
	}

	public PlayEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(PlayEvaluation evaluation) {
		this.evaluation = evaluation;
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
		return this.playType.toString()+" by "+this.player.toString();
	}

	public DateTime getTimeStamp() {
		return this.dateTime;
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
	public Match getMatch() {
		return this.point.getSet().getMatch();
	}
	@Override
    public void setEventOrder(int eventOrder) {
        this.eventOrder = eventOrder;
    }
}

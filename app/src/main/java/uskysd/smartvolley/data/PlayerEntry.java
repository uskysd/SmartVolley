package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

//import uskysd.smartvolley.Core;

@DatabaseTable(tableName="playerEntries")
public class PlayerEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1644014379004949981L;
	
	public static final boolean TEAM_A = true;
	public static final boolean TEAM_B = false;
	
	@DatabaseField(generatedId=true)
	private Integer id;
	
	@DatabaseField(foreign=true)
	private Match match;
	
	@DatabaseField(foreign=true)
	private Player player;
	
	@DatabaseField
	private Integer number;
	
	@DatabaseField
	private boolean teamFlag;

    @DatabaseField(canBeNull=false, defaultValue="NONE", unknownEnumName="NONE")
    private Position startingPosition;

    public PlayerEntry() {
        //for OrmLite
    }
    public PlayerEntry(Match match, Player player, int number, boolean teamFlag, Position startingPosition) {
        if (match.getId()==null || match.getId()==0) {
			throw new IllegalArgumentException("Match should be created on db before register player-entry.");
		}
		this.match = match;
		this.player = player;
        this.number = number;
        this.teamFlag = teamFlag;
		this.startingPosition = startingPosition;
    }
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

    public Position getStartingPosition() {
        return this.startingPosition;
    }

    public void setStartingPosition(Position position) {
        this.startingPosition = position;
    }

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setForTeamA() {
		this.teamFlag = TEAM_A;
	}
	
	public void setForTeamB() {
		this.teamFlag = TEAM_B;
	}
	
	public boolean isForTeamA() {
		return this.teamFlag;
	}
	
	public boolean isForTeamB() {
		return !teamFlag;
	}
	

	public Match getMatch() {
		return this.match;
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (o==null) return false;
		if (!(o instanceof PlayerEntry)) {
			return false;
		}
		PlayerEntry pe = (PlayerEntry) o;
		if ((pe.getId()==0)||(pe.getId()==null)) {
			return false;
		} else if (pe.getId()==this.getId()) {
			return true;
		} else {
			return false;
		}

	}
}

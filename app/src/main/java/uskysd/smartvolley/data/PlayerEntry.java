package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import uskysd.smartvolley.Core;

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
    private Core.Position startingPosition;

    public PlayerEntry() {
        //for OrmLite
    }
    public PlayerEntry(Player player, int number, boolean teamFlag) {
        this.player = player;
        this.number = number;
        this.teamFlag = teamFlag;
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

    public Core.Position getStartingPosition() {
        return this.startingPosition;
    }

    public void setStartingPosition(Core.Position position) {
        this.startingPosition = position;
    }

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setTeamFlag(boolean teamFlag) {
		//true: team A, false: team B
		this.teamFlag = teamFlag;
	}
	
	public boolean getTeamFlag() {
		return this.teamFlag;
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
	
	public void setMatch(Match match) {
		this.match = match;
	}
	
	public Match getMatch() {
		return this.match;
	}
	
	
	
	

}

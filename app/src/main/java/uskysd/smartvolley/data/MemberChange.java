package uskysd.smartvolley.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;


@DatabaseTable(tableName = "memberChanges")
public class MemberChange extends Event implements Serializable {

    private static final long serialVersionUID = -7054422602195971234L;

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true)
    private Set set;

    @DatabaseField(foreign = true)
    private Player playerIn;

    @DatabaseField(foreign = true)
    private Player playerOut;

    @DatabaseField(canBeNull = true)
    private Integer eventOrder;

    @DatabaseField
    private DateTime dateTime;


    public MemberChange() {
        // for OrmLite
    }

    public MemberChange(Set set, Player playerIn, Player playerOut) {
        this.set = set;
        //set.addMemberChange(this);
        this.playerIn = playerIn;
        this.playerOut = playerOut;
        this.dateTime = DateTime.now();
        this.eventOrder = set.getNextEventOrder();

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set getSet() {
        return set;
    }

    public Player getPlayerIn() {
        return playerIn;
    }

    public void setPlayerIn(Player playerIn) {
        this.playerIn = playerIn;
    }

    public Player getPlayerOut() {
        return playerOut;
    }

    public void setPlayerOut(Player playerOut) {
        this.playerOut = playerOut;
    }

    @Override
    public String toString() {
        return "IN: "+playerIn.toString()+"/OUT: "+playerOut.toString();
    }

    @Override
    public String getEventTitle() {
        return this.toString();
    }

    @Override
    public int getEventOrder() {
        return this.eventOrder;
    }

    @Override
    public Match getMatch() {
        return null;
    }

    @Override
    public void setEventOrder(int order) {
        this.eventOrder = order;
    }

    @Override
    public DateTime getTimeStamp() {
        return dateTime;
    }

    @Override
    public void setTimeStamp(DateTime dateTime) {
        this.dateTime = dateTime;
    }


}

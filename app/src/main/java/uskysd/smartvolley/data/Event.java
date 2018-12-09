package uskysd.smartvolley.data;

import org.joda.time.DateTime;

/**
 * Created by 112886 on 3/1/2016.
 */
public abstract class Event implements Comparable<Event> {

    public enum EventType {
        PLAY, MEMBERCHANGE, NOTIFICATION;
    }

    abstract public EventType getEventType();

    abstract public String getEventTitle();

    abstract public int getEventOrder();

    abstract public Match getMatch();

    abstract public void setEventOrder(int order);

    abstract public DateTime getTimeStamp();

    abstract public void setTimeStamp(DateTime dateTime);

    @Override
    public int compareTo(Event another) {
        return this.getEventOrder()-another.getEventOrder();
    }
}

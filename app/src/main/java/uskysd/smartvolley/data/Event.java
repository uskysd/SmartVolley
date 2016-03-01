package uskysd.smartvolley.data;

import org.joda.time.DateTime;

/**
 * Created by 112886 on 3/1/2016.
 */
public abstract class Event implements Comparable<Event> {

    abstract public String getEventTitle();

    abstract public int getEventOrder();

    abstract public DateTime getTimeStamp();

    @Override
    public int compareTo(Event another) {
        return this.getEventOrder()-another.getEventOrder();
    }
}

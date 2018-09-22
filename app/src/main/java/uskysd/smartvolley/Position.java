package uskysd.smartvolley;

/**
 * Created by uskysd on 10/6/2015.
 */
public enum Position {
    BACK_LEFT("Back Left"),
    FRONT_LEFT("Front Left"),
    FRONT_CENTER("Front Center"),
    FRONT_RIGHT("Front Right"),
    BACK_RIGHT("Back Right"),
    BACK_CENTER("Back Center"),
    LIBERO("Libero"),
    SUB("Sub"),
    NONE("None");

    private final String positionName;

    private Position(String name) {
        this.positionName = name;
    }

    public String toString() {
        return this.positionName;
    }

    public static Position fromString(String str) {
        for (Position position: Position.values()) {
            if (position.positionName.equalsIgnoreCase(str)) {
                return position;
            }
        }
        return null;
    }


}

package uskysd.smartvolley.scores;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import uskysd.smartvolley.data.Play;

public abstract class PlayScore {

    public static final String SUMMARY = "Summary";
    private static final String name = "Play Score";
    private static final List<String> variables = Arrays.asList(SUMMARY);

    public PlayScore() {

    }

    public static String getName() {
        return name;
    }
    public static List<String> getVariables() {
        return variables;
    };
    public abstract HashMap<String, String> calculate(List<Play> plays);

}

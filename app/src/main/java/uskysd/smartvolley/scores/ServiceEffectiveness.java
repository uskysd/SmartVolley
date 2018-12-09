package uskysd.smartvolley.scores;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import uskysd.smartvolley.data.Play;

public class ServiceEffectiveness extends PlayScore {



    public static final String SUMMARY = "Summary";
    public static final String PERCENTAGE = "Percentage";
    public static final String COUNTS = "Counts";
    private static final String name = "Service Effectiveness";
    private static final List<String> variables = Arrays.asList(SUMMARY, PERCENTAGE, COUNTS);

    @Override
    public HashMap<String, String> calculate(List<Play> plays) {

        HashMap<String, String> results = new HashMap<String, String>();

        int pointCount = 0;
        int failureCount = 0;
        int serviceCount = 0;
        int effectiveCount = 0;

        for (Play play: plays) {
            if (play.getPlayType()== Play.PlayType.SERVICE) {
                serviceCount +=1;
                Play.PlayResult result = play.getPlayResult();
                if (result==Play.PlayResult.POINT) {
                    pointCount+=1;
                } else if (result== Play.PlayResult.FAILURE) {
                    failureCount += 1;
                } else if (result==Play.PlayResult.EFFECTIVE) {
                    effectiveCount+=1;
                }
            }
        }


        String strscore = "--";
        if (serviceCount!=0) {
            float score = (float) (pointCount+0.5* (float) effectiveCount-failureCount)/serviceCount*100;
            strscore = String.format("%.1f", score);
        }

        results.put(PERCENTAGE, strscore +"%");
        results.put(COUNTS, Integer.toString(pointCount)+"-"+Integer.toString(effectiveCount)
                +"-"+Integer.toString(failureCount)+"/"+Integer.toString(serviceCount));
        results.put(SUMMARY, results.get(PERCENTAGE)+"("+results.get(COUNTS)+")");

        return results;
    }
}

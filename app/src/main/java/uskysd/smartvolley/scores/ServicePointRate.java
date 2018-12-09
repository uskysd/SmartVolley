package uskysd.smartvolley.scores;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import uskysd.smartvolley.data.Play;

public class ServicePointRate extends PlayScore {

    private static final String TAG = ServicePointRate.class.getSimpleName();

    public static final String SUMMARY = "Summary";
    public static final String PERCENTAGE = "Percentage";
    public static final String COUNTS = "Counts";
    private static final String name = "Service Point Rate";
    private static final List<String> variables = Arrays.asList(PERCENTAGE, COUNTS);

    @Override
    public HashMap<String, String> calculate(List<Play> plays) {
        Log.d(TAG, "Calculate Score");
        Log.d(TAG, "Play Count: "+Integer.toString(plays.size()));
        HashMap<String, String> results = new HashMap<String, String>();

        int pointCount = 0;
        int serviceCount = 0;

        for (Play play: plays) {
            if (play.getPlayType()== Play.PlayType.SERVICE) {
                serviceCount +=1;
                if (play.getPlayResult()== Play.PlayResult.POINT) {
                    pointCount += 1;
                }
            }
        }

        String strvalue = "--";
        if (serviceCount!=0) {
            strvalue = String.format("%.1f", ((float) pointCount/serviceCount *100));
        }
        results.put(PERCENTAGE, strvalue +"%");
        results.put(COUNTS, Integer.toString(pointCount)+"/"+Integer.toString(serviceCount));
        results.put(SUMMARY, results.get(PERCENTAGE)+"("+results.get(COUNTS)+")");

        return results;
    }


}

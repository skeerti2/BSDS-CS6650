package helpers;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.concurrent.CopyOnWriteArrayList;

public class StatsHelper
{
    /**
     * Get mean latency
     * @param latencyArray
     * @return
     */
    public static double getMean(double[] latencyArray)
    {
        if (latencyArray == null || latencyArray.length == 0)
            return 0;

        double sumLatency = 0;
        for(int i=0; i< latencyArray.length; i++){
            sumLatency += latencyArray[i];
        }
        return sumLatency/latencyArray.length;
    }

    /**
     * Get median latency
     * @param latencyArray
     * @return
     */
    public static double getMedian(double[] latencyArray)
    {
        Median median = new Median();
        return median.evaluate(latencyArray);
    }

    /**
     * Get latency percentile
     * @param latencyArray
     * @param percentile
     * @return
     */
    public static double getPercentile(double[] latencyArray, int percentile)
    {
        Percentile percentileObject = new Percentile();
        return percentileObject.evaluate(latencyArray, percentile);
    }
}

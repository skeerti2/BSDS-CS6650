package tasks;

import api.SkiersApiWrapper;
import helpers.ApiHelper;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class NewLiftRideTask
        implements Runnable
{
    /**
     * CountDownLatch to track number of new lift ride threads completed
     */
    private CountDownLatch countDownLatch;

    private int startSkierId;

    private int endSkierId;

    private int numLiftRides;

    private int numLifts;

    private int startTime;

    private int endTime;

    /**
     * Maximum wait time for a lift ride in minutes
     */
    private static int MAX_WAIT_TIME = 10;

    private static int RESORT_ID = 3;

    private static String SEASON_ID = "34";

    private static String DAY_ID = "4";

    private static int MAX_RETRIES_ON_FAILURE = 5;

    public NewLiftRideTask(int startSkierId, int endSkierId, int numLiftRides, int numLifts, int startTime, int endTime, CountDownLatch countDownLatch)
    {
        this.countDownLatch = countDownLatch;
        this.startSkierId = startSkierId;
        this.endSkierId = endSkierId;
        this.numLiftRides = numLiftRides;
        this.numLifts = numLifts;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void run()
    {
        ApiResponse res = null;
        try {
            int liftID = getRandomLift();
            int randomTime = getRandomTime();
            for (int i = 0; i < numLiftRides; i++) {
                LiftRide liftRide = new LiftRide().liftID(liftID).time(randomTime).waitTime(getWaitTime());
                res = SkiersApiWrapper.createNewLiftRide(liftRide, RESORT_ID, SEASON_ID, DAY_ID, getRandomSkierId());

                // if request failed, retry the request
                if (!ApiHelper.IsApiRequestSuccessful(res))
                {
                    int counter = 0;
                    while (counter < MAX_RETRIES_ON_FAILURE)
                    {
                        System.out.println("bad response, trying again");
                        res = SkiersApiWrapper.createNewLiftRide(liftRide, RESORT_ID, SEASON_ID, DAY_ID, getRandomSkierId());
                        if (ApiHelper.IsApiRequestSuccessful(res))
                        {
                            break;
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
    }

    private int getRandomLift(){
        return ThreadLocalRandom.current().nextInt(1, numLifts+1);
    }

    private int getRandomTime(){
        return ThreadLocalRandom.current().nextInt(startTime, endTime+1);
    }

    private int getRandomSkierId(){
        return ThreadLocalRandom.current().nextInt(startSkierId, endSkierId+1);
    }

    private int getWaitTime(){
        return ThreadLocalRandom.current().nextInt(0, MAX_WAIT_TIME + 1);
    }
}

package api;

import helpers.ApiHelper;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SkiersApiWrapper
{
    private static SkiersApi skiersApi = new SkiersApi();

    private static AtomicInteger totalRequests = new AtomicInteger();

    private static AtomicInteger successfulRequests = new AtomicInteger();

    private static CopyOnWriteArrayList<String[]> requestList = new CopyOnWriteArrayList<>();

    private static CopyOnWriteArrayList<Long> latencyList = new CopyOnWriteArrayList<>();

    private static int DEFAULT_PORT_NUMBER = 8080;

    private static String LOCALHOST_URL = "http://localhost:{portAddress}/hw2_war_exploded/";

    private static String EC2_INSTANCE_URL = "http://ec2-user@ec2-user@ec2-52-27-166-51.us-west-2.compute.amazonaws.com:{portAddress}/hw2_war/";

    private static String LOAD_BALANCER_URL = "http://hw2-load-balancer-test-2025416995.us-west-2.elb.amazonaws.com/hw2_war/";

    /**
     * Initial API client
     */
    public static void Initialize(Integer portNumber)
    {
        int apiPort = portNumber == null ? DEFAULT_PORT_NUMBER : portNumber;
        String apiUrl = LOCALHOST_URL.replace("{portAddress}", String.valueOf(apiPort));
        skiersApi.getApiClient().setBasePath(apiUrl);
        skiersApi.getApiClient().setConnectTimeout(1*60*1000);
    }

    /**
     * Send API Request to create new lift ride
     * @param liftRide
     * @param resortId
     * @param seasonId
     * @param dayId
     * @param skierId
     * @return ApiResponse
     * @throws ApiException
     */
    public static ApiResponse createNewLiftRide(LiftRide liftRide, int resortId, String seasonId, String dayId, int skierId) throws ApiException
    {
        long timeStampBeforeRequest = System.currentTimeMillis();
        ApiResponse response = skiersApi.writeNewLiftRideWithHttpInfo(liftRide, resortId, seasonId, dayId, skierId);
        long timeStampAfterRequest = System.currentTimeMillis();

        // Increment total requests count
        totalRequests.incrementAndGet();

        // If request is processed successfully, increment successful requests count
        if (ApiHelper.IsApiRequestSuccessful(response))
        {
            successfulRequests.incrementAndGet();
        }

        addToArrayList(timeStampBeforeRequest, timeStampAfterRequest, response.getStatusCode());

        return response;
    }

    private static synchronized void addToArrayList(long startTime, long endTime, int responseCode){
        long latency = endTime - startTime;

        if (requestList.size() == 0) {
            requestList.add(new String[]
                    { "Start time", "Request Type", "Latency", "Response Code"});
        } else {
            requestList.add(new String[]
                    { String.valueOf(startTime), "POST", String.valueOf(latency), String.valueOf(responseCode)});
        }
        latencyList.add(latency);
    }

    /**
     * Get Total number of requests sent to the API server
     * @return
     */
    public static int getTotalRequests()
    {
        return totalRequests.get();
    }

    /**
     * Get number of requests that completed successfully
     * @return
     */
    public static int getSuccessfulRequests()
    {
        return successfulRequests.get();
    }

    /**
     * Get start time, request type, latency and response code data for all requests
     * @return
     */
    public static CopyOnWriteArrayList<String[]> getRequestList()
    {
        return requestList;
    }

    /**
     * Get list of request latencies
     * @return
     */
    public static CopyOnWriteArrayList<Long> getLatencyList()
    {
        return latencyList;
    }
}

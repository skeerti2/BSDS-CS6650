package helpers;

import io.swagger.client.ApiResponse;

import java.util.concurrent.ThreadLocalRandom;

public class ApiHelper
{
    /**
     * Check if an API request completed successfully, using the API Response status code
     * @param response
     * @return True is request completed successfully, else false
     */
    public static Boolean IsApiRequestSuccessful(ApiResponse response)
    {
        // return true if response status code is between 200 - 299
        return response != null && response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }
}

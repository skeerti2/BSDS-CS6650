import api.SkiersApiWrapper;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import tasks.PhaseExecutor;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CountDownLatch;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientOnehw {
    private static double getTheoriticalLatency()
    {
        long startTime;
        long endTime;
        long timeDiffSum= 0L;
        int numRequests = 10000;
        long theoriticalThroughput = 0L;
        for (int i = 0; i < numRequests; i++) {
            startTime = System.currentTimeMillis();
            LiftRide liftRide = new LiftRide().liftID(10).time(8).waitTime(10);
            try {
                SkiersApiWrapper.createNewLiftRide(liftRide, 3, "34", "4", (int)(100000 * Math.random()));
            } catch (Exception e) {
                continue;
            }
            endTime = System.currentTimeMillis();
            timeDiffSum += (endTime - startTime);
        }
        theoriticalThroughput = numRequests / (timeDiffSum/1000);
        return theoriticalThroughput;
    }

    public static void RunPhases(int numSkiers, int numRuns, int numLifts, int numThreads) throws InterruptedException
    {
        // Start timer
        long startTimestamp = System.currentTimeMillis();

        // Phase 1
        int phaseOneThreadCount = numThreads/4;
        PhaseExecutor phase1 = new PhaseExecutor(phaseOneThreadCount, numSkiers, numRuns, numLifts, numThreads, 0, 90);
        phase1.startPhase();
        phase1.awaitCompletion(0.8);

        // Phase 2
        int phaseTwoRequestsPerThread = (int) ((numRuns*0.6)*(numSkiers/numThreads));
        PhaseExecutor phase2 = new PhaseExecutor(phaseTwoRequestsPerThread, numSkiers, numRuns, numLifts, numThreads, 91, 360);
        phase2.startPhase();
        phase2.awaitCompletion(0.8);

        // Phase 3
        int phaseThreeRequestsPerThread = (int) (numRuns*0.1);
        PhaseExecutor phase3 = new PhaseExecutor(phaseThreeRequestsPerThread, numSkiers, numRuns, numLifts, numThreads, 361, 420);
        phase3.startPhase();

        // Wait until all phases complete
        phase1.awaitCompletion();
        phase2.awaitCompletion();
        phase3.awaitCompletion();

        // End timer and record duration
        long endTimeStamp = System.currentTimeMillis();
        long wallTime = (endTimeStamp - startTimestamp)/1000;

        // Report stats
        int totalRequests = SkiersApiWrapper.getTotalRequests();
        int successfulRequests = SkiersApiWrapper.getSuccessfulRequests();
        int unsuccessfulRequests = totalRequests - successfulRequests;

        long throughPut = totalRequests/(wallTime);
        System.out.println("Total requests: "+ totalRequests);
        System.out.println("Number of successful requests sent: " + successfulRequests);
        System.out.println("Number of failed requests sent: " + unsuccessfulRequests);
        System.out.println("wall time is: " + wallTime + " seconds");
        System.out.println("Throughput is: " + throughPut);
        System.out.println("Expected throughput for 10000 requests: " + getTheoriticalLatency());
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter maximum number of threads to run: ");
        int numThreads = in.nextInt();
        while(numThreads > 1024){
            System.out.println("Enter number of threads less than or equal to 1024: ");
            numThreads = in.nextInt();
        }

        System.out.println("Enter the number of skiers ");
        int numSkiers = in.nextInt();
        while(numSkiers > 100000){
            System.out.println("Number of skiers can only be 100000 max: Enter again ");
            numSkiers = in.nextInt();
        }

        // Doing this to cater to the enter key press from the previous input
        in.nextLine();

        int numLifts = 40;
        while(true) {
            System.out.println("Enter number of ski lifts hit enter to assign default value 40 ");
            String inputStr = in.nextLine();
            if (inputStr.equals("")) {
                System.out.println("Assigning default value of 40");
                break;
            } else {
                int inputInt = Integer.parseInt(inputStr);
                if (inputInt < 5 || inputInt > 60) {
                    System.out.println("Error!");
                } else {
                    numLifts = inputInt;
                    break;
                }
            }
        }

        System.out.println("Enter average ski lifts each skier rides each day: ");
        int numRuns = in.nextInt();

        System.out.println("Enter server port number: ");
        int portNumber = in.nextInt();

        // closing scanner
        in.close();

        // Initialize API Client
        SkiersApiWrapper.Initialize(portNumber);

        // Start Phases
        RunPhases(numSkiers, numRuns, numLifts, numThreads);
    }
}



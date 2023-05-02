package tasks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhaseExecutor
{
    private int numThreads;

    private int maxThreads;

    private int numSkiers;

    private int numRuns;

    private int numLifts;

    private int startTime;

    private int endTime;

    private ExecutorService threadPoolExecutor;

    private CountDownLatch countDownLatch;

    public PhaseExecutor(int numThreads, int numSkiers, int numRuns, int numLifts, int maxThreads, int startTime, int endTime)
    {
        threadPoolExecutor = Executors.newFixedThreadPool(numThreads);
        countDownLatch = new CountDownLatch(numThreads);
        this.numThreads = numThreads;
        this.maxThreads = maxThreads;
        this.numSkiers = numSkiers;
        this.numRuns = numRuns;
        this.numLifts = numLifts;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Method to start running tasks (threads) to create lift rides in this phase
     */
    public void startPhase()
    {
        int skierFactor = numSkiers/(maxThreads/4);
        for (int i = 0; i < numThreads; i++){
            int startSkierId = i*skierFactor + 1;
            int endSkierId = (i+1)*skierFactor;
            int numLiftRidesPerThread = (int) ((numRuns*0.2)*(numSkiers/numThreads));
            Runnable newLiftRideTask = new NewLiftRideTask(startSkierId, endSkierId, numLiftRidesPerThread, numLifts, startTime, endTime, countDownLatch);
            threadPoolExecutor.submit(newLiftRideTask);
        }
    }

    /**
     * Wait until at least specified percentage of tasks have completed
     * @param percentage
     */
    public void awaitCompletion(double percentage)
    {
        while(countDownLatch.getCount() <= (int) (percentage*numThreads)) {
            // wait until given percentage of threads have completed
        }
    }

    /**
     * Wait until all tasks have completed
     * @throws InterruptedException
     */
    public void awaitCompletion() throws InterruptedException
    {
        countDownLatch.await();
    }
}

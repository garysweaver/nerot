package nerot.task;

/**
 * Tasks implementing the ability to run once at beginning of schedule and perhaps checking a few times to attempt for it to start correctly.
 */
public interface Primeable {

    /**
     * Executes the task once prior to scheduling, if isPrimeRunOnStart().
     */
    public void primeRun();

    /**
     * Gets whether or not the Task should run once on start.
     *
     * @return true if the Task should run once on start, and false otherwise
     */
    public boolean isPrimeRunOnStart();

    /**
     * Sets whether or not the Task should run once on start.
     *
     * @param runOnceOnStart true if the Task should run once on start, and false otherwise
     */
    public void setPrimeRunOnStart(boolean runOnceOnStart);

    /**
     * Gets maximum number of times to attempt to validate first-run.
     *
     * @return maximum number of times to attempt to validate first-run
     */
    public int getMaxPrimeRunValidationAttempts();

    /**
     * Sets maximum number of times to attempt to validate first-run.
     *
     * @param maxPrimeRunValidationAttempts maximum number of times to attempt to validate first-run
     */
    public void setMaxPrimeRunValidationAttempts(int maxPrimeRunValidationAttempts);

    /**
     * Gets number of milliseconds to sleep between prime run validation attempts.
     *
     * @return number of milliseconds to sleep between prime run validation attempts
     */
    public int getPrimeRunValidationAttemptIntervalMillis();

    /**
     * Sets number of milliseconds to sleep between prime run validation attempts.
     *
     * @param primeRunValidationAttemptIntervalMillis
     *         number of milliseconds to sleep between prime run validation attempts
     */
    public void setPrimeRunValidationAttemptIntervalMillis(int primeRunValidationAttemptIntervalMillis);

    /**
     * Whether or not the Task's prime run was valid.
     *
     * @return true the Task's prime run was valid, and false otherwise
     */
    public boolean isPrimeRunValid();
}

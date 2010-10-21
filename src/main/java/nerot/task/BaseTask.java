package nerot.task;

import nerot.store.Storable;
import nerot.store.Store;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task that has a Store.
 */
public abstract class BaseTask implements Task, Storable, Primeable {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTask.class);

    /**
     * The default prime run on start value, which is true.
     */
    public static final boolean DEFAULT_PRIME_RUN_ON_START = true;

    /**
     * The default number of maximum prime run validation attempts, which is 5.
     */
    public static final int DEFAULT_MAX_PRIME_RUN_VALIDATION_ATTEMPTS = 5;

    /**
     * The default interval in milliseconds between prime run validation attempts, which is 1000 (1 sec).
     */
    public static final int DEFAULT_PRIME_RUN_VALIDATION_ATTEMPT_INTERVAL_MILLIS = 1000;

    private Store store;
    private String storeKey;
    private boolean primeRunOnStart = DEFAULT_PRIME_RUN_ON_START;
    private int maxPrimeRunValidationAttempts = DEFAULT_MAX_PRIME_RUN_VALIDATION_ATTEMPTS;
    private int primeRunValidationAttemptIntervalMillis = DEFAULT_PRIME_RUN_VALIDATION_ATTEMPT_INTERVAL_MILLIS;

    public BaseTask() {
        // To debug issues with the scheduling thread
        if (LOG.isDebugEnabled()) {
            LOG.debug("" + this.getClass().getName() + " instance created");
        }
    }

    /**
     * Execute the task. This is a wrapper around doExecute() that times using Perf4J, logs, stores result, and wraps in
     * a try/catch.
     */
    public void execute() {
        StopWatch stopWatch = new Log4JStopWatch();
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing task: '" + getStoreKey() + "'");
            }
            Object result = doExecute();
            storeResult(result);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Stored result for task: '" + getStoreKey() + "'");
            }
            stopWatch.stop(getStoreKey() + ".success");
        } catch (Throwable t) {
            LOG.error("Task failed: '" + getStoreKey() + "'", t);
            stopWatch.stop(getStoreKey() + ".failure", "" + t);
        }
    }

    /**
     * The implementation of the task, without the storage.
     */
    protected abstract Object doExecute() throws Throwable;

    /**
     * Call execute() asynchronously via TaskRunner in a new Thread.
     */
    public void primeRun() {
        TaskRunner tr = new TaskRunner();
        tr.setTask(this);
        new Thread(tr).start();
    }

    /**
     * Whether or not the Task's prime run was valid.
     *
     * @return true the Task's prime run was valid, and false otherwise
     */
    public boolean isPrimeRunValid() {
        return (store.get(storeKey) != null);
    }

    /**
     * Store value in the Store using the Task's store key.
     *
     * @param value the value to be set on the Store using the Task's store key.
     */
    public void storeResult(Object value) {
        store.set(storeKey, value);
    }

    /**
     * Get the Store.
     *
     * @return the Store
     */
    public Store getStore() {
        return store;
    }

    /**
     * Sets the Store.
     *
     * @param store Set the Store.
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Gets the key for the Store to use for this Task's result.
     *
     * @return the key for the Store to use for this Task's result.
     */
    public String getStoreKey() {
        return storeKey;
    }

    /**
     * Sets the key for the Store to use for this Task's result.
     *
     * @param storeKey the key for the Store to use for this Task's result.
     */
    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    /**
     * Whether or not the Task should run once on start.
     *
     * @return true if the Task should run once on start, and false otherwise
     */
    public boolean isPrimeRunOnStart() {
        return primeRunOnStart;
    }

    /**
     * Sets whether or not the Task should run once on start.
     *
     * @param primeRunOnStart true if the Task should run once on start, and false otherwise
     */
    public void setPrimeRunOnStart(boolean primeRunOnStart) {
        this.primeRunOnStart = primeRunOnStart;
    }

    /**
     * Gets maximum number of times to attempt to validate first-run.
     *
     * @return maximum number of times to attempt to validate first-run
     */
    public int getMaxPrimeRunValidationAttempts() {
        return maxPrimeRunValidationAttempts;
    }

    /**
     * Sets maximum number of times to attempt to validate first-run.
     *
     * @param maxPrimeRunValidationAttempts maximum number of times to attempt to validate first-run
     */
    public void setMaxPrimeRunValidationAttempts(int maxPrimeRunValidationAttempts) {
        this.maxPrimeRunValidationAttempts = maxPrimeRunValidationAttempts;
    }

    /**
     * Gets number of milliseconds to sleep between prime run validation attempts.
     *
     * @return number of milliseconds to sleep between prime run validation attempts
     */
    public int getPrimeRunValidationAttemptIntervalMillis() {
        return primeRunValidationAttemptIntervalMillis;
    }

    /**
     * Sets number of milliseconds to sleep between prime run validation attempts.
     *
     * @param primeRunValidationAttemptIntervalMillis
     *         number of milliseconds to sleep between prime run validation attempts
     */
    public void setPrimeRunValidationAttemptIntervalMillis(int primeRunValidationAttemptIntervalMillis) {
        this.primeRunValidationAttemptIntervalMillis = primeRunValidationAttemptIntervalMillis;
    }

    protected void finalize() throws Throwable {
        // To debug issues with the scheduling thread getting killed off prematurely without warning
        if (LOG.isDebugEnabled()) {
            LOG.debug("finalize() called on " + this.getClass().getName() + ", storeKey='" + storeKey + "'");
        }
        super.finalize();
    }
}
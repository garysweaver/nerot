package nerot.store;

/**
 * Has a store key.
 */
public interface Storer {

    /**
     * Gets the key for the Store to use for this Task's result.
     *
     * @return the key for the Store to use for this Task's result.
     */
    public String getStoreKey();
}

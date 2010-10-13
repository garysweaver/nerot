package nerot.store;

/**
 * Something that can store its result.
 */
public interface Storable {

    /**
     * Sets the Store.
     *
     * @param store the Store to set.
     */
    public void setStore(Store store);

    /**
     * Gets the key for the Store to use for this Task's result.
     *
     * @return the key for the Store to use for this Task's result.
     */
    public String getStoreKey();

    /**
     * Sets the key for the Store to use for this Task's result.
     *
     * @param storeKey the key for the Store to use for this Task's result.
     */
    public void setStoreKey(String storeKey);
}

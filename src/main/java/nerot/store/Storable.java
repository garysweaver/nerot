package nerot.store;

/**
 * Something that has a Store and allows read/write access to a store key.
 */
public interface Storable extends Storer {

    /**
     * Sets the Store.
     *
     * @param store the Store to set.
     */
    public void setStore(Store store);

    /**
     * Sets the key for the Store to use for this Task's result.
     *
     * @param storeKey the key for the Store to use for this Task's result.
     */
    public void setStoreKey(String storeKey);
}

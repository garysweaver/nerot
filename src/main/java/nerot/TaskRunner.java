package nerot;


/**
 * Has a Task and run() calls execute() on the Task.
 */
public class TaskRunner implements Runnable {

    private Task task = null;
    
    /**
     * Set the Task.
     *
     * @param task the Task
     */
    public void setTask(Task task) {
        this.task = task;
    }
    
    /**
     * Calls execute() on Task.
     */
    public void run() {
        task.execute();
    }
}
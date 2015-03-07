package gui;

/**
 * A runnable where a parameter can be passed by initialization.
 * @author Maximilian Awiszus
 * 
 * @param <T> type of parameter which can be passed by initialization
 */
public abstract class PRunnable<T> implements Runnable {
	/**
	 * Paramerter which is set in the constructor.
	 */
    private T t;

    /**
     * Initialize a runnable and set a variable to it.
     * @param p value the parameter will be set 
     */
    public PRunnable(T t) {
        this.t = t;
    }

	@Override
	public void run() {
		run(t);
	}
	
	public abstract void run(T t);

}

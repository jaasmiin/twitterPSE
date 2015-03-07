package gui;

/**
 * Runnable which has run method with an parameter.
 * @author Maximilian Awiszus
 *
 * @param <S> type of parameter
 */
public abstract class PRunnable<S> implements Runnable {
    private S s;

	/**
	 * Create a new runnable which following parameter
	 * @param s parameter
	 */
    public PRunnable(S s) {
        this.s = s;
    }

	@Override
	public void run() {
		run(s);
	}
	
	/**
	 * Method which is called when run method of
	 * runnable is called.  
	 * @param s parameter
	 */
	public abstract void run(S s);

}

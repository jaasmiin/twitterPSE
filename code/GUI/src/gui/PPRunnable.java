package gui;

/**
 * Runnable which has run method with two parameters.
 * @author Maximilian Awiszus
 *
 * @param <S> first parameter
 * @param <T> second parameter
 */
public abstract class PPRunnable<T, S> implements Runnable {
    private T t;
    private S s;

	/**
	 * Create a new runnable which following two parameters
	 * @param s first parameter
	 * @param t second parameter
	 */
    public PPRunnable(T t, S s) {
        this.t = t;
        this.s = s;
    }

    @Override
    public void run() {
        run(t, s);
    }

	/**
	 * Method which is called when run method of
	 * runnable is called.  
	 * @param s first parameter
	 * @param t second parameter
	 */
    public abstract void run(T t, S s);
}

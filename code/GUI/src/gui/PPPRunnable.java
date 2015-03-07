package gui;
/**
 * Runnable which has run method with three parameters.
 * @author Maximilian Awiszus
 *
 * @param <S> first parameter
 * @param <T> second parameter
 * @param <U> third parameter
 */
public abstract class PPPRunnable<S, T, U> implements Runnable {
	private S s;
	private T t;
	private U u;
	
	/**
	 * Create a new runnable which following three parameters
	 * @param s first parameter
	 * @param t second parameter
	 * @param u third parameter
	 */
	public PPPRunnable(S s, T t, U u) {
		this.s = s;
		this.t = t;
		this.u = u;
	}
	
	@Override
	public void run() {
		run(s, t, u);
	}
	
	/**
	 * Method which is called when run method of
	 * runnable is called.  
	 * @param s first parameter
	 * @param t second parameter
	 * @param u third parameter
	 */
	public abstract void run(S s, T t, U u);
}

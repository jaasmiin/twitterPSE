package gui;
/**
 * Runnable which has run method with four parameters.
 * @author Maximilian Awiszus
 *
 * @param <S> first parameter
 * @param <T> second parameter
 * @param <U> third parameter
 * @param <V> fourth parameter
 */
public abstract class PPPPRunnable<S, T, U, V> implements Runnable {
	private S s;
	private T t;
	private U u;
	private V v;
	
	/**
	 * Create a new runnable whith following four parameters
	 * @param s first parameter
	 * @param t second parameter
	 * @param u third parameter
	 * @param v fourth parameter
	 */
	public PPPPRunnable(S s, T t, U u, V v) {
		this.s = s;
		this.t = t;
		this.u = u;
		this.v = v;
	}
	
	@Override
	public void run() {
		run(s, t, u, v);
	}
	
	 
	/**
	 * Method which is called when run method of
	 * runnable is called.  
	 * @param s first parameter
	 * @param t second parameter
	 * @param u third parameter
	 * @param v fourth parameter
	 */
	public abstract void run(S s, T t, U u, V v);
}

package gui;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 
 * @author 
 *
 * @param <T>
 */
public abstract class CallablePSE<T> implements Callable<T> {
    
    /**
     * 
     */
	protected List<Integer> p1, p2, p3;
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public CallablePSE(List<Integer> p1, List<Integer> p2, List<Integer> p3) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
}

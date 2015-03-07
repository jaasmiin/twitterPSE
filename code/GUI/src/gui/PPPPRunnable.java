package gui;

public abstract class PPPPRunnable<S, T, U, V> implements Runnable {
	S s;
	T t;
	U u;
	V v;
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
	
	abstract public void run(S s, T t, U u, V v);
}

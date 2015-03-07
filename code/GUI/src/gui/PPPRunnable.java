package gui;

public abstract class PPPRunnable<S, T, U> implements Runnable {
	S s;
	T t;
	U u;
	public PPPRunnable(S s, T t, U u) {
		this.s = s;
		this.t = t;
		this.u = u;
	}
	
	@Override
	public void run() {
		run(s, t, u);
	}
	
	abstract public void run(S s, T t, U u);
}

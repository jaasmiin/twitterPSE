package gui;

public abstract class PPRunnable<T,S> implements Runnable {
	T t;
	S s;
	public PPRunnable(T t, S s) {
		this.t = t;
		this.s = s;
	}
	
	@Override
	public void run() {
		run(t, s);
	}
	
	abstract public void run(T t, S s);
}

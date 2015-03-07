package gui;

/**
 * 
 * @author
 *
 * @param <T>
 * @param <S>
 */
public abstract class PPRunnable<T, S> implements Runnable {
    T t;
    S s;

    /**
     * 
     * @param t
     * @param s
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
     * 
     * @param t
     * @param s
     */
    public abstract void run(T t, S s);
}

package gui;

/**
 * 
 * @author
 * @version 1.0
 * 
 * @param <T>
 */
public abstract class RunnableParameter<T> implements Runnable {
    
    protected T parameter;

    public RunnableParameter(T p) {
        parameter = p;
    }

}

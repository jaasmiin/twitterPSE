package gui;

/**
 * A runnable where a parameter can be passed by initialization.
 * @author Maximilian Awiszus
 * 
 * @param <T> type of parameter which can be passed by initialization
 */
public abstract class RunnableParameter<T> implements Runnable {
    protected T parameter;

    /**
     * Initialize a runnable and set the protected variable <code>parameter</code> to <code>p</code>.
     * The variable <code>parameter</code> can be accessed in the run method.
     * @param p value the parameter will be set 
     */
    public RunnableParameter(T p) {
        parameter = p;
    }

}

package gui;

public abstract class RunnableParameter<Type> implements Runnable {
	protected Type parameter;
	public RunnableParameter(Type p) {
		parameter = p;
	}

}

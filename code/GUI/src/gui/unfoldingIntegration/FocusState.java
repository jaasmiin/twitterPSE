package gui.unfoldingIntegration;

/**
 * This class models the states of focus, the map can have.
 * 
 * 
 * 
 * @author Philipp
 *
 */
public abstract class FocusState {

	/**
	 * The successor state.
	 */
	protected FocusState nextState;
	
	/**
	 * The controller that models the map.
	 */
	protected IntegrationController reference;
	
	/**
	 * Generates an instance of a FocusState.
	 * 
	 * @param reference
	 */
	public FocusState(IntegrationController reference) {
		this.reference = reference;
	}
	
	/**
	 * Changes the state of the reference.
	 */
	public final void changeState() {		
		FocusState successor = next();
		
		if (successor != this) {
			this.exit();
			successor.entry();
			reference.setState(successor);
		}
	}
	
	/**
	 * Gets the successor state.
	 */
	protected abstract FocusState next();
	
	/**
	 * This method is called, when this state is entered.
	 */
	protected abstract void entry();
	
	/**
	 * This method is called when this state is left.
	 */
	protected abstract void exit();
	
	/**
	 * This method is called when the gui has been started.
	 */
	protected void handleGuiStarted() {
		reference.positionDialogue();
	}
	
	/**
	 * Called whenever the window of the application is resized.
	 */
	protected void handleWindowResize() {
		reference.positionDialogue();
	}
	
	/**
	 * Called whenever the window of the application is closed.
	 */
	protected void handleClose() {
		reference.closeMap();
	}
	
	/**
	 * Called whenever the map has been selected.
	 */
	protected abstract void handleMapSelected();
	
	/**
	 * Called whenever the map has been unselected.
	 */
	protected abstract void handleMapUnselected();
	
	/**
	 * Called whenever the focus has changed.
	 */
	protected abstract void handleWindowFocusChanged();
	
	public abstract String toString();

}

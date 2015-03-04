package gui.unfoldingIntegration;

/**
 * This class models the Invisible state.
 * 
 * Neither GUI nor map have the focus or are visible.
 * 
 * @author Philipp
 *
 */
public class Invisible extends FocusState {

	public Invisible(IntegrationController reference) {
		super(reference);
	}

	@Override
	protected void entry() {
		reference.showDialogue(false);

	}

	@Override
	protected void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleMapSelected() {
		// Gui is not on screen so user can't select
	}

	@Override
	protected void handleMapUnselected() {
		// Gui is not on screen so user can't select
	}

	@Override
	protected void handleWindowFocusChanged() {
		// in this state this implies that gui has gained the focus.
		if (reference.isMapSelected()) {
			nextState = new MapSelected(reference);
		} else {
			nextState = new MapUnselected(reference);
		}
	}

	@Override
	protected FocusState next() {
		if (nextState == null) {
			nextState = this;
		}
		
		return nextState;
	}

	@Override
	public String toString() {
		return "Invisible";
	}

}

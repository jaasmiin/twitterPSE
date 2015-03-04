package gui.unfoldingIntegration;

/**
 * This class models the state where
 * the GUI is visible and has the focus,
 * but the map is not selected.
 * 
 * @author Philipp
 *
 */
public class MapUnselected extends FocusState {

	public MapUnselected(IntegrationController reference) {
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
		nextState = new MapSelected(reference);
	}

	@Override
	protected void handleMapUnselected() {
		// map is already unselected
	}

	@Override
	protected void handleWindowFocusChanged() {
		// Because map is invisible in this state it
		// is impossible that the map has gained the focus.
		// Therefore the focus now belongs to another application.
		nextState = new Invisible(reference);
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
		return "MapUnselected";
	}

}

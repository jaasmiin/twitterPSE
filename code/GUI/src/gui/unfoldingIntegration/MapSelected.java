package gui.unfoldingIntegration;

/**
 * This class models the state where 
 * GUI and map are visible and the GUI has the focus.
 * 
 * @author Philipp
 *
 */
public class MapSelected extends FocusState {

	public MapSelected(IntegrationController reference) {
		super(reference);
	}

	@Override
	protected void entry() {
		reference.showDialogue(true);
	}

	@Override
	protected void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void handleMapSelected() {
		// map is already selected
	}

	@Override
	protected void handleMapUnselected() {
		nextState = new MapUnselected(reference);
	}

	@Override
	protected void handleWindowFocusChanged() {
		if (!(reference.hasGuiFocus() || reference.hasMapFocus())) {
			// foreign app has focus now
			nextState = new Invisible(reference);
		} else if (!reference.hasGuiFocus() && reference.hasMapFocus()) {
			// GUI lost focus to map
			nextState = new MapFocused(reference);
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
		return "MapSelected";
	}

}

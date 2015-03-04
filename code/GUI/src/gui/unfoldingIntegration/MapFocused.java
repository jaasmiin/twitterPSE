package gui.unfoldingIntegration;

/**
 * This class models the state where
 * both map and GUI are visible and the 
 * map has the focus.
 * 
 * @author Philipp
 *
 */
public class MapFocused extends FocusState {

	public MapFocused(IntegrationController reference) {
		super(reference);
	}

	@Override
	protected void entry() {
		// can only reach this state from MapSelected
		// where dialogue is already shown
	}

	@Override
	protected void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void handleMapSelected() {
		// in this state map is already selected
	}

	@Override
	protected void handleMapUnselected() {
		nextState = new MapUnselected(reference);
	}

	@Override
	protected void handleWindowFocusChanged() {
		if (!(reference.hasGuiFocus() || reference.hasMapFocus())) {
			// foreign app now has the focus
			nextState = new Invisible(reference);
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
		return "MapFocused";
	}
}

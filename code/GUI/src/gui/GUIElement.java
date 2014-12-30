package gui;

public interface GUIElement {
	public static enum UpdateType {TWEET, CATEGORY, LOCATION};
	public void update(UpdateType type);
	
}

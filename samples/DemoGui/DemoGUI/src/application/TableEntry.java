package application;

public class TableEntry {

	private String aspect;
	private String value;
	
	public TableEntry(String aspect, String absoluteValue) {
		this.aspect = aspect;
		this.value = absoluteValue;
	}
	
	public String getAspect() {
		return aspect;
	}
	
	public String getValue() {
		return value;
	}

}

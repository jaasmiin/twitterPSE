package application;

import java.util.LinkedList;
import java.util.List;

public class DemoProvider {

	String[] countries;
	String[] categories;
	List<String> selectedCountries;
	List<String> selectedCategories;
	
	
	public DemoProvider() {
		countries = new String[]{"Asia", "NorthAmerica", "SouthAmerica", "Australia", "Africa", "Europe"};
		categories = new String[]{"Human", "Building", "Location", "Nature", "Machine"};
		selectedCountries = new LinkedList<String>();
		selectedCategories = new LinkedList<String>();
	}
	
	public String[] getCountries() {
		return countries;
	}
	
	public String[] getCategories() {
		return categories;
	}
	
	public List<String> getSelectedCountries() {
		return selectedCountries;
	}
	
	public List<String> getSelectedCategories() {
		return selectedCategories;
	}
	
	public void selectCountry(String country) {
		if (!selectedCountries.contains(country)) {
			selectedCountries.add(country);
		}		
	}
	
	public void selectCategory(String category) {
		if (!selectedCountries.contains(category)) {
			selectedCountries.add(category);
		}		
	}
	
	

}

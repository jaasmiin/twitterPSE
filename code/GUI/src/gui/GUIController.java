package gui;
	
import gui.GUIElement.UpdateType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.javafx.scene.traversal.WeightedClosestCorner;

import mysql.AccessData;
import mysql.DBgui;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class GUIController extends Application implements Initializable {
	@FXML
	private Pane paSelectionOfQuery;
	@FXML
	private TextField txtSearch;
	@FXML
	private Label lblInfo;
	
	private static GUIController instance = null;
	private ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
	
	private static DBgui db;
	private Category categories;
	private List<Location> locations = new ArrayList<Location>();
	private List<Account> accounts = new ArrayList<Account>();
	private TweetsAndRetweets dataByLocation = new TweetsAndRetweets();
	private List<Account> dataByAccount = new ArrayList<Account>();
	
	private HashSet<Integer> selectedCategories = new HashSet<Integer>();
	private HashSet<Integer> selectedLocations = new HashSet<Integer>();
	private HashSet<Integer> selectedAccounts = new HashSet<Integer>();
	private Date selectedStartDate, selectedEndDate;
	
	public static GUIController getInstance() {
		if (instance == null) {
			System.out.println("Fehler in GUIController getInstance(). Application nicht gestartet. (instance == null).");
		}
		return instance;
	}
	
	public GUIController() {
		super();
		instance = this; // TODO: JavaFX creates two instances of GUIController?
		System.out.println("public GUIController()");
	}
	
	@Override
	public void start(final Stage primaryStage) {
			try {
				Parent parent = FXMLLoader.load(GUIController.class.getResource("GUIView.fxml"));
				Scene scene = new Scene(parent, 800, 600);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setTitle("PSE-Twitter");
				primaryStage.setScene(scene);
				primaryStage.show();
				scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						if (db != null && db.isConnected()) {
							System.out.println("Verbindung mit Datenbank wird geschlossen...");
							db.disconnect();
							System.out.println("Verbindung geschlossen.");
						}
					}
				});
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private Runnable rnbInitDBConnection = new Runnable() {
		
		@Override
		public void run() {
			boolean success = true;
			lblInfo.setText("Verbindung mit DB wird aufgebaut...");
			AccessData accessData = null;
			try {
				accessData = getDBAccessData();
			} catch (IOException e1) {
				success = false;
			}
			if (success) {
				try {
					db = new DBgui(accessData, getLogger() );
				} catch (SecurityException | IOException | InstantiationException | IllegalAccessException
						| ClassNotFoundException e) {
					success = false;
				}
				if (success) {
					try {
						db.connect();
					} catch (SQLException e) {
	//					e.printStackTrace();
						success = false;
					}
				} else {
					setInfo("Fehler, es konnte keine Verbindung zur DB hergestellt werden.");
				}
			} else {
				setInfo("Fehler, es konnten konnten keine Login Daten geladen werden.");
			}
			if (success) {
				setInfo("Erfolreich mit DB verbunden.");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						reloadAll();
					}
				});
			}
		}
	};
    
	private AccessData getDBAccessData() throws IOException {
		String path = System.getenv("APPDATA") + "\\KIT\\twitterPSE\\dblogin.txt";
		if (!(new File(path)).isFile()) {
			path = System.getenv("APPDATA") + "\\KIT";
			File file = new File(path);
			if (!file.isDirectory()) {
				if (!file.mkdir()) {
					throw new IOException();
				}
			}
			path += "\\twitterPSE";
			file = new File(path);
			if (!file.isDirectory()) {
				if (!file.mkdir()) {
					throw new IOException();
				}
			}
			path += "\\dblogin.txt";
			file = new File(path);
			if (!file.isFile()) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				Scanner in = new Scanner(System.in);
				System.out.print("Host: ");
				writer.write(in.nextLine() + "\n");
				System.out.print("Port: ");
				writer.write(in.nextLine() + "\n");
				System.out.print("Database: ");
				writer.write(in.nextLine() + "\n");
				System.out.print("Username: ");
				writer.write(in.nextLine() + "\n");
				System.out.print("Password: ");
				writer.write(in.nextLine() + "\n");
				in.close();
				writer.close();
			}
			
		}
		BufferedReader in = new BufferedReader(new FileReader(path));;
		String host = in.readLine();
		String port = in.readLine();
		String dbName = in.readLine();
		String userName = in.readLine();
		String password = in.readLine();
		in.close();
		return new AccessData(host, port, dbName, userName, password);
	}
	
	private Logger getLogger() throws SecurityException, IOException {
		File directory = new File("logs");
		if (!directory.isDirectory()) {
			directory.mkdir();
		}
        Logger l = Logger.getLogger("logger");
        new File(directory.getPath() + "\\LogFile.log").createNewFile();
        FileHandler fh = new FileHandler(directory.getPath() + "\\LogFile.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }
	
	private void reloadLocation() {
		locations = db.getLocations();	
		System.out.println("Location size: " + locations.size());
		update(UpdateType.LOCATION);
	}
	
	private void reloadAccounts() {
		accounts = db.getAccounts("Abc");
//		TODO: remove test code:
//		accounts.clear();
//		accounts.add(new Account(1, 243, "Max Mustermann", true, null, 1, 0, null, null, null));
//		accounts.add(new Account(2, 122, "Max Mustermann", true, null, 2, 0, null, null, null));
//		accounts.add(new Account(5, 432, "Max Mustermann", true, null, 3, 0, null, null, null));
		update(UpdateType.ACCOUNT);
	}
	
	private void reloadCategories() {
//		categories = db.getCategories();
		categories = new Category(1, "test", 0);
//		System.out.println("Größe: " + categories.size());
//		TODO: remove test code:
//		categories.clear();
//		categories.add(new Category(31, "Musiker", 0));
//		categories.add(new Category(22, "Schriftsteller", 0));
//		categories.add(new Category(13, "Politiker", 0));
		update(UpdateType.CATEGORY);
	}
	
	private void reloadData() {
		final Integer[] selectedCategoriesArray = selectedCategories.toArray(new Integer[selectedCategories.size()]);
		final Integer[] selectedLocationsArray = selectedLocations.toArray(new Integer[selectedLocations.size()]);
		final Integer[] selectedAccountsArray = selectedAccounts.toArray(new Integer[selectedAccounts.size()]);
		final boolean dateSelected = selectedStartDate != null && selectedEndDate != null;
		boolean success = true;
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					dataByLocation = db.getSumOfData(selectedCategoriesArray, selectedLocationsArray, selectedAccountsArray, dateSelected);
				} catch (IllegalArgumentException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					dataByAccount = db.getAllData(selectedCategoriesArray, selectedLocationsArray, selectedAccountsArray, dateSelected);
				} catch (IllegalArgumentException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
		t1.start();
		t2.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			success = false;
			setInfo(e.getLocalizedMessage());
		}
		try {
			t2.join();
		} catch (InterruptedException e) {
			success = false;
			setInfo(e.getLocalizedMessage());
		}
		if (success) {
			update(UpdateType.TWEET);
		}
	}
	
	private void reloadAll() {
		reloadAccounts();
		reloadCategories();
		reloadLocation();
	}
	
	private void setInfo(String text) {
		Platform.runLater(new InfoRunnable(lblInfo, text));
	}
	
	/**
	 * Get list of all categories
	 * @return list of categories
	 */
	public Category getCategories() {
		return categories;
	}
	
	/**
	 * Get categories containing text
	 * @param text which categories should contain
	 * @return list of categories containing text
	 */
	public ArrayList<Category> getCategories(String text) {
		ArrayList<Category> filteredCategories = new ArrayList<Category>();
//		for (Category category : categories) {
//			if (category.toString().contains(text)) {
//				filteredCategories.add(category);
//			}
//		}
		// TODO: implement
		return filteredCategories;
	}
	
	/**
	 * Get list of all locations.
	 * @return list of locations
	 */
	public List<Location> getLocations() {
		return locations;
	}
	
	/**
	 * Get locations containing text
	 * @param text which locations should contain
	 * @return list of locations containing text
	 */
	public ArrayList<Location> getLocations(String text) {
		ArrayList<Location> filteredLocations = new ArrayList<Location>();
		for (Location location : locations) {
			if (location.toString().contains(text)) {
				filteredLocations.add(location);
			}
		}
		return filteredLocations;
	}
	
	/**
	 * Set if a category is selected.
	 * @param id of category
	 * @param selected is true if category should be selected, false otherwise
	 */
	public void setCategory(int id, boolean selected) {
		if (selected) {
			selectedCategories.add(id);
		} else {
			selectedCategories.remove(id);
		}
		reloadData();
	}
	
	/**
	 * Set if a location is selected.
	 * @param id of location
	 * @param selected is true if location should be selected, false otherwise
	 */
	public void setLocation(int id, boolean selected) {
		if (selected) {
			selectedLocations.add(id);
		} else {
			selectedLocations.remove(id);
		}
		reloadData();
	}
	
	/**
	 * Get list of all accounts.
	 * @return a list of all accounts
	 */
	public ArrayList<String> getSelectedAccounts() {
		// TODO: add correct code
		ArrayList<String> a = new ArrayList<String>();
		a.add("ZDF");
		return a;
	}
	
	/**
	 * Get list of selected categories.
	 * @return selected categories
	 */
	public ArrayList<String> getSelectedCategories() {
		// TODO: add correct code
		ArrayList<String> a = new ArrayList<String>();
		a.add("Musiker");
		a.add("Schornsteinfeger");
		return a;
	}
	/**
	 * Get list of selected locations.
	 * @return selected locations
	 */
	public ArrayList<String> getSelectedLocations() {
		// TODO: add correct code
		ArrayList<String> a = new ArrayList<String>();
		a.add("Deutschland");
		a.add("Frankreich");
		a.add("Österreich");
		return a;
	}
	
	/**
	 * Set of an account is selected.
	 * @param id of account
	 * @param selected is true if account should be selected, false otherwise
	 */
	public void setAccount(int id, boolean selected) {
		if (selected) {
			selectedAccounts.add(id);
		} else {
			selectedAccounts.remove(id);
		}
		reloadData();
	}
	
	/**
	 * Get data grouped by account.
	 * @return
	 */
	public List<Account> getDataByAccount() {
		return dataByAccount;
	}
	
	/**
	 * Get data grouped by location.
	 * @return
	 */
	public TweetsAndRetweets getDataByLocation() {
		return dataByLocation;
	}
	/**
	 * Select start and end or one day if start and end date are the same.
	 * Earlier date will automatically be taken as start date.
	 * If one date is null selected date range will be removed.
	 * @param startDate of the date range
	 * @param endDate of the date range
	 */
	public void setDateRange(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			selectedStartDate = null;
			selectedEndDate = null;
		} else {
			if(startDate.before(endDate)) {
				selectedStartDate = startDate;
				selectedEndDate = endDate;
			} else {
				selectedStartDate = endDate;
				selectedEndDate = startDate;
			}
		}
	}
	
	private void update(UpdateType type) {
		Platform.runLater(new RunnableParameter<UpdateType>(type) {
			@Override
			public void run() {
				for (GUIElement element : guiElements) {
					element.update(parameter);
				}
			}
		});
	}
	
	/**
	 * Add a GUIElement as subscriber
	 * @param element which will later be notified on update.
	 */
	public void subscribe(GUIElement element) {
		guiElements.add(element);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		new Thread(rnbInitDBConnection).start();
	}
	
}

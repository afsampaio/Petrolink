package PetroLink;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import DatabaseHandler.DBReader;
import DatabaseHandler.Writer;


public class Simulator extends Thread{
	private ArrayList<Coordinates> coordinate_registry;
	private Connection dbConnection = null;
	
	
	private DBReader databaseReader;
	private Writer writer;
	
	private int id;
	private String databaseName;
	private String csvName;
	private String xmlName;
	
	private boolean active;
	
	public Simulator(int id, String databaseName) {
		this.id = id;
		this.databaseName = databaseName;
		this.xmlName = "xmlFile.xml";
		this.csvName = "csvFile.csv";
		this.coordinate_registry = new ArrayList<>();
		this.active = true;
	}
	
	public Simulator(int id, String databaseName, String csvName, String xmlName) {
		this.id = id;
		this.databaseName = databaseName;
		this.xmlName = xmlName;
		this.csvName = csvName;
		this.coordinate_registry = new ArrayList<>();
		
		this.active = true;
	}
	
	public Connection makeDbConnection() {
		Connection conn = null;
		
		try {
			String url = "jdbc:sqlite:" + this.databaseName;
			conn = DriverManager.getConnection(url);
		} catch(SQLException e){
			System.out.println("Connection error: " + e.getMessage());
		}
		
		return conn;
	}
	
	public synchronized void insertIntoCoordinateRegistry(Coordinates coords) {
		this.coordinate_registry.add(coords);
		notify();
	}
	
	public synchronized Coordinates getFromCoordinateRegistry(int index) {
		Coordinates coords = null;
		if(this.coordinate_registry.size() != 0 && this.coordinate_registry.size() >= index) {
			coords =  this.coordinate_registry.get(index);
		}
		else {
			while(this.active == true) {
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Problem with semaphore: " + e.getMessage());
				}
				if(this.coordinate_registry.size() >= index) {
					return this.coordinate_registry.get(index);
				}
			}
		}
		
		return coords;
	}
	
	public synchronized void deleteFromCoordinateRegistry(int index) {
		this.coordinate_registry.remove(index);
	}
	
	public synchronized void setStatus(boolean b) {
		this.active = b;
		notify();
	}
	
	public boolean getStatus() {
		return this.active;
	}
	
	public boolean getStatusAndEnd(int index) {
		if(this.coordinate_registry.size() > index) {
			return true;
		}
		return this.active;
	}
	
	public void run() {
		this.dbConnection = makeDbConnection();
		
		if(this.dbConnection == null) {
			System.out.println("Error establishing connection, aborting.");
			return;
		}
		
		this.writer = new Writer(this.databaseName, this, this.xmlName, this.csvName);
		this.writer.start();
		
		this.databaseReader = new DBReader(this.databaseName, this.dbConnection, this);
		this.databaseReader.start();
		
		try {
			this.databaseReader.join(); System.out.println("DatabaseReader Joined.");
			this.writer.join(); System.out.println("Writer Joined.");
		} catch (InterruptedException e) {
			System.out.println("Problem while waiting for threads to finished.");
		}
	}
}
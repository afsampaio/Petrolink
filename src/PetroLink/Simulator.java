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
	private ArrayList<DataEntry> data;
	private Connection dbConnection = null;
	
	
	private DBReader databaseReader;
	private Writer writer;
	//private Writer databaseWriter;
	//private Writer xmlWriter;
	//private Writer csvWriter;
	
	private int id;
	private String databaseName;
	
	public Simulator(int id, String databaseName) {
		this.id = id;
		this.databaseName = databaseName;
		this.coordinate_registry = new ArrayList<>();
	}
	
	public Connection makeDbConnection() {
		Connection conn = null;
		
		try {
			//To test in other operating systems, path must be changed;
			String url = "jdbc:sqlite:C:\\Users\\andr3mp\\eclipse-workspace\\PetroLinkTest\\petrolink_challenge.db";
			conn = DriverManager.getConnection(url);
			
			System.out.println("Connection has been established");
			
		} catch(SQLException e){
			System.out.println("Connection error: " + e.getMessage());
		}
		
		return conn;
	}
	
	public void insertIntoCoordinateRegistry(Coordinates coords) {
		this.coordinate_registry.add(coords);
	}
	
	public void run() {
		this.dbConnection = makeDbConnection();
		
		if(this.dbConnection == null) {
			System.out.println("Error establishing connection, aborting.");
			return;
		}
		
		this.databaseReader = new DBReader(this.databaseName, this.dbConnection, this);
		this.databaseReader.start();
		
		try {
			this.databaseReader.join();
		} catch (InterruptedException e) {
			System.out.println("Problem while waiting for threads to finished.");
		}
		
		this.writer = new Writer(this.databaseName, this.dbConnection, "xmlFile.xml", "csvFile.xml", "jsonFile.json");
		
		System.out.println("Vou agora escrever " + this.coordinate_registry.size() + " elementos para o ficheiro.");
		for(Coordinates coords : this.coordinate_registry) {
			this.writer.writeAllFormats(coords);
		}
		this.writer.closeAllFormats();
	}
}
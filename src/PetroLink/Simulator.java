package PetroLink;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import DatabaseHandler.CSVHandler;
import DatabaseHandler.DBReader;
import DatabaseHandler.SQLiteHandler;
import DatabaseHandler.XMLHandler;


public class Simulator extends Thread{
	private ArrayList<Coordinates> coordinate_registry;
	private Connection dbConnection = null;
	
	private DBReader databaseReader;
	private CSVHandler csvHandler;
	private XMLHandler xmlHanlder;
	private SQLiteHandler sqliteHandler;
	
	private int id;
	private String databaseName;
	private String csvName;
	private String xmlName;
	
	final Lock lock;
	final Condition noValue;
	private boolean active;
	
	public Simulator(int id, String databaseName) {
		this.id = id;
		this.databaseName = databaseName;
		this.xmlName = "xmlFile.xml";
		this.csvName = "csvFile.csv";
		this.coordinate_registry = new ArrayList<>();
		this.active = true;
		lock = new ReentrantLock();
		noValue = lock.newCondition();
	}
	
	public Simulator(int id, String databaseName, String csvName, String xmlName) {
		this.id = id;
		this.databaseName = databaseName;
		this.xmlName = xmlName;
		this.csvName = csvName;
		this.coordinate_registry = new ArrayList<>();
		
		this.active = true;
		lock = new ReentrantLock();
		noValue = lock.newCondition();
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
	
	/*public synchronized void insertIntoCoordinateRegistry(Coordinates coords) {
		this.coordinate_registry.add(coords);
		notifyAll();
	}
	
	public synchronized Coordinates getFromCoordinateRegistry(int index) {
		Coordinates coords = null;

		while(this.active == true || this.coordinate_registry.size() > index) {
			if(this.coordinate_registry.size() > index) {
				return this.coordinate_registry.get(index);
			}else {
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println("Problem with semaphore: " + e.getMessage());
				}
			}			
		}
		
		return coords;
	}*/
	
	public void insertIntoCoordinateRegistry(Coordinates coords) {
		lock.lock();
		this.coordinate_registry.add(coords);
		noValue.signalAll();
		lock.unlock();
	}
	
	public Coordinates getFromCoordinateRegistry(int index) { //Current working version with lock, for semaphore.
		Coordinates coords = null;
		
		//lock.lock() //would require try up here.
		if(this.coordinate_registry.size() > index) {
			return this.coordinate_registry.get(index);
		}
		
		while(this.active == true || this.coordinate_registry.size() > index) {
			if(this.coordinate_registry.size() > index) {
				return this.coordinate_registry.get(index);
			}
			else {
				try {
					lock.lock();
					noValue.await();
				} catch (InterruptedException e) {
					System.out.println("Problem waiting for next value: " + e.getMessage());
				}finally {
					lock.unlock();
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
		notifyAll();
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
		
		this.xmlHanlder = new XMLHandler(this.xmlName, this);
		Thread xmlHandlerT = new Thread(this.xmlHanlder); xmlHandlerT.start();
		
		this.csvHandler = new CSVHandler(this.csvName, this);
		Thread csvHandlerT = new Thread(this.csvHandler); csvHandlerT.start();
		
		this.sqliteHandler = new SQLiteHandler(this.databaseName, this);
		Thread sqliteHandlerT = new Thread(this.sqliteHandler); sqliteHandlerT.start();
		
		this.databaseReader = new DBReader(this.databaseName, this.dbConnection, this);
		this.databaseReader.start();
		
		try {
			this.databaseReader.join(); System.out.println("DatabaseReader Joined.");
			csvHandlerT.join(); System.out.println("CSVHandler Joined.");
			xmlHandlerT.join(); System.out.println("XMLHandler Joined.");
			sqliteHandlerT.join(); System.out.println("SQLiteHandler Joined.");
			//this.writer.join(); System.out.println("Writer Joined.");
		} catch (InterruptedException e) {
			System.out.println("Problem while waiting for threads to finished.");
		}
	}
}
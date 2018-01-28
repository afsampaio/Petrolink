package DatabaseHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import PetroLink.Coordinates;
import PetroLink.Simulator;

public class SQLiteHandler extends Writer implements Runnable{
	
	private Connection dbConnection;
	private int indexer;
	ArrayList<Coordinates> tempCoordinates = new ArrayList<>();
	
	
	public SQLiteHandler(String database, Simulator sim) {
		super(database, sim);
		
		try {
			Connection conn = null;
			
			String url = "jdbc:sqlite:" + this.getDestinationName();
			conn = DriverManager.getConnection(url);
			
			this.dbConnection = conn;
			
		} catch(SQLException e){
			System.out.println("Connection error: " + e.getMessage());
		}
	}
	
	public void setupWriter() throws SQLException {
		String sqlCreate = "CREATE TABLE IF NOT EXISTS Results (x_0 real, y_0 real, z_0 real);";
		Statement stmt = null;
		
		stmt = dbConnection.createStatement();
		stmt.execute(sqlCreate);
		
		this.indexer = 100;
	}
	
	
	/*
	 * Setting up the writer in this way, provides an enormous speed enhancement as we reduce the number of 
	 * inserts into the database, inserting in blocks instead of inserting each new arrival separately
	 */
	public void pushToDb(Coordinates coords) { 
		this.tempCoordinates.add(coords);
		
		if( (this.tempCoordinates.size() / this.indexer) == 1 ) {//Push to Db
			int pushIndexer = 1;
			StringBuilder sqlPushBuilder = new StringBuilder();
			sqlPushBuilder.append("INSERT INTO Results(x_0, y_0, z_0) VALUES(?,?,?)");
			
			for(int i = 1; i<indexer; i++) {
				sqlPushBuilder.append(", (?,?,?)");
			}
			sqlPushBuilder.append(";");
			String sqlPush = sqlPushBuilder.toString();
			
			PreparedStatement pstmt = null;
			try {
				pstmt = this.dbConnection.prepareStatement(sqlPush);
				
				for(Coordinates c : this.tempCoordinates) {
					pstmt.setDouble(pushIndexer, c.getx()); pushIndexer++;
					pstmt.setDouble(pushIndexer, c.gety()); pushIndexer++;
					pstmt.setDouble(pushIndexer, c.getz()); pushIndexer++;
				}
				
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.tempCoordinates = new ArrayList<>();
		}
	}
	
	/*This code inserts in the database on a per-arrival schema, not used for efficiency purposes*/
	/*public void pushToDb(Coordinates coords) {
		String sqlPush = "INSERT INTO Results(x_0, y_0, z_0) VALUES(?,?,?);";
		
		PreparedStatement pstmt = null;
		
		try {
			pstmt = this.dbConnection.prepareStatement(sqlPush);
			
			pstmt.setDouble(1, coords.getx());
			pstmt.setDouble(2, coords.gety());
			pstmt.setDouble(3, coords.getz());
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void writeToDestination(Coordinates coords) {
		this.pushToDb(coords);
	}
	
	/*
	 * CloseDestination method is rather ugly due to the fact that it must insert any remainder elements in the temporary
	 * channel;
	 */
	public void closeDestination() {
		
		if(this.tempCoordinates.size() > 0 ) {
			int pushIndexer = 1;
			StringBuilder sqlPushBuilder = new StringBuilder();
			sqlPushBuilder.append("INSERT INTO Results(x_0, y_0, z_0) VALUES(?,?,?)");
			
			for(int i = 0; i<this.tempCoordinates.size()-1; i++) {
				sqlPushBuilder.append(", (?,?,?)");
			}
			sqlPushBuilder.append(";");
			String sqlPush = sqlPushBuilder.toString();
			
			PreparedStatement pstmt = null;
			try {
				pstmt = this.dbConnection.prepareStatement(sqlPush);
				
				for(Coordinates c : this.tempCoordinates) {
					pstmt.setDouble(pushIndexer, c.getx()); pushIndexer++;
					pstmt.setDouble(pushIndexer, c.gety()); pushIndexer++;
					pstmt.setDouble(pushIndexer, c.getz()); pushIndexer++;
				}
				
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.tempCoordinates = new ArrayList<>();
		}		
		
		try {
			this.dbConnection.close();
		} catch (SQLException e) {
			System.out.println("SQLiteHandler: Problem closing connection: " + e.getMessage() );
		}
	}
	
	public void run() {
		try {
			this.setupWriter();
		} catch (SQLException e) {
			System.out.println("SQLiteHandler: Couldn't setup Handler: " + e.getMessage());
		}
		
		boolean active = true;
		int index = 0;
		Coordinates coords;
		
		while(active) {
			coords = this.getFromRegistry(index);
			//System.out.println("SQLiteHandler: Got coordinates from registry");
			
			if(coords != null) {
				this.writeToDestination(coords);
				index++;
				active = this.getSimulator().getStatusAndEnd(index);
			}
			else {
				active = this.getSimulator().getStatus();
			}
		}
		
		this.closeDestination();
	}
}

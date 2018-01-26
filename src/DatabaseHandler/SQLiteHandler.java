package DatabaseHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import PetroLink.Coordinates;

public class SQLiteHandler {
	
	private String dbname;
	private Connection dbConnection;
	
	public SQLiteHandler() {
		try {
			Connection conn = null;
			
			//To test in other operating systems, path must be changed;
			String url = "jdbc:sqlite:C:\\Users\\andr3mp\\eclipse-workspace\\PetroLinkTest\\petrolink_challenge.db";
			conn = DriverManager.getConnection(url);
			
			System.out.println("Writer: Connection has been established");
			this.dbConnection = conn;
			
		} catch(SQLException e){
			System.out.println("Connection error: " + e.getMessage());
		}
	}
	
	public void setupWriter() {
		String sqlCreate = "CREATE TABLE IF NOT EXISTS Results (x_0 real, y_0 real, z_0 real);";
		Statement stmt = null;
		
		try {
			stmt = dbConnection.createStatement();
			stmt.execute(sqlCreate);
			System.out.println("TAble created with success********************************************************************************");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void pushToDb(Coordinates coords) {
		String sqlPush = "INSERT INTO Results(x_0, y_0, z_0) VALUES(?,?,?)";
		
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
	
	}
}

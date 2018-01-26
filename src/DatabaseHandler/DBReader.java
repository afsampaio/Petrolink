package DatabaseHandler;

//import java.sql.Statement;
//import java.sql.Connection;
//import java.sql.ResultSet;
import java.sql.*;

import javax.swing.text.html.MinimalHTMLWriter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import PetroLink.Coordinates;
import PetroLink.MinCurvMethod;
import PetroLink.Simulator;

public class DBReader extends Thread{
	private String databaseName;
	private Connection dbConnection = null;
	private Simulator simulator;
	
	public DBReader(String dbName) {
		this.databaseName = dbName;
		//this.dbConnection = null;
	}
	
	public DBReader(String dbName, Connection dbConn, Simulator sim) {
		this.databaseName = dbName; //Not required at this point.
		this.dbConnection = dbConn;
		this.simulator = sim;
	}
	
	public void run() {
		
		if(this.dbConnection == null) {
			System.out.println("Database connection not working properly.");
		}else {
			System.out.println("Database connection is working and there should be no null pointer");
		}
		
		Statement stmt = null;
		
		try {			 
			String query = "SELECT md_ft, azim_dega, incl_dega FROM Data;";
			String queryCoordinates = "SELECT x_0, y_0, z_0 FROM InitialCoordinates;";
			
			stmt = this.dbConnection.createStatement();
			
			
			ResultSet rsCoordinates = stmt.executeQuery(queryCoordinates);
			double x_0 = rsCoordinates.getDouble("x_0");
			double y_0 = rsCoordinates.getDouble("y_0");
			double z_0 = rsCoordinates.getDouble("z_0");
			
			ResultSet rs = stmt.executeQuery(query);
			
			//System.out.println("Initial Coordinates: (" + x_0 + " ; " + y_0 + " ; " + z_0 + ")");
			
			
			MinCurvMethod mcm = new MinCurvMethod();
			//int i=0; 
			//System.out.println("Beta1: " + mcm.calcBeta(25, 15, 45, 20));
			//System.out.println("Beta2: " + mcm.calcBeta(15, 25, 20, 45));
			//double beta = mcm.calcBeta(15, 25, 20, 45); System.out.println("Beta = " + beta);
			//double rf = mcm.calcRF(beta); System.out.println("RF: " + rf);
			//double north = mcm.calcNorth(100, 15, 25, 20, 45, rf);	System.out.println("North = " + north);
			//double east = mcm.calcEast(100, 15, 25, 20, 45, rf); System.out.println("East = " + east);
			//double tvd = mcm.calcTVD(100, 15, 25, rf); System.out.println("Vertical = " + tvd);
			
			rs.next();
			double md = rs.getDouble("md_ft");
			double azim = rs.getDouble("azim_dega");
			double incl = rs.getDouble("incl_dega");
			double mdNext, azimNext, inclNext, beta, rf, north, east, tvd;
			
			
			while(rs.next()) {
				mdNext = rs.getDouble("md_ft");
				azimNext = rs.getDouble("azim_dega");
				inclNext = rs.getDouble("incl_dega");
				
				beta = mcm.calcBeta(incl, inclNext, azim, azimNext); //System.out.println("Beta = " + beta);
				rf = mcm.calcRF(beta); //System.out.println("RF: " + rf);
				north = mcm.calcNorth(mdNext - md, incl, inclNext, azim, azimNext, rf); //System.out.println("North = " + north);
				east = mcm.calcEast(mdNext - md, incl, inclNext, azim, azimNext, rf); //System.out.println("East = " + east);
				tvd = mcm.calcTVD(mdNext - md, incl, inclNext, rf); //System.out.println("Vertical = " + tvd);
				
				x_0 += east; y_0 += north; z_0 += tvd;
				Coordinates newCoordinates = new Coordinates(x_0, y_0,z_0);
				this.simulator.insertIntoCoordinateRegistry(newCoordinates); //System.out.println("New Coordinates: (" + x_0 + ", " + y_0 + ", " + z_0 + ")" );
				
				md = mdNext;
				azim = azimNext;
				incl = inclNext;
			}
		}catch(SQLException e) {
			System.out.println("SQL exception: " + e.getMessage());
		} finally{
			try {
				if(stmt != null) { stmt.close(); }
			}catch(SQLException e) {
				System.out.println("Couldn't close statement: " + e.getMessage());
			}
		}
	}
}
package DatabaseHandler;


import java.io.IOException;
import java.sql.Connection;

import javax.xml.stream.XMLStreamException;

import PetroLink.Coordinates;
import PetroLink.Simulator;

public class Writer extends Thread{
	private String databaseName;
	private Simulator simulator;
	private String xmlFileName;
	private String csvFileName;
	
	private XMLHandler xmlHandler;
	private CSVHandler csvHandler;
	private SQLiteHandler dbHandler;
	
	public Writer(String dbname, Simulator sim, String xmlName, String csvName) {
		this.databaseName = dbname;
		this.simulator = sim;
		this.xmlFileName = xmlName;
		this.csvFileName = csvName;
		
		this.xmlHandler = new XMLHandler(this.xmlFileName);
		this.csvHandler = new CSVHandler(this.csvFileName);
		this.dbHandler = new SQLiteHandler(this.databaseName);
		
		try {
			this.xmlHandler.setupWriterDom();
			this.csvHandler.setupWriter();
			this.dbHandler.setupWriter();
		}catch(Exception e) {
			System.out.println("Error creating Files. " + e.getMessage());
		}		
	}
	
	public void writeToXML(Coordinates coords) throws XMLStreamException, IOException {
		this.xmlHandler.appendToFileDom(coords);
		
	}
	
	public void writeToCSV(Coordinates coords) throws IOException {
		this.csvHandler.appendToFile(coords);
	}
	
	public void writeToDatabase(Coordinates coords) {
		this.dbHandler.pushToDb(coords);
	}

	
	public void writeAllFormats(Coordinates coords) {
		try {
			this.writeToXML(coords);
			this.writeToCSV(coords);
			this.writeToDatabase(coords);
		} catch (XMLStreamException e) {
			System.out.println("Error writing to files. " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error writing to files. " + e.getMessage());
		}
	}
	
	public void closeAllFormats() {
		try {
			this.xmlHandler.closeWriterDom();
			this.csvHandler.closeWriter();
		} catch (Exception e) {
			System.out.println("Error closing Files. " + e.getMessage());
		}
	}
	
	public void run() {
		boolean active = true;
		int index = 0;
		Coordinates coords;
		
		while(active) {
			coords = this.simulator.getFromCoordinateRegistry(index);
			
			if(coords != null) {
				this.writeAllFormats(coords);
				index++;
				active = this.simulator.getStatusAndEnd(index); //This code is required here because I removed the delete idea;
			}else {
				active = this.simulator.getStatus();
			}
		}
		
		closeAllFormats();
	}
}
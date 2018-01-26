package DatabaseHandler;


import java.io.IOException;
import java.sql.Connection;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import PetroLink.Coordinates;

public class Writer {
	private String databaseName;
	private String xmlFileName;
	private String csvFileName;
	private String jsonFileName;
	private Connection dbConnection;
	
	private XMLHandler xmlHandler;
	private CSVHandler csvHandler;
	private SQLiteHandler dbHandler;
	
	public Writer(String dbname, Connection conn, String xmlName, String csvName, String jsonName) {
		this.databaseName = dbname;
		this.dbConnection = conn;
		this.xmlFileName = xmlName;
		this.csvFileName = csvName;
		this.jsonFileName = jsonName;
		
		this.xmlHandler = new XMLHandler(this.xmlFileName);
		this.csvHandler = new CSVHandler(this.csvFileName);
		this.dbHandler = new SQLiteHandler();
		
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
	
	public void writeToJSon() {
		
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
}

/*class DbWriter extends Writer implements Runnable{
	public DbWriter() {
		super(" ", " ", " "," ");
	}
	
	public boolean write(){
		return true;
	}
	
	public void run() {
		
	}
}

class XMLWriter extends Writer implements Runnable{
	String filename;
	
	public XMLWriter(String filename) {
		super();
	}
	
	public boolean write(){
		return true;
	}
	
	public void run() {
		
	}
}

class CSVWriter extends Writer implements Runnable{
	String filename;
	public CSVWriter(String filename) {
		super();
		this.filename = filename;
	}
	
	public boolean write(){
		return true;
	}
	
	public void run() {
		
	}
}*/
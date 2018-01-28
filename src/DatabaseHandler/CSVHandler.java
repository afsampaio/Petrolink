package DatabaseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import PetroLink.Coordinates;
import PetroLink.Simulator;

public class CSVHandler extends Writer implements Runnable{	
	
	private File file;
	private FileWriter writer;
	
	
	public CSVHandler(String file, Simulator sim) {
		super(file, sim);
	}
	
	public void setupWriter() throws IOException {
		this.file = new File(super.getDestinationName());
		
		if (this.file.createNewFile()){
			System.out.println("File created!");
		}else{
			System.out.println("File already exists!");
		}
		
		this.writer = new FileWriter(this.file);
		this.writer.write("!Results \n");
		this.writer.write("!X,Y,Z \n");
	}
	
	public void closeWriter() throws IOException {
		this.writer.close();
	}
	
	public void appendToFile(Coordinates coords) throws IOException {
		StringBuilder str = new StringBuilder();
		
		str.append(Double.toString(coords.getx()));
		str.append(',');
		str.append(Double.toString(coords.gety()));
		str.append(',');
		str.append(Double.toString(coords.getz()));
		str.append('\n');
		
		this.writer.write(str.toString());
		this.writer.flush();
	}
	
	public void writeToDestination(Coordinates coords) {
		try {
			this.appendToFile(coords);
		} catch (IOException e) {
			System.out.println("CSVHandler: Problem writing to file: " + e.getMessage());
		}
	}
	
	public void closeDestination() {
		try {
			this.writer.close();
		} catch (IOException e) {
			System.out.println("CSVHandler: Problem closing destination file: " + e.getMessage());
		}
	}
	
	public void run() {
		try {
			this.setupWriter();
		} catch (IOException e) {
			System.out.println("CSVHandler: Couldn't setup Handler: " + e.getMessage());
			return;
		}
		boolean active = true;
		int index = 0;
		Coordinates coords;
		
		while(active) {
			coords = this.getFromRegistry(index);
			//System.out.println("CSVHandler: Got coordinates from registry");
			
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

package DatabaseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import PetroLink.Coordinates;

public class CSVHandler {
	private String filename;
	
	private File file;
	private FileWriter writer;
	
	
	public CSVHandler(String file) {
		this.filename = file;
	}
	
	public void setupWriter() throws IOException {
		this.file = new File(this.filename);
		
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
}

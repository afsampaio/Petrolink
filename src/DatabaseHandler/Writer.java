package DatabaseHandler;


import java.io.IOException;
import java.sql.Connection;

import javax.xml.stream.XMLStreamException;

import PetroLink.Coordinates;
import PetroLink.Simulator;

public abstract class Writer{
	private String destinationName;
	private Simulator simulator;
	
	public Writer(String name, Simulator sim) {
		this.destinationName = name;
		this.simulator = sim;
	}
	
	public String getDestinationName() {
		return this.destinationName;
	}
	
	public Simulator getSimulator() {
		return this.simulator;
	}
	
	//public abstract Coordinates getFromRegistry(int index);
	public Coordinates getFromRegistry(int index) {
		return this.getSimulator().getFromCoordinateRegistry(index);
	}
	
	public abstract void writeToDestination(Coordinates coords);
	
	public abstract void closeDestination();
}
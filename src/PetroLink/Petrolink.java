package PetroLink;

import java.util.GregorianCalendar;

public class Petrolink {
	
	public static void main(String[] args) {
		String databasePath;
		String csvPath;
		String xmlPath;
		Simulator simulator;
		
		if(args.length > 0) {
			//Given Info Constructor,
			databasePath = args[0];
			csvPath = args[1];
			xmlPath = args[2];
			simulator = new Simulator(1, databasePath, csvPath, xmlPath);
		}
		else {
			//Default Constructor
			simulator = new Simulator(1, "petrolink_challenge.db");
		}
		GregorianCalendar previous = new GregorianCalendar(); //For very very basic time performance measurements
		
		simulator.start();
		
		try {
			simulator.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		GregorianCalendar posterior = new GregorianCalendar();
		System.out.println("Done: " + (posterior.getTimeInMillis() - previous.getTimeInMillis()));
	}
}
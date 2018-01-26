package PetroLink;

import java.util.GregorianCalendar;

public class Petrolink {
	
	public static void main(String[] args) {
		GregorianCalendar previous = new GregorianCalendar();
		
		Simulator simulator = new Simulator(1, "PetroLink");
		simulator.start();
		
		try {
			simulator.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		GregorianCalendar posterior = new GregorianCalendar();
		System.out.println("Done and Done. " + (posterior.getTimeInMillis() - previous.getTimeInMillis()));
	}
}
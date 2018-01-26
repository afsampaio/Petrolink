package PetroLink;

public class Coordinates {
	private double x, y, z;
	
	public Coordinates(double nx, double ny, double nz) {
		this.x = nx;
		this.y = ny;
		this.z = nz;
	}
	
	public double getx() {
		return this.x;
	}
	
	public double gety() {
		return this.y;
	}
	
	public double getz() {
		return this.z;
	}
}

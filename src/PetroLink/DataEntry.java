package PetroLink;

public class DataEntry {
	private float md_ft;
	private double azim_dega, incl_dega;
	
	public DataEntry(float md, double azim, double incl) {
		this.md_ft = md;
		this.azim_dega = azim;
		this.incl_dega = incl;
	}
	
	public float getMd() {
		return this.md_ft;
	}
	
	public double getAzim() {
		return this.azim_dega;
	}
	
	public double getIncl() {
		return this.incl_dega;
	}
}

package PetroLink;


public class MinCurvMethod {
	
	public MinCurvMethod() {}
	
	public double calcBeta(double i1, double i2, double a1, double a2) {
		double ri1 = Math.toRadians(i1);
		double ri2 = Math.toRadians(i2);
		double ra1 = Math.toRadians(a1);
		double ra2 = Math.toRadians(a2);
		
		//System.out.println( "Variables: " + i1 + " - " + i2 + " " + a1 + " " + a2);
		double temp = (Math.cos(ri2-ri1) - (Math.sin(ri1)*Math.sin(ri2) * (1-Math.cos(ra2-ra1))));
		double result = Math.acos(temp);
		
		//double temp = Math.cos(i2-i1) - (Math.sin(i1)*Math.sin(i2) * (1-Math.cos(a2-a1)));
		//double result = Math.acos(temp);
		
		//System.out.println("Degrees: " + Math.toDegrees(result) + " - Rad: " + result);
		return result;
	}
	
	public double calcRF(double beta) {
		return (2 / beta) * Math.tan(beta/2);
	}
	
	public double calcNorth(double md, double i1, double i2, double a1, double a2, double rf) {
		double ri1 = Math.toRadians(i1);
		double ri2 = Math.toRadians(i2);
		double ra1 = Math.toRadians(a1);
		double ra2 = Math.toRadians(a2);
		//No need to convert rf or md;
		//double rrf = Math.toRadians(rf);
	
		double result = (md/2) * (Math.sin(ri1) * Math.cos(ra1) + Math.sin(ri2) * Math.cos(ra2)) * rf;
		
		return result;
	}
	
	public double calcEast(double md, double i1, double i2, double a1, double a2, double rf) {
		double ri1 = Math.toRadians(i1);
		double ri2 = Math.toRadians(i2);
		double ra1 = Math.toRadians(a1);
		double ra2 = Math.toRadians(a2);
		
		double result = (md/2) * (Math.sin(ri1) * Math.sin(ra1) + Math.sin(ri2)*Math.sin(ra2)) * rf;
		
		return result;
	}
	
	public double calcTVD(double md, double i1, double i2, double rf) {
		double ri1 = Math.toRadians(i1);
		double ri2 = Math.toRadians(i2);
		
		double result = (md/2) * (Math.cos(ri1) + Math.cos(ri2)) * rf;
		
		return result;
	}
	
	public double calcDLS(double i1, double i2, double a1, double a2, double md) {
		double rmd = Math.toRadians(md);
		double ri1 = Math.toRadians(i1);
		double ri2 = Math.toRadians(i2);
		double ra1 = Math.toRadians(a1);
		double ra2 = Math.toRadians(a2);
		
		double result = (Math.acos((Math.cos(ri1 * ri2) + (Math.sin(ri1) + Math.sin(ri2)) * Math.cos(ra2-ra1)))) * ((double)100/rmd);
		
		return result;
	}
}

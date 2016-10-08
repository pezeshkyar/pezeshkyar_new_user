package primitives;

public class Subspec {
	public int id;
	public int specId;
	public String name;
	
	@Override
	public String toString() {
		return "" + id + "(" + specId + "): " + name; 
	}
}

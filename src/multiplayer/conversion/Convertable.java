package multiplayer.conversion;

public interface Convertable {

	public void save(OutConverter out);
	
	public void load(InConverter in);
	
}

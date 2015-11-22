package pixel.pixelList;

import pixel.AD;
import pixel.Material;

public class Leaf extends Material<AD>{

	public Leaf(){
		super(null);
		ID = 5;
		name = "Leaf";
		usePickaxe = 1;
		burnable = 10;
		tick = false;
	}
}
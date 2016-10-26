package pixel.pixelList;

import pixel.AD;
import pixel.Material;

public class Air extends Material<AD>{

	public Air(){
		super(null);
		ID = 0;
		name = "Air";
		displayName = "Air";
		usePickaxe = 0;
		tick = false;
		texture = new int[1];
		textureWidth = 1;
		textureHeight = 1;
		frontLightReduction = 1;
		backLightReduction = 0;
	}
	
}

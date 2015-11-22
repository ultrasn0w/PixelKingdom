package pixel.pixelList;

import pixel.Ore;
import pixel.ads.OreAD;

public class Ore_Gold extends Ore<OreAD>{

	public Ore_Gold(){
		super(new OreAD());
		adl = 2;
		melt = 400;
		ID = 18;
		ingot = 34;
		name = "GoldOre";
		usePickaxe = 2.0;
		tick = false;
	}
}

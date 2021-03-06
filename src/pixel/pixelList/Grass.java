package pixel.pixelList;

import map.Map;
import pixel.UDS;
import pixel.Material;

public class Grass extends Material<UDS>{

	public Grass(){
		super(null);
		ID = 3;
		name = "Grass";
		displayName = "Grass";
		requiredType = MINING_TYPE_PICKAXE;
		requiredTier = 1;
		miningResistance = 1;
		loadTexture();
	}
	
	public boolean tick(int x, int y, int l, Map map, int numTick) {
		if(Math.random()*100 < 1){
			for(int X = -1; X <= 1; X++){for(int Y = -1; Y <= 1; Y++){
				if(map.getID(x+X, y+Y, Map.LAYER_FRONT)==2){
					for(int aX = -1; aX <= 1; aX++){for(int aY = -1; aY <= 1; aY++){
						if(map.getID(x+X+aX, y+Y+aY, Map.LAYER_FRONT)==0 && map.getID(x+X+aX, y+Y+aY, Map.LAYER_LIQUID)==0){map.setID(x+X, y+Y, Map.LAYER_FRONT, 3);return true;}
					}}
				}
			}}
			for(int X = -1; X <= 1; X++){for(int Y = -1; Y <= 1; Y++){
				if(map.getID(x+X, y+Y, Map.LAYER_FRONT)==0 && map.getID(x+X, y+Y, Map.LAYER_LIQUID)==0)return true;
			}}
			map.setID(x, y, Map.LAYER_FRONT, 2);
		}
		return false;
	}

}

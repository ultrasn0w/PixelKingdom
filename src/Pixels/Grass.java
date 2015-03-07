package Pixels;

import Maps.Map;

public class Grass extends Material{

	public Grass(){
		ID = 3;
		name = "Grass";
		usePickaxe = 1;
		tick = true;
	}
	
	public void tick(int numTick, Map map) {
		if(Math.random()*100 < 1){
			for(int X = -1; X <= 1; X++){for(int Y = -1; Y <= 1; Y++){
				if(map.getID(x+X, y+Y, 1)==2){
					for(int aX = -1; aX <= 1; aX++){for(int aY = -1; aY <= 1; aY++){
						if(map.getID(x+X+aX, y+Y+aY, 1)==0 & map.getID(x+X+aX, y+Y+aY, 2)==0){map.setID(x+X, y+Y, (byte)3,1);return;}
					}}			
				}
			}}
			for(int X = -1; X <= 1; X++){for(int Y = -1; Y <= 1; Y++){
				if(map.getID(x+X, y+Y, 1)==0 & map.getID(x+X, y+Y, 2)==0)return;
			}}
			map.setID(x, y, (byte)2,1);
		}
	}

}
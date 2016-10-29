package item;

import java.util.ArrayList;

import dataUtils.conversion.ConvertData;
import entities.Player;
import gfx.Mouse;
import gfx.Mouse.MouseType;
import gfx.SpriteSheet;
import main.Game;
import main.MouseInput;
import map.Map;
import pixel.Material;
import pixel.PixelList;

public abstract class Pickaxe extends Tool{
	
	private double npxs = 0;
	public Pickaxe(){
		type = 1;
		name = "Pickaxe";
		displayName = "Pickaxe";
		stack  = 1;
		stackMax  = 1;
		gfxs = new SpriteSheet("/Items/Pickaxeh.png");
	}
	
	public void useItem(Player plr, Map map) {
	anim = 0;
	
	if((MouseInput.mousel.isPressed() | MouseInput.mouser.isPressed()) && !plr.iscrouching && !plr.isinair){
		if(npxs <= 0)npxs += ((double)size*strength)/4;
		double r = 0;
		while(npxs > 0){
			r = size;
			int pX = 0, pY = 0;
			Material<?> m = null;
			int l = Map.LAYER_FRONT;
			if(MouseInput.mousel.isPressed()){
				for(int x = -size; x <= size; x++){
					for(int y = -size; y <= size; y++){
						int X = MouseInput.mouse.getMapX()+x, Y = MouseInput.mouse.getMapY()+y;
						if(Math.sqrt(x*x+y*y) < r & Math.sqrt((plr.x-X)*(plr.x-X)+(plr.y-Y)*(plr.y-Y)) <= range){
							m = PixelList.GetMat(map.getID(X,Y,Map.LAYER_FRONT));
							if(m.ID!=0 && m.usePickaxe() <= strength && m.usePickaxe()>0){
								r = Math.sqrt(x*x+y*y);
								pX = X;pY = Y;
							}
						}
					}
				}
			}else{
				for(int x = -size; x <= size; x++){
					for(int y = -size; y <= size; y++){
						int X = MouseInput.mouse.getMapX()+x, Y = MouseInput.mouse.getMapY()+y;
						if(Math.sqrt(x*x+y*y) < r & Math.sqrt((plr.x-X)*(plr.x-X)+(plr.y-Y)*(plr.y-Y)) <= range){
							m = PixelList.GetMat(map.getID(X,Y,Map.LAYER_BACK));
							if(m.ID!=0 && m.usePickaxe() <= strength && m.usePickaxe()>0){
								r = Math.sqrt(x*x+y*y);
								pX = X;pY = Y;
							}
						}
					}
				}
				l=Map.LAYER_BACK;
			}
			m = PixelList.GetMat(map.getID(pX,pY,l));
			if(m.ID!=0){
//				if(plr.PickUp(ItemList.NewItem(m.ID)) || Game.devmode){
				if(plr.addToStack(m.ID, 1)==0 || Game.devmode){
					npxs -= PixelList.GetMat((byte)map.getID(pX, pY, l)).usePickaxe;
					map.setID(pX, pY, l, 0);
				}else{npxs = 0;}
			}else{npxs = 0;}
		}
		
		if((System.nanoTime()/100000000)%10 < 5)anim = 11;
		else anim = 10;
		}
	}
	
	public void setMouse(){
		Mouse.mouseType=MouseType.MINING;
		Mouse.mousesize=(byte)size;
	}

	public void save(ArrayList<Byte> file) {
		ConvertData.I2B(file, ID);
		ConvertData.I2B(file, size);
	}

	public void load(ArrayList<Byte> file) {
		size = ConvertData.B2I(file);
	}
	
	public void upgrade(int up){
		size += up;
	}
}

package gameFields;

import gfx.Mouse;
import gfx.SpriteSheet;
import item.*;
import main.Game;
import main.PArea;

public class BeltBagInv extends GameField {

	private SpriteSheet Background = new SpriteSheet("/Items/field.png");
	public int savefile;
	public static int width = 4;

	public BeltBag bag;
	
	public BeltBagInv(BeltBag bag, int savefile) {
		super(width*12, bag.inventory.length/width*12+11, savefile);
		this.bag = bag;
		this.savefile = savefile;
	}
	
	public void tick() {
		Drag();
		if(mouseover(Game.input.mouse.x/Game.SCALE, Game.input.mouse.y/Game.SCALE)){
			Mouse.mousetype=0;
			if(Game.input.mousel.click()){
				int mx = Game.input.mousel.x/Game.SCALE, my = Game.input.mousel.y/Game.SCALE;
				PArea r = new PArea(0,0,10,10);
				for(int x = 0; x < width; x++){
					for(int y = 0; y < bag.inventory.length/width; y++){
						r.setBounds(x*12+1+field.x,y*12+12+field.y,10,10);
						if(r.contains(mx, my)){
							if(bag.inventory[x+y*width]!=null){
								if(Mouse.Item==null){
									Mouse.Item = bag.inventory[x+y*width]; bag.inventory[x+y*width] = null;
								}
							}else{
								try{
									bag.inventory[x+y*width]=(Item)Mouse.Item;Mouse.Item = null;
								}catch(ClassCastException e){}
							}
						}
					}
				}
			}
		}
	}
	
	public void render() {
		renderfield();
		Game.sfont.render(Game.screen.xOffset+field.x+2, Game.screen.yOffset+field.y+1, "Belt", 0, 0xff000000, Game.screen);
		for(int x = 0; x < width; x++){
			for(int y = 0; y < bag.inventory.length/width; y++){
				Game.screen.drawGUITile(Game.screen.xOffset+field.x+x*12, Game.screen.yOffset+field.y+y*12+11, 0, 0, Background, 0xff000000);
				if(bag.inventory[x+y*width]!=null) bag.inventory[x+y*width].render(Game.screen, Game.screen.xOffset+field.x+x*12+1, Game.screen.yOffset+field.y+y*12+12,true);
			}
		}
	}
}

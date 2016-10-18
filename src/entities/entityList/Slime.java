package entities.entityList;

import entities.Colision;
import entities.Hitbox;
import entities.Mob;
import gfx.SpriteSheet;
import main.Game;
import map.Map;

public class Slime extends Mob {

	private Hitbox col = new Hitbox(-3,-2,3,2);
	
	public Slime(Map map, int x, int y) {
		super(map, "Slime", x, y, new SpriteSheet("/Mobs/slime.png"));
		xOffset=6;
		yOffset=4;
		sheet.tileWidth = 11*3;
		sheet.tileHeight = 7*3;
	}

	public void tick(int numTick) {
		int n;
		if(speedY < 0){
			for(n = 0; n >= speedY; n--){
				if(!Colision.canMove(map, col, x, y, 0, n)) speedY = n+1;
			}
		}else{
			for(n = 0; n <= speedY; n++){
				if(!Colision.canMove(map, col, x, y, 0, n)) speedY = n-1;
			}
		}
		y += speedY;
	}

	public void render() {
		Game.screen.drawMapTile(x-xOffset, y-yOffset, 0, 0, sheet, 0);
	}
	
}
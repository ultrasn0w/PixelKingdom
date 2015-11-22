package item.itemList;

import entities.Player;
import gfx.Screen;
import gfx.SpriteSheet;
import item.*;
import main.InputHandler;
import map.Map;

public class NormalMaterialBag extends MaterialBag {

	public NormalMaterialBag(){
		super(12);
		ID = 451;
		name = "NormalMaterialBag";
		gfx = new SpriteSheet("/Items/NormalMaterialBag.png");
	}

	public void useItem(InputHandler input, Player plr, Map map, Screen screen) {
	}
}
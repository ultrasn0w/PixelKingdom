package entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import gfx.SpriteSheet;
import item.*;
import item.Recipe.component;
import main.Game;
import main.conversion.ConvertData;
import map.Map;
import gameFields.*;

public class Player extends Mob{
	
	public static enum BAG{
		TOOL_1(ToolBag.class,new SpriteSheet("/Items/ToolBag.png"),1),
		MAT_1(MaterialBag.class,new SpriteSheet("/Items/MaterialBag.png"),2),
		MAT_2(MaterialBag.class,new SpriteSheet("/Items/MaterialBag.png"),3),
		BELT_1(BeltBag.class,new SpriteSheet("/Items/BeltBag.png"),4),
		ITEM_1(ItemBag.class,new SpriteSheet("/Items/ItemBag.png"),5),
		ITEM_2(ItemBag.class,new SpriteSheet("/Items/ItemBag.png"),6);
		
		public final Class<? extends Bag<?>> bagClass;
		public final int saveID;
		public final SpriteSheet defaultSprite;
		
		BAG(Class<? extends Bag<?>> bagClass, SpriteSheet defaultSprite, int saveID) {
			this.bagClass = bagClass;
			this.defaultSprite = defaultSprite;
			this.saveID = saveID;
		}
	}
	//bag Enums
	private EnumMap<BAG, Bag<?>> bags;
	private EnumMap<BAG, BagInv> bagInvs;
	
	//visual and Hitboxes
	private int anim;
	private int Color;
	private Hitbox col = new Hitbox(-1,-7,1,6); //walking
	private Hitbox cols = new Hitbox(-1,-1,1,6);//sneaking
	
	//internal values for physics
	private int gravity = 1;
	private int jumpspeed = 4;
	private int walkspeed = 1;
	
	//physical abilities
	public boolean isinair;
	private boolean canJump;
	public boolean iscrouching;
	private int jumpcooldown;
	
	//Player stuff, equipment, hotbar, etc
	public Equipment equipment;
	public Crafting crafting;
	public RecipeList recipelist = new RecipeList();
	private Point hotBar = new Point(0,0);
	private boolean openInv = false;
	private boolean openEquip = false;
	private boolean openCraft = false;
	private byte openHotBar = 0;
	private byte selected = 0;
	private SpriteSheet itemBackground = new SpriteSheet("/Items/field.png");
	private SpriteSheet itemBackgrounds = new SpriteSheet("/Items/fields.png");

	public Player(Map map) {
		super(map ,"Player", 0, 0, new SpriteSheet("/sprite_sheet_player.png"));
		this.map = map;
		this.bags = new EnumMap<>(BAG.class);
		this.bagInvs = new EnumMap<>(BAG.class);
		equipment = new Equipment(this, bags);
		crafting = new Crafting(this);
		Color = Game.configs.PlrCol;
		xOffset=6;
		yOffset=9;
		sheet.tileWidth = 13*3;
		sheet.tileHeight = 16*3;
		try{this.bags.get(BAG.BELT_1).getItem(selected).setMouse();}catch(NullPointerException e){}
	}
	
	public void resetBagInv(BAG bagEnum){
		if(this.bagInvs.containsKey(bagEnum)) {
			this.bagInvs.put(bagEnum, new BagInv(this.bags.get(bagEnum), bagEnum));
		}else {
			this.bagInvs.remove(bagEnum);
		}
	}
	
	public byte getAnim() {
		return (byte) anim;
	}
	public byte getDir() {
		return (byte) movingDir;
	}
	
	public void applyGravity(){
		if(gravity != 0 && isinair){
			if(speedX < -1) speedX = -1;
			if(speedX > 1) speedX = 1;
			speedY += gravity;
		}
	}
	
	public boolean equipItem(BAG bag_enum, Item item) {
		if(bag_enum.bagClass.isInstance(item) && !this.bags.containsKey(bag_enum)) {
			this.bags.put(bag_enum, (Bag<?>)item);
			this.bagInvs.put(bag_enum, new BagInv((Bag<?>) item, bag_enum));
			return true;
		}
		return false;
	}
	
	public Item unequipItem(BAG bag_enum) {
		this.bagInvs.remove(bag_enum);
		return this.bags.remove(bag_enum);
	}
	
	public void delItem(Item item){
		for (Bag<?> bag : this.bags.values()) {
			if(bag.canContain(item) && bag.removeItem(item))break;
		}
	}
	
	/**
	 * Will add the amout specified in <code>count</code> to an existing ItemStack with the <code>ID</code>
	 * If this Method was sucessfull it will return 0.
	 * @param ID
	 * @param count
	 * @return the leftover that could not be added
	 */
	public int addToStack(int ID, int count){
		while(count>0) {
			Bag<?> pBag = null;
			int cIndex, pIndex = 0;
			for (Bag<?> cBag : bags.values()) {
				if(cBag.canContain(ItemList.GetItem(ID)) && (cIndex = cBag.hasSpace(ID)) >= 0) {
					if(pBag==null || cBag.getItemPriority() > pBag.getItemPriority()) {
						pBag = cBag;
						pIndex = cIndex;
					}
				}
			}
			if(pBag!=null) {
				if(pBag.getItem(pIndex) == null) {
					pBag.setItem(pIndex, ItemList.NewItem(ID));
				}
				count = pBag.getItem(pIndex).addStack(count);
				if(count > 0) {
					continue;
				}
				break;
			}else{
				break;
			}
		}
		return count;
	}
	
	/**
	 * Will try to add the Item into the Bags with the highes priority.
	 * If this item is a Stack of Items it will try to fit as many Items into the bags as Possible,
	 * during this prozess the stacksize of <code>item</code> will be changed.
	 * After sucess the stacksize will be at 0.
	 * @param item
	 * @return true if there was enough space for the whole Item Stack
	 */
	public boolean PickUp(Item item){
		while(item.getStack()>0) {
			Bag<?> pBag = null;
			int cIndex, pIndex = 0;
			for (Bag<?> cBag : bags.values()) {
				if(cBag.canContain(item) && (cIndex = cBag.hasSpace(item, true)) >= 0) {
					if(pBag==null || cBag.getItemPriority() > pBag.getItemPriority()) {
						pBag = cBag;
						pIndex = cIndex;
					}
				}
			}
			if(pBag!=null) {
				if(pBag.insertItem(pIndex, item)) {
					return true;
				}
				continue;
			}else{
				return false;
			}
		}
		return true;
	}
	
	public boolean CraftItem(Recipe r){
		for(component c : r.educts){
			int n = c.n;
			for (Bag<?> bag : this.bags.values()) {
				for(int i = 0; i < bag.invSize(); i++){
					if(bag.getItem(i)!=null){
						if(bag.getItem(i).getID()==c.ID) {
							n-=bag.getItem(i).getStack();
						}
					}
				}
			}
			if(n > 0)return false;
		}
		for(component c : r.educts){
			int n = c.n;
			for (Bag<?> bag : this.bags.values()) {
				for(int i = 0; i < bag.invSize(); i++){
					if(bag.getItem(i)!=null){
						if(bag.getItem(i).getID()==c.ID) {
							n=bag.getItem(i).delStack(n);
							if(bag.getItem(i).getStack() <= 0) {
								this.delItem(bag.getItem(i));
							}
						}
					}
				}
			}
		}
		for(component c : r.products){
			PickUp(ItemList.NewItem(c.ID, c.n));
		}
//		System.out.println("craftable");
		return true;
	}
	
	public void tick(int numTick) {
		
//		map.setlight(x, y, (byte) (64));map.addBlockUpdate(x, y,0); // just for testing lightsystem
		if(Game.input.space.isPressed() && canJump){
			speedY-= jumpspeed;
			canJump = false;
			jumpcooldown = 10;
		}
		if(Game.input.left.isPressed() && speedX > -walkspeed){
			speedX-=1;
			movingDir = 1;
		}
		if(Game.input.right.isPressed()  && speedX < walkspeed){
			speedX+=1;
			movingDir = 0;
		}
		if(Game.input.shift.isPressed() && !isinair){
			iscrouching=true;
		}else{
			iscrouching=false;
		}
		if((!Game.input.right.isPressed() && !Game.input.left.isPressed()) | (Game.input.right.isPressed() && Game.input.left.isPressed())){
			speedX=0;
			isMoving = false;
		}else{
			isMoving = true;
		}
		if(speedX!=0) numSteps++;
		if(jumpcooldown > 0) jumpcooldown--;
		
		Hitbox temp = col;
		if(iscrouching) temp =cols;
		if(!iscrouching || numTick%2==0){
			if(Colision.onGround(map, temp, x, y)){
				isinair = false;
				if(jumpcooldown == 0)canJump = true;
			}else{isinair = true;}
			if(Colision.canMove(map, temp, x, y, speedX, 0)){ x += speedX;
			}else{
				if(speedX != 0 & Colision.canWalkOver(map, temp, x, y, speedX, 0)){y --; x += speedX;}
				else speedX=0;
			}
			int n;
			if(speedY < 0){
				for(n = 0; n >= speedY; n--){
					if(!Colision.canMove(map, temp, x, y, 0, n)) speedY = n+1;
				}
			}else{
				for(n = 0; n <= speedY; n++){
					if(!Colision.canMove(map, temp, x, y, 0, n)) speedY = n-1;
				}
			}
			y += speedY;
		}
		
		/*		HOTBAR		*/
		if(Game.input.mousem.click() || Game.input.H.click()){
			if(openHotBar == 0) {
				hotBar.x = Game.input.mouse.x;
				hotBar.y = Game.input.mouse.y;
				openHotBar++;
			}else
				openHotBar=0;
		}
		if(openHotBar != 0){
			if(this.bags.get(BAG.BELT_1) != null){
				if(openHotBar < 20+this.bags.get(BAG.BELT_1).invSize())openHotBar +=2;
				if(Math.sqrt(Math.pow((Game.input.mouse.x-hotBar.x)/Game.SCALE, 2)+Math.pow((Game.input.mouse.y-hotBar.y)/Game.SCALE, 2)) >= 20+this.bags.get(BAG.BELT_1).invSize()){
					selected = (byte)(this.bags.get(BAG.BELT_1).invSize()/2-Math.atan2(Game.input.mouse.x-hotBar.x, Game.input.mouse.y-hotBar.y)/(Math.PI*2/this.bags.get(BAG.BELT_1).invSize()));
				}
			}
		}
		if(this.bags.containsKey(BAG.BELT_1) && this.bags.get(BAG.BELT_1).getItem(selected)!=null) {
			this.bags.get(BAG.BELT_1).getItem(selected).setMouse();
		}

		/*		EQUIPMENT		*/
		if(Game.input.equip.click()){
			openEquip = !openEquip;
		}if(openEquip)equipment.tick();
		
		/*		CRAFTING		*/
		if(Game.input.craft.click()){
			openCraft = !openCraft;
		}if(openCraft)crafting.tick();
		
		/*		INVENTORY		*/
		if(Game.input.inv.click()){
			openInv = !openInv;
		}if(openInv){
			for (BagInv bagInv : bagInvs.values()) {
				if(bagInv!=null){bagInv.tick();}
			}
		}

		if(this.bags.containsKey(BAG.BELT_1) && this.bags.get(BAG.BELT_1).getItem(selected)!=null && Game.input.mousel.isClickable()) {
			this.bags.get(BAG.BELT_1).getItem(selected).useItem(Game.input, this, map, Game.screen);
		}else{
			Game.input.mousel.click();
		}

		
		/*		ANIMATION		*/
		if(isMoving){
			if(numSteps%24==0)anim=3;
			if(numSteps%24==4)anim=4;
			if(numSteps%24==8)anim=5;
			if(numSteps%24==12)anim=6;
			if(numSteps%24==16)anim=5;
			if(numSteps%24==20)anim=4;
			if(iscrouching){
				if(numSteps%30<30)anim=7;
				if(numSteps%30<15)anim=8;
			}
		}else{
			if(numTick%30<30)anim = 0;
			if(numTick%30<15)anim = 1;
			if(this.bags.get(BAG.BELT_1) != null){if(this.bags.get(BAG.BELT_1).getItem(selected) != null){
				if(this.bags.get(BAG.BELT_1).getItem(selected).getAnim()!=0) anim = this.bags.get(BAG.BELT_1).getItem(selected).getAnim();
			}}
			if(iscrouching)anim = 9;
		}
		if(isinair){anim = 2;}
	}
	
	public void render() {
		Game.screen.drawTile(x-xOffset, y-yOffset, anim, movingDir*16, sheet, Color);
		
		if((anim == 10 || anim == 11) & this.bags.get(BAG.BELT_1) != null){
			try{
				if(anim == 10)	this.bags.get(BAG.BELT_1).getItem(selected).render(Game.screen, x+3-movingDir*10, y-5, movingDir*16);
				if(anim == 11)	this.bags.get(BAG.BELT_1).getItem(selected).render(Game.screen, x+4-movingDir*12, y-3, movingDir*16);
			}catch(NullPointerException e){}
		}
		if(anim == 12 & this.bags.get(BAG.BELT_1) != null){
			try{
				this.bags.get(BAG.BELT_1).getItem(selected).render(Game.screen, x+3-movingDir*8, y-2, movingDir*16);
			}catch(NullPointerException e){}
		}
		/*		EQUIPMENT		*/
		if(openEquip){equipment.render();}
		
		/*		EQUIPMENT		*/
		if(openCraft){crafting.render();}
		
		/*		INVENTORY		*/
		if(openInv){
			for (BagInv bagInv : bagInvs.values()) {
				if(bagInv!=null){bagInv.render();}
			}
		}
		
		/*		HOTBAR		*/
		if(openHotBar != 0){
			if(this.bags.get(BAG.BELT_1) != null){
				for(int i = 0; i < this.bags.get(BAG.BELT_1).invSize(); i ++){
					Game.screen.drawGUITile(
							hotBar.x/Game.SCALE+Game.screen.xOffset+(int)(openHotBar*Math.sin((i+0.5)*Math.PI*2/this.bags.get(BAG.BELT_1).invSize()))-6
							,hotBar.y/Game.SCALE+Game.screen.yOffset+(int)(-openHotBar*Math.cos((i+0.5)*Math.PI*2/this.bags.get(BAG.BELT_1).invSize()))-6
							, 0, 0, itemBackground, 0);
					if(i == selected){
						Game.screen.drawGUITile(
								hotBar.x/Game.SCALE+Game.screen.xOffset+(int)(openHotBar*Math.sin((selected+0.5)*Math.PI*2/this.bags.get(BAG.BELT_1).invSize()))-6
								,hotBar.y/Game.SCALE+Game.screen.yOffset+(int)(-openHotBar*Math.cos((selected+0.5)*Math.PI*2/this.bags.get(BAG.BELT_1).invSize()))-6
								, 0, 0, itemBackgrounds, 0);
					}
					try{
						this.bags.get(BAG.BELT_1).getItem(i).render(Game.screen
								,hotBar.x/Game.SCALE+Game.screen.xOffset+(int)(openHotBar*Math.sin((i+0.5)*Math.PI*2/this.bags.get(BAG.BELT_1).invSize()))-5
								,hotBar.y/Game.SCALE+Game.screen.yOffset+(int)(-openHotBar*Math.cos((i+0.5)*Math.PI*2/this.bags.get(BAG.BELT_1).invSize()))-5
								,true);
					}catch(NullPointerException e){}
				}
				try{Game.font.render(Game.input.mouse.x/Game.SCALE+Game.screen.xOffset, Game.input.mouse.y/Game.SCALE+Game.screen.yOffset, (this.bags.get(BAG.BELT_1).getItem(selected).getName()), 0, 0xff000000, Game.screen);}
			catch(NullPointerException e){}
			}
		}
	}

	public void save(ArrayList<Byte> file){
		ConvertData.I2B(file, x);
		ConvertData.I2B(file, y);
		for (BAG bagEnum : Player.BAG.values()) {
			if(this.bags.containsKey(bagEnum)) {
				this.bags.get(bagEnum).save(file);
			}else {
				ConvertData.I2B(file, 0);
			}
		}
	}

	public void load(ArrayList<Byte> file){
		x = ConvertData.B2I(file);
		y = ConvertData.B2I(file);
		for (BAG bagEnum : Player.BAG.values()) {
			Bag<?> bag = (Bag<?>) ItemList.NewItem(ConvertData.B2I(file));
			if(bag != null){
				bag.load(file);
				this.equipItem(bagEnum, bag);
			}
		}
	}

	public void create(){
		this.x = 524800;
		int h=524500;//for(h = 524500; PixelList.GetMat(this.x, h, map,1).ID==0 && h < 525000; h++){}
		this.y = h-yOffset;
		
		//initializing Equipment
		this.equipItem(BAG.BELT_1, ItemList.NewItem("SmallBeltBag"));
		this.equipItem(BAG.ITEM_1, ItemList.NewItem("NormalItemBag"));
		this.equipItem(BAG.MAT_1, ItemList.NewItem("NormalMaterialBag"));
		this.equipItem(BAG.TOOL_1, ItemList.NewItem("SmallToolBag"));
		
		//initializing Inventory
		Pickaxe newpick = (Pickaxe) ItemList.NewItem("StonePickaxe");
		this.PickUp(newpick);
		if(Game.devmode){
			Item newitem = ItemList.NewItem(64); newitem.addStack(999);
			this.PickUp(newitem);
			newitem = ItemList.NewItem(16); newitem.addStack(999);
			this.PickUp(newitem);
			newitem = ItemList.NewItem(17); newitem.addStack(999);
			this.PickUp(newitem);
			newitem = ItemList.NewItem(18); newitem.addStack(999);
			this.PickUp(newitem);
			
			this.PickUp(ItemList.NewItem(333));
			newitem = ItemList.NewItem(400);newitem.addStack(999);
			this.PickUp(newitem);
			this.PickUp(ItemList.NewItem(332));
		}
	}
}

package gui.menu.options;

import static main.KeyConfig.keyMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import gfx.Screen;
import gui.Button;
import gui.menu.OptionScreen;
import main.Game;
import main.KeyInput;
import main.Keys;
import main.MouseInput;

public class Controls {
	private OptionScreen mainMenu;
	
	private Button  back,scrollUP,scrollDOWN;
	private Map<Button, Integer> buttonValues;
	private ArrayList<KeyConfig> keyConfigs;
	
	private int lOffset;
	private int lVisibleEntries;
	private int lFieldTop, lFieldBottom, lFieldSize;
	
	private boolean changing;
	private KeyConfig selectedConfig;
	
	public Controls(OptionScreen mainMenu) {
		this.mainMenu = mainMenu;
		this.back = new Button(50, 50, 60, 60);
		this.back.gfxData("/Buttons/back.png", true);
		this.lOffset = 0;
		this.lVisibleEntries = 5;
		this.lFieldTop = Screen.height/10*3;
		this.lFieldBottom = Screen.height-90;
		this.lFieldSize = this.lFieldBottom-this.lFieldTop;
		
		this.buttonValues = new HashMap<>();
		this.keyConfigs = new ArrayList<>();
		this.resetKeyButtons();
		
		this.scrollUP = new Button(Screen.width/2, this.lFieldTop-30, 60, 60);
		this.scrollUP.gfxData("/Buttons/ArrowDown.png", false, true, false);
		this.scrollDOWN = new Button(Screen.width/2, this.lFieldBottom+30, 60, 60);
		this.scrollDOWN.gfxData("/Buttons/ArrowDown.png", false);
	}
	
	private void resetKeyButtons() {
		this.changing = false;
		this.selectedConfig = null;
		this.buttonValues.clear();
		this.keyConfigs.clear();
		for (Keys key : Keys.values()) {
			addKeyButton(key.getName(), key);
		}
		this.setButtonPositions();
	}
	
	private void addKeyButton(String name, Keys key) {
		Button button = new Button(100, 100, 300, 60);
		for (Entry<Integer, Keys> entry : keyMapping.entrySet()) {
			if(key.equals(entry.getValue())) {
				buttonValues.put(button, entry.getKey());
				button.TextData(main.KeyConfig.getKeyName(entry.getKey()), false, true);
				break;
			}
		}
		keyConfigs.add(new KeyConfig(button, name));
	}
	
	private void setButtonPositions() {
		int buttonHeight = keyConfigs.get(0).button.getHeight();
		int gapHeight = (this.lFieldSize-(buttonHeight*this.lVisibleEntries))/(this.lVisibleEntries+1);
		for(int i = 0; i < this.lVisibleEntries; i++) {
			keyConfigs.get(i+this.lOffset).button.setPos(Screen.width/2, this.lFieldTop+(i+1)*gapHeight+i*buttonHeight, false, false);
			keyConfigs.get(i+this.lOffset).height = this.lFieldTop+(i+1)*gapHeight+i*buttonHeight;
		}
	}
	
	public void tick(){
		int scroll = MouseInput.mouse.getScroll();
		if(this.lOffset > 0)scrollUP.tick();
		if((scrollUP.isclicked || scroll<0) && this.lOffset > 0) {this.lOffset--;this.setButtonPositions();}
		if(this.lOffset < keyConfigs.size()-this.lVisibleEntries)scrollDOWN.tick();
		if((scrollDOWN.isclicked || scroll>0) && this.lOffset < keyConfigs.size()-this.lVisibleEntries) {this.lOffset++;this.setButtonPositions();}
		
		if(!changing) {
			for(int i = this.lOffset; i < this.lOffset+this.lVisibleEntries; i++) {
				if(keyConfigs.get(i).tick())break;
			}
		}else {
			int keyCode = 0;
			if((keyCode=KeyInput.lastKeyCode)!=0) {
				if(!keyMapping.containsKey(keyCode)) {
					int keyCodeOld = this.buttonValues.get(selectedConfig.button);
					Keys key = keyMapping.get(keyCodeOld);
					keyMapping.remove(keyCodeOld);
					keyMapping.put(keyCode, key);
				}
				this.resetKeyButtons();
			}
		}
		back.tick();
		if(back.isclicked || Keys.MENU.click()){
			this.changing = false;
			this.selectedConfig = null;
			this.mainMenu.resetMenu();
		}
	}
	
	public void render(){
		back.render();
		Game.font.render(Screen.width/2, 50, "Controls", 0, 0xff000000);
		if(changing)Game.font.render(Screen.width/2, 100, "Please press a Key", 0, 0xffff0000);
		if(this.lOffset > 0)scrollUP.render();
		if(this.lOffset < keyConfigs.size()-this.lVisibleEntries)scrollDOWN.render();
		for(int i = this.lOffset; i < this.lOffset+this.lVisibleEntries; i++) {
			keyConfigs.get(i).render();
		}
	}
	
	private class KeyConfig{
		protected Button button;
		protected int height = 0;
		private String text;
		
		public KeyConfig(Button button, String text) {
			this.button = button;
			this.text = text;
		}
		
		public boolean tick(){
			button.tick();
			if(button.isclicked){
				changing = true;
				selectedConfig = this;
				return true;
			}
			return false;
		}
		
		public void render(){
			Game.font.render(Screen.width/2-400, height, false, false, text, 0, 0xff000000);
			button.render();
		}
	}
}

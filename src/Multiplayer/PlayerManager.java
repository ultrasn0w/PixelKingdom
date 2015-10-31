package Multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Main.Game;
import Main.IOConverter;
import entities.MPlayer;

	public class PlayerManager implements InputReceiver{
		private Game game;
		private ArrayList<MPlayer> plrs;
		public PlayerManager(Game g, ArrayList<MPlayer> players) {
			game = g;
			plrs = players;
			Client.server.requests.add(this);
		}
		
		public boolean useInput(InputStream in) throws IOException {
			int i = 0,n;
			try {i = in.read();} catch (IOException e) {e.printStackTrace();}
			switch(i) {
			case Request.PLAYER.NEW:
				plrs.add(new MPlayer(game.client.map, game, in.read()));
				Game.logInfo("new player");
				break;
			case Request.PLAYER.DELETE:
				n = in.read();
				for(int j = 0; j < plrs.size(); j++) {
					if(plrs.get(j).number==n) {
						plrs.remove(j);
						Game.logInfo("player left");
						break;
					}
				}
				break;
			case Request.PLAYER.COLOR:
				n = 0;
				try {n = in.read();} catch (IOException e) {}
				for(int j = 0; j < plrs.size(); j++) {
					if(plrs.get(j).number==n) {
						plrs.get(j).color = IOConverter.receiveInt(in);
					}
				}
				break;
			case Request.PLAYER.REFRESH:
				n = 0;
				try {n = in.read();} catch (IOException e) {}
				for(int j = 0; j < plrs.size(); j++) {
					if(plrs.get(j).number==n) {
						plrs.get(j).x = IOConverter.receiveInt(in);
						plrs.get(j).y = IOConverter.receiveInt(in);
						plrs.get(j).anim = in.read();
						plrs.get(j).setDir(in.read());
					}
				}
				break;
			}
			return false;
		}

		public int requestType() {return Request.PLAYER_DATA;}
	}
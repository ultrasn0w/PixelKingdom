package multiplayer.server;

import static main.Game.logWarning;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import entities.MPlayer;
import main.Game;
import main.conversion.ConverterInStream;
import main.conversion.ConverterQueue;
import main.conversion.ConverterOutStream;
import multiplayer.ConnectionManager;
import multiplayer.MapUpdater.UpdateList;
import multiplayer.Request;

public class ClientManager implements Runnable, ConnectionManager {
	
	public MPlayer player;
	public byte ID;
	private boolean running;
	private ClientsManager clientsManager;
	private ConverterInStream clientIn;
	private ConverterOutStream clientOut;
	private HashMap<Byte,InputReceiver> receivers;
	
	public ClientManager(Socket clientConnection, ClientsManager clientsManager, HashMap<Byte,InputReceiver> receivers, byte id){
		this.running = true;
		try {
			this.clientIn = new ConverterInStream(clientConnection.getInputStream());
			this.clientOut = new ConverterOutStream(clientConnection.getOutputStream());
		} catch (IOException e) {e.printStackTrace();}
		this.receivers = receivers;
		this.clientsManager = clientsManager;
		this.ID = id;
		this.player = new MPlayer(id);
		
		Thread t = new Thread(this);
		t.setName("Client"+id);
		t.start();
	}

	public void run() {
		byte request;
		int length;
		while (running){
			try {
				Thread.sleep(1);
				length = clientIn.readInt()-1;
				request = clientIn.readByte();
				if(request==Request.CLOSE_CONNECTION || request==Request.END_OF_STREAM) {
					Game.logInfo("Disconnect of Client Nr. "+this.ID+" received");
					this.running=false;
					this.clientsManager.closeConnection(this.ID);
				}else if(this.receivers.containsKey(request)){
					ConverterQueue data = new ConverterQueue();
					for (int i = 0; i < length; i++) {
						data.addByte(this.clientIn.readByte());
					}
					try{
						this.receivers.get(request).useInput(data, this.ID);
					}catch(NullPointerException e) {e.printStackTrace();}
				}else{
					logWarning("Skipping "+length+" bytes of request type "+request);
					this.clientIn.skipBytes(length);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Game.logError("Exception with Client Nr. "+this.ID+" catched");
				this.running = false;
				this.clientsManager.closeConnection(this.ID);
			}
		}
	}
	
	private synchronized void sendToClient(ConverterQueue data) throws IOException {
		this.sendToClient(data.emptyToArray());
	}
	
	private synchronized void sendToClient(byte ... data) throws IOException {
		this.clientOut.writeInt(data.length);
		this.clientOut.write(data);
		this.clientOut.flush();
	}
	
	public void closeConnection() throws IOException {
		clientOut.close();
		clientIn.close();
	}

	public void sendPlayerConnect(byte ID) throws IOException {
		this.sendToClient(Request.PLAYER_DATA, Request.PLAYER_NEW, ID);
	}
	
	public void sendPlayerDisconnect(byte ID){
		try {
			this.sendToClient(Request.PLAYER_DATA, Request.PLAYER_DELETE, ID);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void sendPlayerColor(byte ID, int color) throws IOException {
		ConverterQueue data = new ConverterQueue();
		data.addByte(Request.PLAYER_DATA);
		data.addByte(Request.PLAYER_COLOR);
		data.addByte(ID);
		data.addInt(Game.configs.PlrCol);
		this.sendToClient(data);
	}
	
	public void sendPlayerRefresh(byte ID) throws IOException {
		MPlayer player = clientsManager.getClient(ID).player;
		ConverterQueue data = new ConverterQueue();
		data.addByte(Request.PLAYER_DATA);
		data.addByte(Request.PLAYER_REFRESH);
		data.addByte(ID);
		data.addInt(player.x);
		data.addInt(player.y);
		data.addByte(player.getAnim());
		data.addByte(player.getDir());
		this.sendToClient(data);
	}
	
	public void sendMapUpdates(ArrayList<UpdateList> UpdateLists) throws IOException {
		for (UpdateList updateList : UpdateLists) {
			this.sendToClient(updateList.compress());
		}
	}
	
	public void sendChunk(int cx, int cy, byte[] rawData) throws IOException {
		ConverterQueue data = new ConverterQueue();
		data.addByte(Request.CHUNK_DATA);
		data.addInt(rawData.length);
		data.addInt(cx);
		data.addInt(cy);
		for(byte b : rawData) {
			data.addByte(b);
		}
		this.sendToClient(data);
	}
}

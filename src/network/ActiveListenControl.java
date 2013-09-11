package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import network.controller.Controller;
import network.packets.incoming.ControlIncomingPacket;
import network.packets.incoming.StateUpdate;
import network.packets.outgoing.ConnectionType;


import android.util.Log;

import com.example.watermaze.WaterMaze_Control;

public class ActiveListenControl implements Runnable {
	protected WaterMaze_Control wmc;
	protected Socket sock;
	protected String server;
	protected int port;
	protected PrintWriter out;
	protected BufferedReader in;
	
	//constructor to be used.
	public ActiveListenControl(WaterMaze_Control control, Controller ps)
	{
		wmc = control;
		//TODO: this will not be hard coded server = ps.getServer();
		server = "137.110.118.118";	//Set to dev for now
		port = 12012;		
	}
	
	@Override
	public void run() {
		//connect
		try{
			if(sock != null)
				sock.close();
			try{
				sock = new Socket(server, port);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new PrintWriter(sock.getOutputStream(), true);
				
				//send a connection type packet
				ConnectionType p = new ConnectionType("active listen control");
				out.println(p.sendStart());
				//send packets
				String s;
				while(p.hasLine())
				{
					s = p.readLine();
					out.println(s);
				}
				//send end
				out.println(p.sendEnd());
				
			} catch (UnknownHostException e) {
				Log.d("Connection", "Could not connect");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//active read
		while(wmc.isActive())
		{
			try {
				//1	we already know this is a control
				if(!in.readLine().contains("State Response"))
					Log.d("Network error", "active listen control not a State Response");
				//2
				String state = in.readLine();
				handleState(state.substring(1));	//strip the 'b'
				//3 last line
				in.readLine();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//cleanup
		out.close();
		try {
			in.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	//this is not implemented. Possibly not needed.  
	ControlIncomingPacket startParse(String type)
	{
		ControlIncomingPacket cip = null;
		if(type == "State Update")
		{
			cip = new StateUpdate(wmc);	//wmc would be passed through.
		} else{
			//other command types to go here.  there are none which is why it's not implemented.
		}
		return cip;	
	}
	
	void handleState(String line)
	{
		//TODO: implement other states (this will be done towards the end
		if(line == "default state")
		{	//load out state
			wmc.defaultState();
		}else if(line == "geometry loaded")
		{	//ready to begin trials
			
		}else if(line == "running")
		{	//currently running trial
			wmc.runningTrial();
		}else if(line == "trial finished")
		{	//this is mostly done for validation purposes
			
		}else if(line == "trial loaded")
		{	//this may be redundant
			
		}else{
			Log.d("ALC handleState", "error: " + line);
		}
	}

}

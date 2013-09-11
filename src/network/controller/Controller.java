package network.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import network.packets.outgoing.ConnectionType;
import network.packets.outgoing.OutboundPacket;


import com.example.watermaze.WaterMaze_Control;
import android.util.Log;

public class Controller {
	protected Socket sock;
	protected String server;
	protected int port;
	protected PrintWriter out;
	protected BufferedReader in;
	protected String destination;
	
	protected ControllerInput calVRin;
	protected ControllerOutput calVRout;
	
	protected Boolean active;	//its a wrapper object so it gets passed by reference
	
	public Controller(String server, int port) {
		this.server = server;
		this.port = port;
		
	}
	
	public void init() throws UnknownHostException, IOException{
		Thread t = new Thread(new SenderThread_Connect());
		t.start();
		//intialize input and output controllers
		calVRin = new ControllerInput()
		active = true;
	}
	
	public void send(OutboundPacket p)
	{
		Thread t = new Thread(new SenderThread_Send(p));
		t.start();
	}
	
	public void read(WaterMaze_Control control)
	{
		Thread t = new Thread(new StateRequestRead(control));
		t.start();
	}
	
	public void setDestination(String s)
	{
		server = s;
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public class SenderThread_Connect implements Runnable
	{
		@Override
		public void run() {
			try{
				if(sock != null)
					sock.close();
				try{
					sock = new Socket(server, port);
					out = new PrintWriter(sock.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				} catch (UnknownHostException e) {
					Log.d("Connection", "Could not connect");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//TODO: send connection type
				ConnectionType ct = new ConnectionType("control");
				Log.d("Connection", "this is a control socket");
				//1
				out.println(ct.sendStart());
				//2
				while(ct.hasLine())
				{
					out.println(ct.readLine());
				}
				//3
				out.println(ct.sendEnd());
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
	
	public class SenderThread_Send implements Runnable
	{
		protected OutboundPacket p;
		
		public SenderThread_Send(OutboundPacket p)
		{
			this.p = p;
		}
		
		@Override
		public void run() {
			try{
				Log.d("Connection", "Sending " + p.toString());
				//out = new PrintWriter(sock.getOutputStream(), true);
				String s = "";
				//send start
				out.println(p.sendStart());
				//send packets
				while(p.hasLine())
				{
					s = p.readLine();
					out.println(s);
				}
				//send end
				out.println(p.sendEnd());
			}
			catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	public class StateRequestRead implements Runnable
	{
		protected WaterMaze_Control control;

		
		public StateRequestRead(WaterMaze_Control control)
		{
			this.control = control;
		}
		@Override
		public void run() {
			try{
				Log.d("Connection", "Reading State Request");
				//1
				String type = in.readLine();
				Log.d("type", type);
				//2
				String data = in.readLine();
				Log.d("data", data);
				handleData(type, data);
				//3
				String term = in.readLine();
				Log.d("term", term);
				
			}
			catch(Exception e1){
				e1.printStackTrace();
			}
		}
		
		protected void handleData(String type, String data)
		{
			//data confirmation
			if(!type.contains("State Response"))
			{
				Log.d("StateResponse", "type: " + type + " data: " + data);
			}
			
			if(data.contains("running"))
			{
				control.runningTrial();
			}else if(data.contains("default state"))
			{
				control.defaultState();
			}
			
		}
				
	}
	
	public String getServer()
	{
		return server;
	}
	
	
}

package network.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import network.packets.outgoing.OutboundPacket;


import com.example.watermaze.WaterMaze_Control;
import com.example.watermaze.WaterMaze_Results;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Controller {
	protected Socket sock;
	protected String server;
	protected int port;
	protected PrintWriter out;
	protected BufferedReader in;
	protected String destination;
	
	protected ControllerInput calVRin;
	protected ControllerOutput calVRout;
	protected WaterMaze_Control wmc;
	protected WaterMaze_Results wmr;
	
	protected Activity a;
	
	protected Boolean active;	//its a wrapper object so it gets passed by reference
	
	public Controller() {
		server = "not connected";
		this.port = 12012;	//default port for CalVR
		active = false;
		
		// IO controllers
		calVRout = new ControllerOutput();
	}
	
	public void disconnect()
	{
		server = "not connected";
		try {
			sock.close();
			active = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() throws UnknownHostException, IOException
	{
		new ControllerConnect().execute();
	}
	
	//Synchronization method.  This way in and out are completely initialized before they are passed.
	public void postConnect(ConnectionResults cr)
	{
		if(cr == null)
		{
			Context context = a.getApplicationContext();
			Toast.makeText(context, "Could not connect", Toast.LENGTH_SHORT).show();
		}
		else{
			//activate the controller
			active = true;
			sock = cr.getSock();
			in = cr.getIn();
			out = cr.getOut();
			
			if(wmc != null && wmr != null)
			{
				//initialize input
				calVRin = new ControllerInput(wmc, wmr, active);
				calVRin.newInputDevice(cr.getIn());
				
				//initialize output
				calVRout = new ControllerOutput();
				calVRout.newOutputDevice(cr.getOut());
				
				//run input thread
				Thread tIn = new Thread(calVRin);
				tIn.start();
				
				//run output thread
				Thread tOut = new Thread(calVRout);
				tOut.start();
				
				
			}
			else
			{
				Log.d("Controller", "tabs are null? whaaat?");
			}
		}
	}
	
	public void send(OutboundPacket p)
	{
		calVRout.newPacket(p);
	}
	
	public void setDestination(String s, boolean override)
	{
		Log.d("server: " , server);
		Log.d("s: ", s);
		if(server != s || !active || override){
			server = s;
			try {
				connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//needs to be async task
	protected class ControllerConnect extends AsyncTask<Void, Void, ConnectionResults>
	{
		@Override
		protected ConnectionResults doInBackground(Void... params) 
		{
			Log.d("Async", "connecting");
			try
			{
				if(sock != null)
				{
					active = false;
					sock.close();
					Log.d("Connection", "disconnecting");
				}
				try{
					Socket s = new Socket(server, port);
					PrintWriter o = new PrintWriter(s.getOutputStream(), true);
					BufferedReader i = new BufferedReader(new InputStreamReader(s.getInputStream()));
					return (new ConnectionResults(o, i, s));
				} catch (UnknownHostException e) {
					Log.d("Connection", "Could not connect");
				} catch (IOException e) {
					e.printStackTrace();
					Log.d("Connection", "io exception");
				}
			}catch(Exception e){
				Log.d("Connection", "if(sock != null) exception");
			}
			return null;
		}
		
		protected void onPostExecute(ConnectionResults results)
		{
			Log.d("Async", "onPostExecute");
			postConnect(results);
		}
	}
	
	public String getServer()
	{
		return server;
	}
	
	public void setWMC(WaterMaze_Control wmc)
	{
		this.wmc = wmc;
	}
	
	public void setWMR(WaterMaze_Results wmr)
	{
		this.wmr = wmr;
	}
	
	public boolean isConnected()
	{
		return active.booleanValue();
	}
	
	public void setActivity(Activity a)
	{
		this.a = a;
	}	
}

package network.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import network.packets.incoming.CueList;
import network.packets.incoming.DataPoint;
import network.packets.incoming.GeneralComm;
import network.packets.incoming.InboundPacket;
import network.packets.incoming.StateUpdate;
import network.packets.incoming.TrialSetup;

import android.os.Handler;
import android.util.Log;

import com.example.watermaze.WaterMaze_Control;
import com.example.watermaze.WaterMaze_Results;


public class ControllerInput implements Runnable {
	protected BufferedReader in;
	protected WaterMaze_Control wmc;
	protected WaterMaze_Results wmr;
	protected Boolean active;
	protected Handler handler;
	protected AtomicReference<Runnable> takeAction;
	
	public ControllerInput(WaterMaze_Control wmc, WaterMaze_Results wmr, Boolean active)
	{
		this.in = null;
		this.wmc = wmc;
		this.wmr = wmr;
		this.active = active;
		handler = new Handler();
		takeAction = new AtomicReference<Runnable>();
	}
	@Override
	public void run() {
		while(active)
		{
			try {
				// Stage 1: get packet type
				String header = in.readLine().substring(1);
				InboundPacket ip = getPacketType(header);
				
				// Stage 2: get all the data packets
				String dataLine = in.readLine();
				while(dataLine.charAt(0) != 'c')
				{
					if(ip != null)
					{
						ip.addLine(dataLine.substring(1));	//substring from char 1 to remove packet type header
					}
					dataLine = in.readLine();
				}
				
				// Stage 3: take action
				takeAction.set(getAction(ip));
				handler.post(takeAction.get());
				
				//process incoming packet
			} catch (IOException e) {
				//close the connection
				wmc.getController().disconnect();
			}
			catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	protected Runnable getAction(final InboundPacket ip)
	{
		Runnable r = new Runnable() {
			public void run() {
				if(ip != null)
					ip.takeAction();
			}
		};
		
		return r;
	}

	private InboundPacket getPacketType(String type) {
		InboundPacket ip = null;
		//Log.d("Controller Input", type);
		if(type.equals("Data Point"))
		{
			ip = new DataPoint(wmr);
		}else if(type.equals("Trial Setup"))
		{
			ip = new TrialSetup(wmr);
		}else if(type.equals("Paradigm Header"))
		{
			//TODO: will i even do this?
		}else if(type.equals("State Update"))
		{
			ip = new StateUpdate(wmc);
		}else if(type.equals("Cue List"))
		{
			ip = new CueList(wmc);
		}else if(type.equals("General Comm"))
		{
			Log.i("ControllerInput", "General Comm");
			ip = new GeneralComm(wmc, wmr);
		}
		
		return ip;
	}
	
	public void newInputDevice(BufferedReader in)
	{
		this.in = in;
	}

}

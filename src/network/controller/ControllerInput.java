package network.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import network.packets.incoming.DataPoint;
import network.packets.incoming.InboundPacket;
import network.packets.incoming.StateUpdate;
import network.packets.incoming.TrialSetup;

import com.example.watermaze.WaterMaze_Control;
import com.example.watermaze.WaterMaze_Results;


public class ControllerInput implements Runnable {
	protected PrintWriter out;
	protected BufferedReader in;
	protected WaterMaze_Control wmc;
	protected WaterMaze_Results wmr;
	protected Boolean active;
	
	
	public ControllerInput(PrintWriter out, BufferedReader in, WaterMaze_Control wmc, WaterMaze_Results wmr, Boolean active)
	{
		this.out = out;
		this.in = in;
		this.wmc = wmc;
		this.wmr = wmr;
		this.active = active;
	}
	@Override
	public void run() {
		// TODO HEY! LISTEN!
		while(active)
		{
			try {
				//1	get the type
				InboundPacket ip = getPacketType(in.readLine().substring(1));
				//2 get all the data packets
				String dataLine = in.readLine();
				while(dataLine.charAt(0) != 'c')
				{
					ip.addLine(dataLine.substring(1));	//substring from char 1 to remove packet type header
					dataLine = in.readLine();
				}
				//3 do not need to read stage 3 packet.  take action from packet
				ip.takeAction();
				
				//process incoming packet
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private InboundPacket getPacketType(String type) {
		InboundPacket ip = null;
		if(type == "Data Point")
		{
			ip = new DataPoint(wmr);
		}else if(type == "Trial Setup")
		{
			ip = new TrialSetup(wmr);
		}else if(type == "Paradigm Header")
		{
			//TODO: will i even do this?
		}else if(type == "State Update")
		{
			ip = new StateUpdate(wmc);	//wmc would be passed through.
		}
		
		return ip;
	}

}

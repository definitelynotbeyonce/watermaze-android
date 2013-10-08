package network.controller;

import java.io.PrintWriter;
import java.util.ArrayList;

import android.util.Log;
import network.packets.outgoing.OutboundPacket;

public class ControllerOutput implements Runnable{
	protected PrintWriter out;
	protected OutboundPacket currentPacket;
	protected boolean usable;
	private ArrayList<OutboundPacket> packets;
	
	public ControllerOutput() {
		this.out = null;
		usable = true;
		packets = new ArrayList<OutboundPacket>();
	}

	@Override
	public void run() {
		try{
			Log.i("ControllerOutput", "Sending " + currentPacket.toString());
			if(out == null)
			{
				Log.d("ControllerOutput", "printwriter is null");
			}
			String s = "";
			
			//send start
			out.println(currentPacket.sendStart());
			//send packets
			while(currentPacket.hasLine())
			{
				s = currentPacket.readLine();
				out.println(s);
			}
			//send end
			out.println(currentPacket.sendEnd());
			usable = true;
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
		
	}

	public void newPacket(OutboundPacket p) {
		currentPacket = p;
	}
	
	public void newOutputDevice(PrintWriter out)
	{
		this.out = out;
	}

}

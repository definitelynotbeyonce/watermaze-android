package network.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import android.util.Log;
import network.packets.outgoing.OutboundPacket;

public class ControllerOutput implements Runnable{
	protected PrintWriter out;
	protected boolean usable;
	private volatile ArrayList<OutboundPacket> packets;
	
	public ControllerOutput() {
		this.out = null;
		usable = true;
		packets = new ArrayList<OutboundPacket>();
	}

	@Override
	public void run() {
		try{
			while(true)
			{
				while(packets.size() > 0)
				{
					OutboundPacket currentPacket = packets.remove(0);
					
					Log.i("ControllerOutput", "Sending " + currentPacket.toString());
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
				}
			}
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
		
	}

	public void newPacket(OutboundPacket p) {
		packets.add(p);
	}
	
	public void newOutputDevice(PrintWriter out)
	{
		this.out = out;
	}

}

package network.packets.outgoing;

public class StateRequest extends OutboundPacket{
	public StateRequest()
	{
		super();
		writeSize = 256;
		type = "State Request";
		data = "";
		state = 1;
	}
	
	public String toString()
	{
		return type + " " + data;
	}
}

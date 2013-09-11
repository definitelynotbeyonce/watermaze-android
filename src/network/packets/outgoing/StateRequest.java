package network.packets.outgoing;



public class StateRequest extends OutboundPacket{
	public StateRequest()
	{
		writeSize = 256;
		type = "State Request";
		data = "";
		state = 1;
	}
}

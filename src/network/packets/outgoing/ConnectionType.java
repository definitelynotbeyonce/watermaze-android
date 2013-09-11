package network.packets.outgoing;



public class ConnectionType extends OutboundPacket{
	public ConnectionType(String s)
	{
		writeSize = 256;
		data = s;
		type = "Initial Connect";
		state = 1;
	}
}

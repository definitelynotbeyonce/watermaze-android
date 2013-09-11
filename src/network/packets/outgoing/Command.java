package network.packets.outgoing;



public class Command extends OutboundPacket{
	public Command(String s)
	{
		writeSize = 256;
		data = s;
		type = "Command";
		state = 1;
	}
	
	public String toString()
	{
		return type + " " + data;
	}
}

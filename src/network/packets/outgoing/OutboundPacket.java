package network.packets.outgoing;

import java.nio.ByteBuffer;

//class used to send over the network
public class OutboundPacket{
	protected String type;	//what is the type of this packet
	protected String data;	//data being sent
	protected int writeIndex;
	protected int writeSize;
	protected int state;
	
	public OutboundPacket(){
		writeSize = 256;
		state = 1;
	}
	
	//TODO: create string method.  this is what will pass over the network
	public String toString(){
		return "";
	}
	
	public byte[] toBytes(){
		return new byte[6];
	}
	
	public boolean hasLine()
	{
		return state != 3;
	}
	
	public String sendStart()
	{
		//state
		String s1 = new StringBuffer(new String(intToByte(1))).reverse().toString();
		//size
		String s2 = new StringBuffer(new String(intToByte(type.length() + 1))).reverse().toString();
		
		//build packet
		String sendString = s1 + s2 + type;
		
		state = 2;
		
		return sendString;
	}
	
	public String readLine()
	{
		//get state
		String s1 = new StringBuffer(new String(intToByte(state))).reverse().toString();
		//how many bytes are we sending
		int size = Math.min(data.length() - writeIndex, writeSize) + 1; 
		String s2 = new StringBuffer(new String(intToByte(size))).reverse().toString();
		
		//build data string	
		String s3 = data.substring(writeIndex, Math.min(data.length(), writeIndex + writeSize));
		
		//figure out what the next state is
		writeIndex += writeSize;
		if(writeIndex >= data.length())
		{
			state = 3;
		}
		
		//return all three parts of the packet
		return s1 + s2 + s3;
	}
	
	public String sendEnd()
	{
		String s1 = new StringBuffer(new String(intToByte(3))).reverse().toString();
		String s2 = new StringBuffer(new String(intToByte(1))).reverse().toString();
		return s1 + s2;
	}
	
	public byte[] intToByte(int i)
	{
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
		return bytes;
	}
}

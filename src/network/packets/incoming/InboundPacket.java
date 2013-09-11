package network.packets.incoming;

public abstract class InboundPacket {
	//not really a useful class pretty much only used for polymorphisms
		public String type;
		
		public abstract void addLine(String line);
		
		public String getType()
		{
			return type;
		}

		public abstract void takeAction();
}

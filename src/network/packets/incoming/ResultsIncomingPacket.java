package network.packets.incoming;

import com.example.watermaze.WaterMaze_Results;

public abstract class ResultsIncomingPacket extends InboundPacket{
	protected WaterMaze_Results resultsTab;
	
	ResultsIncomingPacket(WaterMaze_Results results)
	{
		resultsTab = results;
	}
}

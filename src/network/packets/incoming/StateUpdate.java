//Not currently used

package network.packets.incoming;

import com.example.watermaze.WaterMaze_Control;

public class StateUpdate extends ControlIncomingPacket{
	protected String state;
	
	public StateUpdate(){}
	public StateUpdate(WaterMaze_Control wmc)
	{
		this.wmc = wmc;
	}
	@Override
	public void addLine(String line) {
		state = line.substring(1);	//strip the 'b' at the beginning
	}
	
	public String getState()
	{
		return state;
	}
	@Override
	public void takeAction() {
		// TODO Auto-generated method stub
		
	}

}

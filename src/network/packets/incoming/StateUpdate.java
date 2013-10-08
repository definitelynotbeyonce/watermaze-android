//Not currently used

package network.packets.incoming;

import android.util.Log;

import com.example.watermaze.WaterMaze_Control;

public class StateUpdate extends ControlInboundPacket{
	protected String state;
	
	public StateUpdate(WaterMaze_Control wmc)
	{
		super(wmc);
		state = "";
	}
	@Override
	public void addLine(String line) {
		state = line;	//strip the 'b' at the beginning
	}
	
	public String getState()
	{
		return state;
	}
	
	@Override
	public void takeAction() {
		Log.d("State Update", state);
		controlTab.setState(state);
	}

}

package network.packets.incoming;

import com.example.watermaze.WaterMaze_Results;

import android.util.Log;



public class DataPoint extends ResultsInboundPacket{
	public DataPoint(WaterMaze_Results results) {
		super(results);
		pos = new float[2];
	}

	protected float[] pos;
	protected float time;
	//TODO: eye data
	
	//return this as a percentage (0-100) of the total grid.
	public float[] toGraphCoords(TrialSetup ts)
	{
		float[] returnVal = new float[2]; //x and y
		
		//get x
		float xScale = (ts.width * ts.gridSize);
		returnVal[0] = (pos[0] - ts.zero_zero[0])/xScale;
		
		//get y
		float yScale = (ts.length * ts.gridSize);
		returnVal[1] = (pos[1] - ts.zero_zero[1])/yScale;
		
		return returnVal;
	}

	@Override
	public void addLine(String line) {
		//Parse line
		String[] tokens = line.split("\\|");
		for(String s: tokens)
		{
			if(s.contains("Time"))
			{
				s = s.substring(s.indexOf(":") + 1);
				time = Float.parseFloat(s);
			}else if(s.contains("X"))
			{
				pos[0] = Float.parseFloat(s.substring(s.indexOf(":") + 1));
			}
			else if(s.contains("Y"))
			{
				pos[1] = Float.parseFloat(s.substring(s.indexOf(":") + 1));
			}
			else
			{
				//no idea what to do here this is if i get something erroneous
				Log.d("DataPoint parser", "unknown field: " + line);
			}
		}
	}
	
	public String toString()
	{
		return "x: " + pos[0] + " y: " + pos[1];
	}
	
	@Override
	public void takeAction() {
		resultsTab.addDataPoint(this);
	}
	
	public float getTime()
	{
		return time;
	}
}

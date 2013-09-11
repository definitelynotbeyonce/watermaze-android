package network.packets.incoming;

import com.example.watermaze.WaterMaze_Results;

import android.util.Log;



public class DataPoint extends ResultsIncomingPacket{
	public DataPoint(WaterMaze_Results results) {
		super(results);
	}

	protected float[] pos;
	protected float time;
	//TODO: eye data
	
	public float[] toGraphCoords(TrialSetup ts, float sizeX, float sizeY)
	{
		float[] returnVal = new float[2]; //x and y
		
		//get x
		float xScale = (ts.width * ts.gridSize)/sizeX;
		returnVal[0] = (pos[0] - ts.zero_zero[0])/xScale;
		
		//get y
		float yScale = (ts.length * ts.gridSize)/sizeY;
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
			}else if(s.contains("Pos"))
			{
				s = s.substring(s.indexOf(":") + 1);
				String[] a = s.split("_");
				pos = new float[2];
				pos[0] = Float.parseFloat(a[0]);
				pos[1] = Float.parseFloat(a[1]);
				
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
		// TODO Auto-generated method stub
		
	}
}

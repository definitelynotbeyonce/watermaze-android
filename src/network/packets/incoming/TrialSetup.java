package network.packets.incoming;

import com.example.watermaze.WaterMaze_Results;


public class TrialSetup extends ResultsIncomingPacket{
	//all fields are public so this essentially becomes a c struct.  
	public int trialNo;
	public float timeLimit;
	public float gridSize;
	public int length;
	public int width;
	public float[] zero_zero;
	public int startingPos;
	public int finishPos;
	
	//TODO: cues? ArrayList<Cue> cues;
	
	public TrialSetup(WaterMaze_Results results)
	{
		super(results);
		type = "Trial Setup";
		// TODO: for cues: cues = new ArrayList<Cue>();
	}
	@Override
	public void addLine(String line) {
		//Parse line
		String[] tokens = line.split("\\|");
		for(String s: tokens)
		{
			if(s.contains("TrialNo"))
			{
				s = s.substring(s.indexOf(":") + 1);
				trialNo = Integer.parseInt(s);
			}else if(s.contains("TimeLimit"))
			{
				s = s.substring(s.indexOf(":") + 1);
				timeLimit = Float.parseFloat(s);
			}else if(s.contains("GridSize"))
			{
				s = s.substring(s.indexOf(":") + 1);
				gridSize = Float.parseFloat(s);
			}else if(s.contains("Length"))
			{
				s = s.substring(s.indexOf(":") + 1);
				length = Integer.parseInt(s);
			}else if(s.contains("Width"))
			{
				s = s.substring(s.indexOf(":") + 1);
				width = Integer.parseInt(s);
			}else if(s.contains("ZeroZero"))
			{
				s = s.substring(s.indexOf(":") + 1);
				String[] a = s.split("_");
				zero_zero = new float[2];
				zero_zero[0] = Float.parseFloat(a[0]);
				zero_zero[1] = Float.parseFloat(a[1]);
				
			}else if(s.contains("StartingPos"))
			{
				s = s.substring(s.indexOf(":") + 1);
				startingPos = Integer.parseInt(s);
			}else if(s.contains("FinishPos"))
			{
				s = s.substring(s.indexOf(":") + 1);
				finishPos = Integer.parseInt(s);
			}
			else
			{
				//no idea what to do here this is if i get something erroneous
			}
		}
	}
	@Override
	public void takeAction() {
		// TODO Auto-generated method stub
		
	}
}

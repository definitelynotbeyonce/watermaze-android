package com.example.watermaze;

import network.controller.Controller;
import network.packets.incoming.DataPoint;
import network.packets.incoming.TrialSetup;

import com.example.watermaze.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("ValidFragment")
public class WaterMaze_Results extends Fragment {
	protected Controller sender;
	protected float[] lastPoint;
	protected TrialSetup trial;
	protected View v;
	protected Canvas c;
	
	
	public WaterMaze_Results(){
		//TODO: this isn't really used. Refactor out?
	}
	
	public WaterMaze_Results(Controller sender) {
		// TODO Auto-generated constructor stub
		this.sender = sender;
		
		
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		v = inflater.inflate(R.layout.watermaze_results_layout, container, false);
//		if(alr == null)
//		{
//			alr = new ActiveListenResults(this);
//			Thread t = new Thread(alr);
//			t.start();
//		}
        return v;
    }
	
	public boolean isActive()
	{
		return true;
	}
	
	public void addDataPoint(DataPoint d)
	{
		//TODO: draw a line from last point to 
		//alpha
		Log.d("DataPoint", d.toString());
	}
	
	protected class resultsView extends View
	{
		Canvas c;
		public resultsView(Context context, Canvas c) {
			super(context);
			this.c = c;
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			canvas = c;
		}
	}
	
	public void addTrial(TrialSetup ts)
	{
		//TODO: implement
		trial = ts;
		//set last point to starting position.
		//new color.
		//change 
	}
}

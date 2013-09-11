package com.example.watermaze;

import java.util.ArrayList;


import com.example.watermaze.R;

import network.controller.Controller;
import network.packets.outgoing.Command;
import network.packets.outgoing.StateRequest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

@SuppressLint("ValidFragment")
public class WaterMaze_Control extends Fragment 
{
	private Controller sender;
	private ArrayList<Button> buttonCollection;
	private boolean running;
	
	public WaterMaze_Control() 
	{
		Log.d("Constructor", "default constructor");
	}
	
	public WaterMaze_Control(Controller sender) 
	{
		// TODO Auto-generated constructor stub
		this.sender = sender;
		Log.d("Constructor", "non default constructor");
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
	{
		//get the view from inflater
		View v = inflater.inflate(R.layout.watermaze_control_layout, container, false);
		//add the buttons to the collection
		initCollection(v);
		initListeners(v);
		defaultState();
		
		//TODO: request state from CalVR. next parse that state.
		requestState();
        return v;
    }
	
	private void initCollection(View v)
	{
		if(buttonCollection == null)
		{
			buttonCollection = new ArrayList<Button>();
			GridLayout g = (GridLayout)v.findViewById(R.id.watermaze_control_layout);
			for(int i = 0; i < g.getChildCount(); ++i)
			{
				buttonCollection.add((Button)g.getChildAt(i));
			}			
		}
		else
		{
			buttonCollection = null;
			initCollection(v);
		}
	}
	
	private void initListeners(View v)
	{
		for(Button b: buttonCollection)
		{
			BListener bl = new BListener(b.getId());
			b.setOnClickListener(bl);
		}
	}
	private class BListener implements OnClickListener
	{
		private int buttonID;
		
		
		public BListener(int num){
			buttonID = num;
		}
		
		@Override
		public void onClick(View v) 
		{
			switch(buttonID)
			{
			case R.id.loadGeometry:
				loadGeometryClick(v);
				break;
			case R.id.nextParadigm:
				nextParadigmClick(v);
				break;
			case R.id.addTrial:
				addTrialClick(v);
				break;
			case R.id.nextTrial:
				nextTrialClick(v);
				break;
			case R.id.playPause:
				playPauseClick(v);
				break;
			case R.id.previousParadigm:
				previousParadigmClick(v);
				break;
			case R.id.startStop:
				startStopClick(v);
				break;
			default:
				break;
			}
			
		}
		
	}
	
	//listeners
	public void loadGeometryClick(View v) 
	{
		Command c = new Command("Load Geometry");
		sender.send(c);
	}
	
	public void startStopClick(View v) 
	{
		// TODO finish
		
		//which state are we in currently
		if(running)
		{
			running = false;
			defaultState();
			Button b = (Button)v.findViewById(R.id.startStop);
			b.setText("Start");
			Command c = new Command("Stop");
			sender.send(c);
		}
		else
		{
			running = true;
			runningTrial();
			Button b = (Button)v.findViewById(R.id.startStop);
			b.setText("Stop");
			Command c = new Command("Start");
			sender.send(c);
		}
	}

	public void previousParadigmClick(View v) 
	{
		// TODO Auto-generated method stub
		Command c = new Command("Previous Paradigm");
		sender.send(c);
		
	}

	public void playPauseClick(View v) 
	{
		// TODO Auto-generated method stub
		if(running)
		{
			running = false;
			defaultState();
			Button b = (Button)v.findViewById(R.id.playPause);
			b.setText("Play");
			Command c = new Command("Pause");
			sender.send(c);
		}
		else
		{
			running = true;
			runningTrial();
			Button b = (Button)v.findViewById(R.id.playPause);
			b.setText("Pause");
			Command c = new Command("Play");
			sender.send(c);
		}
		
	}

	public void nextTrialClick(View v) 
	{
		// TODO Auto-generated method stub
		Command c = new Command("Next Trial");
		sender.send(c);
		
	}

	public void addTrialClick(View v) 
	{
		// TODO Auto-generated method stub
		Command c = new Command("Add Trial");
		sender.send(c);
	}

	public void nextParadigmClick(View v) 
	{
		// TODO Auto-generated method stub
		Command c = new Command("Next Paradigm");
		sender.send(c);
	}

	//TODO: handle state switching
	public void defaultState()
	{
		for(Button b:buttonCollection)
		{
			switch(b.getId())
			{
			case R.id.loadGeometry:
				b.setEnabled(true);
				break;
			case R.id.nextParadigm:
				b.setEnabled(true);
				break;
			case R.id.addTrial:
				b.setEnabled(true);
				break;
			case R.id.nextTrial:
				b.setEnabled(true);
				break;
			case R.id.playPause:
				b.setEnabled(true);
				break;
			case R.id.previousParadigm:
				b.setEnabled(true);
				break;
			case R.id.startStop:
				b.setEnabled(true);
				break;
			default:
				break;
			}
		}
	}
	public void disabledState(Button b)
	{
		b.setEnabled(false);
	}
	public void runningTrial()
	{
		for(Button b:buttonCollection)
		{
			switch(b.getId())
			{
			case R.id.loadGeometry:
				b.setEnabled(false);
				break;
			case R.id.nextParadigm:
				b.setEnabled(false);
				break;
			case R.id.addTrial:
				b.setEnabled(false);
				break;
			case R.id.nextTrial:
				b.setEnabled(false);
				break;
			case R.id.playPause:
				b.setEnabled(true);
				break;
			case R.id.previousParadigm:
				b.setEnabled(false);
				break;
			case R.id.startStop:
				b.setEnabled(true);
				break;
			default:
				break;
			}
		}
	}
	
	public void requestState()
	{
		StateRequest sr = new StateRequest();
		sender.send(sr);
		
		sender.read(this);
	}
	
	public boolean isActive()
	{
		//TODO: this is based on the result of sender
		return true;
	}
}
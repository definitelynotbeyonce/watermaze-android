package com.example.watermaze;

import java.util.ArrayList;


import com.example.watermaze.R;
import com.example.watermaze.util.NewSubjectDialog;
import com.example.watermaze.util.StateManager;

import network.controller.Controller;
import network.packets.incoming.CueList;
import network.packets.outgoing.Command;
import network.packets.outgoing.CueListRequest;
import network.packets.outgoing.CueToggle;
import network.packets.outgoing.NewSubject;
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
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

@SuppressLint("ValidFragment")
public class WaterMaze_Control extends Fragment 
{
	protected Controller controller;
	protected ArrayList<Button> buttonCollection;
	protected boolean running;
	protected StateManager stateManager;
	private View v;
	
	public WaterMaze_Control() 
	{
		Log.d("Constructor", "default constructor");
	}
	
	public WaterMaze_Control(Controller sender) 
	{
		this.controller = sender;
		Log.d("Constructor", "non default constructor");
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
	{
		//get the view from inflater
		v = inflater.inflate(R.layout.watermaze_control_layout, container, false);
		//add the buttons to the collection
		initCollection(v);
		initListeners(v);
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
			stateManager = new StateManager(buttonCollection);
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
	
	
	//listeners
	public void loadGeometryClick(View v) 
	{
		Command c = new Command("Load Geometry");
		controller.send(c);
	}
	
	public void startStopClick(View v) 
	{
		//which state are we in currently
		if(running)
		{
			Command c = new Command("Stop");
			controller.send(c);
		}
		else
		{
			Command c = new Command("Start");
			controller.send(c);
		}
	}

	public void previousParadigmClick(View v) 
	{
		Command c = new Command("Previous Paradigm");
		controller.send(c);
	}

	public void endExperimentClick(View v) 
	{
		Command c = new Command("End Experiment");
		controller.send(c);
	}

	public void nextTrialClick(View v) 
	{
		Command c = new Command("Next Trial");
		controller.send(c);
		
	}

	public void addTrialClick(View v) 
	{
		Command c = new Command("Add Trial");
		controller.send(c);
	}

	public void nextParadigmClick(View v) 
	{
		Command c = new Command("Next Paradigm");
		controller.send(c);
	}
	
	public void requestState()
	{
		if(controller.isConnected()){
			//request from CalVR that it send you the state of WaterMaze
			StateRequest sr = new StateRequest();
			controller.send(sr);
			
			//the controller will handle the response
		}
	}
	
	public void requestCues()
	{
		if(controller.isConnected()){
			//request from CalVR that it send you the state of WaterMaze
			CueListRequest clr = new CueListRequest();
			controller.send(clr);
			
			//the controller will handle the response
		}
	}
	
	protected void newSubjectClick(View v) {
		//make dialog window appear.
		NewSubjectDialog nsd = new NewSubjectDialog(v.getContext(), this);
		nsd.show();
	}
	
	//Too long to have above.  gigantic switch statement.
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
			case R.id.endExperiment:
				endExperimentClick(v);
				break;
			case R.id.previousParadigm:
				previousParadigmClick(v);
				break;
			case R.id.startStop:
				startStopClick(v);
				break;
			case R.id.newSubject:
				newSubjectClick(v);
				break;
			default:
				break;
			}
			
		}

		
		
	}
	
	public void setState(String state)
	{
		if(stateManager.changeState(state))
			requestCues();
		
	}

	public Controller getController() {
		return controller;
	}
	
	public void addCueList(CueList list)
	{
		LinearLayout main = (LinearLayout)v.findViewById(R.id.watermaze_control_cue_list);
		if (null != main && main.getChildCount() > 0) {                 
		    try {
		        main.removeViews (0, main.getChildCount());
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		for(int i = 0; i < list.list.size(); ++i)
		{
			ToggleButton button = new ToggleButton(v.getContext());
			button.setTextOff(list.list.get(i).buttonText);
			button.setTextOn(list.list.get(i).buttonText);
			button.setChecked(list.list.get(i).toggle);
			
			button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			    	ToggleButton b = (ToggleButton)buttonView;
			    	CueToggle c = new CueToggle(b.getTextOn().toString(), isChecked);
			    	controller.send(c);
			    }
			});
			
			//add it to the linear layout
			main.addView(button);
		}
		
		if(!stateManager.isCueListEnabled())
			disableCueList();
	}
	
	public void disableCueList()
	{
		LinearLayout main = (LinearLayout)v.findViewById(R.id.watermaze_control_cue_list);
		for(int i = 0; i < main.getChildCount(); ++i)
		{
			Button b = (Button)main.getChildAt(i);
			b.setEnabled(false);
		}
	}

	public void newSubject(String name) {
		if(controller.isConnected()){
			//request from CalVR that it send you the state of WaterMaze
			NewSubject ns = new NewSubject(name);
			controller.send(ns);
			
			//the controller will handle the response
		}
		Log.i("NewSubject", name);
	}
}
package com.example.watermaze;

import network.controller.Controller;

import com.example.watermaze.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

@SuppressLint("ValidFragment")
public class WaterMaze_Connectivity extends Fragment {
	private Controller controller;
	protected RadioGroup networks;
	protected int previous;
	
	public WaterMaze_Connectivity(Controller sender) {
		this.controller = sender;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.watermaze_connectivity_layout, container, false);
		//init listeners
		initListener(v);
        return v;
    }
	
	protected void initListener(View v)
	{
		CListener cl = new CListener();
		networks = (RadioGroup)v.findViewById(R.id.radio_grp_connectivity);
		networks.setOnCheckedChangeListener(cl);
		BListener_Connect bl_c = new BListener_Connect();
		v.findViewById(R.id.connect_connect_button).setOnClickListener(bl_c);
		BListener_Disconnect bld = new BListener_Disconnect();
		v.findViewById(R.id.connect_disconnect).setOnClickListener(bld);
	}
	
	//Wrapper class
	private class CListener implements OnCheckedChangeListener
	{
		public CListener(){
		}
		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			onChange(arg0, arg1);
		}
	}
	
	
	private class BListener_Connect implements OnClickListener
	{		
		@Override
		public void onClick(View v) 
		{
			int selected = ((RadioGroup)v.getRootView().findViewById(R.id.radio_grp_connectivity)).getCheckedRadioButtonId();
			switch(selected)
			{
			case R.id.radio_nexcave:
				controller.setDestination("nexcave.ucsd.edu", true);
				break;
			case R.id.radio_starcave:
				controller.setDestination("starcave.ucsd.edu", true);
				break;
			case R.id.radio_dev:
				controller.setDestination("137.110.118.118", true);
				break;
			}
		}
		
	}
	
	private class BListener_Disconnect implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			controller.disconnect();
			//change the radio button?			
		}
	}
	
	
	protected void onChange(RadioGroup arg0, int arg1)
	{
		//get selected index.
		switch(arg1)
		{
		case R.id.radio_nexcave:
			controller.setDestination("nexcave.ucsd.edu", false);
			break;
		case R.id.radio_starcave:
			controller.setDestination("starcave.ucsd.edu", false);
			break;
		case R.id.radio_dev:
			controller.setDestination("137.110.118.118", false);
			break;
			
		}
	}
	
	
	
}
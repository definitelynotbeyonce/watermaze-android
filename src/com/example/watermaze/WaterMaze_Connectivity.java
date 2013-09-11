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
	private Controller sender;
	protected RadioGroup networks;
	
	public WaterMaze_Connectivity() {
		
	}
	
	public WaterMaze_Connectivity(Controller sender) {
		this.sender = sender;
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
		BListener bl = new BListener(R.id.radio_grp_connectivity);
		v.findViewById(R.id.connect_connect_button).setOnClickListener(bl);
	}
	
	//Wrapper class
	private class CListener implements OnCheckedChangeListener
	{
		public CListener(){
		}
		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			onChange(arg0, arg1);
		}
	}
	
	
	private class BListener implements OnClickListener
	{
		private int radioID;
		
		public BListener(int num){
			radioID = num;
		}
		
		@Override
		public void onClick(View v) 
		{
			int selected = ((RadioGroup)v.findViewById(R.id.radio_grp_connectivity)).getCheckedRadioButtonId();
			switch(selected)
			{
			case R.id.radio_nexcave:
				sender.setDestination("nexcave.ucsd.edu");
				break;
			case R.id.radio_starcave:
				sender.setDestination("starcave.ucsd.edu");
				break;
			case R.id.radio_dev:
				sender.setDestination("137.110.118.118");
				break;
			}
		}
		
	}
	
	
	protected void onChange(RadioGroup arg0, int arg1)
	{
		//get selected index.
		switch(arg1)
		{
		case R.id.radio_nexcave:
			sender.setDestination("nexcave.ucsd.edu");
			break;
		case R.id.radio_starcave:
			sender.setDestination("starcave.ucsd.edu");
			break;
		case R.id.radio_dev:
			sender.setDestination("137.110.118.118");
			break;
			
		}
	}
	
	
}
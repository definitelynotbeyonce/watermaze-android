package com.example.watermaze;

import java.util.ArrayList;

import network.controller.Controller;
import network.packets.incoming.DataPoint;
import network.packets.incoming.TrialSetup;
import network.packets.outgoing.TrialSetupRequest;

import com.example.watermaze.R;
import com.example.watermaze.util.ResultsGraph;
import com.example.watermaze.util.ResultsLine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class WaterMaze_Results extends Fragment {
	protected Controller controller;
	protected View v;
	protected ResultsGraph currGraph;
	protected Thumbnail currThumb;
	protected ResultsGraph bigGraph;
	protected TrialSetup setup;
	protected float sizeX;
	protected float sizeY;
	
	private ArrayList<ResultsGraph> graphList;
	
	public WaterMaze_Results(Controller sender) {
		this.controller = sender;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		v = inflater.inflate(R.layout.watermaze_results_layout, container, false);
		
		//get the view for the graph.
		LinearLayout graphHolder = (LinearLayout)v.findViewById(R.id.results_view);
		if(currGraph == null)
		{
			graphList = new ArrayList<ResultsGraph>();
			createGraph();
		}
		else
		{
			graphHolder.addView(currGraph);
		}
		
        return v;
    }
	
	public void addDataPoint(DataPoint d)
	{
		//alpha
		synchronized(this){
			//Log.i("DataPoint", d.toString());
			if(!currGraph.isSetup()){
				requestTrialSetup();
			}
			else{
				currGraph.addPoint(d.toGraphCoords(setup), d.getTime());
				//change timeElapsed.
				if(currGraph == bigGraph)
				{
					TextView tv = (TextView)v.findViewById(R.id.results_trial_legend).findViewById(setup.trialNo);
					tv.setText(Float.toString(d.getTime()));
				}
				
				//update views.
				currGraph.invalidate();
				currThumb.invalidate();
			}
		}
	}
	
	public void addTrial(TrialSetup ts)
	{
		synchronized(this){
			if(ts == null)
			{
				Log.d("error1", "null");
			}
			else{
				Log.i("addTrial", Integer.toString(ts.trialNo));
				//create line
				setup = ts;
				currGraph.newLine(setup);
				if(bigGraph == currGraph)
				{
					TextView h1 = (TextView)v.findViewById(R.id.resluts_trialNo_prompt);
					TextView h2 = (TextView)v.findViewById(R.id.results_line_color_prompt);
					TextView h3 = (TextView)v.findViewById(R.id.results_time_elapsed_prompt);
					//create table row in legend
					TableLayout table = (TableLayout)v.findViewById(R.id.results_trial_legend);
					TableRow newRow = new TableRow(v.getContext());
					TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					//trial number
					TextView c1 = new TextView(v.getContext());
					c1.setHeight(h1.getHeight());
					c1.setWidth(h1.getWidth());
					c1.setText(Integer.toString(ts.trialNo));
					
					//line color
					/*LegendLine c2 = new LegendLine(v.getContext());
					c2.setColor(graph.getLatestColor());*/
					TextView c2 = new TextView(v.getContext());
					c2.setText(" ");
					c2.setHeight(h2.getHeight());
					c2.setWidth(h2.getWidth());
					int color = currGraph.getLatestColor();
					c2.setBackgroundColor(color);
					
					//time elapsed (actual data provided by add data point).
					TextView c3 = new TextView(v.getContext());
					c3.setHeight(h3.getHeight());
					c3.setWidth(h3.getWidth());
					c3.setText("");
					c3.setId(setup.trialNo);
					
					newRow.addView(c1);
					newRow.addView(c2);
					newRow.addView(c3);
					table.addView(newRow, layoutParams);
					newRow.forceLayout();
					
					Log.i("table size", Integer.toString(table.getChildCount()));
				}
			}
		}
	}
	
	protected class LegendLine extends View{
		Paint color;
		
		public LegendLine(Context context) {
			super(context);
		}
		
		@Override
		protected void onDraw(Canvas canvas){
			//draw line
			Log.i("LegendLine", "onDraw");
			canvas.drawColor(color.getColor());
		}
		
		public void setColor(Paint p){
			color = p;
		}
		
	}
	
	public void requestTrialSetup()
	{
		if(!controller.isConnected()){
			//request from CalVR that it send you the state of WaterMaze
			TrialSetupRequest tsr = new TrialSetupRequest();
			controller.send(tsr);
		}
	}
	
	public void createGraph()
	{
		currGraph = new ResultsGraph(v.getContext());
		currGraph.setFullSize(true);
		if(graphList.size() > 0)
		{
			if(graphList.get(graphList.size() - 1) == bigGraph)
				bigGraph = currGraph;	//the big graph is the new graph if we are not looking at an old graph
		}
		else
		{
			bigGraph = currGraph;
		}
		LinearLayout graphHolder = (LinearLayout)v.findViewById(R.id.results_view);
		graphHolder.removeAllViews();
		graphHolder.addView(currGraph);
		graphList.add(currGraph);
		
		//add a thumbnail for this guy.
		LinearLayout thumbs = (LinearLayout)v.findViewById(R.id.results_thumbnails);
		Thumbnail newThumb = new Thumbnail(v.getContext(), currGraph);
		newThumb.setPadding(2, 2, 2, 2);
		currThumb = newThumb;
		thumbs.addView(newThumb);
		//newThumb.setImageBitmap(currGraph.drawBitmap(bigGraphView));
		newThumb.setOnClickListener(new TN_Click());
		Log.i("WMR", "thumnails size " + Integer.toString(thumbs.getChildCount()));
	} 
	
	
	private class TN_Click implements OnClickListener
	{
		@Override
		public void onClick(View arg0) {
			//get our index
			ViewGroup parent = (ViewGroup)arg0.getParent();
			int index = parent.indexOfChild(arg0);
			int prevIndex = graphList.indexOf(currGraph);
			if(index == graphList.size() - 1)
			{
				bigGraph = currGraph;
				LinearLayout graphHolder = (LinearLayout)v.findViewById(R.id.results_view);
				graphHolder.removeAllViews();
				graphHolder.addView(bigGraph);
			}
			else
			{
				bigGraph = graphList.get(index);
				LinearLayout graphHolder = (LinearLayout)v.findViewById(R.id.results_view);
				graphHolder.removeAllViews();
				graphHolder.addView(bigGraph);
			}
			
			//modify the legend
			if(index != prevIndex)	//the legend needs to be changed, otherwise we good...
			{
				loadLegend(index);
			}
		}
		
	}
	protected void loadLegend(int index)
	{
		TableLayout tl = (TableLayout)v.findViewById(R.id.results_trial_legend);
		while(tl.getChildCount() > 1)
		{
			tl.removeView(tl.getChildAt(1));
		}
		
		for(ResultsLine l: graphList.get(index).getLines())
		{
			TextView h1 = (TextView)v.findViewById(R.id.resluts_trialNo_prompt);
			TextView h2 = (TextView)v.findViewById(R.id.results_line_color_prompt);
			TextView h3 = (TextView)v.findViewById(R.id.results_time_elapsed_prompt);
			//create table row in legend
			TableRow newRow = new TableRow(v.getContext());
			TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			//trial number
			TextView c1 = new TextView(v.getContext());
			c1.setHeight(h1.getHeight());
			c1.setWidth(h1.getWidth());
			c1.setText(Integer.toString(l.getTrialNo()));
			
			//line color
			/*LegendLine c2 = new LegendLine(v.getContext());
			c2.setColor(graph.getLatestColor());*/
			TextView c2 = new TextView(v.getContext());
			c2.setText(" ");
			c2.setHeight(h2.getHeight());
			c2.setWidth(h2.getWidth());
			int color = l.getColor();
			c2.setBackgroundColor(color);
			
			//time elapsed (actual data provided by add data point).
			TextView c3 = new TextView(v.getContext());
			c3.setHeight(h3.getHeight());
			c3.setWidth(h3.getWidth());
			c3.setText(Float.toString(l.getTime()));
			c3.setId(l.getTrialNo());
			
			newRow.addView(c1);
			newRow.addView(c2);
			newRow.addView(c3);
			tl.addView(newRow, layoutParams);
			newRow.forceLayout();
		}
	}
	
	private class Thumbnail extends View
	{
		ResultsGraph graph;
		public Thumbnail(Context context) { super(context); }
		
		public Thumbnail(Context context, ResultsGraph g) {
			super(context);
			graph = g;
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			graph.setFullSize(false);
			graph.draw(canvas);
			graph.setFullSize(true);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			float trialAR = graph.getAspectRatio();
			
			int scaledW = (int)(trialAR * heightMeasureSpec);
			scaledW = Math.min(scaledW, widthMeasureSpec);
			int scaledH = (int)((float)scaledW / trialAR);
			
			setMeasuredDimension(scaledW, scaledH);
		}
		
		@Override
		protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
		{
			float trialAR = graph.getAspectRatio();
			
			int scaledW = (int)(trialAR * h);
			scaledW = Math.min(scaledW, w);
			int scaledH = (int)((float)scaledW / trialAR);
			
			super.onSizeChanged(scaledW, scaledH, oldw, oldh);
		}
	}
	
	public void newExperiment()
	{
		//time to delete some data!
		graphList.clear();
		LinearLayout graphHolder = (LinearLayout)v.findViewById(R.id.results_view);
		graphHolder.removeAllViews();
		LinearLayout thumbnails = (LinearLayout)v.findViewById(R.id.results_thumbnails);
		thumbnails.removeAllViews();
		TableLayout tl = (TableLayout)v.findViewById(R.id.results_trial_legend);
		while(tl.getChildCount() > 1)
		{
			tl.removeView(tl.getChildAt(1));
		}
	}
}

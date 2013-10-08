package com.example.watermaze;

import java.util.ArrayList;

import network.controller.Controller;
import network.packets.incoming.DataPoint;
import network.packets.incoming.TrialSetup;
import network.packets.outgoing.TrialSetupRequest;

import com.example.watermaze.R;
import com.example.watermaze.util.ResultsGraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
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
	protected ResultsGraph bigGraph;
	protected TrialSetup setup;
	protected float sizeX;
	protected float sizeY;
	protected ImageView bigGraphView;
	protected ImageView latestThumb;
	
	private ArrayList<ResultsGraph> prevGraphs;
	
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
		bigGraphView = new ImageView(v.getContext());
		graphHolder.addView(bigGraphView);
		if(currGraph == null)
		{
			prevGraphs = new ArrayList<ResultsGraph>();
			createGraph();
			bigGraph = currGraph;
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
				TextView tv = (TextView)v.findViewById(R.id.results_trial_legend).findViewById(setup.trialNo);
				tv.setText(Float.toString(d.getTime()));
				
				//update views.
				Bitmap newMap = currGraph.drawBitmap(bigGraphView);
				if(bigGraph == currGraph)
					bigGraphView.setImageBitmap(newMap);
				latestThumb.setImageBitmap(newMap);
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
				
				//create table row in legend
				TableLayout table = (TableLayout)v.findViewById(R.id.results_trial_legend);
				TableRow newRow = new TableRow(v.getContext());
				TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//trial number
				TextView c1 = new TextView(v.getContext());
				c1.setText(Integer.toString(ts.trialNo));
				
				//line color
				/*LegendLine c2 = new LegendLine(v.getContext());
				c2.setColor(graph.getLatestColor());*/
				TextView c2 = new TextView(v.getContext());
				c2.setBackgroundColor(currGraph.getLatestColor().getColor());
				
				//time elapsed (actual data provided by add data point).
				TextView c3 = new TextView(v.getContext());
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
		currGraph = new ResultsGraph();
		if(prevGraphs.size() > 0)
		{
			if(prevGraphs.get(prevGraphs.size() - 1) == bigGraph)
				bigGraph = currGraph;	//the big graph is the new graph if we are not looking at an old graph
		}
		prevGraphs.add(currGraph);
		
		//add a thumbnail for this guy.
		LinearLayout thumbs = (LinearLayout)v.findViewById(R.id.results_thumbnails);
		ImageView newThumb = new ImageView(v.getContext());
		latestThumb = newThumb;
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
			bigGraph = prevGraphs.get(index);
			bigGraphView.setImageBitmap(bigGraph.drawBitmap(bigGraphView));
		}
		
	}
	
	private class Thumbnail extends View
	{
		private ResultsGraph graph;
		private Matrix matrix;
		public Thumbnail(Context context) {super(context);}
		public Thumbnail(Context context, ResultsGraph graph) {
			super(context);
			matrix = new Matrix();
			this.graph = graph;
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			//copy bitmap
			Bitmap b = graph.drawBitmap(bigGraphView);
			float scaleWidth = (float)canvas.getWidth() / b.getWidth();
			float scaleHeight = (float)canvas.getHeight() / b.getHeight();
			matrix.reset();
			matrix.postScale(scaleWidth,  scaleHeight);
			
			canvas.drawBitmap(b, matrix, null);
		}
		public ResultsGraph getGraph()
		{
			return graph;
		}
		
	}
	private class BigGraph extends View
	{
		private ResultsGraph graph;
		private Matrix matrix;
		public BigGraph(Context context) {super(context);}
		public BigGraph(Context context, ResultsGraph graph) {
			super(context);
			matrix = new Matrix();
			this.graph = graph;
		}
		
		@Override
		protected void onDraw(Canvas canvas)
		{
			//copy bitmat
			Bitmap b = graph.drawBitmap(bigGraphView);
			float scaleWidth = (float)canvas.getWidth() / b.getWidth();
			float scaleHeight = (float)canvas.getHeight() / b.getHeight();
			matrix.reset();
			matrix.postScale(scaleWidth,  scaleHeight);
			
			canvas.drawBitmap(b, matrix, null);
		}
		public void setGraph(ResultsGraph g)
		{
			graph = g;
		}
		
	}
}

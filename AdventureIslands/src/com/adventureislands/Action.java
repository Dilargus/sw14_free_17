package com.adventureislands;

import java.util.ArrayList;

import android.util.Log;

import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.sprite.AnimatedSprite.Frame;

public class Action implements
java.io.Serializable{
	private int duration;
	private int identifier;
	private int loops=0;
	private int disruptionTime;
	private ArrayList<myFrame> frames;
	public Action(int identifier) {
		this.identifier = identifier;
		frames = new ArrayList<myFrame>();
		switch(identifier){
		case SessionData.DO_NOTHING:
			disruptionTime=1;
			duration=200;
			frames.add(new myFrame(1, 0));
			break;
		case SessionData.GO_DOWN:
			disruptionTime=100;
			duration=150;
			frames.add(new myFrame(0, 0));
			frames.add(new myFrame(1, 0));
			frames.add(new myFrame(2, 0));
			break;
		case SessionData.GO_UP:
			disruptionTime=100;
			duration=150;
			frames.add(new myFrame(0, 3));
			frames.add(new myFrame(1, 3));
			frames.add(new myFrame(2, 3));
			break;
		case SessionData.GO_LEFT:
			disruptionTime=100;
			duration=150;
			frames.add(new myFrame(0, 1));
			frames.add(new myFrame(1, 1));
			frames.add(new myFrame(2, 1));
			break;
		case SessionData.GO_RIGHT:
			disruptionTime=100;
			duration=150;
			frames.add(new myFrame(0, 2));
			frames.add(new myFrame(1, 2));
			frames.add(new myFrame(2, 2));
			break;
		case SessionData.DIG:
			disruptionTime=800;
			duration=300;
			frames.add(new myFrame(0, 4));
			frames.add(new myFrame(1, 4));
			frames.add(new myFrame(2, 4));
			frames.add(new myFrame(0, 5));
			frames.add(new myFrame(1, 5));
			frames.add(new myFrame(2, 5));
			frames.add(new myFrame(0, 6));
			frames.add(new myFrame(1, 6));
			frames.add(new myFrame(2, 6));
		}
	}
	
	
	
	public int getDisruptionTime() {
		return disruptionTime;
	}



	public void setDisruptionTime(int disruptionTime) {
		this.disruptionTime = disruptionTime;
	}



	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getIdentifier() {
		return identifier;
	}
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	public ArrayList<AnimatedSprite.Frame> getFrames() {
		 ArrayList<AnimatedSprite.Frame> ASframes = new ArrayList<AnimatedSprite.Frame>();
		for(int i=0;i<frames.size();i++){
			AnimatedSprite.Frame ASframe = new Frame(frames.get(i).getX(),frames.get(i).getY(),frames.get(i).getDuration());
			ASframes.add(ASframe);
		}
		return ASframes;
	}
	
	public int getLoops() {
		return loops;
	}
	public void setLoops(int loops) {
		this.loops = loops;
	}
	
	
	
}

package com.adventureislands;

import java.util.ArrayList;
import android.graphics.Rect;
import android.util.Log;

import com.datastructure.Surface;
import com.datastructure.objects.Object;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXTile;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;

public class Cannonball extends MySprite {

	public int difx;
	public int dify;
	public int rise = 2;
	public int max_time;
	public int direction;
	boolean splash=false;
	 
	public Cannonball(Map map, TiledTexture texture, int x, int y, int distance, int deviation, int direction) {
		super(map, texture, x, y);
		max_time = 32;
		Log.i("cb before","cannonball x: "+this.getRealX() +" cannonball y: " + this.getRealY());
		int random = SessionData.instance().randomInt.nextInt(deviation*2+1)-deviation;	
		this.direction = direction + random;
		random = SessionData.instance().randomInt.nextInt(deviation*2+1)-deviation;
		this.difx = distance + random;
		int times = 0;
		for(int i =0; i < max_time/2; i++){
			if(i % rise == 1){
				times ++;
			}
		}
		dify = -times;
		actions = new ArrayList<Action>();
		actions.add(new Action(SessionData.FLY));
		actions.add(new Action(SessionData.CLASHINTOWATER));
		actions.add(new Action(SessionData.DO_NOTHING));
		
		actual_action = getAction(SessionData.DO_NOTHING);
		next_action =  getAction(SessionData.DO_NOTHING);
	}
	
	public void fly(){	
		if(working){
			if(action_timer<max_time){ 
				if(action_timer % rise == 1){ 
					dify = dify+1;
				}
				this.moveRelative(difx, dify+direction);
			}
			else{
				
				next_action = getAction(SessionData.CLASHINTOWATER);
				doSomething();
				
				working = false;
				splash = true;
				action_timer=0; 
			}
		} 
		if(splash){
			if(action_timer>actual_action.getDisruptionTime()){
				stop();
				splash=false;
				action_timer=0;
				visible=false;
				Log.i("cb before","cannonball x: "+this.getRealX() +" cannonball y: " + this.getRealY());
			}
		}
	}


	@Override
	public Rect getCollisionRect() {
		// king's collision rectangle is just around his body.
		Rect rect = this.getRect();
		rect.inset(7, 0);
		rect.top = rect.top + 20;
		return rect;
	}
	@Override
	public Rect getActionRect() {
		// king's collision rectangle is just around his body.
		Rect rect = this.getRect();
		//rect.inset(7, 0);
		//rect.top = rect.top + 20;
		return rect;
	}
	
	public void doSomething(){
		actual_action = next_action;
		if(actual_action.getLoops()==0){
			this.animate(actual_action.getDuration(), actual_action.getFrames());
		}
		else{
			this.animate(actual_action.getDuration(),actual_action.getLoops(), actual_action.getFrames());
		}
	}
	
}

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

public class Arrow extends MySprite {

	public int difx;
	public int dify;
	public int rise = 2;
	public int max_time;
	public int direction;
	boolean splash=false;
	 
	public Arrow(Map map, TiledTexture texture, int x, int y) {
		super(map, texture, x, y);
		actions = new ArrayList<Action>();
		actions.add(new Action(SessionData.DO_NOTHING));
		actual_action = getAction(SessionData.DO_NOTHING);
		next_action =  getAction(SessionData.DO_NOTHING);
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

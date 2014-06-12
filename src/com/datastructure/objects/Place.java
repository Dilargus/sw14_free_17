package com.datastructure.objects;

import java.util.ArrayList;

import android.util.Log;

import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.datastructure.objects.Object;
import com.datastructure.tmx.TMXTile;

public class Place {
	
	public TMXTile left_tile;
	public TMXTile right_tile;
	public int type;
	public Object left_object=null;
	public Object right_object=null;
	private ArrayList<Object> possible_objects;
	
	public Place(TMXTile left_tile, ArrayList<Object> possible_objects) {
		this.left_tile = left_tile;
		this.possible_objects = possible_objects;
	}
	
	public void setRandomObject(Map map){ 
		
		int max_value=0;
		for(Object object : possible_objects){
			max_value = max_value + object.chance;
		}
		int chosenObjekt = SessionData.instance().randomInt.nextInt(max_value);
		
		int current_value=0;
		for(Object object : possible_objects){
			if(current_value <= chosenObjekt && chosenObjekt < current_value + object.chance){
				if(object.type==SessionData.CANNON){
					left_object = new Cannon(map,true);
					right_object = new Cannon(map,false);
					return; 
				}
			}
			current_value = current_value + object.chance;
		}
		Log.i("setRandomObject", "only EMPTY");
		left_object = new Empty(map);
		right_object = new Empty(map);
	}
}

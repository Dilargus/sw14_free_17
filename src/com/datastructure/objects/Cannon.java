package com.datastructure.objects;

import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.datastructure.objects.Object;


public class Cannon extends Object {

	public Cannon(Map map, boolean left) {
		super(map);
		type = SessionData.CANNON;
        columns=2;
        verfügbareTMXFiles = 1;
        rows=1;
        if(left){
        	tmxPfad = "Cannonl";
        }
        else{
        	tmxPfad = "Cannonr";
        }
       
    }
	
	public Cannon() {
		super();
		type = SessionData.CANNON;
		chance = 50;
    }
	
	public void setupEvents(){
	}
	
}
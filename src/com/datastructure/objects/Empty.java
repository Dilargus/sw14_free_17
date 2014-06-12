package com.datastructure.objects;

import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.datastructure.objects.Object;

public class Empty extends Object {

	public Empty(Map map) {
		super(map);
		type = SessionData.EMPTY;
        columns=2;
        verfügbareTMXFiles = 1;
        rows=1;
        tmxPfad = "Empty";
    }
	
	public Empty() {
		super();
		type = SessionData.EMPTY;
		chance = 30;
    }
	
	public void setupEvents(){
	}
	
}
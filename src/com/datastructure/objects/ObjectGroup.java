package com.datastructure.objects;

import java.util.ArrayList;
import java.util.List;
import com.adventureislands.Map;
import com.adventureislands.SessionData;

public class ObjectGroup {
    protected List<Object> objekte = new ArrayList<Object>();

 
	public ObjectGroup(Map map) {
		switch (map.type) {
		case SessionData.ISLAND:
			objekte.add(new Palm(map));
			break;
		}
	}


	public List<Object> getObjekte() {
        return objekte;
    }
	
}
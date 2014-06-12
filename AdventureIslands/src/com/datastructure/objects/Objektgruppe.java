package com.datastructure.objects;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.adventureislands.SessionData;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;

public class Objektgruppe {
    protected List<Objekt> objekte = new ArrayList<Objekt>();

 
	public Objektgruppe(int art, Context context,TMXObjectGroup collisionObjects, TMXObjectGroup clickableObjects,
			ArrayList<TMXObjectGroup> collisiongroups,
			ArrayList<TMXLayer> objektvorLayers, ArrayList<TMXLayer> mapLayers,
			TMXLayer vorlageLayer) {
		switch (art) {
		case SessionData.SANDINSEL:
			objekte.add(new Palme4x5(context,collisionObjects,clickableObjects,collisiongroups,objektvorLayers,mapLayers,vorlageLayer));
			break;
		}
	}


	public List<Objekt> getObjekte() {
        return objekte;
    }
	
}
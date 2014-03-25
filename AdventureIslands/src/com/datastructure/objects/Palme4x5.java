package com.datastructure.objects;

import java.util.ArrayList;

import android.content.Context;

import com.adventureislands.SessionData;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;

public class Palme4x5 extends Objekt {
	

	public Palme4x5(Context context, TMXObjectGroup collisionObjects, TMXObjectGroup clickableObjects,
			ArrayList<TMXObjectGroup> collisiongroups,
			ArrayList<TMXLayer> objektvorLayers, ArrayList<TMXLayer> mapLayers,
			TMXLayer vorlageLayer) {
		super(context, collisionObjects,clickableObjects, collisiongroups, objektvorLayers, mapLayers,
				vorlageLayer);
        columns=4;
        verfügbareTMXFiles = 1;
        rows=5;
        tmxPfad = "Palme4x5";
      
        tiles = new TMXTile[columns][rows];
        for(int c=0;c<columns;c++){
        	for(int r=0; r<rows;r++){
        		tiles[c][r] = new TMXTile(vorlageLayer.getTileAt(0, 0));
        		ArrayList<Integer> arten = new ArrayList<Integer>();
        		ArrayList<Integer> ebenenunterschied = new ArrayList<Integer>();
        		ArrayList<Integer> layerunterschied = new ArrayList<Integer>();
        		ebenenunterschied.add(0);
        		ebenenunterschied.add(1);
        		layerunterschied.add(0);
        		layerunterschied.add(1);
        		arten.add(SessionData.HELLERSAND);
        		arten.add(SessionData.KLIPPENSAND);
        		arten.add(SessionData.SANDSTRAND);
        		tiles[c][r].boden_arten.addAll(arten);
        		tiles[c][r].ebenen_difference.addAll(ebenenunterschied);
        		tiles[c][r].layer_difference.addAll(layerunterschied);
        	}
        }
    }
	public void setupEvents(){
	}
}
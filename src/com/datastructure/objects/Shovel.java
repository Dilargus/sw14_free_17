package com.datastructure.objects;

import java.util.ArrayList;
import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.datastructure.tmx.TMXTile;

public class Shovel extends Object {
	

	public Shovel(Map map) {
		super(map);
		type = SessionData.SHOVEL;
        columns=1;
        verfügbareTMXFiles = 1;
        rows=1;
        tmxPfad = "Shovel";
        pickable = true;
        tiles = new TMXTile[columns][rows];
        for(int c=0;c<columns;c++){
        	for(int r=0; r<rows;r++){
        		tiles[c][r] = new TMXTile(map.sample_layer.getTileAt(0, 0));
        		ArrayList<Integer> arten = new ArrayList<Integer>();
        		ArrayList<Integer> ebenenunterschied = new ArrayList<Integer>(); 
        		ArrayList<Integer> layerunterschied = new ArrayList<Integer>();
        		ebenenunterschied.add(0);
        		layerunterschied.add(0);
        		arten.add(SessionData.HELLERSAND);
        		arten.add(SessionData.KLIPPENSAND);
        		arten.add(SessionData.SANDSTRAND);
        		tiles[c][r].surface_types.addAll(arten);
        		tiles[c][r].surfaces_difference.addAll(ebenenunterschied);
        		tiles[c][r].layers_difference.addAll(layerunterschied);
        	}
        }
    }
	
	public void setupEvents(){
	}
}
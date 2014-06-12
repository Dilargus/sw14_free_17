package com.datastructure.objects;

import java.util.ArrayList;
import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.datastructure.tmx.TMXTile;

public class Cross extends Object {
	

	public Cross(Map map) {
		super(map);
		
		
		type = SessionData.CROSS;
        columns=5;
        verfügbareTMXFiles = 1;
        rows=3;
        tmxPfad = "Cross";
        doable = true;
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
	@Override
	public void setupEvents(){
		events.add(new Event(100, fragment.getLayers().get(0).getTileAt(2, 1), 12, 26, 1));
		events.add(new Event(100, fragment.getLayers().get(0).getTileAt(1, 1), 11, 25, 1));
		events.add(new Event(200, fragment.getLayers().get(0).getTileAt(2, 1), 12, 25, 1));
		events.add(new Event(400, fragment.getLayers().get(0).getTileAt(1, 1), 10, 25, 1));
		events.add(new Event(800, fragment.getLayers().get(0).getTileAt(3, 1), 11, 26, 1));
		events.add(new Event(600, fragment.getLayers().get(0).getTileAt(1, 1), 9, 25, 1));
	}
	
}
package com.datastructure.objects;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.drm.DrmStore.RightsStatus;
import android.util.Log;

import com.adventureislands.SessionData;
import com.adventureislands.loadMap;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;

public class Cannon extends Objekt {
	

	public Cannon(Context context, TMXObjectGroup collisionObjects,TMXObjectGroup clickableObjects,
			ArrayList<TMXObjectGroup> collisiongroups,
			ArrayList<TMXLayer> objektvorLayers, ArrayList<TMXLayer> mapLayers,
			TMXLayer vorlageLayer) {
		super(context, collisionObjects,clickableObjects, collisiongroups, objektvorLayers, mapLayers,
				vorlageLayer);
        columns=2;
        verfügbareTMXFiles = 2;
        rows=1;
        tmxPfad = "Cannon";
        this.setDoable(true);
        tiles = new TMXTile[columns][rows];
        for(int c=0;c<columns;c++){
        	for(int r=0; r<rows;r++){
        		tiles[c][r] = new TMXTile(vorlageLayer.getTileAt(0, 0));
        	}
        }
        ArrayList<Integer> arten = new ArrayList<Integer>();
		ArrayList<Integer> ebenenunterschied = new ArrayList<Integer>(); 
		ArrayList<Integer> layerunterschied = new ArrayList<Integer>();
		ebenenunterschied.add(0);
		layerunterschied.add(0);
		arten.add(SessionData.SCHIFFSDECK);
		tiles[0][0].boden_arten.addAll(arten);
		tiles[0][0].ebenen_difference.addAll(ebenenunterschied);
		tiles[0][0].layer_difference.addAll(layerunterschied);
		
		arten = new ArrayList<Integer>();
		ebenenunterschied = new ArrayList<Integer>(); 
		layerunterschied = new ArrayList<Integer>();
		ebenenunterschied.add(1);
		layerunterschied.add(1);
		arten.add(SessionData.SCHIFFSHUELLE);
		tiles[1][0].boden_arten.addAll(arten);
		tiles[1][0].ebenen_difference.addAll(ebenenunterschied);
		tiles[1][0].layer_difference.addAll(layerunterschied);
    }
	
	public void setupEvents(){
	}
	
	@Override
	public void loadTMX(){
		if(!tmxPfad.contains(" ")){
			if(SessionData.instance().player1.side==SessionData.RIGHT_SIDE){
				tmxPfad = tmxPfad + " r";
			}
			if(SessionData.instance().player1.side==SessionData.LEFT_SIDE){
				tmxPfad = tmxPfad + " l";
			}
			
		}
		Log.i("tmxpfad", tmxPfad);
		loadMap loader = new loadMap(tmxPfad, context);
		try {
			fragment = loader.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
package com.datastructure.objects;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import android.util.Log;
import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.adventureislands.loadMap;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;

public abstract class Object implements
java.io.Serializable{
	
	public int startcolumn=-1;
	public int startrow=-1; 
	public int startlayer=-1;
	public String tmxPfad;
	public int type;
	public int chance;

	transient public int rows=-1;
	transient public int columns=-1;
	transient public boolean pickable=false;
	transient public boolean moveable=false;
	transient public boolean doable=false;
	transient public int verfügbareTMXFiles =-1;
	transient public TMXTiledMap fragment=null;
	transient public ArrayList<Event> events= new ArrayList<Event>();
	transient public TMXTile[][] tiles=null;
	transient Map map;
	transient ArrayList<TMXObject> collision_objects = new ArrayList<TMXObject>();
	transient ArrayList<TMXObject> clickable_objects = new ArrayList<TMXObject>();
	
	public Object() {
	}


	public Object(Map map) {
		this.map = map;
	}
	
	public void loadObjektIntoMap(TMXTile starttile, int tileset){
		this.startcolumn = starttile.getColumn();
		this.startrow = starttile.getRow(); 
		this.startlayer = Integer.parseInt(starttile.layer.getName());
		ArrayList<TMXObjectGroup> collisiongroups;
		if (fragment.getObjectGroups() != null ) {
			collisiongroups = fragment.getObjectGroups();
			for(TMXObjectGroup collisiongroup : collisiongroups) {
				if("1".equals(collisiongroup.getName())){
					for(TMXObject collisionObject : collisiongroup.getObjects()){
						collisionObject.x = starttile.getWidth()*starttile.getColumn()+collisionObject.x;
		    			collisionObject.y = starttile.getHeight()*starttile.getRow()+collisionObject.y;
		    			collision_objects.add(collisionObject);
		    			map.collision_rectangles.add(collisionObject);
		    		}
				}
				if("Clickable".equals(collisiongroup.getName())){
					for(TMXObject clickableObject : collisiongroup.getObjects()){
						clickableObject.corresponding_object = this;
						clickableObject.x = starttile.getWidth()*starttile.getColumn()+clickableObject.x;
						clickableObject.y = starttile.getHeight()*starttile.getRow()+clickableObject.y;
						clickable_objects.add(clickableObject);
		    			map.clickable_rectangles.add(clickableObject);
		    		}
				}
			}
		}
		collisiongroups=null;
    	
		for(int c=starttile.getColumn(); c < starttile.getColumn() + fragment.getColumns(); c++){
		    for(int r=starttile.getRow(); r < starttile.getRow() + fragment.getRows(); r++){
		    	map.free_tiles.remove(map.free_tiles_array[c][r]);
		    	map.free_tiles_array[c][r].free=false;
		    	map.not_tested_tiles.remove(map.free_tiles_array[c][r]);
				int objektrow=r-starttile.getRow();
		    	int objektcolumn=c-starttile.getColumn();
		    	TMXLayer objektvorLayer=null;
		    	TMXLayer objektLayer=null;
		    	for(int l=0; l<fragment.getLayers().size();l++){
		    		TMXLayer iteratorLayer  = fragment.getLayers().get(l);
		    		if(objektcolumn>=iteratorLayer.getColumns() || objektrow >=iteratorLayer.getRows()){
		    			continue;
		    		}
		    		TMXTile iteratorTile = iteratorLayer.getTileAt(objektcolumn, objektrow);
		    		if(iteratorLayer.getName().contains("vor")){
		    			
		    			if(iteratorLayer.getName().endsWith("1")){
		    				objektvorLayer = map.in_front_layers.get(0);
		    			}
		    			else{
		    				objektvorLayer = objektvorLayer.getNextLayer(map.in_front_layers, map.sample_layer);
		    			}
		    			TMXTile vorTile = objektvorLayer.getTileAt(c, r);
		    			vorTile.setAtlas(iteratorTile.getAtlasColumn(), iteratorTile.getAtlasRow());
		    			vorTile.setGID(tileset);
		    			iteratorLayer.setTileAt(objektcolumn, objektrow, vorTile);
		    		}
		    		else{
		    			if(objektLayer==null){
		    				for(TMXLayer maplayer : map.map_layers){
	    						objektLayer = map.free_tiles_array[c][r].layer.putInLayer(map.map_layers, map.sample_layer); 
	    						break;
		    				}
		    				 
		    			} 
		    			else{
		    				objektLayer = objektLayer.putInLayer(map.map_layers, map.sample_layer);
		    			}
		    			TMXTile objektTile = objektLayer.getTileAt(c, r);
		    			objektTile.setAtlas(iteratorTile.getAtlasColumn(), iteratorTile.getAtlasRow());
		    			objektTile.setGID(tileset);
		    			iteratorLayer.setTileAt(objektcolumn, objektrow, objektTile);
		    		}
		    	}	
		    }
		}
	}
	
	
	public boolean placeSingleObject(int tileset){
    	boolean isPlaced=false;
    	int randomStandort;
		boolean isPossible=false;
		int objektrow;
		int objektcolumn;
		int tries=0;
    	while(isPlaced==false){
    		tries++;
    		if(tries>1000){
    			return false;
    		}
    		isPossible=true;
    		randomStandort = SessionData.instance().randomInt.nextInt(map.not_tested_tiles.size());
    		TMXTile starttile = map.not_tested_tiles.get(randomStandort);
				if(starttile.getColumn() + this.columns < map.sample_layer.getColumns() && starttile.getRow() + this.rows < map.sample_layer.getRows()){
					loop:
					for(int c=starttile.getColumn(); c < starttile.getColumn() + this.columns; c++){
			    		for(int r=starttile.getRow(); r < starttile.getRow() + this.rows; r++){
			    			objektrow=r-starttile.getRow();
			    			objektcolumn=c-starttile.getColumn();
			    			TMXTile freetile = map.free_tiles_array[c][r];
			    			TMXTile objekttile = this.tiles[objektcolumn][objektrow];
			    			if(!objekttile.surfaces_difference.contains(freetile.surface.getEbenenDif(starttile.surface)) ||
					    			!objekttile.layers_difference.contains(Math.abs(map.map_layers.indexOf(freetile.layer) - map.map_layers.indexOf(starttile.layer))) ||
					    			!objekttile.surface_types.contains(freetile.surface.surface_texture.type)|| !map.free_tiles.contains(freetile)){
			    				map.not_tested_tiles.remove(randomStandort);
				    		isPossible=false;
				    		break loop;
			    		}
			    		
			    	} 
				}
				if(isPossible==true){
	    			isPlaced=true;
	    			this.loadTMX();
	    			this.loadObjektIntoMap( starttile,tileset); 
	    		}
			}
		}
    	return true;
	}
	
	public abstract void setupEvents();
	
	public void loadTMX(){
		if(!tmxPfad.contains(" ")){
			int chosenTMXFile = SessionData.instance().randomInt.nextInt(verfügbareTMXFiles);
			tmxPfad = tmxPfad + " " + chosenTMXFile;
		}
		Log.i("tmxpfad", tmxPfad);
		loadMap loader = new loadMap(tmxPfad, map.context);
		try {
			fragment = loader.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public void moveX(boolean pos){
		if(!pos){
			for(TMXObject clickable_rectangle : clickable_objects){
				clickable_rectangle.x = clickable_rectangle.x-32;
			}
			for(TMXObject collision_rectangle : collision_objects){
				collision_rectangle.x = collision_rectangle.x-32;
			}
			for(TMXLayer layer : fragment.getLayers()){
				for(int c=0; c < fragment.getColumns(); c++){
				    for(int r=0; r < fragment.getRows(); r++){
					    TMXTile tile = layer.getTileAt(c, r);
				    	TMXLayer real_layer = tile.layer;
				    	TMXTile fromTile = real_layer.getTileAt(tile.getColumn(), tile.getRow());
						TMXTile toTile = real_layer.getTileAt(tile.getColumn()-1, tile.getRow());
						toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
						toTile.copyProperties(fromTile);
						toTile.setGID(fromTile.getGID());
						fromTile.setEmpty();
						layer.setTileAt(c, r, toTile);
				    }
				}
			}
		}
		else{
			for(TMXObject clickable_rectangle : clickable_objects){
				clickable_rectangle.x = clickable_rectangle.x+32;
			}
			for(TMXObject collision_rectangle : collision_objects){
				collision_rectangle.x = collision_rectangle.x+32;
			}
			for(TMXLayer layer : fragment.getLayers()){
				for(int c=fragment.getColumns()-1; c >=0; c--){
				    for(int r=fragment.getRows()-1; r >=0; r--){
					    TMXTile tile = layer.getTileAt(c, r);
					    TMXLayer real_layer = tile.layer;
				    	TMXTile fromTile = real_layer.getTileAt(tile.getColumn(), tile.getRow());
						TMXTile toTile = real_layer.getTileAt(tile.getColumn()+1, tile.getRow());
						toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
						toTile.copyProperties(fromTile);
						toTile.setGID(fromTile.getGID());
						fromTile.setEmpty();
						layer.setTileAt(c, r, toTile);
				    }
				}
			}
		}
	}
	
	
}

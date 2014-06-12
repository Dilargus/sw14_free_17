package com.datastructure.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.util.Log;
import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.adventureislands.loadMap;
import com.datastructure.Surface;
import com.datastructure.tmx.*;

public class Ship extends Object {


	private Surface waterEbene;
	private Surface hullEbene;
    private Surface deckEbene;
    private boolean movable = true;
 
    private ArrayList<TMXTile> vorTiles = new ArrayList<TMXTile>();
    private List<Object> objectsOnShip = new ArrayList<Object>(); 
	private ArrayList<Place> weapon_places = new ArrayList<Place>();
    
    
	public Ship(Map map) {
		super(map);
		type = SessionData.SHIP;
		this.waterEbene=map.first_surface;
		verfügbareTMXFiles = 1;
	    tmxPfad = "Ship";
	}
	
	@Override
	public void loadTMX(){
		if(!tmxPfad.contains(" ")){
			int chosenTMXFile = SessionData.instance().randomInt.nextInt(verfügbareTMXFiles);
			tmxPfad = tmxPfad + " " + chosenTMXFile;
		}
		loadMap loader = new loadMap(tmxPfad, map.context);
		try {
			fragment = loader.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		for(TMXLayer layer : fragment.getLayers()){
			if(layer.getName().equals("weapons")){
				for(int r=0; r < fragment.getRows(); r++){
					Place p = null;
					for(int c=0; c < fragment.getColumns(); c++){
				    	TMXTile tile  = layer.getTileAt(c, r);

				    	if(tile.getAtlasColumn()==0 && tile.getAtlasRow()==56){
				    		p = new Place(tile,SessionData.instance().SMALL_WEAPONS);
				    	}
				    	if(tile.getAtlasColumn()==2 && tile.getAtlasRow()==56 && p!=null){
				    		p.right_tile = tile;
				    		weapon_places.add(p);
				    	}
				    }
				}
			}
		}
		for(Place place : weapon_places){	
			place.setRandomObject(map);
			place.left_object.loadTMX();
			place.right_object.loadTMX();
		}
	}
	
	

	@Override
	public void loadObjektIntoMap(TMXTile starttile, int tileset){
		this.startcolumn = starttile.getColumn();
		this.startrow = starttile.getRow(); 
		this.startlayer = Integer.parseInt(starttile.layer.getName());
		ArrayList<TMXObjectGroup> rectanglegroups=null;
		if (fragment.getObjectGroups() != null ) {
			rectanglegroups = fragment.getObjectGroups();
			for(TMXObjectGroup collisiongroup : rectanglegroups) {
				if("1".equals(collisiongroup.getName())){
					for(TMXObject collisionObject : collisiongroup.getObjects()){
						collisionObject.x = starttile.getWidth()*starttile.getColumn()+collisionObject.x;
		    			collisionObject.y = starttile.getHeight()*starttile.getRow()+collisionObject.y;
		    			map.collision_rectangles.add(collisionObject);
		    			collision_objects.add(collisionObject);
		    		}
				}
				if("Clickable".equals(collisiongroup.getName())){
					for(TMXObject clickableObject : collisiongroup.getObjects()){
						clickableObject.corresponding_object = this;
						clickableObject.x = starttile.getWidth()*starttile.getColumn()+clickableObject.x;
						clickableObject.y= starttile.getHeight()*starttile.getRow()+clickableObject.y;
		    			map.clickable_rectangles.add(clickableObject);
		    			clickable_objects.add(clickableObject);
		    		}
				}
			}
		}
    	 
		ArrayList<TMXTile> hullRahmen= new ArrayList<TMXTile>();
		ArrayList<TMXTile> hullinnerTiles = new ArrayList<TMXTile>();
		ArrayList<TMXTile> deckRahmen= new ArrayList<TMXTile>();
		ArrayList<TMXTile> deckinnerTiles = new ArrayList<TMXTile>();
		Surface wassersandEbene = waterEbene.inner_surfaces.get(0);
		Surface sandEbene = wassersandEbene.inner_surfaces.get(0);
		Surface mountainEbene = sandEbene.inner_surfaces.get(0);
    	TMXLayer hullLayer = sandEbene.layer;
    	TMXLayer deckLayer = mountainEbene.layer;
		
		
		for(int c=starttile.getColumn(); c < starttile.getColumn() + this.fragment.getColumns(); c++){
		    for(int r=starttile.getRow(); r < starttile.getRow() + this.fragment.getRows(); r++){
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
		    		if(iteratorTile.getGID()!=0){
		    			if(iteratorLayer.getName().contains("vor")){
			    			if(iteratorLayer.getName().endsWith("1")){
			    				objektvorLayer = map.in_front_layers.get(0);
			    			}
			    			else{
			    				objektvorLayer = objektvorLayer.getNextLayer(map.in_front_layers, map.sample_layer);
			    			}
			    			TMXTile tile = objektvorLayer.getTileAt(c, r);
			    			tile.setAtlas(iteratorTile.getAtlasColumn(), iteratorTile.getAtlasRow());
			    			tile.setGID(tileset);
			    			iteratorLayer.setTileAt(objektcolumn, objektrow, objektvorLayer.getTileAt(c, r));
			    			vorTiles.add(tile);
			    			
			    		}
			    		else if(iteratorLayer.getName().contains("hull") || iteratorLayer.getName().contains("deck")){
			    			if(iteratorLayer.getName().contains("hull")){
			    				objektLayer = hullLayer;
		    					if(iteratorLayer.getName().contains("rahmen")){
		    						hullRahmen.add(objektLayer.getTileAt(c, r));
		    					}
		    					else if(iteratorLayer.getName().contains("inner")){
		    						hullinnerTiles.add(objektLayer.getTileAt(c, r));
		    					}
		    					
			    			}
			    			else if(iteratorLayer.getName().contains("deck")){
			    				objektLayer = deckLayer;
		    					if(iteratorLayer.getName().contains("rahmen")){
		    						deckRahmen.add(objektLayer.getTileAt(c, r));
		    					}
		    					else if(iteratorLayer.getName().contains("inner")){
		    						deckinnerTiles.add(objektLayer.getTileAt(c, r));
		    					}
			    			}
			    			TMXTile objectTile = objektLayer.getTileAt(c, r);
			    			objectTile.setAtlas(iteratorTile.getAtlasColumn(), iteratorTile.getAtlasRow());
			    			objectTile.setGID(tileset);
			    		
			    			iteratorLayer.setTileAt(objektcolumn, objektrow, objektLayer.getTileAt(c, r));
			    			
			    		}
			    		else{
			    			objektLayer = hullLayer;
			    			TMXTile objectTile = objektLayer.getTileAt(c, r);
			    			if(iteratorTile.getAtlasColumn()==0 && iteratorTile.getAtlasRow()==56){
			    				for(Place place : weapon_places){
			    					if(place.left_tile.getColumn() == iteratorTile.getColumn() && place.left_tile.getRow() == iteratorTile.getRow()){
			    						place.left_tile = objectTile;
			    						break;
			    					}
			    				}
			    			}
			    			if(iteratorTile.getAtlasColumn()==2 && iteratorTile.getAtlasRow()==56){
			    				for(Place place : weapon_places){
			    					if(place.right_tile.getColumn() == iteratorTile.getColumn() && place.right_tile.getRow() == iteratorTile.getRow()){
			    						place.right_tile = objectTile;
			    						break;
			    					}
			    				}
			    			}
			    		}
		    		}
		    		
		    	}	
		    }
		}

		hullEbene = new Surface(SessionData.SCHIFFSHUELLE, hullRahmen, hullinnerTiles, wassersandEbene, hullLayer, map);
		wassersandEbene.inner_surfaces.add(hullEbene);
		deckEbene = new Surface(SessionData.SCHIFFSDECK, deckRahmen, deckinnerTiles, hullEbene, deckLayer, map);
		hullEbene.inner_surfaces.add(deckEbene);
		map.calculateFreeTiles();
		
		for(Place place : weapon_places){	
			place.left_object.loadObjektIntoMap(place.left_tile,tileset); 
			objectsOnShip.add(place.left_object);
			place.right_object.loadObjektIntoMap(place.right_tile,tileset); 
			objectsOnShip.add(place.right_object);
		}
	}
	
	public void setupEvents(){
		
	}
	
	public void moveShipX(int columns){
		if(columns>0){
			while(movable==true && columns!=0){
				int result;
				Log.i("hullebene index",""+hullEbene.surface_index);
				result = hullEbene.moveEbeneX(true);
				if(result==1){
					return;
				}
				else if(result==2){
					movable=false;
				}
				deckEbene.moveEbeneX(true);
				
				for(TMXObject object : clickable_objects){
					object.x = object.x + 32;
				}
				for(TMXObject object : collision_objects){
					object.x = object.x + 32;
				}
				for(TMXLayer vorlayer : map.in_front_layers){
					for(int c=vorlayer.getColumns()-1;c>=0;c--){
						for(int r=vorlayer.getRows()-1;r>=0;r--){ 
							if(vorTiles.contains(vorlayer.getTileAt(c, r))){
								TMXTile fromTile = vorlayer.getTileAt(c, r);
								TMXTile toTile = vorlayer.getTileAt(c+1, r);
								toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
								toTile.copyProperties(fromTile);
								toTile.setGID(fromTile.getGID());
								fromTile.setEmpty();
								vorTiles.set(vorTiles.indexOf(fromTile), toTile);
							}
						}
					}
				}
				for(Object object : objectsOnShip){
					object.moveX(true);
				}
				
				columns--;
			}
		}
		else if(columns<0){
			while(movable==true && columns!=0){
				int result;
				result = hullEbene.moveEbeneX(false);
				if(result==1){
					return;
				}
				else if(result==2){
					movable=false;
				}
				deckEbene.moveEbeneX(false);
				
				for(TMXObject object : clickable_objects){
					object.x = object.x - 32;
				}
				for(TMXObject object : collision_objects){
					object.x = object.x - 32;
				}
				for(TMXLayer vorlayer : map.in_front_layers){
					for(int c=0;c<vorlayer.getColumns();c++){
						for(int r=0;r<vorlayer.getRows();r++){ 
							if(vorTiles.contains(vorlayer.getTileAt(c, r))){
								TMXTile fromTile = vorlayer.getTileAt(c, r);
								TMXTile toTile = vorlayer.getTileAt(c-1, r);
								toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
								toTile.copyProperties(fromTile);
								toTile.setGID(fromTile.getGID());
								fromTile.setEmpty();
								vorTiles.set(vorTiles.indexOf(fromTile), toTile);
							}
						}
					}
				}
				for(Object object : objectsOnShip){
					object.moveX(false); 
				}
				
				columns++;
			}
		}
	}
	
	public void moveShipY(int rows){
		if(rows>0){
			while(movable==true && rows!=0){
				int result;
				result = hullEbene.moveEbeneY(true);
				if(result==1){
					return;
				}
				else if(result==2){
					movable=false;
				}
				deckEbene.moveEbeneY(true);
				
				for(TMXObject object : clickable_objects){
					object.y = object.y + 32;
				}
				for(TMXObject object : collision_objects){
					object.y = object.y + 32;
				}
				for(TMXLayer vorlayer : map.in_front_layers){
					for(int c=vorlayer.getColumns()-1;c>=0;c--){
						for(int r=vorlayer.getRows()-1;r>=0;r--){ 
							if(vorTiles.contains(vorlayer.getTileAt(c, r))){
								TMXTile fromTile = vorlayer.getTileAt(c, r);
								TMXTile toTile = vorlayer.getTileAt(c, r+1);
								toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
								toTile.copyProperties(fromTile);
								toTile.setGID(fromTile.getGID());
								fromTile.setEmpty();
								vorTiles.set(vorTiles.indexOf(fromTile), toTile);
							}
						}
					}
				}
				rows--;
			}
		}
		else if(rows<0){
			while(movable==true && rows!=0){
				int result;
				result = hullEbene.moveEbeneY(false);
				if(result==1){
					return;
				}
				else if(result==2){
					movable=false;
				}
				deckEbene.moveEbeneY(false);
				
				for(TMXObject object : clickable_objects){
					object.y = object.y - 32;
				}
				for(TMXObject object : collision_objects){
					object.y = object.y - 32;
				}
				for(TMXLayer vorlayer : map.in_front_layers){
					for(int c=0;c<vorlayer.getColumns();c++){
						for(int r=0;r<vorlayer.getRows();r++){ 
							if(vorTiles.contains(vorlayer.getTileAt(c, r))){
								TMXTile fromTile = vorlayer.getTileAt(c, r);
								TMXTile toTile = vorlayer.getTileAt(c, r-1);
								toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
								toTile.copyProperties(fromTile);
								toTile.setGID(fromTile.getGID());
								fromTile.setEmpty();
								vorTiles.set(vorTiles.indexOf(fromTile), toTile);
							}
						}
					}
				}
				rows++;
			}
		}
	}
  
}
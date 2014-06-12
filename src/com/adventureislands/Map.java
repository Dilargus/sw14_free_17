package com.adventureislands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.content.Context;
import android.util.Log;
import com.datastructure.Surface;
import com.datastructure.objects.Cross;
import com.datastructure.objects.Exit;
import com.datastructure.objects.Object;
import com.datastructure.objects.Palm;
import com.datastructure.objects.Rank;
import com.datastructure.objects.Ship;
import com.datastructure.objects.ObjectGroup;
import com.datastructure.objects.Shovel;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;

public class Map implements
java.io.Serializable{
	
	public int type;
	public int surface_counter=0;
	public Map inner_map = null;
	public Map outer_map = null;
	public Surface first_surface;
	public List<Object> map_objects = new ArrayList<Object>();
	public ArrayList<TMXLayer> map_layers;
	
	public transient Ship ship;
	public transient ArrayList<TMXObject> collision_rectangles;
	public transient ArrayList<TMXObject> clickable_rectangles;
	public transient TMXLayer ground_layer;
	public transient TMXLayer sample_layer;
	public transient TMXTiledMap tmx_map;
	public transient ArrayList<TMXLayer> in_front_layers = new ArrayList<TMXLayer>();
	public transient ArrayList<TMXTile> not_tested_tiles;
	public transient  ArrayList<TMXTile> free_tiles = new ArrayList<TMXTile>();
	public transient TMXTile[][] free_tiles_array;
	public transient TMXObject start_point = null;
	public transient Context context;
	
	public Map(Map outer_map,Context context, int type, String path){
		this.type = type;
		boolean loadable=false;
		this.context = context;
		while(loadable==false){
			loadable=true;

			setUpLayer(path);
	    	
	    	if(type==SessionData.ISLAND || type==SessionData.TESTISLAND){
	    		createIsland(outer_map,loadable);		
	    	}
	    	else if(type==SessionData.CITY){
	    		ArrayList<TMXLayer> to_delete = new ArrayList<TMXLayer>();
	    		for(TMXLayer layer : map_layers){  
	    			if(layer.getName().contains("vor")){
	    				Log.i("name of vorlayer: ", layer.getName());
	    				in_front_layers.add(layer);
	    				to_delete.add(layer);
	    			} 
	    		}
	    		map_layers.removeAll(to_delete);
	    		first_surface = new Surface(SessionData.CITY,null,null,ground_layer,this);
	    	}
		}
		TMXLayer first_front_layer = new TMXLayer(sample_layer,"" + map_layers.size());
    	map_layers.add(first_front_layer); 
	}
	
	public void setUpLayer(String path){
		
		loadMap loader = new loadMap(path, context); 
        try {
			tmx_map = loader.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        
        
    	if (tmx_map != null && (map_layers = tmx_map.getLayers()) != null) {
			for (TMXLayer layer : map_layers) {
				if ("1".equals(layer.getName())) {
					ground_layer= layer;
				}
				if ("vorlage".equals(layer.getName())) {
					sample_layer = layer;				
				}
				
				for(int c=0;c<layer.getColumns();c++){
		    		for(int r=0;r<layer.getRows();r++){
		    			layer.getTileAt(c, r).layer = layer;
		    		}
		    	}
			}
    	}
    	free_tiles_array = new TMXTile[sample_layer.getColumns()][sample_layer.getRows()];
    	map_layers.remove(sample_layer);
    	
    	ArrayList<TMXObjectGroup> rectanglegroups=null;
    	if ((rectanglegroups = tmx_map.getObjectGroups()) != null ) {
			for(TMXObjectGroup rectanglegroup : rectanglegroups) {
				if("Collision".equals(rectanglegroup.getName())){
					collision_rectangles = rectanglegroup.getObjects();
				}
				if("Clickable".equals(rectanglegroup.getName())){
					clickable_rectangles = rectanglegroup.getObjects();
				}
				if("Startpoints".equals(rectanglegroup.getName())){
					start_point = rectanglegroup.getObjects().get(0);
				}
			}
		}
	}

	public void createIsland(Map outer_map,boolean loadable){
		if(outer_map==null){
    		first_surface = new Surface(SessionData.WASSER,null,null,ground_layer,this);
		} 
		else{
    		first_surface = new Surface(SessionData.DUNKELHEIT,null,null,ground_layer,this);
		}
		calculateFreeTiles();
    	
		in_front_layers.clear();
		map_objects.clear();
    	TMXLayer in_front_layer = new TMXLayer(sample_layer, "0");
		in_front_layers.add(in_front_layer);
		  
		/*if(outer_map==null){
			inner_map = new Map(this,context,SessionData.HÖHLE,SessionData.ISLAND,"cave");
			
			
	    	
	    	Shovel shovel = new Shovel(this);
	    	loadable = shovel.placeSingleObject(tmx_map.getTileSets().get(0).getFirstGID());
	    	map_objects.add(shovel);
	    	
	    	Cross cross = new Cross(this);
	    	loadable = cross.placeSingleObject(tmx_map.getTileSets().get(0).getFirstGID());
	    	map_objects.add(cross);
	    	
	    	Exit exit = new Exit(this);
	    	loadable = exit.placeSingleObject(tmx_map.getTileSets().get(0).getFirstGID());
	    	map_objects.add(exit);
			placeVariousObjects();
		}
		else{
			this.outerMap=outer_map;
			Rank rank = new Rank(this);
			loadable = rank.placeSingleObject(tmx_map.getTileSets().get(0).getFirstGID());
			map_objects.add(rank);
		}*/
	}


	public void calculateFreeTiles(){
		free_tiles.clear();
    	for(int c = 0; c < sample_layer.getColumns(); c++){
    		for(int r = 0; r < sample_layer.getRows(); r++){
				for(int l = map_layers.size() - 1; l >= 0; l--){
					if(map_layers.get(l).getTileAt(c, r).free == true){
						free_tiles_array[c][r] = map_layers.get(l).getTileAt(c, r);
						free_tiles.add(free_tiles_array[c][r]);
						break;
					}
				}
			}
    	}
    	not_tested_tiles = (ArrayList<TMXTile>) free_tiles.clone();
	}
	
	public void placeVariousObjects(){
		ArrayList<Object> possible_objects = new ArrayList<Object>();
    	int random_location;
		boolean is_possible=false;
		int object_row;
		int object_column;
		while(not_tested_tiles.size() > (sample_layer.getColumns()*sample_layer.getRows())/2){
			ObjectGroup object_group = new ObjectGroup(this);
    		random_location = SessionData.instance().randomInt.nextInt(not_tested_tiles.size());
    		TMXTile start_tile = not_tested_tiles.get(random_location);
    		possible_objects.clear();
			for(Object object : object_group.getObjekte()){
				is_possible=true;
				if(start_tile.getColumn() + object.columns < sample_layer.getColumns() && start_tile.getRow() + object.rows < sample_layer.getRows()){
					loop:
					for(int c=start_tile.getColumn(); c < start_tile.getColumn() + object.columns; c++){
			    		for(int r=start_tile.getRow(); r < start_tile.getRow() + object.rows; r++){
			    			object_row = r - start_tile.getRow();
			    			object_column = c - start_tile.getColumn();
			    			TMXTile free_tile = free_tiles_array[c][r];
			    			TMXTile object_tile = object.tiles[object_column][object_row];
			    			if(!object_tile.surfaces_difference.contains(free_tile.surface.getEbenenDif(start_tile.surface)) ||
			    			!object_tile.layers_difference.contains(Math.abs(map_layers.indexOf(free_tile.layer) - map_layers.indexOf(start_tile.layer))) ||
			    			!object_tile.surface_types.contains(free_tile.surface.surface_texture.type)|| !free_tiles.contains(free_tile)){
				    			is_possible=false;
				    			break loop;
			    			}
			    		}
					}
					if(is_possible){
						possible_objects.add(object);
					}
				}
			}
			
			for(Object object : object_group.getObjekte()){
				object=null;
			}
			object_group=null;
		
			if(possible_objects.size()>0){
				int random_object_index = SessionData.instance().randomInt.nextInt(possible_objects.size());
				Object created_object = possible_objects.get(random_object_index);
				created_object.loadTMX();
				created_object.loadObjektIntoMap(start_tile, tmx_map.getTileSets().get(0).getFirstGID()); 
				map_objects.add(created_object);
			}
			else{
				not_tested_tiles.remove(random_location);
			}
		}
	}
	
	
	public Map(Map to_copy, Map outer_map, Context context, String path){
		this.context = context;
		setUpLayer(path);
		 
		this.type = to_copy.type;
    	for(int i = map_layers.size() - 1; i < to_copy.map_layers.size() - 1; i++){
    		TMXLayer new_layer = map_layers.get(i).getNextLayer(map_layers, sample_layer);
    		new_layer.setName(to_copy.map_layers.get(i + 1).getName());
    	}
    	
        first_surface = new Surface(to_copy.first_surface, null, ground_layer, this);
        
        calculateFreeTiles();
    	TMXLayer front_layer = new TMXLayer(sample_layer, "0");
		in_front_layers.add(front_layer);
		
		copyVariousObjects(context, to_copy.map_objects);
		
		ship = new Ship(this);
    	ship.loadTMX();
    	TMXTile ship_start_tile = first_surface.layer.getTileAt(0, first_surface.layer.getRows()-ship.fragment.getRows()); 
    	
    	tmx_map.addTileSet(ship.fragment.getTileSets().get(0));
    	tmx_map.getTileSets().get(1).setFirstGID(2);
    	
    	ship.loadObjektIntoMap(ship_start_tile, tmx_map.getTileSets().get(1).getFirstGID()); 
    	map_objects.add(ship);
	}
	
	
	public void copyVariousObjects(Context context, List<Object> objects_to_copy){
		
		for(Object objekt_to_copy : objects_to_copy){
			reconstructObject(objekt_to_copy, context);
		}
		
		for(Object new_objekt : map_objects){
			TMXTile start_tile=null;
		
			for(int l=map_layers.size()-1;l>=0;l--){
				if(Integer.parseInt(map_layers.get(l).getName()) == new_objekt.startlayer){
					start_tile = map_layers.get(l).getTileAt(new_objekt.startcolumn, new_objekt.startrow);
					break;
				}
			}
			if(start_tile==null){
				Log.e("ERROR", "tried to place an object on an unknown location");
				break;
			}
			new_objekt.loadTMX();
			if(new_objekt.tmxPfad.startsWith("Ship")){
				tmx_map.addTileSet(new_objekt.fragment.getTileSets().get(0));
		    	tmx_map.getTileSets().get(1).setFirstGID(2);
				new_objekt.loadObjektIntoMap(start_tile,tmx_map.getTileSets().get(1).getFirstGID()); 
			}
			else{
				new_objekt.loadObjektIntoMap(start_tile,tmx_map.getTileSets().get(0).getFirstGID()); 
			}
		}
	}
	
	public void reconstructObject(Object objekt_to_copy, Context context){
		if(objekt_to_copy.tmxPfad.startsWith("Shovel")){
			Shovel shovel = new Shovel(this);
			map_objects.add(shovel);
		}
		else if(objekt_to_copy.tmxPfad.startsWith("Exit")){
			Exit exit = new Exit(this);
			map_objects.add(exit);
		}
		else if(objekt_to_copy.tmxPfad.startsWith("Cross")){
			Cross cross = new Cross(this);
			map_objects.add(cross);
		}
		else if(objekt_to_copy.tmxPfad.startsWith("Palme4x5")){
			Palm palme = new Palm(this);
			map_objects.add(palme);
		}
		else if(objekt_to_copy.tmxPfad.startsWith("Rank")){
			Rank rank = new Rank(this);
			map_objects.add(rank);
		}
		else if(objekt_to_copy.tmxPfad.startsWith("Ship")){
			ship = new Ship(this);
			map_objects.add(ship);
		}
		else{
			Log.i("error", objekt_to_copy.tmxPfad+" is none of my Object Classes");
		}
		map_objects.get(map_objects.size()-1).startcolumn = objekt_to_copy.startcolumn;
		map_objects.get(map_objects.size()-1).startrow = objekt_to_copy.startrow;
		map_objects.get(map_objects.size()-1).startlayer = objekt_to_copy.startlayer;
	}
	
	public ArrayList<Surface> getAllSurfaces(){
		ArrayList<Surface> surfaces =  new ArrayList<Surface>();
		surfaces.add(first_surface);
		surfaces.addAll(getInnerSurfacesOf(first_surface));
		return surfaces;
	}
	public ArrayList<Surface> getInnerSurfacesOf(Surface current_surface){
		ArrayList<Surface> surfaces =  new ArrayList<Surface>();
		for(Surface obereEbene : current_surface.inner_surfaces){
			surfaces.add(obereEbene);
			surfaces.addAll(getInnerSurfacesOf(obereEbene));
		}
		return surfaces;
	}
	
}

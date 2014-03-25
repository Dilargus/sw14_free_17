package com.datastructure.objects;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


import android.content.Context;
import android.util.Log;

import com.adventureislands.SessionData;
import com.adventureislands.loadMap;
import com.datastructure.Ebene;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;

public abstract class Objekt implements
java.io.Serializable{
	
	int startcolumn=-1;
	int startrow=-1; 
	int startlayer=-1;
	String tmxPfad;
	

	transient int rows=-1;
	transient int columns=-1;
	transient boolean pickable=false;
	transient boolean moveable=false;
	transient boolean doable=false;
	transient int verfügbareTMXFiles =-1;
	transient TMXTiledMap fragment=null;
	transient Context context;
	transient TMXLayer vorlageLayer=null;
	transient TMXObjectGroup collisionObjects=null;
	transient TMXObjectGroup clickableObjects=null;
	transient ArrayList<TMXObjectGroup> collisiongroups=null;
	transient ArrayList<TMXLayer> objektvorLayers=null;
	transient ArrayList<TMXLayer> mapLayers=null;
	transient ArrayList<Event> events= new ArrayList<Event>();
	transient TMXTile[][] tiles=null;
	

	
	public ArrayList<Event> getTileEvents(){
		return events;
	}
	
	public boolean isPickable() {
		return pickable;
	}

	public void setPickable(boolean pickable) {
		this.pickable = pickable;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public boolean isDoable() {
		return doable;
	}

	public void setDoable(boolean doable) {
		this.doable = doable;
	}

	public Objekt(Context context,
			TMXObjectGroup collisionObjects, TMXObjectGroup clickableObjects,
			ArrayList<TMXObjectGroup> collisiongroups,
			ArrayList<TMXLayer> objektvorLayers, ArrayList<TMXLayer> mapLayers,
			TMXLayer vorlageLayer) {
		this.context = context;
		this.collisionObjects = collisionObjects;
		this.clickableObjects = clickableObjects;
		this.collisiongroups = collisiongroups;
		this.objektvorLayers = objektvorLayers;
		this.mapLayers = mapLayers;
		this.vorlageLayer = vorlageLayer;
	}
	
	public TMXTile[][] getTiles() {
		return tiles;
	}
	public void setTiles(TMXTile[][] tiles) {
		this.tiles = tiles;
	}

	public void loadObjektIntoMap(TMXTile starttile, int tileset, TMXTile[][] freeTiles, ArrayList<TMXTile> freeTilesList, ArrayList<TMXTile> nottestedTiles){
		this.startcolumn = starttile.getColumn();
		this.startrow = starttile.getRow(); 
		this.startlayer = Integer.parseInt(starttile.layer.getName());
		
		if (this.getFragment().getObjectGroups() != null ) {
			collisiongroups = this.getFragment().getObjectGroups();
			for(TMXObjectGroup collisiongroup : collisiongroups) {
				if("1".equals(collisiongroup.getName())){
					for(TMXObject collisionObject : collisiongroup.getObjects()){
						collisionObject.setX(starttile.getWidth()*starttile.getColumn()+collisionObject.getX());
		    			collisionObject.setY(starttile.getHeight()*starttile.getRow()+collisionObject.getY());
		    			collisionObjects.addObject(collisionObject);
		    		}
				}
				if("Clickable".equals(collisiongroup.getName())){
					for(TMXObject clickableObject : collisiongroup.getObjects()){
						clickableObject.setZugehoerigesObjekt(this);
						clickableObject.setX(starttile.getWidth()*starttile.getColumn()+clickableObject.getX());
						clickableObject.setY(starttile.getHeight()*starttile.getRow()+clickableObject.getY());
		    			clickableObjects.addObject(clickableObject);
		    		}
				}
			}
		}
		collisiongroups=null;
    	
		for(int c=starttile.getColumn(); c < starttile.getColumn() + this.getFragment().getColumns(); c++){
		    for(int r=starttile.getRow(); r < starttile.getRow() + this.getFragment().getRows(); r++){
		    	freeTilesList.remove(freeTiles[c][r]);
		    	freeTiles[c][r].free=false;
		    	nottestedTiles.remove(freeTiles[c][r]);
				int objektrow=r-starttile.getRow();
		    	int objektcolumn=c-starttile.getColumn();
		    	TMXLayer objektvorLayer=null;
		    	TMXLayer objektLayer=null;
		    	for(int l=0; l<this.getFragment().getLayers().size();l++){
		    		TMXLayer iteratorLayer  = this.getFragment().getLayers().get(l);
		    		if(objektcolumn>=iteratorLayer.getColumns() || objektrow >=iteratorLayer.getRows()){
		    			continue;
		    		}
		    		if(iteratorLayer.getName().contains("vor")){
		    			if(iteratorLayer.getName().endsWith("1")){
		    				objektvorLayer = objektvorLayers.get(0);
		    			}
		    			else{
		    				objektvorLayer = objektvorLayer.getNextLayer(objektvorLayers, vorlageLayer);
		    			}
		    			objektvorLayer.getTileAt(c, r).setAtlas(iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasColumn(), iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasRow());
		    			objektvorLayer.getTileAt(c, r).setGID(tileset);
		    		}
		    		else{
		    			if(objektLayer==null){
		    				for(TMXLayer maplayer : mapLayers){
	    						objektLayer = freeTiles[c][r].layer.putInLayer(mapLayers, vorlageLayer); 
	    						break;
		    				}
		    				 
		    			} 
		    			else{
		    				objektLayer = objektLayer.putInLayer(mapLayers, vorlageLayer);
		    			}
		    			objektLayer.getTileAt(c, r).setAtlas(iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasColumn(), iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasRow());
		    			objektLayer.getTileAt(c, r).setGID(tileset);
		    		}
		    	}	
		    }
		}
		Log.i("objectload", "successful");
	}
	public boolean placeSingleObject(int tileset,  TMXTile[][] freeTiles, ArrayList<TMXTile> freeTilesList, ArrayList<TMXTile> nottestedTiles){
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
    		randomStandort = SessionData.instance().randomInt.nextInt(nottestedTiles.size());
    		TMXTile starttile = nottestedTiles.get(randomStandort);
				if(starttile.getColumn() + this.getColumns() < vorlageLayer.getColumns() && starttile.getRow() + this.getRows() < vorlageLayer.getRows()){
					loop:
					for(int c=starttile.getColumn(); c < starttile.getColumn() + this.getColumns(); c++){
			    		for(int r=starttile.getRow(); r < starttile.getRow() + this.getRows(); r++){
			    			objektrow=r-starttile.getRow();
			    			objektcolumn=c-starttile.getColumn();
			    			TMXTile freetile = freeTiles[c][r];
			    			TMXTile objekttile = this.getTiles()[objektcolumn][objektrow];
			    			if(!objekttile.ebenen_difference.contains(freetile.ebene.getEbenenDif(starttile.ebene)) ||
					    			!objekttile.layer_difference.contains(Math.abs(mapLayers.indexOf(freetile.layer) - mapLayers.indexOf(starttile.layer))) ||
					    			!objekttile.boden_arten.contains(freetile.ebene.getBoden().getArt())|| !freeTilesList.contains(freetile)){
				    		nottestedTiles.remove(randomStandort);
				    		isPossible=false;
				    		break loop;
			    		}
			    		
			    	} 
				}
				if(isPossible==true){
	    			isPlaced=true;
	    			this.loadTMX();
	    			this.loadObjektIntoMap( starttile,tileset,freeTiles,freeTilesList,nottestedTiles); 
	    		}
			}
		}
    	return true;
	}
	
	public TMXTiledMap getFragment() {
		return fragment;
	}
	public void setFragment(TMXTiledMap fragment) {
		this.fragment = fragment;
	}
	public String getTmxPfad() {
		return tmxPfad;
	}
	public void setTmxPfad(String tmxPfad) {
		this.tmxPfad = tmxPfad;
	}
	public int getStartlayer() {
		return startlayer;
	}

	public void setStartlayer(int startlayer) {
		this.startlayer = startlayer;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public int getStartcolumn() {
		return startcolumn;
	}
	public void setStartcolumn(int startcolumn) {
		this.startcolumn = startcolumn;
	}
	public int getStartrow() {
		return startrow;
	}
	public void setStartrow(int startrow) {
		this.startrow = startrow;
	}
	public abstract void setupEvents();
	
	public void loadTMX(){
		if(!tmxPfad.contains(" ")){
			int chosenTMXFile = SessionData.instance().randomInt.nextInt(verfügbareTMXFiles);
			tmxPfad = tmxPfad + " " + chosenTMXFile;
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

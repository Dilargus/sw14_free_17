package com.adventureislands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

import com.datastructure.Boden;
import com.datastructure.Ebene;
import com.datastructure.objects.Cross;
import com.datastructure.objects.Exit;
import com.datastructure.objects.Objekt;
import com.datastructure.objects.Palme4x5;
import com.datastructure.objects.Rank;
import com.datastructure.objects.Ship;
import com.datastructure.objects.Objektgruppe;
import com.datastructure.objects.Shovel;
import com.datastructure.tmx.TMXException;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;
import com.datastructure.tmx.TMXTiledMapLoader;

public class Map implements
java.io.Serializable{
	private int art;
	private Map innerMap=null;
	private Map outerMap=null;
	private Ebene firstEbene;
	public transient Ship ship;
	private transient TMXObjectGroup collisionRectangles;
	private transient TMXObjectGroup clickableRectangles;
	private transient TMXLayer groundLayer;
	private transient TMXLayer vorlageLayer;
	private transient TMXTiledMap TMXmap;
	private List<Objekt> objectsOnMap = new ArrayList<Objekt>();
	public ArrayList<TMXLayer> mapLayers;
	public transient ArrayList<TMXLayer> objektvorLayers = new ArrayList<TMXLayer>();
	transient ArrayList<TMXObjectGroup>  rectanglegroups=null;
	transient ArrayList<TMXTile> nottestedTiles;
	private transient  ArrayList<TMXTile> freeTilesList;
	transient TMXTile[][] freeTiles;
	
	public Map(Map outermap,Context context, int art, int form, String path){
		boolean loadable=false;
		
		while(loadable==false){
			loadable=true;
			
			
			
			setUpLayer(path, context);
	    	
	    	if(form==SessionData.INSEL){
	    		if(outermap==null){
	    			this.art = SessionData.SANDINSEL;
	        		firstEbene = new Ebene(SessionData.WASSER,null,null,groundLayer,mapLayers,vorlageLayer,context);
	    		}
	    		else{
	    			this.art = SessionData.HÖHLE;
	        		firstEbene = new Ebene(SessionData.DUNKELHEIT,null,null,groundLayer,mapLayers,vorlageLayer,context);
	    		}
	    	}

	    	calcFreeTiles();
	    	
			
	    	TMXLayer objektvorLayer= new TMXLayer(vorlageLayer, "0");
			objektvorLayers.add(objektvorLayer);
			  
			if(outermap==null){
				innerMap = new Map(this,context,SessionData.HÖHLE,SessionData.INSEL,"cave");
				
				ship = new Ship(context,collisionRectangles,clickableRectangles,rectanglegroups,objektvorLayers,mapLayers,vorlageLayer,firstEbene);
		    	ship.loadTMX();
		    	TMXTile shipstart = firstEbene.getLayer().getTileAt(0, firstEbene.getLayer().getRows()-ship.getFragment().getRows()); 
		    	
		    	TMXmap.addTileSet(ship.getFragment().getTileSets().get(0));
		    	TMXmap.getTileSets().get(1).setFirstGID(2);
		    	
		    	ship.loadObjektIntoMap( shipstart, TMXmap.getTileSets().get(1).getFirstGID(), freeTiles, freeTilesList, nottestedTiles); 
		    	objectsOnMap.add(ship);
		    	
		    	Shovel shovel = new Shovel(context, collisionRectangles,clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
		    	loadable = shovel.placeSingleObject(TMXmap.getTileSets().get(0).getFirstGID(), freeTiles, freeTilesList, nottestedTiles);
		    	objectsOnMap.add(shovel);
		    	
		    	Cross cross = new Cross(context, collisionRectangles,clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
		    	loadable = cross.placeSingleObject(TMXmap.getTileSets().get(0).getFirstGID(), freeTiles, freeTilesList, nottestedTiles);
		    	objectsOnMap.add(cross);
		    	
		    	Exit exit = new Exit(context, collisionRectangles,clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
		    	loadable = exit.placeSingleObject(TMXmap.getTileSets().get(0).getFirstGID(), freeTiles, freeTilesList, nottestedTiles);
		    	objectsOnMap.add(exit);
				placeVariousObjects(context);
		    	

	    	
			}
			else{
				this.outerMap=outermap;
				Rank rank = new Rank(context, collisionRectangles,clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
				loadable = rank.placeSingleObject(TMXmap.getTileSets().get(0).getFirstGID(), freeTiles, freeTilesList, nottestedTiles);
				objectsOnMap.add(rank);
			}
			TMXLayer infront = new TMXLayer(vorlageLayer,"" + mapLayers.size());
	    	mapLayers.add(infront); 
		}
	}
	

	


	public ArrayList<Ebene> getAllEbenen(){
		ArrayList<Ebene> ebenen =  new ArrayList<Ebene>();
		ebenen.add(firstEbene);
		ebenen.addAll(getEbenen(firstEbene));
		return ebenen;
	}
	public ArrayList<Ebene> getEbenen(Ebene thisEbene){
		ArrayList<Ebene> ebenen =  new ArrayList<Ebene>();
		for(Ebene obereEbene : thisEbene.getInnerEbenen()){
			ebenen.add(obereEbene);
			ebenen.addAll(getEbenen(obereEbene));
		}
		return ebenen;
	}
	
	public boolean testIfPossible(ArrayList<TMXTile> tester,ArrayList<TMXTile> freeTiles,TMXLayer layer, TMXTile starttile,int columns, int rows)
	{
		if(freeTiles.containsAll(tester) ){
			for(TMXTile test : tester){
				if (test.layer != starttile.layer){
					return false;
				}
			}
		}
		return true;
	}


	public void calcFreeTiles(){
    	freeTiles = new TMXTile[vorlageLayer.getColumns()][vorlageLayer.getRows()];
    	for(int c=0;c<vorlageLayer.getColumns();c++){
    		for(int r=0;r<vorlageLayer.getRows();r++){
				for(int l=mapLayers.size()-1;l>=0;l--){
					if(mapLayers.get(l).getTileAt(c, r).free==true){
						freeTiles[c][r] = mapLayers.get(l).getTileAt(c, r);
						freeTilesList.add(freeTiles[c][r]);
						break;
					}
				}
			}
    	}
    	nottestedTiles = (ArrayList<TMXTile>) freeTilesList.clone();
	}
	
	public void placeVariousObjects(Context context){
		ArrayList<Objekt> possibleObjekte = new ArrayList<Objekt>();
    	int randomStandort;
		boolean isPossible=false;
		int objektrow;
		int objektcolumn;
		while(nottestedTiles.size() > (vorlageLayer.getColumns()*vorlageLayer.getRows())/2){
			  
    		Objektgruppe objektgruppe = new Objektgruppe(this.art, context, collisionRectangles,clickableRectangles,rectanglegroups,objektvorLayers,mapLayers,vorlageLayer);
    		randomStandort = SessionData.instance().randomInt.nextInt(nottestedTiles.size());
    		TMXTile starttile = nottestedTiles.get(randomStandort);
    		possibleObjekte.clear();
			for(Objekt objekt : objektgruppe.getObjekte()){
				isPossible=true;
				if(starttile.getColumn() + objekt.getColumns() < vorlageLayer.getColumns() && starttile.getRow() + objekt.getRows() < vorlageLayer.getRows()){
					loop:
					for(int c=starttile.getColumn(); c < starttile.getColumn() + objekt.getColumns(); c++){
			    		for(int r=starttile.getRow(); r < starttile.getRow() + objekt.getRows(); r++){
			    			objektrow=r-starttile.getRow();
			    			objektcolumn=c-starttile.getColumn();
			    			TMXTile freetile = freeTiles[c][r];
			    			TMXTile objekttile = objekt.getTiles()[objektcolumn][objektrow];
			    			if(!objekttile.ebenen_difference.contains(freetile.ebene.getEbenenDif(starttile.ebene)) ||
			    			!objekttile.layer_difference.contains(Math.abs(mapLayers.indexOf(freetile.layer) - mapLayers.indexOf(starttile.layer))) ||
			    			!objekttile.boden_arten.contains(freetile.ebene.getBoden().getArt())|| !freeTilesList.contains(freetile)){
				    			isPossible=false;
				    			break loop;
			    			}
			    		}
					}
					if(isPossible){
						possibleObjekte.add(objekt);
					}
				}
			}
			
			for(Objekt objekt : objektgruppe.getObjekte()){
				objekt=null;
			}
			objektgruppe=null;
		
			if(possibleObjekte.size()>0){
				int randomObjektIndex = SessionData.instance().randomInt.nextInt(possibleObjekte.size());
				Objekt erstelltesObjekt = possibleObjekte.get(randomObjektIndex);
				erstelltesObjekt.loadTMX();
				erstelltesObjekt.loadObjektIntoMap( starttile,TMXmap.getTileSets().get(0).getFirstGID(),freeTiles,freeTilesList,nottestedTiles); 
				objectsOnMap.add(erstelltesObjekt);
				erstelltesObjekt.setFragment(null);
			}
			else{
				nottestedTiles.remove(randomStandort);
			}
		}
	}
	
	
	public Map(Map tocopy,Map outermap,Context context, int art, int form, String path){
		setUpLayer(path, context);
		
		this.art = tocopy.art;
    	for(int i =this.mapLayers.size()-1; i<tocopy.mapLayers.size()-1;i++){
    		TMXLayer newLayer = mapLayers.get(i).getNextLayer(mapLayers, vorlageLayer);
    		newLayer.setName(tocopy.mapLayers.get(i+1).getName());
    		Log.i("AdventureLog reconstructing layers", newLayer.getName());
    	}
    	
        firstEbene = new Ebene(tocopy.firstEbene,null,mapLayers,groundLayer,vorlageLayer,context);
        
        calcFreeTiles();
    	TMXLayer objektvorLayer= new TMXLayer(vorlageLayer, "0");
		objektvorLayers.add(objektvorLayer);
		
		for(TMXLayer maplayer : mapLayers){
	    	for(int c=0;c<maplayer.getColumns();c++){
	    		for(int r=0;r<maplayer.getRows();r++){
	    				maplayer.getTileAt(c, r).layer = maplayer;
	    		}
	    	}
    	}
		
		copyVariousObjects(context, tocopy.objectsOnMap);
	}
	
	
	public void copyVariousObjects(Context context, List<Objekt> objects_to_copy){
		
		for(Objekt objekt_to_copy : objects_to_copy){
			reconstructObject(objekt_to_copy, context);
		}
		
		for(Objekt new_objekt : objectsOnMap){
			TMXTile starttile=null;
		
			for(int l=mapLayers.size()-1;l>=0;l--){
				if(Integer.parseInt(mapLayers.get(l).getName()) == new_objekt.getStartlayer()){
					starttile = mapLayers.get(l).getTileAt(new_objekt.getStartcolumn(), new_objekt.getStartrow());
					Log.i("starttile zugewiesen",""+mapLayers.get(l).getName());
					break;
				}
			}
			if(starttile==null){
				Log.e("ERROR", "tried to place an object on an unknown location");
				break;
			}
			new_objekt.loadTMX();
			if(new_objekt.getTmxPfad().startsWith("Ship")){
				TMXmap.addTileSet(new_objekt.getFragment().getTileSets().get(0));
		    	TMXmap.getTileSets().get(1).setFirstGID(2);
		    	Log.i("in ship",
						" startcolumn: "+ starttile.getColumn() + 
						" startrow: " + starttile.getRow() + 
						"startlayer: " +starttile.layer.getName());
				new_objekt.loadObjektIntoMap(starttile,TMXmap.getTileSets().get(1).getFirstGID(),freeTiles,freeTilesList,nottestedTiles); 
			}
			else{
				new_objekt.loadObjektIntoMap(starttile,TMXmap.getTileSets().get(0).getFirstGID(),freeTiles,freeTilesList,nottestedTiles); 
			}
			new_objekt.setFragment(null);
		}
		
	}
	
	public void reconstructObject(Objekt objekt_to_copy, Context context){
		
		if(objekt_to_copy.getTmxPfad().startsWith("Shovel")){
			Shovel shovel = new Shovel(context, collisionRectangles, clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
			objectsOnMap.add(shovel);
		}
		else if(objekt_to_copy.getTmxPfad().startsWith("Exit")){
			Exit exit = new Exit(context, collisionRectangles, clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
			objectsOnMap.add(exit);
		}
		else if(objekt_to_copy.getTmxPfad().startsWith("Cross")){
			Cross cross = new Cross(context, collisionRectangles, clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
			objectsOnMap.add(cross);
		}
		else if(objekt_to_copy.getTmxPfad().startsWith("Palme4x5")){
			Palme4x5 palme = new Palme4x5(context, collisionRectangles, clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
			objectsOnMap.add(palme);
		}
		else if(objekt_to_copy.getTmxPfad().startsWith("Rank")){
			Rank rank = new Rank(context, collisionRectangles,clickableRectangles, rectanglegroups, objektvorLayers, mapLayers, vorlageLayer);
			objectsOnMap.add(rank);
		}
		else if(objekt_to_copy.getTmxPfad().startsWith("Ship")){
			ship = new Ship(context,collisionRectangles,clickableRectangles,rectanglegroups,objektvorLayers,mapLayers,vorlageLayer,firstEbene);
			objectsOnMap.add(ship);
		}
		else{
			Log.i("error", objekt_to_copy.getTmxPfad()+" is none of my Object Classes");
		}
		objectsOnMap.get(objectsOnMap.size()-1).setStartcolumn(objekt_to_copy.getStartcolumn());
		objectsOnMap.get(objectsOnMap.size()-1).setStartrow(objekt_to_copy.getStartrow());
		objectsOnMap.get(objectsOnMap.size()-1).setStartlayer(objekt_to_copy.getStartlayer());
		Log.i("reconstructed objekt", objectsOnMap.get(objectsOnMap.size()-1).getTmxPfad() + 
				" startcolumn: "+ objectsOnMap.get(objectsOnMap.size()-1).getStartcolumn() + 
				" startrow: " + objectsOnMap.get(objectsOnMap.size()-1).getStartrow() + 
				"startlayer: " + objectsOnMap.get(objectsOnMap.size()-1).getStartlayer());
	}
	
	
	public void setUpLayer(String path, Context context){
		freeTilesList = new ArrayList<TMXTile>(); 
		loadMap loader = new loadMap(path, context); 
        try {
			TMXmap = loader.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        
        
    	if (TMXmap != null && (mapLayers = TMXmap.getLayers()) != null) {
			for (TMXLayer layer : mapLayers) {
				if ("1".equals(layer.getName())) {
					groundLayer= layer;
				}
				if ("vorlage".equals(layer.getName())) {
					vorlageLayer = layer;				
				}
			}
    	}
    	
    	mapLayers.remove(vorlageLayer);
    	
    	if ((rectanglegroups = TMXmap.getObjectGroups()) != null ) {
			for(TMXObjectGroup rectanglegroup : rectanglegroups) {
				if("Collision".equals(rectanglegroup.getName())){
					collisionRectangles = rectanglegroup;
				}
				if("Clickable".equals(rectanglegroup.getName())){
					clickableRectangles = rectanglegroup;
				}
			}
		}
    	rectanglegroups=null;
	}
	
	
	public Map getInnerMap() {
		return innerMap;
	}

	public void setInnerMap(Map innerMap) {
		this.innerMap = innerMap;
	}

	public Map getOuterMap() {
		return outerMap;
	}

	public void setOuterMap(Map outerMap) {
		this.outerMap = outerMap;
	}

	public ArrayList<TMXLayer> getMapLayers() {
		return mapLayers;
	}

	public void setMapLayers(ArrayList<TMXLayer> mapLayers) {
		this.mapLayers = mapLayers;
	}

	public TMXLayer getVorlageLayer() {
		return vorlageLayer;
	}

	public void setVorlageLayer(TMXLayer vorlageLayer) {
		this.vorlageLayer = vorlageLayer;
	}
	
	public TMXObjectGroup getCollisionRectangles() {
		return collisionRectangles;
	}

	public void setCollisionRectangles(TMXObjectGroup collisionRectangles) {
		this.collisionRectangles = collisionRectangles;
	}

	public TMXObjectGroup getClickableRectangles() {
		return clickableRectangles;
	}

	public void setClickableRectangles(TMXObjectGroup clickableRectangles) {
		this.clickableRectangles = clickableRectangles;
	}
	
	public ArrayList<TMXLayer> getObjektvorLayers() {
		return objektvorLayers;
	}

	public void setObjektvorLayers(ArrayList<TMXLayer> objektvorLayers) {
		this.objektvorLayers = objektvorLayers;
	}
	
	public Ebene getFirstEbene() {
		return firstEbene;
	}

	public void setFirstEbene(Ebene firstEbene) {
		this.firstEbene = firstEbene;
	}

	public TMXTiledMap getTMXmap() {
		return TMXmap;
	}

	public void setTMXmap(TMXTiledMap tMXmap) {
		TMXmap = tMXmap;
	}
	
	
}

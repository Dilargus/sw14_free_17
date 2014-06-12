package com.datastructure.objects;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.adventureislands.SessionData;
import com.datastructure.Ebene;
import com.datastructure.tmx.*;

public class Ship extends Objekt {


	private Ebene waterEbene;
	private Ebene hullEbene;
    private Ebene deckEbene;
    private boolean movable = true;
    private ArrayList<TMXObject> ship_collision_objects = new ArrayList<TMXObject>();
    private ArrayList<TMXObject> ship_clickable_objects = new ArrayList<TMXObject>();
    private  ArrayList<TMXTile> vorTiles = new ArrayList<TMXTile>();
	private ArrayList<TMXTile> nottestedTiles;
	private ArrayList<TMXTile> freeTilesList;
	private TMXTile[][] freeTiles;
    
    
	public Ship(Context context,TMXObjectGroup collisionObjects,TMXObjectGroup clickableObjects,ArrayList<TMXObjectGroup> collisiongroups,ArrayList<TMXLayer> objektvorLayers, ArrayList<TMXLayer> mapLayers,TMXLayer vorlageLayer, Ebene waterEbene) {
		super(context, collisionObjects,clickableObjects, collisiongroups,objektvorLayers, mapLayers, vorlageLayer);
		this.waterEbene=waterEbene;
		verfügbareTMXFiles = 1;
	    tmxPfad = "Ship";
	}

	@Override
	public void loadObjektIntoMap(TMXTile starttile, int tileset, TMXTile[][] freeTiles, ArrayList<TMXTile> freeTilesList, ArrayList<TMXTile> nottestedTiles){
		Log.i("in ship",
				" startcolumn: "+ starttile.getColumn() + 
				" startrow: " + starttile.getRow() + 
				"startlayer: " +starttile.layer.getName());
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
		    			ship_collision_objects.add(collisionObject);
		    		}
				}
				if("Clickable".equals(collisiongroup.getName())){
					for(TMXObject clickableObject : collisiongroup.getObjects()){
						clickableObject.setZugehoerigesObjekt(this);
						clickableObject.setX(starttile.getWidth()*starttile.getColumn()+clickableObject.getX());
						clickableObject.setY(starttile.getHeight()*starttile.getRow()+clickableObject.getY());
		    			clickableObjects.addObject(clickableObject);
		    			ship_clickable_objects.add(clickableObject);
		    		}
				}
			}
		}
		collisiongroups=null;
    	
		
		ArrayList<TMXTile> hullRahmen= new ArrayList<TMXTile>();
		ArrayList<TMXTile> hullinnerTiles = new ArrayList<TMXTile>();
		ArrayList<TMXTile> deckRahmen= new ArrayList<TMXTile>();
		ArrayList<TMXTile> deckinnerTiles = new ArrayList<TMXTile>();
		Ebene wassersandEbene = waterEbene.getInnerEbenen().get(0);
		Ebene sandEbene = wassersandEbene.getInnerEbenen().get(0);
		Ebene mountainEbene = sandEbene.getInnerEbenen().get(0);
    	TMXLayer hullLayer = sandEbene.getLayer();
    	TMXLayer deckLayer = mountainEbene.getLayer();
		
		
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
		    		if(iteratorLayer.getTileAt(objektcolumn, objektrow).getGID()!=0){
		    			if(iteratorLayer.getName().contains("vor")){
			    			if(iteratorLayer.getName().endsWith("1")){
			    				objektvorLayer = objektvorLayers.get(0);
			    			}
			    			else{
			    				objektvorLayer = objektvorLayer.getNextLayer(objektvorLayers, vorlageLayer);
			    			}
			    			TMXTile tile = objektvorLayer.getTileAt(c, r);
			    			tile.setAtlas(iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasColumn(), iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasRow());
			    			tile.setGID(tileset);
			    			vorTiles.add(tile);
			    		}
			    		else{
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
			    			
			    			objektLayer.getTileAt(c, r).setAtlas(iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasColumn(), iteratorLayer.getTileAt(objektcolumn, objektrow).getAtlasRow());
			    			objektLayer.getTileAt(c, r).setGID(tileset); 
			    			
			    		}
		    		}
		    		
		    	}	
		    }
		}
		hullEbene = new Ebene(SessionData.SCHIFFSHUELLE,hullRahmen,hullinnerTiles,wassersandEbene,mapLayers,hullLayer,vorlageLayer,context);
		wassersandEbene.addEbeneToInnerEbenen(hullEbene);
		//hullEbene.giveWalkability(waterEbene.getEbenenindex());
		deckEbene = new Ebene(SessionData.SCHIFFSDECK,deckRahmen,deckinnerTiles,hullEbene,mapLayers,deckLayer,vorlageLayer,context);
		hullEbene.addEbeneToInnerEbenen(deckEbene);
		Log.i("objectload", "successful");
	}
	public void setupEvents(){
		
	}
	
	public void moveShipX(int columns){
		if(columns>0){
			while(movable==true && columns!=0){
				int result;
				result = hullEbene.moveEbeneX(true);
				if(result==1){
					return;
				}
				else if(result==2){
					movable=false;
				}
				deckEbene.moveEbeneX(true);
				
				for(TMXObject object : ship_clickable_objects){
					object.setX(object.getX()+32);
				}
				for(TMXObject object : ship_collision_objects){
					object.setX(object.getX()+32);
				}
				for(TMXLayer vorlayer : objektvorLayers){
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
				
				for(TMXObject object : ship_clickable_objects){
					object.setX(object.getX()-32);
				}
				for(TMXObject object : ship_collision_objects){
					object.setX(object.getX()-32);
				}
				for(TMXLayer vorlayer : objektvorLayers){
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
				
				for(TMXObject object : ship_clickable_objects){
					object.setY(object.getY()+32);
				}
				for(TMXObject object : ship_collision_objects){
					object.setY(object.getY()+32);
				}
				for(TMXLayer vorlayer : objektvorLayers){
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
				
				for(TMXObject object : ship_clickable_objects){
					object.setY(object.getY()-32);
				}
				for(TMXObject object : ship_collision_objects){
					object.setY(object.getY()-32);
				}
				for(TMXLayer vorlayer : objektvorLayers){
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
	
	public void calcFreeTiles(){
    	freeTiles = new TMXTile[vorlageLayer.getColumns()][vorlageLayer.getRows()];
    	for(int c=0;c<vorlageLayer.getColumns();c++){
    		for(int r=0;r<vorlageLayer.getRows();r++){
				for(int l=mapLayers.size()-1;l>=0;l--){
					if(mapLayers.get(l).getTileAt(c, r).ebene==hullEbene || mapLayers.get(l).getTileAt(c, r).ebene==deckEbene){
						freeTiles[c][r] = mapLayers.get(l).getTileAt(c, r);
						freeTilesList.add(freeTiles[c][r]);
						break;
					}
				}
			}
    	}
    	nottestedTiles = (ArrayList<TMXTile>) freeTilesList.clone();
	}
    
}
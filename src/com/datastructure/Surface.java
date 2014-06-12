package com.datastructure;

import java.util.ArrayList;
import java.util.List;
import com.adventureislands.Map;
import com.adventureislands.SessionData;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXTile;
import android.util.Log;

public class Surface implements
java.io.Serializable {
	public ArrayList<TMXTile> border = new ArrayList<TMXTile>();
	public ArrayList<TMXTile> inner_tiles = new ArrayList<TMXTile>();
	public ArrayList<TMXTile> all_inner_tiles = new ArrayList<TMXTile>();
	public TMXLayer layer;
	public Surface outer_surface;
	public List<Surface> inner_surfaces = new ArrayList<Surface>();
	public SurfaceTexture surface_texture;
	public int surface_index;
	
	transient private Map map;
	
	public Surface(int type,TMXTile start_location, Surface outer_surface,TMXLayer ground_layer, Map map) {
		surface_index = new Integer(map.surface_counter);
		map.surface_counter++;
		surface_texture = new SurfaceTexture (type);
		this.map = map;
		this.layer = ground_layer;  
		this.outer_surface = outer_surface;
		
		if(outer_surface!=null){ 
			if(outer_surface.surface_texture.multi_level_surface){
				for(int index=0;index<outer_surface.inner_tiles.size();index++){
					TMXTile äußeresInnerTile = outer_surface.inner_tiles.get(index);
					TMXTile thisInnerTile = layer.getTileAt(äußeresInnerTile.getColumn(), äußeresInnerTile.getRow()-1);
					inner_tiles.add(thisInnerTile);
					all_inner_tiles.add(thisInnerTile);
					fillInnerTile(thisInnerTile);
				}
				outer_surface.inner_tiles.clear();
				for(int index=0;index<outer_surface.border.size();index++){
					TMXTile äußeresRahmentile = outer_surface.border.get(index);
					TMXTile thisRahmentile;
					thisRahmentile = layer.getTileAt(äußeresRahmentile.getColumn(), äußeresRahmentile.getRow()-1);
					
					addtoRahmen(border.size(), thisRahmentile);
				}
				fillRahmen(true);
			}
			else{
				int size_modificator;
				if(surface_texture.random_size){
					size_modificator = SessionData.instance().randomInt.nextInt((int)(outer_surface.inner_tiles.size()*surface_texture.max_size)); 
				}
				else{
					size_modificator = (int)(outer_surface.inner_tiles.size()*surface_texture.max_size);
				}
				erstelleRahmen(start_location,size_modificator);
				closeGaps();
				fillRahmen(true);
			}
		}
		else{
			includeEverything();
		} 
		
		ArrayList<Integer> possible_surfaces = surface_texture.next_surface_textures;
		if(possible_surfaces.size() > 0){
			int possible_surface_index = SessionData.instance().randomInt.nextInt(possible_surfaces.size());
			int next_surface = possible_surfaces.get(possible_surface_index);
			boolean new_surface_possible = true;
			if(outer_surface != null){ 
				while(new_surface_possible == true){
					new_surface_possible = checkAndCreateSurface(next_surface);
					possible_surface_index = SessionData.instance().randomInt.nextInt(possible_surfaces.size());
					next_surface = possible_surfaces.get(possible_surface_index);
				}
			} 
			else{
				TMXLayer nextLayer = layer.getNextLayer(map.map_layers, map.sample_layer);  
				Surface innereebene = new Surface(next_surface,layer.getTileAt(layer.getColumns()/2, layer.getRows()/2),this, nextLayer, map);
				inner_surfaces.add(innereebene);
			}
		}
	}
	
	public void includeEverything(){
		for(int c=0;c<layer.getColumns()-1;c++){
			addtoRahmen(border.size(), layer.getTileAt(c, 0));
			fillInnerTile(layer.getTileAt(c, 0));
		}
		for(int r=0;r<layer.getRows()-1;r++){ 
			addtoRahmen(border.size(), layer.getTileAt(layer.getColumns()-1, r));
			fillInnerTile(layer.getTileAt(layer.getColumns()-1, r));
		}
		for(int c=layer.getColumns()-1;c>0;c--){
			addtoRahmen(border.size(), layer.getTileAt(c, layer.getRows()-1));
			fillInnerTile(layer.getTileAt(c, layer.getRows()-1));
		}
		for(int r=layer.getRows()-1;r>0;r--){
			addtoRahmen(border.size(), layer.getTileAt(0, r));
			fillInnerTile(layer.getTileAt(0, r));
		}
		for(int c=1;c<layer.getColumns()-1;c++){
			for(int r=1;r<layer.getRows()-1;r++){ 
				TMXTile tile = layer.getTileAt(c, r);
				if(surface_texture.type == SessionData.WASSER){
					if(map.type == SessionData.ISLAND){
						if(c >12 && c < layer.getColumns() - 12){
							inner_tiles.add(tile);
						} 
					}
					else if(map.type == SessionData.TESTISLAND){
						if(		c > layer.getColumns() / 2 - 2 && c < layer.getColumns() / 2 + 2 &&
								r > layer.getRows() / 2 - 2 && r < layer.getRows() / 2 + 2){
							inner_tiles.add(tile);
						}
					}
				}
				else{
					inner_tiles.add(tile);
				}
				all_inner_tiles.add(tile);
				fillInnerTile(tile);
    		}
		}
	}
	
	public Surface(int art,ArrayList<TMXTile> feinerRahmen,ArrayList<TMXTile> innerTiles,Surface äußereEbene,TMXLayer groundLayer, Map map) {
		surface_index = new Integer(map.surface_counter);
		map.surface_counter++;
		this.surface_texture = new SurfaceTexture (art);
		this.map=map;
		this.layer= groundLayer;  
		this.outer_surface = äußereEbene;
		this.border = feinerRahmen;
		this.inner_tiles = innerTiles;
		this.all_inner_tiles = new ArrayList<TMXTile>(innerTiles);
		for(TMXTile rahmentile : feinerRahmen){
			äußereEbene.inner_tiles.remove(äußereEbene.layer.getTileAt(rahmentile.getColumn(), rahmentile.getRow()));
			äußereEbene.border.remove(äußereEbene.layer.getTileAt(rahmentile.getColumn(), rahmentile.getRow()));
		}
		fillRahmen(false);
		
		for(TMXTile innertile : innerTiles){
			äußereEbene.inner_tiles.remove(äußereEbene.layer.getTileAt(innertile.getColumn(), innertile.getRow()));
			äußereEbene.border.remove(äußereEbene.layer.getTileAt(innertile.getColumn(), innertile.getRow()));
			innertile.resetProperties(); 
			innertile.surface = this;
		}
	}
	
	

	
	

	
	
	public boolean checkAndCreateSurface(int type){
		
		
		boolean possible = false;
		ArrayList<TMXTile> tester = null;
		int index = 0;
		if(inner_tiles.size() > 100){ 
			int random = SessionData.instance().randomInt.nextInt(inner_tiles.size() - 2) + 2;
			for(index = random + 1; index != random; index++){
				if(index == inner_tiles.size()){
					index = 0;
				}
				tester = layer.fünfundzwanzigerNachbarschaft(inner_tiles.get(index));
				if(tester.isEmpty()){
					continue;
				}
				if(inner_tiles.containsAll(tester)){
					possible=true; 
					break;
				} 
			} 
			
		}
		if(possible){
			TMXLayer next_layer = layer.getNextLayer(map.map_layers, map.sample_layer);  
			
			Surface inner_surface = new Surface(type, inner_tiles.get(index), this, next_layer, map);
			inner_surfaces.add(inner_surface);
		}
		else if (surface_texture.multi_level_surface){
			TMXLayer next_layer = layer.getNextLayer(map.map_layers, map.sample_layer);  
		 
			Surface inner_surface = new Surface(type, null, this, next_layer, map);
			inner_surfaces.add(inner_surface);
		}
		return possible;
	}
	

	public void erstelleRahmen(TMXTile start_location, int size_modificator){
		ArrayList <TMXTile> basic_border = layer.neunerinOrderNachbarschaft(start_location);
		for(TMXTile basic_border_tile : basic_border){
			if(basic_border.indexOf(basic_border_tile)!=0){
				addtoRahmen(border.size(),basic_border_tile);	
			}
			else{
				inner_tiles.add(basic_border_tile);
				all_inner_tiles.add(basic_border_tile);
				if(outer_surface!=null){
					outer_surface.inner_tiles.remove(outer_surface.layer.getTileAt(basic_border_tile.getColumn(), basic_border_tile.getRow()));
				}
				fillInnerTile(basic_border_tile);
			}
			
		}
		basic_border=null;
		ArrayList<TMXTile> andereRahmen = new ArrayList<TMXTile>();
		for(Surface EbenegleicherHöhe : outer_surface.inner_surfaces){
			if(!EbenegleicherHöhe.equals(this)){
				andereRahmen.addAll(EbenegleicherHöhe.border);
			}
		}
		 
		while(size_modificator>0){
			TMXTile randomTile=null;
			
			ArrayList<Integer> randomIndezes = getRandomIndezes(border);
			loop:
			while(randomIndezes.size()>0){
				int randomIndex= SessionData.instance().randomInt.nextInt(randomIndezes.size());
				int index = randomIndezes.get(randomIndex);
				randomIndezes.remove(randomIndex);
			
				if(border.get(index).getColumn()>1 && border.get(index).getRow()>1){
					if(border.get(index).getColumn()<layer.getColumns()-1 && border.get(index).getRow()<layer.getRows()-1){
						int vierersum=0;
						int angrenzend=0;
						int neunersum=0;
						int viererdirection=0;
						int neunerdirection=0;
						ArrayList<TMXTile> neunertiles = layer.neunerNachbarschaft(border.get(index));
						ArrayList<TMXTile> vierertiles = layer.viererNachbarschaft(border.get(index));
						ArrayList<TMXTile> fünfundzwanzigertiles = layer.fünfundzwanzigerNachbarschaft(border.get(index));
						for(int i=0; i<fünfundzwanzigertiles.size();i++){
							if(andereRahmen.contains(fünfundzwanzigertiles.get(i))){
								continue loop;
							}
							
							if(i<neunertiles.size()){
								if((!border.contains(neunertiles.get(i))) && (!inner_tiles.contains(neunertiles.get(i)) && outer_surface.inner_tiles.contains(outer_surface.layer.getTileAt(neunertiles.get(i).getColumn(), neunertiles.get(i).getRow())))){
									neunersum++;
									neunerdirection = i+1;
								}
								if(i<vierertiles.size()){
									if((!border.contains(vierertiles.get(i))) && (!inner_tiles.contains(vierertiles.get(i))&&outer_surface.inner_tiles.contains(outer_surface.layer.getTileAt(vierertiles.get(i).getColumn(), vierertiles.get(i).getRow())))){
										vierersum++;
										viererdirection = i+1;
									}
									if(inner_tiles.contains(vierertiles.get(i))){
										angrenzend++;
									}
								}
							}
						}
						if(vierersum==1 && neunersum==3 && angrenzend==1){
							randomTile = border.get(index);
							randomTile.direction = viererdirection;
							randomTile.index= index;
							//randomTile = new directedTile(border.get(index), viererdirection,index);
							break;
						}
						else if (vierersum==0 && neunersum==1 && angrenzend==2){
							randomTile = border.get(index);
							randomTile.direction = neunerdirection;
							randomTile.index = index;
							//randomTile = new directedTile(border.get(index), neunerdirection,index);
							break;
						}
						neunertiles=null;
						vierertiles=null;
						fünfundzwanzigertiles=null;
					}
				}
			}
			
			if(randomTile==null){
				break;
			} 
			switch(randomTile.direction){
				case SessionData.UP:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()-1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn(), randomTile.getRow()-1));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()-1));
				break;
				case SessionData.DOWN:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()+1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn(), randomTile.getRow()+1));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()+1));
				break;
				case SessionData.LEFT:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()+1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()-1));
				break;
				case SessionData.RIGHT:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()-1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()+1));
				break;	
				case SessionData.DOWNLEFT:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()+1));
				break;	
				case SessionData.LEFTUP:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()-1));
				break;
				case SessionData.UPRIGHT:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()-1));
				break;
				case SessionData.RIGHTDOWN:
					inner_tiles.add(randomTile);
					all_inner_tiles.add(randomTile);
					border.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()+1));
				break;
			}
			fillInnerTile(inner_tiles.get(inner_tiles.size()-1));
			size_modificator--;
		}	
	} 
	
	public ArrayList<Integer> getRandomIndezes(ArrayList<TMXTile> tiles){
		ArrayList<Integer> randomIndezes = new ArrayList<Integer>();
		for(int i = 0; i<tiles.size();i++){
			randomIndezes.add(i);
		}
		for(int i = 0; i<randomIndezes.size();i++){
			int indexsaver = randomIndezes.get(i);
			int randomindex = SessionData.instance().randomInt.nextInt(randomIndezes.size());
			randomIndezes.set(i, randomIndezes.get(randomindex));
			randomIndezes.set(randomindex, indexsaver);
		}
		return randomIndezes;
	}
	
	public void closeGaps(){
		for(int index=0;index<border.size();index++){
			int gapsum=0;
			ArrayList<TMXTile> gapList = new ArrayList<TMXTile>();
			ArrayList<TMXTile> vierertiles = layer.viererNachbarschaft(border.get(index));
			for(int i=0; i<vierertiles.size();i++){
				if(border.contains(vierertiles.get(i))){
					gapsum++;
					gapList.add(vierertiles.get(i));
				}
			}
			if(gapsum==3){
				for(TMXTile gapTile : gapList){
					if(border.indexOf(gapTile) > index + surface_texture.gap_size ){
						ArrayList<TMXTile> delete = new ArrayList<TMXTile>();
						for(int t= index+1; t<border.indexOf(gapTile);t++){
							delete.add(border.get(t));
						}
						if(delete.size()>border.size()/2){ 
							delete.clear();
							for(int t= border.indexOf(gapTile)+1; t<border.size();t++){
								delete.add(border.get(t));
							}
							for(int t=0; t<index;t++){
								delete.add(border.get(t)); 
							}
						}
						border.removeAll(delete);
						for(int m=0;m<delete.size();m++){
							delete = findLoseTiles(delete.get(m),delete);
							inner_tiles.add(delete.get(m));
							all_inner_tiles.add(delete.get(m));
							if(outer_surface!=null){
								outer_surface.inner_tiles.remove(outer_surface.layer.getTileAt(delete.get(m).getColumn(), delete.get(m).getRow()));
							}
							fillInnerTile(delete.get(m));
						}
						delete = null;
					}
				} 
			}
		}
	}
	
	public void addtoRahmen(int stelle,TMXTile tile){
		border.add(stelle,tile); 
		if(outer_surface!=null){
			outer_surface.inner_tiles.remove(outer_surface.layer.getTileAt(tile.getColumn(), tile.getRow()));
		}
	} 
	
	public ArrayList<TMXTile> findLoseTiles(TMXTile losestile,ArrayList<TMXTile> losetiles){
		ArrayList<TMXTile> kreuzFeld = layer.neunerNachbarschaft(losestile);
		for(TMXTile feld : kreuzFeld){
			if(outer_surface.inner_tiles.contains(outer_surface.layer.getTileAt(feld.getColumn(), feld.getRow()))){
				if(outer_surface!=null){
					outer_surface.inner_tiles.remove(outer_surface.layer.getTileAt(feld.getColumn(), feld.getRow()));
				}
				losetiles.add(feld);
				losetiles = findLoseTiles(feld,losetiles);
			}
		}
		return losetiles;
	}
	
	public void fillInnerTile(TMXTile tile){
		if(!surface_texture.multi_level_surface){
			tile.setAtlas(surface_texture.inner_tile_texture.x, surface_texture.inner_tile_texture.y);
		}
		
		for(int i=0;i<map.map_layers.indexOf(layer);i++){
			TMXTile tile_under = map.map_layers.get(i).getTileAt(tile.getColumn(), tile.getRow());
			tile_under.free=false;
			tile_under.on_highest_surface=false;
		}

		tile.resetProperties();
		tile.setGID(map.tmx_map.getTileSets().get(0).getFirstGID());
		tile.surface = this;
		tile.free=true;
		tile.on_highest_surface=true;
		
	}
	
	
	
	public void fillRahmen(boolean draw){
		TMXTile following_tile;
		TMXTile previous_tile;
		
		for(TMXTile border_tile : border){
			border_tile.resetProperties();
			if(border.size() - 1 == border.indexOf(border_tile)){
				following_tile= border.get(0);
			}
			else{
				following_tile = border.get(border.indexOf(border_tile) + 1);
			}
				
			if(0 == border.indexOf(border_tile)){
				previous_tile = border.get(border.size() - 1);	
			}
			else{
				previous_tile = border.get(border.indexOf(border_tile) - 1);
			}
			
			if(draw==true){
				//linke obere ecke 
				if(border_tile.getColumn()<following_tile.getColumn() && border_tile.getRow()==following_tile.getRow() && border_tile.getColumn()==previous_tile.getColumn() && border_tile.getRow()<previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_LEFTTOP);
				}
				//oben gerade
				else if(border_tile.getColumn()<following_tile.getColumn() && border_tile.getRow()==following_tile.getRow() && border_tile.getColumn()>previous_tile.getColumn() && border_tile.getRow()==previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_TOP);
				}
				//rechte obere ecke
				else if(border_tile.getColumn()==following_tile.getColumn() && border_tile.getRow()<following_tile.getRow() && border_tile.getColumn()>previous_tile.getColumn() && border_tile.getRow()==previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_TOPRIGHT);
				}
				//rechts gerade
				else if(border_tile.getColumn()==following_tile.getColumn() && border_tile.getRow()<following_tile.getRow() && border_tile.getColumn()==previous_tile.getColumn() && border_tile.getRow()>previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_RIGHT);
				}
				//rechte untere ecke
				else if(border_tile.getColumn()>following_tile.getColumn() && border_tile.getRow()==following_tile.getRow() && border_tile.getColumn()==previous_tile.getColumn() && border_tile.getRow()>previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_RIGHTBOTTOM);
				}
				//unten gerade
				else if(border_tile.getColumn()>following_tile.getColumn() && border_tile.getRow()==following_tile.getRow() && border_tile.getColumn()<previous_tile.getColumn() && border_tile.getRow()==previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_BOTTOM);
				}
				//linke untere ecke
				else if(border_tile.getColumn()==following_tile.getColumn() && border_tile.getRow()>following_tile.getRow() && border_tile.getColumn()<previous_tile.getColumn() && border_tile.getRow()==previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_BOTTOMLEFT);
				}
				//links gerade
				else if(border_tile.getColumn()==following_tile.getColumn() && border_tile.getRow()>following_tile.getRow() && border_tile.getColumn()==previous_tile.getColumn() && border_tile.getRow()<previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.NINE_LEFT);
				}
				//innere ecke links nach oben
				else if(border_tile.getColumn()==following_tile.getColumn() && border_tile.getRow()>following_tile.getRow() && border_tile.getColumn()>previous_tile.getColumn() && border_tile.getRow()==previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.FOUR_LEFTTOP);
				}
				//innere ecke oben nach rechts
				else if(border_tile.getColumn()<following_tile.getColumn() && border_tile.getRow()==following_tile.getRow() && border_tile.getColumn()==previous_tile.getColumn() && border_tile.getRow()>previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.FOUR_TOPRIGHT);
				}
				//innere ecke rechts nach unten
				else if(border_tile.getColumn()==following_tile.getColumn() && border_tile.getRow()<following_tile.getRow() && border_tile.getColumn()<previous_tile.getColumn() && border_tile.getRow()==previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.FOUR_RIGHTBOTTOM);
				}
				//innere ecke unten nach links
				else if(border_tile.getColumn()>following_tile.getColumn() && border_tile.getRow()==following_tile.getRow() && border_tile.getColumn()==previous_tile.getColumn() && border_tile.getRow()<previous_tile.getRow()){
					modifyRahmenTile(border_tile, SessionData.FOUR_BOTTOMLEFT);
				}
				border_tile.setGID(1);
			}
			
			for(int i=0;i<map.map_layers.indexOf(layer);i++){
				TMXTile tile_under = map.map_layers.get(i).getTileAt(border_tile.getColumn(), border_tile.getRow());
				tile_under.free=false;
				tile_under.on_highest_surface=false;
			}
			
			
			border_tile.surface = this;
			border_tile.free=true;
			border_tile.on_highest_surface=true;
			
			if(surface_texture.accessible){
				ArrayList<Integer> prop = new ArrayList<Integer>();
				prop.add(outer_surface.surface_index);
				border_tile.joints.addAll(prop);
			}
			
			
		}
	}
	
	public void giveWalkability(int ebenenindex){
		for(TMXTile randstück : border){
			ArrayList<Integer> prop = randstück.joints;
			prop.add(ebenenindex);
		}
	}
	
	public void modifyRahmenTile(TMXTile border_tile, int direction){
			border_tile.setAtlas(surface_texture.border_texture.get(direction).x, surface_texture.border_texture.get(direction).y);
			border_tile.direction = direction;
	}
	
	private boolean ebeneCollides(TMXTile movedTile){
		ArrayList<Surface> ebenen = map.getAllSurfaces();
		for(int layerindex = map.map_layers.indexOf(layer); layerindex>0; layerindex--){
			for(Surface ebene : ebenen){
				if(!ebene.equals(this)){
					TMXTile tile_to_check = map.map_layers.get(layerindex).getTileAt(movedTile.getColumn(), movedTile.getRow());
					if(ebene.border.contains(tile_to_check)){
						return true;
					}
				} 
				
			}
		}
		return false;
	}
	
	private void moveTile(TMXTile fromTile, TMXTile toTile){
		toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
		if(!fromTile.joints.isEmpty())
			toTile.joints.addAll(fromTile.joints);
		toTile.setGID(fromTile.getGID());
		toTile.free = fromTile.free;
		toTile.on_highest_surface = fromTile.on_highest_surface;
		
		fromTile.free=true;
		fromTile.on_highest_surface=true;
		fromTile.setEmpty();	
	}
	
	public int moveEbeneX(boolean positive){
		boolean stop = false;
		if(!positive){
			
			for(TMXTile tile : border){
				if(tile.getColumn()==0){
					return 1;
				}
			}
			for(int c=0;c<layer.getColumns();c++){
				for(int r=0;r<layer.getRows();r++){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(border.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()-1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						border.set(border.indexOf(currentTile), movedTile);
					}
					
					
					if(inner_tiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()-1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						inner_tiles.set(inner_tiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		else{
			for(TMXTile tile : border){
				if(tile.getColumn()==layer.getColumns()-1){
					return 1;
				}
			}
			
			for(int c=layer.getColumns()-1;c>=0;c--){
				for(int r=layer.getRows()-1;r>=0;r--){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(border.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()+1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						border.set(border.indexOf(currentTile), movedTile);
					}
					
					
					if(inner_tiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()+1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						inner_tiles.set(inner_tiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		return 0;
	}
	
	public int moveEbeneY(boolean positive){
		boolean stop = false;
		if(!positive){
			
			for(TMXTile tile : border){
				if(tile.getRow()==0){
					return 1;
				}
			}
			for(int c=0;c<layer.getColumns();c++){
				for(int r=0;r<layer.getRows();r++){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(border.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()-1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						border.set(border.indexOf(currentTile), movedTile);
					}
					
					
					if(inner_tiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()-1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						inner_tiles.set(inner_tiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		else{
			for(TMXTile tile : border){
				if(tile.getColumn()==layer.getRows()-1){
					return 1;
				}
			}
			
			for(int c=layer.getColumns()-1;c>=0;c--){
				for(int r=layer.getRows()-1;r>=0;r--){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(border.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()+1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						border.set(border.indexOf(currentTile), movedTile);
					}
					
					
					if(inner_tiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()+1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						inner_tiles.set(inner_tiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		return 0;
	}
	
	public int getEbenenDif(Surface b){
		Surface counterEbene = this;
		while (counterEbene.outer_surface!=null){
			counterEbene = counterEbene.outer_surface;
		}
		return Math.abs(getHops(0,counterEbene,this)-getHops(0,counterEbene,b));
	}
	
	public int getHops(int hops, Surface actualEbene, Surface searchedEbene){
		int result;
		if(actualEbene==searchedEbene){
			return hops;
		}
		if( actualEbene.inner_surfaces.isEmpty()){
			return -1;
		}
		for(Surface innereEbene : actualEbene.inner_surfaces){
			result = getHops(hops+1,innereEbene,searchedEbene);
			if(result!=-1){
				return result;
			}
		}
		return -1;
		 
	}
	
	public Surface(Surface downloaded_surface, Surface outer_surface, TMXLayer layer, Map map) {
		this.surface_index = downloaded_surface.surface_index;
		this.surface_texture = downloaded_surface.surface_texture;
		this.map=map;
		this.layer= layer;  
		this.outer_surface = outer_surface;
		this.border = downloaded_surface.border;
		this.inner_tiles = downloaded_surface.inner_tiles;
		this.all_inner_tiles=downloaded_surface.all_inner_tiles;
		
		copyTMXTiles(downloaded_surface);
		
		TMXLayer inner_layer = null;
		for(Surface downloaded_inner_surface : downloaded_surface.inner_surfaces){
			for(TMXLayer map_layer : map.map_layers){
				if(downloaded_inner_surface.layer.getName().equals(map_layer.getName())){
					inner_layer = map_layer;
					break;
				}
			}
			Surface new_surface = new Surface(downloaded_inner_surface, this, inner_layer, map);
			inner_surfaces.add(new_surface);
		}
	}

	public void copyTMXTiles(Surface downloaded_surface){
		for(int c = 0; c < layer.getColumns(); c++){
    		for(int r = 0; r < layer.getRows(); r++){
    			TMXTile downloaded_tile = downloaded_surface.layer.getTileAt(c, r);
    			TMXTile new_tile = layer.getTileAt(c, r);
    			
    			if(border.contains(downloaded_tile) || all_inner_tiles.contains(downloaded_tile)){
    				new_tile.setGID(downloaded_tile.getGID());
        			new_tile.setAtlas(downloaded_tile.getAtlasColumn(), downloaded_tile.getAtlasRow());
        			if(!downloaded_tile.joints.isEmpty()){
        				new_tile.joints.addAll(downloaded_tile.joints);
        			}
	    			new_tile.surface=this;
	    			new_tile.free=true;
	    			new_tile.on_highest_surface=true; 
					
					for(int i = 0; i < map.map_layers.indexOf(layer); i++){
						TMXTile tile_under = map.map_layers.get(i).getTileAt(new_tile.getColumn(), new_tile.getRow());
						tile_under.free=false;
						tile_under.on_highest_surface=false;
					}
					
	    			if(border.contains(downloaded_tile)){
	    				border.set(downloaded_surface.border.indexOf(downloaded_tile), new_tile);
	    			}
	    			if(inner_tiles.contains(downloaded_tile)){
	    				inner_tiles.set(downloaded_surface.inner_tiles.indexOf(downloaded_tile), new_tile);
	    			}
    			}
			
    			
    		}
		}

	}
}

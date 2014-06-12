package com.datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adventureislands.SessionData;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXTile;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

public class Ebene implements
java.io.Serializable {
	private ArrayList<TMXTile> feinerRahmen = new ArrayList<TMXTile>();
	private ArrayList<TMXTile> innerTiles = new ArrayList<TMXTile>();
	private ArrayList<TMXTile> allInnerTiles = new ArrayList<TMXTile>();
	private TMXLayer layer;
	private ArrayList<TMXLayer> mapLayers;
	
	private Ebene �u�ereEbene;
	private List<Ebene> innerEbenen = new ArrayList<Ebene>();
	private Boden boden;
	public int ebenenindex;
	

	public Ebene(int art,TMXTile startpunkt, Ebene �u�ereEbene,TMXLayer groundLayer, ArrayList<TMXLayer> mapLayers,TMXLayer vorlageLayer, Context context) {
		ebenenindex = new Integer(SessionData.instance().ebenencounter);
		SessionData.instance().ebenencounter++;
		boden = new Boden (art);
		this.mapLayers=mapLayers;
		this.layer= groundLayer;  
		this.�u�ereEbene = �u�ereEbene;
		
		if(�u�ereEbene!=null){ 
			if(�u�ereEbene.getBoden().isMehrst�ckig()){
				for(int index=0;index<�u�ereEbene.innerTiles.size();index++){
					TMXTile �u�eresInnerTile = �u�ereEbene.innerTiles.get(index);
					TMXTile thisInnerTile = layer.getTileAt(�u�eresInnerTile.getColumn(), �u�eresInnerTile.getRow()-1);
					innerTiles.add(thisInnerTile);
					allInnerTiles.add(thisInnerTile);
					fillInnerTile(thisInnerTile);
				}
				�u�ereEbene.innerTiles.clear();
				ArrayList<TMXTile> leer = new ArrayList<TMXTile>();
				for(int index=0;index<�u�ereEbene.feinerRahmen.size();index++){
					TMXTile �u�eresRahmentile = �u�ereEbene.feinerRahmen.get(index);
					TMXTile thisRahmentile;
					thisRahmentile = layer.getTileAt(�u�eresRahmentile.getColumn(), �u�eresRahmentile.getRow()-1);
					
					addtoRahmen(feinerRahmen.size(), thisRahmentile);
				}
				fillRahmen(true);
			}
			else{
				int groe�emod;
				if(boden.isZufallsgroesse()){
					groe�emod = SessionData.instance().randomInt.nextInt((int)(�u�ereEbene.getInnerTiles().size()*boden.getMaxgroesse())); 
				}
				else{
					groe�emod = (int)(�u�ereEbene.getInnerTiles().size()*boden.getMaxgroesse());
				}
				Log.i("AdventureLog groe�emod",""+groe�emod);
				erstelleRahmen(startpunkt,groe�emod);
				closeGaps();
				fillRahmen(true);
			}
		}
		else{
			ArrayList<TMXTile> leer = new ArrayList<TMXTile>();
			getAlles(leer);
			leer=null;
		} 
		
		ArrayList<Integer> nextB�den = boden.getnextB�den();
		if(nextB�den.size()!=0){
			int indexofboden = SessionData.instance().randomInt.nextInt(nextB�den.size());
			int nextboden = nextB�den.get(indexofboden);
			Log.i("AdventureLog nextboden","" +nextboden);
			boolean ebenepossible = true;
			if(�u�ereEbene!=null){ 
				while(ebenepossible==true){
					ebenepossible = newEbene(vorlageLayer,context,nextboden);
					indexofboden = SessionData.instance().randomInt.nextInt(nextB�den.size());
					nextboden = nextB�den.get(indexofboden);
					Log.i("AdventureLog boden",nextboden+"");
				}
			} 
			else{
			
				TMXLayer nextLayer = layer.getNextLayer(mapLayers, vorlageLayer);  
				Ebene innereebene = new Ebene(nextboden,layer.getTileAt(layer.getColumns()/2, layer.getRows()/2),this,nextLayer,mapLayers,vorlageLayer,context);
				innerEbenen.add(innereebene);
			}
		}
	}
	
	public Ebene(int art,ArrayList<TMXTile> feinerRahmen,ArrayList<TMXTile> innerTiles,Ebene �u�ereEbene,ArrayList<TMXLayer> mapLayers,TMXLayer groundLayer,TMXLayer vorlageLayer, Context context) {
		this.ebenenindex = new Integer(SessionData.instance().ebenencounter);
		SessionData.instance().ebenencounter++;
		this.boden = new Boden (art);
		this.mapLayers=mapLayers;
		this.layer= groundLayer;  
		this.�u�ereEbene = �u�ereEbene;
		this.feinerRahmen = feinerRahmen;
		this.innerTiles = innerTiles;
		this.allInnerTiles = new ArrayList<TMXTile>(innerTiles);
		for(TMXTile rahmentile : feinerRahmen){
			�u�ereEbene.getInnerTiles().remove(�u�ereEbene.getLayer().getTileAt(rahmentile.getColumn(), rahmentile.getRow()));
			�u�ereEbene.getFeinerRahmen().remove(�u�ereEbene.getLayer().getTileAt(rahmentile.getColumn(), rahmentile.getRow()));
		}
		fillRahmen(false);
		
		for(TMXTile innertile : innerTiles){
			�u�ereEbene.getInnerTiles().remove(�u�ereEbene.getLayer().getTileAt(innertile.getColumn(), innertile.getRow()));
			�u�ereEbene.getFeinerRahmen().remove(�u�ereEbene.getLayer().getTileAt(innertile.getColumn(), innertile.getRow()));
			innertile.resetProperties(); 
			innertile.ebene = this;
			innertile.layer = layer;
		}
	}
	
	

	
	

	public void getAlles(ArrayList<TMXTile> unterbei_innerfreetiles){
		for(int c=0;c<layer.getColumns()-1;c++){
			addtoRahmen(feinerRahmen.size(), layer.getTileAt(c, 0));
			fillInnerTile(layer.getTileAt(c, 0));
		}
		for(int r=0;r<layer.getRows()-1;r++){ 
			addtoRahmen(feinerRahmen.size(), layer.getTileAt(layer.getColumns()-1, r));
			fillInnerTile(layer.getTileAt(layer.getColumns()-1, r));
		}
		for(int c=layer.getColumns()-1;c>0;c--){
			addtoRahmen(feinerRahmen.size(), layer.getTileAt(c, layer.getRows()-1));
			fillInnerTile(layer.getTileAt(c, layer.getRows()-1));
		}
		for(int r=layer.getRows()-1;r>0;r--){
			addtoRahmen(feinerRahmen.size(), layer.getTileAt(0, r));
			fillInnerTile(layer.getTileAt(0, r));
		}
		for(int c=1;c<layer.getColumns()-1;c++){
			for(int r=1;r<layer.getRows()-1;r++){ 
				if(c >12 && c < layer.getColumns() - 12){
					innerTiles.add(layer.getTileAt(c, r));
					
				} 
				allInnerTiles.add(layer.getTileAt(c, r));
				fillInnerTile(layer.getTileAt(c, r));
    		}
		}
	}
	
	public boolean newEbene(TMXLayer vorlageLayer, Context context, int art){
		boolean possible = false;
		ArrayList<TMXTile> tester=null;
		int index=0;
		if(innerTiles.size()>100){ 
			int random= SessionData.instance().randomInt.nextInt(innerTiles.size()-2)+2;
			for(index=random+1;index!=random;index++){
				if(index==innerTiles.size()){
					index=0;
				}
				tester = layer.f�nfundzwanzigerNachbarschaft(innerTiles.get(index));
				if(tester.isEmpty()){
					continue;
				}
				if(innerTiles.containsAll(tester)){
					possible=true; 
					break;
				} 
			} 
			
		}
		if(possible){
			TMXLayer nextLayer = layer.getNextLayer(mapLayers, vorlageLayer);  
			
			Ebene innereebene = new Ebene(art,innerTiles.get(index),this,nextLayer,mapLayers,vorlageLayer,context);
			innerEbenen.add(innereebene);
		}
		else if (boden.isMehrst�ckig()){
			TMXLayer nextLayer = layer.getNextLayer(mapLayers, vorlageLayer);  
		 
			Ebene innereebene = new Ebene(art,null,this,nextLayer,mapLayers,vorlageLayer,context);
			innerEbenen.add(innereebene);
		}
		return possible;
	}
	

	public void erstelleRahmen(TMXTile startpunkt, int groe�emod){
		ArrayList <TMXTile> grundrahmen = layer.neunerinOrderNachbarschaft(startpunkt);
		for(TMXTile grundrahmentile : grundrahmen){
			if(grundrahmen.indexOf(grundrahmentile)!=0){
				addtoRahmen(feinerRahmen.size(),grundrahmentile);	
			}
			else{
				innerTiles.add(grundrahmentile);
				allInnerTiles.add(grundrahmentile);
				if(�u�ereEbene!=null){
					�u�ereEbene.getInnerTiles().remove(�u�ereEbene.getLayer().getTileAt(grundrahmentile.getColumn(), grundrahmentile.getRow()));
				}
				fillInnerTile(grundrahmentile);
			}
			
		}
		grundrahmen=null;
		ArrayList<TMXTile> andereRahmen = new ArrayList<TMXTile>();
		for(Ebene EbenegleicherH�he : �u�ereEbene.getInnerEbenen()){
			if(!EbenegleicherH�he.equals(this)){
				andereRahmen.addAll(EbenegleicherH�he.getFeinerRahmen());
			}
		}
		 
		while(groe�emod>0){
			TMXTile randomTile=null;
			
			ArrayList<Integer> randomIndezes = getRandomIndezes(feinerRahmen);
			loop:
			while(randomIndezes.size()>0){
				int randomIndex= SessionData.instance().randomInt.nextInt(randomIndezes.size());
				int index = randomIndezes.get(randomIndex);
				randomIndezes.remove(randomIndex);
			
				if(feinerRahmen.get(index).getColumn()>1 && feinerRahmen.get(index).getRow()>1){
					if(feinerRahmen.get(index).getColumn()<layer.getColumns()-1 && feinerRahmen.get(index).getRow()<layer.getRows()-1){
						int vierersum=0;
						int angrenzend=0;
						int neunersum=0;
						int viererdirection=0;
						int neunerdirection=0;
						ArrayList<TMXTile> neunertiles = layer.neunerNachbarschaft(feinerRahmen.get(index));
						ArrayList<TMXTile> vierertiles = layer.viererNachbarschaft(feinerRahmen.get(index));
						ArrayList<TMXTile> f�nfundzwanzigertiles = layer.f�nfundzwanzigerNachbarschaft(feinerRahmen.get(index));
						for(int i=0; i<f�nfundzwanzigertiles.size();i++){
							if(andereRahmen.contains(f�nfundzwanzigertiles.get(i))){
								continue loop;
							}
							
							if(i<neunertiles.size()){
								if((!feinerRahmen.contains(neunertiles.get(i))) && (!innerTiles.contains(neunertiles.get(i)) && �u�ereEbene.getInnerTiles().contains(�u�ereEbene.getLayer().getTileAt(neunertiles.get(i).getColumn(), neunertiles.get(i).getRow())))){
									neunersum++;
									neunerdirection = i+1;
								}
								if(i<vierertiles.size()){
									if((!feinerRahmen.contains(vierertiles.get(i))) && (!innerTiles.contains(vierertiles.get(i))&&�u�ereEbene.getInnerTiles().contains(�u�ereEbene.getLayer().getTileAt(vierertiles.get(i).getColumn(), vierertiles.get(i).getRow())))){
										vierersum++;
										viererdirection = i+1;
									}
									if(innerTiles.contains(vierertiles.get(i))){
										angrenzend++;
									}
								}
							}
						}
						if(vierersum==1 && neunersum==3 && angrenzend==1){
							randomTile = feinerRahmen.get(index);
							randomTile.direction = viererdirection;
							randomTile.index= index;
							//randomTile = new directedTile(feinerRahmen.get(index), viererdirection,index);
							break;
						}
						else if (vierersum==0 && neunersum==1 && angrenzend==2){
							randomTile = feinerRahmen.get(index);
							randomTile.direction = neunerdirection;
							randomTile.index = index;
							//randomTile = new directedTile(feinerRahmen.get(index), neunerdirection,index);
							break;
						}
						neunertiles=null;
						vierertiles=null;
						f�nfundzwanzigertiles=null;
					}
				}
			}
			
			if(randomTile==null){
				Log.i("AdventureLog exit","FELL OUT");
				break;
			} 
			switch(randomTile.direction){
				case SessionData.UP:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()-1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn(), randomTile.getRow()-1));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()-1));
				break;
				case SessionData.DOWN:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()+1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn(), randomTile.getRow()+1));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()+1));
				break;
				case SessionData.LEFT:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()+1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()-1));
				break;
				case SessionData.RIGHT:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()-1));
					addtoRahmen(randomTile.index+1,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()));
					addtoRahmen(randomTile.index+2,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()+1));
				break;	
				case SessionData.DOWNLEFT:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()+1));
				break;	
				case SessionData.LEFTUP:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()-1, randomTile.getRow()-1));
				break;
				case SessionData.UPRIGHT:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()-1));
				break;
				case SessionData.RIGHTDOWN:
					innerTiles.add(randomTile);
					allInnerTiles.add(randomTile);
					feinerRahmen.remove(randomTile.index);
					addtoRahmen(randomTile.index,layer.getTileAt(randomTile.getColumn()+1, randomTile.getRow()+1));
				break;
			}
			Log.i("AdventureLog testen",innerTiles.get(innerTiles.size()-1).getColumn()+"/"+innerTiles.get(innerTiles.size()-1).getRow() + " "+groe�emod);
			fillInnerTile(innerTiles.get(innerTiles.size()-1));
			groe�emod--;
			
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
		for(int index=0;index<feinerRahmen.size();index++){
			int gapsum=0;
			ArrayList<TMXTile> gapList = new ArrayList<TMXTile>();
			ArrayList<TMXTile> vierertiles = layer.viererNachbarschaft(feinerRahmen.get(index));
			for(int i=0; i<vierertiles.size();i++){
				if(feinerRahmen.contains(vierertiles.get(i))){
					gapsum++;
					gapList.add(vierertiles.get(i));
				}
			}
			if(gapsum==3){
				for(TMXTile gapTile : gapList){
					if(feinerRahmen.indexOf(gapTile) > index + boden.getGapsgroesse() ){
						ArrayList<TMXTile> delete = new ArrayList<TMXTile>();
						for(int t= index+1; t<feinerRahmen.indexOf(gapTile);t++){
							delete.add(feinerRahmen.get(t));
						}
						if(delete.size()>feinerRahmen.size()/2){ 
							delete.clear();
							for(int t= feinerRahmen.indexOf(gapTile)+1; t<feinerRahmen.size();t++){
								delete.add(feinerRahmen.get(t));
							}
							for(int t=0; t<index;t++){
								delete.add(feinerRahmen.get(t)); 
							}
						}
						feinerRahmen.removeAll(delete);
						for(int m=0;m<delete.size();m++){
							delete = findLoseTiles(delete.get(m),delete);
							innerTiles.add(delete.get(m));
							allInnerTiles.add(delete.get(m));
							if(�u�ereEbene!=null){
								�u�ereEbene.getInnerTiles().remove(�u�ereEbene.getLayer().getTileAt(delete.get(m).getColumn(), delete.get(m).getRow()));
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
		feinerRahmen.add(stelle,tile); 
		if(�u�ereEbene!=null){
			�u�ereEbene.getInnerTiles().remove(�u�ereEbene.getLayer().getTileAt(tile.getColumn(), tile.getRow()));
		}
	} 
	
	public ArrayList<TMXTile> findLoseTiles(TMXTile losestile,ArrayList<TMXTile> losetiles){
		ArrayList<TMXTile> kreuzFeld = layer.neunerNachbarschaft(losestile);
		for(TMXTile feld : kreuzFeld){
			if(�u�ereEbene.getInnerTiles().contains(�u�ereEbene.getLayer().getTileAt(feld.getColumn(), feld.getRow()))){
				if(�u�ereEbene!=null){
					�u�ereEbene.getInnerTiles().remove(�u�ereEbene.getLayer().getTileAt(feld.getColumn(), feld.getRow()));
				}
				losetiles.add(feld);
				losetiles = findLoseTiles(feld,losetiles);
			}
		}
		return losetiles;
	}
	
	public void fillInnerTile(TMXTile tile){
		if(!boden.isMehrst�ckig()){
			tile.setAtlas(boden.getGrund().x, boden.getGrund().y);
		}
		
		for(int i=0;i<mapLayers.indexOf(layer);i++){
			TMXTile tile_under = mapLayers.get(i).getTileAt(tile.getColumn(), tile.getRow());
			tile_under.free=false;
			tile_under.on_highest_ebene=false;
		}

		tile.resetProperties();
		tile.setGID(1);
		tile.ebene = this;
		tile.layer = layer;
		tile.free=true;
		tile.on_highest_ebene=true;
		
	}
	
	
	
	public void fillRahmen(boolean draw){
		TMXTile folgend;
		TMXTile vorher;
		ArrayList<Integer> prop = new ArrayList<Integer>();
		prop.add(�u�ereEbene.getEbenenindex());
		for(TMXTile randst�ck : feinerRahmen){
			randst�ck.resetProperties();
			if(feinerRahmen.size()-1 == feinerRahmen.indexOf(randst�ck)){
				folgend= feinerRahmen.get(0);
			}
			else{
				folgend = feinerRahmen.get(feinerRahmen.indexOf(randst�ck)+1);
			}
				
			if(0 == feinerRahmen.indexOf(randst�ck)){
				vorher = feinerRahmen.get(feinerRahmen.size()-1);	
			}
			else{
				vorher = feinerRahmen.get(feinerRahmen.indexOf(randst�ck)-1);
			}
			
			if(draw==true){
				//linke obere ecke 
				if(randst�ck.getColumn()<folgend.getColumn() && randst�ck.getRow()==folgend.getRow() && randst�ck.getColumn()==vorher.getColumn() && randst�ck.getRow()<vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.LINKSOBEN);
				}
				//oben gerade
				else if(randst�ck.getColumn()<folgend.getColumn() && randst�ck.getRow()==folgend.getRow() && randst�ck.getColumn()>vorher.getColumn() && randst�ck.getRow()==vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.OBEN);
				}
				//rechte obere ecke
				else if(randst�ck.getColumn()==folgend.getColumn() && randst�ck.getRow()<folgend.getRow() && randst�ck.getColumn()>vorher.getColumn() && randst�ck.getRow()==vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.OBENRECHTS);
				}
				//rechts gerade
				else if(randst�ck.getColumn()==folgend.getColumn() && randst�ck.getRow()<folgend.getRow() && randst�ck.getColumn()==vorher.getColumn() && randst�ck.getRow()>vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.RECHTS);
				}
				//rechte untere ecke
				else if(randst�ck.getColumn()>folgend.getColumn() && randst�ck.getRow()==folgend.getRow() && randst�ck.getColumn()==vorher.getColumn() && randst�ck.getRow()>vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.RECHTSUNTEN);
				}
				//unten gerade
				else if(randst�ck.getColumn()>folgend.getColumn() && randst�ck.getRow()==folgend.getRow() && randst�ck.getColumn()<vorher.getColumn() && randst�ck.getRow()==vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.UNTEN);
				}
				//linke untere ecke
				else if(randst�ck.getColumn()==folgend.getColumn() && randst�ck.getRow()>folgend.getRow() && randst�ck.getColumn()<vorher.getColumn() && randst�ck.getRow()==vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.UNTENLINKS);
				}
				//links gerade
				else if(randst�ck.getColumn()==folgend.getColumn() && randst�ck.getRow()>folgend.getRow() && randst�ck.getColumn()==vorher.getColumn() && randst�ck.getRow()<vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.LINKS);
				}
				//innere ecke links nach oben
				else if(randst�ck.getColumn()==folgend.getColumn() && randst�ck.getRow()>folgend.getRow() && randst�ck.getColumn()>vorher.getColumn() && randst�ck.getRow()==vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.LINKSNACHOBEN);
				}
				//innere ecke oben nach rechts
				else if(randst�ck.getColumn()<folgend.getColumn() && randst�ck.getRow()==folgend.getRow() && randst�ck.getColumn()==vorher.getColumn() && randst�ck.getRow()>vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.OBENNACHRECHTS);
				}
				//innere ecke rechts nach unten
				else if(randst�ck.getColumn()==folgend.getColumn() && randst�ck.getRow()<folgend.getRow() && randst�ck.getColumn()<vorher.getColumn() && randst�ck.getRow()==vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.RECHTSNACHUNTEN);
				}
				//innere ecke unten nach links
				else if(randst�ck.getColumn()>folgend.getColumn() && randst�ck.getRow()==folgend.getRow() && randst�ck.getColumn()==vorher.getColumn() && randst�ck.getRow()<vorher.getRow()){
					modifyRahmenTile(randst�ck, SessionData.UNTENNACHLINKS);
				}
				randst�ck.setGID(1);
			}
			
			for(int i=0;i<mapLayers.indexOf(layer);i++){
				TMXTile tile_under = mapLayers.get(i).getTileAt(randst�ck.getColumn(), randst�ck.getRow());
				tile_under.free=false;
				tile_under.on_highest_ebene=false;
			}
			
			
			randst�ck.ebene = this;
			randst�ck.layer = layer;
			randst�ck.free=true;
			randst�ck.on_highest_ebene=true;
			
			if(boden.isBetretbar()){
				randst�ck.joints.addAll(prop);
			}
			
			
		}
	}
	
	public void giveWalkability(int ebenenindex){
		for(TMXTile randst�ck : feinerRahmen){
			ArrayList<Integer> prop = randst�ck.joints;
			prop.add(ebenenindex);
		}
	}
	
	public void modifyRahmenTile(TMXTile randst�ck, int randdirection){
			randst�ck.setAtlas(boden.getRand().get(randdirection).x,boden.getRand().get(randdirection).y);
			randst�ck.direction = randdirection;
	}
	
	private boolean ebeneCollides(TMXTile movedTile){
		int layerindex = mapLayers.indexOf(layer);
		int ebenencounter = 2;
		while(ebenencounter>0 && layerindex>0){
			if(!mapLayers.get(layerindex).getName().contains("obj")){
				ebenencounter--;
			}
			TMXTile tile_to_check = mapLayers.get(layerindex).getTileAt(movedTile.getColumn(), movedTile.getRow());
			if(!(tile_to_check.getGID()==0)){
				return true;
			}
			layerindex--;
		}
		return false;
	}
	
	private void moveTile(TMXTile fromTile, TMXTile toTile){
		toTile.setAtlas(fromTile.getAtlasColumn(), fromTile.getAtlasRow());
		if(!fromTile.joints.isEmpty())
			toTile.joints.addAll(fromTile.joints);
		toTile.setGID(fromTile.getGID());
		toTile.free = fromTile.free;
		toTile.on_highest_ebene = fromTile.on_highest_ebene;
		
		fromTile.free=true;
		fromTile.on_highest_ebene=true;
		fromTile.setEmpty();	
	}
	
	public int moveEbeneX(boolean positive){
		boolean stop = false;
		if(!positive){
			
			for(TMXTile tile : feinerRahmen){
				if(tile.getColumn()==0){
					return 1;
				}
			}
			for(int c=0;c<layer.getColumns();c++){
				for(int r=0;r<layer.getRows();r++){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(feinerRahmen.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()-1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						feinerRahmen.set(feinerRahmen.indexOf(currentTile), movedTile);
					}
					
					
					if(innerTiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()-1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						innerTiles.set(innerTiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		else{
			for(TMXTile tile : feinerRahmen){
				if(tile.getColumn()==layer.getColumns()-1){
					return 1;
				}
			}
			
			for(int c=layer.getColumns()-1;c>=0;c--){
				for(int r=layer.getRows()-1;r>=0;r--){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(feinerRahmen.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()+1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						feinerRahmen.set(feinerRahmen.indexOf(currentTile), movedTile);
					}
					
					
					if(innerTiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn()+1, currentTile.getRow());
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						innerTiles.set(innerTiles.indexOf(currentTile), movedTile);
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
			
			for(TMXTile tile : feinerRahmen){
				if(tile.getRow()==0){
					return 1;
				}
			}
			for(int c=0;c<layer.getColumns();c++){
				for(int r=0;r<layer.getRows();r++){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(feinerRahmen.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()-1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						feinerRahmen.set(feinerRahmen.indexOf(currentTile), movedTile);
					}
					
					
					if(innerTiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()-1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						innerTiles.set(innerTiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		else{
			for(TMXTile tile : feinerRahmen){
				if(tile.getColumn()==layer.getRows()-1){
					return 1;
				}
			}
			
			for(int c=layer.getColumns()-1;c>=0;c--){
				for(int r=layer.getRows()-1;r>=0;r--){ 
					TMXTile currentTile = layer.getTileAt(c, r);
					if(feinerRahmen.contains(currentTile)){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()+1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						feinerRahmen.set(feinerRahmen.indexOf(currentTile), movedTile);
					}
					
					
					if(innerTiles.contains(layer.getTileAt(c, r))){
						TMXTile movedTile = layer.getTileAt(currentTile.getColumn(), currentTile.getRow()+1);
						if(ebeneCollides(movedTile)){
							stop = true;
						}
						moveTile(currentTile,movedTile);
						innerTiles.set(innerTiles.indexOf(currentTile), movedTile);
					}
				}
			}
			if(stop==true){
				return 2;
			}
		}
		return 0;
	}
	
	public int getEbenenDif(Ebene b){
		Ebene counterEbene = this;
		while (counterEbene.�u�ereEbene!=null){
			counterEbene = counterEbene.�u�ereEbene;
		}
		return Math.abs(getHops(0,counterEbene,this)-getHops(0,counterEbene,b));
	}
	
	public int getHops(int hops, Ebene actualEbene, Ebene searchedEbene){
		int result;
		if(actualEbene==searchedEbene){
			return hops;
		}
		if( actualEbene.getInnerEbenen().isEmpty()){
			return -1;
		}
		for(Ebene innereEbene : actualEbene.getInnerEbenen()){
			result = getHops(hops+1,innereEbene,searchedEbene);
			if(result!=-1){
				return result;
			}
		}
		return -1;
		 
	}
	
	public Ebene(Ebene downloadedEbene,Ebene �u�ereEbene,ArrayList<TMXLayer> mapLayers,TMXLayer groundLayer,TMXLayer vorlageLayer, Context context) {
		this.ebenenindex = downloadedEbene.ebenenindex;
		this.boden = downloadedEbene.boden;
		this.mapLayers=mapLayers;
		this.layer= groundLayer;  
		this.�u�ereEbene = �u�ereEbene;
		this.feinerRahmen = downloadedEbene.feinerRahmen;
		this.innerTiles = downloadedEbene.innerTiles;
		this.allInnerTiles=downloadedEbene.allInnerTiles;
		TMXLayer innerLayer= null;
		copyTMXTiles(downloadedEbene);
		for(Ebene innereEbene : downloadedEbene.innerEbenen){
			for(TMXLayer mapLayer : mapLayers){
				if(innereEbene.layer.getName().equals(mapLayer.getName())){
					innerLayer = mapLayer;
				}
			}
			Ebene newEbene = new Ebene(innereEbene,this,mapLayers,innerLayer,vorlageLayer,context);
			innerEbenen.add(newEbene);
		}
	}

	public void copyTMXTiles(Ebene downloadedEbene){
		for(int c=0;c<layer.getColumns();c++){
    		for(int r=0;r<layer.getRows();r++){
    			
    			
    			
    			TMXTile downloadedTile = downloadedEbene.layer.getTileAt(c, r);
    			TMXTile newTile = layer.getTileAt(c, r);
    			
    			
    			if(feinerRahmen.contains(downloadedTile) || allInnerTiles.contains(downloadedTile)){
    				newTile.setGID(downloadedTile.getGID());
        			newTile.setAtlas(downloadedTile.getAtlasColumn(), downloadedTile.getAtlasRow());
        			if(!downloadedTile.joints.isEmpty())
        				newTile.joints.addAll(downloadedTile.joints);
	    			newTile.ebene=this;
	    			newTile.free=true;
	    			newTile.on_highest_ebene=true; 
					
					for(int i=0;i<mapLayers.indexOf(layer);i++){
						TMXTile tile_under = mapLayers.get(i).getTileAt(newTile.getColumn(), newTile.getRow());
						tile_under.free=false;
						tile_under.on_highest_ebene=false;
					}
					
	    			if(feinerRahmen.contains(downloadedTile)){
	    				feinerRahmen.set(downloadedEbene.feinerRahmen.indexOf(downloadedTile), newTile);
	    			}
	    			if(innerTiles.contains(downloadedTile)){
	    				innerTiles.set(downloadedEbene.innerTiles.indexOf(downloadedTile), newTile);
	    			}
    			}
			
    			
    		}
		}

	}
	
	
	public ArrayList<TMXTile> getFeinerRahmen() {
		return feinerRahmen;
	}

	public void setFeinerRahmen(ArrayList<TMXTile> feinerRahmen) {
		this.feinerRahmen = feinerRahmen;
	}

	public ArrayList<TMXTile> getInnerTiles() {
		return innerTiles;
	}

	public void setInnerTiles(ArrayList<TMXTile> innerTiles) {
		this.innerTiles = innerTiles;
	}

	public TMXLayer getLayer() {
		return layer;
	}

	public void setLayer(TMXLayer layer) {
		this.layer = layer;
	}

	public List<Ebene> getInnerEbenen() {
		return innerEbenen;
	}

	public void setInnerEbenen(List<Ebene> innerEbenen) {
		this.innerEbenen = innerEbenen;
	}

	public int getArt() {
		return boden.getArt();
	}

	public Ebene get�u�ereEbene() {
		return �u�ereEbene;
	}

	public void set�u�ereEbene(Ebene �u�ereEbene) {
		this.�u�ereEbene = �u�ereEbene;
	}
	
	public int getEbenenindex() {
		return ebenenindex;
	}

	public void setEbenenindex(int ebenenindex) {
		this.ebenenindex = ebenenindex;
	}

	public Boden getBoden() {
		return boden;
	}

	public void setBoden(Boden boden) {
		this.boden = boden;
	}
	
	public void addEbeneToInnerEbenen(Ebene ebene){
		innerEbenen.add(ebene);
	}
}

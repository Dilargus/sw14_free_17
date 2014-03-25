package com.adventureislands;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.datastructure.tmx.TMXException;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;
import com.datastructure.tmx.TMXTiledMapLoader;

public class loadMap extends AsyncTask<Void,Integer,TMXTiledMap> 
{
	 String path;
	 Context context;


	  public loadMap (String path, Context context)
	{
		 this.context = context;
	     this.path = path;

	}
	  
	  
    @Override
    protected TMXTiledMap doInBackground(Void... param)  
    {
		try {
			TMXTiledMapLoader mapLoader = new TMXTiledMapLoader();
			TMXTiledMap TMXmap = mapLoader.loadFromAsset(path+ ".tmx", context);
			mapLoader = null;
			Log.i("AdventureLog file",path + ".tmx");
			for(TMXLayer layer : TMXmap.getLayers()){
				for(int c=0;c<layer.getColumns();c++){
		    		for(int r=0;r<layer.getRows();r++){
		    			if(layer.getTileAt(c, r).getAtlasColumn()==0 && layer.getTileAt(c, r).getAtlasRow()==0){
		    				layer.getTileAt(c, r).setEmpty();
		    			}
		    		}
				}
			}
			return TMXmap;
		} catch (TMXException e) {
		}
		return null;
    } 
}
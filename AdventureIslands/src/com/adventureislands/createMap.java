package com.adventureislands;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.datastructure.tmx.TMXException;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;
import com.datastructure.tmx.TMXTiledMapLoader;

public class createMap extends AsyncTask<Void,Integer,Map> 
{
	 Map outermap;
	 Context context;
	 int art;
	 int form;
	 String path;
	 Map tocopy;

	  public createMap (Map outermap,Context context, int art, int form, String path)
	{
		 this.context = context;
	     this.path = path;
	     this.outermap = outermap;
	     this.form = form;
	     this.art = art;
	}
	  
	public createMap (Map tocopy,Map outermap,Context context, int art, int form, String path)
	{
		 this.context = context;
	     this.path = path;
	     this.outermap = outermap;
	     this.form = form;
	     this.art = art;
	     this.tocopy=tocopy;
	}
	  
	  
    @Override
    protected Map doInBackground(Void... param)  
    {
    	Map map;
    	if(tocopy==null){
    		map = new Map(outermap,context, art, form, path);
    	}
    	else{
    		map = new Map(tocopy,outermap,context, art, form, path);
    	}
		return map;
    } 
}
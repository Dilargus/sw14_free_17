package com.adventureislands;

import android.content.Context;
import android.os.AsyncTask;


public class createMap extends AsyncTask<Void,Integer,Map> 
{
	 Map outer_map;
	 Context context;
	 int form;
	 String path;
	 Map to_copy;

	  public createMap (Map outermap,Context context, int form, String path)
	{
		 this.context = context;
	     this.path = path;
	     this.outer_map = outermap;
	     this.form = form;
	}
	  
	public createMap (Map to_copy,Map outer_map,Context context, int form, String path)
	{
		 this.context = context;
	     this.path = path;
	     this.outer_map = outer_map;
	     this.form = form;
	     this.to_copy=to_copy;
	}
	  
	  
    @Override
    protected Map doInBackground(Void... param)  
    { 
    	Map map;
    	if(to_copy==null){
    		map = new Map(outer_map,context, form, path);
    	}
    	else{
    		map = new Map(to_copy,outer_map,context, path);
    	}
		return map;
    } 
}
package com.datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import com.adventureislands.SessionData;


public class SurfaceTexture implements
java.io.Serializable{
	
	public ArrayList<myPoint> border_texture;
	public myPoint inner_tile_texture;
	public int type;
	public ArrayList<Integer> next_surface_textures;
	public boolean multi_level_surface = false;
	public boolean random_size = false;
	public boolean accessible = true;
	public int gap_size = 5;
	public double max_size = 0.5;

	public SurfaceTexture(int art) {
		next_surface_textures = new ArrayList<Integer>();
		this.type = art;
		switch(art){

			case SessionData.MINENBODEN:
				inner_tile_texture = new myPoint(11, 2); 
				max_size=0.3;
				random_size=false;
				accessible=false;
			break;
			case SessionData.DUNKLESCHLUCHT:
				inner_tile_texture = new myPoint(1, 11);
				max_size=0.3;
				multi_level_surface=true;
				accessible=false;
				random_size=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.MINENBODEN)));
			break;
			case SessionData.DUNKELHEIT: 
				inner_tile_texture = new myPoint(6, 2);
				random_size=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.DUNKLESCHLUCHT)));
			break;
			case SessionData.SANDSTRAND:
				max_size=0.7;
				random_size=false;
				inner_tile_texture = new myPoint(1, 14); 
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.SANDKLIPPE),new Integer(SessionData.BROCKENSAND), new Integer(SessionData.HELLERSAND)));
			break;
			case SessionData.UNTERWASSERSAND:
				inner_tile_texture = new myPoint(11, 11); 
				max_size=0.4;
				random_size=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.SANDSTRAND)));
			break;
			case SessionData.TIEFESWASSER:
				inner_tile_texture = new myPoint(1, 11);
				accessible=false;
				random_size=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.WASSER)));
			break;
			case SessionData.WASSER: 
				inner_tile_texture = new myPoint(11, 14);
				random_size=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.UNTERWASSERSAND)));
			break;
			case SessionData.STEINMAUER:
				accessible=false;
				random_size=true;
				inner_tile_texture = new myPoint(11, 6);
			break;
			case SessionData.BRACHLAND:
				inner_tile_texture = new myPoint(1, 20);
				random_size=true;
				gap_size = 100;
			break;
			case SessionData.ERHÖHUNG:
				accessible=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENLAND)));
				inner_tile_texture = new myPoint(6, 17);
				random_size=true;
			break;
			case SessionData.HELLERSAND:
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.PFLASTER),new Integer(SessionData.EISENKLIPPE),new Integer(SessionData.BRACHLAND)));
				inner_tile_texture = new myPoint(6, 20);
				random_size=true;
			break;
			case SessionData.PFLASTER:
				inner_tile_texture = new myPoint(11, 20);
				random_size=true;
			break;
			case SessionData.SANDKLIPPE:
				multi_level_surface=true;
				accessible=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.KLIPPENSAND)));
				inner_tile_texture = new myPoint(6, 8);
				max_size = 0.1;
				random_size=true;
			break;
			case SessionData.KLIPPENSAND:
				accessible=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENSAND)));
				inner_tile_texture = new myPoint(6, 6);
				random_size=true;
			break;
			case SessionData.EISENKLIPPE:
				multi_level_surface=true;
				accessible=false;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.EISENKLIPPENSAND)));
				inner_tile_texture = new myPoint(1, 8);
				random_size=true;
			break;
			case SessionData.EISENKLIPPENSAND:
				accessible=false;
				inner_tile_texture = new myPoint(1, 6);
				random_size=true;
			break;
			case SessionData.BROCKENSAND:
				inner_tile_texture = new myPoint(1, 23);
				random_size=true;
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENLAND),new Integer(SessionData.ERHÖHUNG)));
			break;
			case SessionData.BROCKENLAND:
				random_size=true;
				gap_size = 50;
				inner_tile_texture = new myPoint(6, 23);
				next_surface_textures = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.STEINMAUER),new Integer(SessionData.VERTIEFUNG)));
			break;
			case SessionData.VERTIEFUNG:
				accessible=false;
				random_size=true;
				inner_tile_texture = new myPoint(1, 17);
			break;
			case SessionData.SCHIFFSHUELLE:
				accessible=false;
				inner_tile_texture = null;
				max_size = 0;
				random_size=false;
			break;
			case SessionData.SCHIFFSDECK:
				accessible=false;
				inner_tile_texture = null;
				max_size = 0;
				random_size=false;
			case SessionData.CITY:
				accessible=false;
				inner_tile_texture = null;
				max_size = 0;
				random_size=false;
				multi_level_surface=true;
			break;
		}
		if(inner_tile_texture!=null){
			if(!multi_level_surface){
				border_texture = new ArrayList<myPoint>(Arrays.asList(
						//links oben, 					 	oben waagrecht,                			rechts oben,                     	rechts senkrecht,
						new myPoint(inner_tile_texture.x-1, inner_tile_texture.y-1), 	new myPoint(inner_tile_texture.x, inner_tile_texture.y-1), 		new myPoint(inner_tile_texture.x+1, inner_tile_texture.y-1), 	new myPoint(inner_tile_texture.x+1, inner_tile_texture.y),  
						//rechts unten,                  	unten waagrecht,               			links unten,                     	links senkrecht,
						new myPoint(inner_tile_texture.x+1, inner_tile_texture.y+1), 	new myPoint(inner_tile_texture.x, inner_tile_texture.y+1), 		new myPoint(inner_tile_texture.x-1, inner_tile_texture.y+1), 	new myPoint(inner_tile_texture.x-1, inner_tile_texture.y),   
						//links nach oben,               	oben nach rechts,                		rechts nach unten,             		unten nach links
						new myPoint(inner_tile_texture.x+2, inner_tile_texture.y-1), 	new myPoint(inner_tile_texture.x+3, inner_tile_texture.y-1), 		new myPoint(inner_tile_texture.x+3, inner_tile_texture.y), 	new myPoint(inner_tile_texture.x+2, inner_tile_texture.y)));
			}
			else{
				border_texture = new ArrayList<myPoint>(Arrays.asList(
						//links oben, 					 	oben waagrecht,                			rechts oben,                     	rechts senkrecht,
						new myPoint(16, 0), 				new myPoint(16, 0), 					new myPoint(16, 0), 				new myPoint(16, 0),  
						//rechts unten,                  	unten waagrecht,               			links unten,                     	links senkrecht,
						new myPoint(inner_tile_texture.x+1, inner_tile_texture.y+1), 	new myPoint(inner_tile_texture.x, inner_tile_texture.y+1), 		new myPoint(inner_tile_texture.x-1, inner_tile_texture.y+1), 	new myPoint(16, 0),   
						//links nach oben,               	oben nach rechts,                		rechts nach unten,             		unten nach links
						new myPoint(16, 0), 				new myPoint(16, 0), 					new myPoint(16, 0), 				new myPoint(16, 0)));
			}
		}
	}
}

package com.adventureislands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.datastructure.Surface;
import com.datastructure.objects.Cannon;
import com.datastructure.objects.Event;
import com.datastructure.objects.Exit;
import com.datastructure.objects.Object;
import com.datastructure.objects.Rank;
import com.datastructure.objects.Shovel;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXTile;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Drawable;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.modifier.AlphaModifier;
import com.e3roid.drawable.modifier.ParallelModifier;
import com.e3roid.drawable.modifier.ProgressModifier;
import com.e3roid.drawable.modifier.RotateModifier;
import com.e3roid.drawable.modifier.ScaleModifier;
import com.e3roid.drawable.modifier.ShapeModifier;
import com.e3roid.drawable.modifier.SpanModifier;
import com.e3roid.drawable.modifier.function.Linear;
import com.e3roid.drawable.sprite.TextSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.ModifierEventListener;
import com.e3roid.event.SceneUpdateListener;



public class StandardGameScreenActivity extends E3Activity  implements  SceneUpdateListener, ModifierEventListener{
	private int width;
	private int height;
	private int diff_x = 0;
	private int diff_y = 0;
	private int elapsed_time = 0;
	private int click_time = 0;
	private Map map;
	private boolean surface_changed = false;
	private boolean sailor_drag_drop = false;
	private ArrayList<MySprite> my_sprites = new ArrayList<MySprite>();
	private TiledTexture texture;
	private TiledTexture texture2;
	private Sailor sprite;
	private Cannonball cb;
	private Object works_on;
	private Sprite sailors_symbol;
	private Sprite sailors_symbol2;
	private int clicks=0;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {    
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.height = 480;
            this.width = (int) (1.5*this.height);
        } 
        else{
	        String height_from_intent = extras.getString(Intent.EXTRA_TEXT);
	        if (height_from_intent != null) {
	          this.height = Integer.parseInt(height_from_intent);
	          this.width = (int) (1.5*this.height);
	        } 
        }
        
        super.onCreate(savedInstanceState);
    }
	
	@Override
	protected void onStop(){
		if(SessionData.instance().state == SessionData.EXITING){
			SessionData.instance().musicPlayer.release();
		}
		
		super.onStop();
		
	}
	@Override
	protected void onStart(){
		if(SessionData.instance().musicPlayer==null){
			SessionData.instance().musicPlayer = MediaPlayer.create(this, R.raw.menu_intro);
			SessionData.instance().musicPlayer.setLooping(true);
			this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	
			if(SessionData.instance().musicOn==true){
				SessionData.instance().musicPlayer.start();
			}
		}
		super.onStart();
	}
	

	
	@Override
	public E3Engine onLoadEngine() {

		E3Engine engine = new E3Engine(this, width, height);
		engine.requestFullScreen();
		engine.requestLandscape();
		 
		return engine;
	} 

	@Override
	public E3Scene onLoadScene() {
		final E3Scene scene = new E3Scene();

		final TextSprite loading_text = new TextSprite("Loading map...", 24, this);
		loading_text.move((getWidth() - loading_text.getWidth()) / 2, (getHeight() - loading_text.getHeight()) / 2); 
		loading_text.addModifier(new SpanModifier(500L, new AlphaModifier(0, 0, 1)));
		scene.getTopLayer().add(loading_text);
		
		engine.setRefreshMode(E3Engine.REFRESH_LIMITED);
		engine.setPreferredFPS(10);
		
		new AsyncTask<Void,Integer,Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
		        switch(SessionData.instance().desired_map){ 
		        case -1: 
					loading_text.setText("Map Identification Failed!");
					loading_text.setAlpha(1);
					loading_text.clearModifier();
					return false;
		        case SessionData.CITY: 
		        	createCity();
		            break; 
				case SessionData.ISLAND: 
					createIsland();
					break; 
				case SessionData.TESTISLAND: 
					createTestIsland();
					break; 
				case SessionData.TESTSHIP: 
					createTestShip();
					break; 
				}
		        return true;
			}
			
			@Override
			protected void onPostExecute(Boolean finished) {
				if(finished && map!=null){
					scene.addEventListener(StandardGameScreenActivity.this);
					scene.registerUpdateListener(20, StandardGameScreenActivity.this);
					
					initializeHUD(scene);
					
					texture = new TiledTexture("pirate.png", 32, 32, 0, 0, 0, 0, StandardGameScreenActivity.this);
					sprite = new Sailor(map,texture, 0, 0);
					
					texture2 = new TiledTexture("cannonball.png", 32, 64, 0, 0, 0, 0, StandardGameScreenActivity.this);
					cb = new Cannonball(map,texture2, sprite.getRealX(), sprite.getRealY(),3, 1, 0);
					//automated_sprites.add(cb);
					
					for(int l=0;l<map.map_layers.size();l++){
						map.map_layers.get(l).setSceneSize(getWidth(), getHeight());
					}	
					
					for(int l=0;l<map.map_layers.size();l++){
						if(l==0){
							map.map_layers.get(l).addChild(sprite);
							map.map_layers.get(l).addChild(cb);
						}
						scene.getTopLayer().add(map.map_layers.get(l)); //adds to loadable Drawables
					}
					scene.getTopLayer().add(sprite);
					scene.getTopLayer().add(cb);
					
					for(TMXLayer vorlayer : map.in_front_layers){
						vorlayer.setSceneSize(getWidth(), getHeight());
						scene.getTopLayer().add(vorlayer);
					}
					sprite.current_surface = map.first_surface;
					cb.current_surface = map.first_surface;
					if(map.type==SessionData.TESTISLAND){
						map.start_point = new TMXObject("startpoint", "startpoint", map.sample_layer.getWidth()/2-16, map.sample_layer.getHeight()/2-16, 1, 1);
					}
					if(map.start_point!=null){
						sprite.moveAndSetCurrentSurface(map.start_point,getWidth(),getHeight());
					}
					
					scene.getTopLayer().remove(loading_text);
					
					switch(map.type){ 
			        	case SessionData.CITY: 
			        		Log.i("StandardGameScreenActivity", "City Map is ready to use");
			        	break;
			        	case SessionData.ISLAND: 
			        		Log.i("StandardGameScreenActivity", "Island Map is ready to use");
			        	break;
			        	case SessionData.TESTISLAND: 
			        		Log.i("StandardGameScreenActivity", "TestIsland Map is ready to use");
			        	break;
			        	case SessionData.TESTSHIP: 
			        		Log.i("StandardGameScreenActivity", "TestShip Map is ready to use");
			        	break;
					}
					
					engine.setRefreshMode(E3Engine.REFRESH_DEFAULT);
				}
				else {
					loading_text.setText("Failed to load!");
					loading_text.setAlpha(1);
					loading_text.clearModifier();
				}
			}
		}.execute();
		
		
		return scene;
	}

	public void createIsland(){
		Map downloaded_map=null;
		File mapFile = new File(getFilesDir(), "map1.txt");
		//if(!mapFile.exists()){
			Log.i("StandardGameScreenActivity", "Start Download");
			downloadFile();
		//}
		FileInputStream fis = null;
		try{
			File downloadFile = new File(getFilesDir(), "map1.txt");
			fis = new FileInputStream(downloadFile);
			ObjectInputStream ois = null;
			try{
				ois = new ObjectInputStream(fis);
				downloaded_map = (Map) ois.readObject();
			}
			catch (IOException ex){
				downloadFile.delete();
				createIsland();
				ex.printStackTrace();
			}
			catch(ClassNotFoundException ex){
				ex.printStackTrace();
			}
			finally{
				try{
	                if(ois!=null){ 
	                   ois.close();
	                }
				}
				catch (IOException ex){
					ex.printStackTrace();
				}
			}
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
		finally{
			try{
				if(fis!=null){
					fis.close();
				} 
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
		}
		map = new Map(downloaded_map, null, getBaseContext(), "large"); 
	}
	
	public void createTestIsland(){
		map = new Map(null, getBaseContext(), SessionData.TESTISLAND, "large");
	}
	
	public void createTestShip(){
		map = new Map(null, getBaseContext(), SessionData.TESTSHIP, "large");
	}
	
	public void createCity(){
		map = new Map(null, getBaseContext(), SessionData.CITY, "city");
	}
	
	public void initializeHUD(E3Scene scene){
		Texture sailors_symbol_texture  =  new AssetTexture("HUD_sailor.png", StandardGameScreenActivity.this);
		sailors_symbol = new Sprite( sailors_symbol_texture, 0, 0);
		
		int sailor_amount = 5;
		TextSprite sailors_counter = new TextSprite("00", 20, Color.WHITE, Color.TRANSPARENT, Typeface.DEFAULT_BOLD, StandardGameScreenActivity.this);	
		sailors_counter.setText(String.format("%01d", sailor_amount));
		sailors_counter.move(sailors_symbol_texture.getWidth(), 5);	
		
		int gold_value = 3600;
		TextSprite gold_counter = new TextSprite("0000", 20,0xffff8c00,Color.TRANSPARENT,Typeface.DEFAULT_BOLD, StandardGameScreenActivity.this);	
		gold_counter.setText(String.format("%03d", gold_value));
		gold_counter.move((getWidth() - gold_counter.getWidth()), 5);	
		
		Texture gold_symbol_texture  =  new AssetTexture("HUD_gold.png", StandardGameScreenActivity.this);
		Sprite gold_symbol = new Sprite(gold_symbol_texture, getWidth() - gold_symbol_texture.getWidth() - gold_counter.getWidth(), 0);
		
		scene.addHUD(sailors_symbol);
		scene.addHUD(sailors_counter);
		
		scene.addHUD(gold_symbol);
		scene.addHUD(gold_counter);
	}
	
	@Override
	public void onLoadResources() {
	}
	
	@Override
	public boolean onSceneTouchEvent(E3Scene scene, MotionEvent motionEvent) {
		if(loaded){
			if(!sailorDragAndDrop(scene, motionEvent) && !sprite.working && sprite != null){
				int toX = getTouchEventX(scene, motionEvent) - (texture.getTileWidth()  / 2);
				int toY = getTouchEventY(scene, motionEvent) - (texture.getTileHeight() / 2);
				
				if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
					turnAndMove(toX, toY);
				}
				else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
					sprite.stop();
					sprite.resetDirection();
					diff_x=0;
					diff_y=0;
				}
				else if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
					turnAndMove(toX, toY);
					if(click_time + 25 > elapsed_time){
//						if(toX > sprite.getRealX()){
//							map.ship.moveShipX(1);
//						}
//						else{
//							map.ship.moveShipX(-1);
//						}
						
						clickAnObject(toX,toY);
					}
					click_time = elapsed_time;
				}
			}
		}
		return true; 
	}
	
	private void clickAnObject(int toX, int toY){
		for(TMXObject clickable_rectangle : map.clickable_rectangles){
			Rect object_rectangle = clickable_rectangle.getRectangle(map.map_layers.get(0));
			if(object_rectangle.contains(toX, toY)){
				if(sprite.intersects(object_rectangle)){
					if(clickable_rectangle.corresponding_object.pickable){
						for(TMXLayer layer : clickable_rectangle.corresponding_object.fragment.getLayers()){
							layer.getTileAt(0, 0).setEmpty();
						}
						sprite.equipped_object = clickable_rectangle.corresponding_object;
					}
					else if(clickable_rectangle.corresponding_object.type == SessionData.EXIT){
						pause();
						this.loaded=false;
						mapChange(map.inner_map,map.inner_map.clickable_rectangles.get(0));
					} 
					else if(clickable_rectangle.corresponding_object.type == SessionData.RANK){
						pause();
						this.loaded=false;
						mapChange(map.outer_map, map.outer_map.clickable_rectangles.get(map.outer_map.clickable_rectangles.size()-1));
						Log.i("AdventureLog tmppath of exit",""+map.clickable_rectangles.size());
					} 
					else if (clickable_rectangle.corresponding_object.doable && (sprite.equipped_object.type ==  SessionData.SHOVEL)){
						works_on = clickable_rectangle.corresponding_object;
						works_on.setupEvents();
						sprite.working = true;
						sprite.action_timer = 0;
						sprite.next_action = sprite.getAction(SessionData.DIG);
						sprite.doSomething();
					}
					
					else if (clickable_rectangle.corresponding_object.type == SessionData.CANNON){	
						switch(clicks){
						case 0:
							clicks = 1;
							TiledTexture arrow_texture  = new TiledTexture("arrow.png", 64, 32, 0, 0, 0, 0, StandardGameScreenActivity.this);
							Arrow arrow = new Arrow(map, arrow_texture, clickable_rectangle.x+clickable_rectangle.width,  clickable_rectangle.y+clickable_rectangle.height/2-16);
							arrow.setRotationAndScaleCenter(0, 16);
							RotateModifier rotmod = new RotateModifier(0, -45, 45);
							ProgressModifier promod = new ProgressModifier(rotmod, 3000, Linear.getInstance(), this);
							ParallelModifier arrow_rotator = new ParallelModifier(promod);
					
							arrow_rotator.setLoop(true);
							
							arrow.addModifier(arrow_rotator);
							this.getEngine().getScene().getTopLayer().add(arrow);
							for(int l=0;l<map.map_layers.size();l++){
								if(l==0){
									map.map_layers.get(l).addChild(arrow);
								}
							}
							arrow.current_surface = map.first_surface;
							my_sprites.add(arrow);
							break;
						case 1:
							clicks = 2;
							my_sprites.get(0).clearModifier();
							ScaleModifier scalemod = new ScaleModifier(0, 1, 3);
							ProgressModifier progressmod = new ProgressModifier(scalemod, 3000, Linear.getInstance(), this);
							ParallelModifier arrow_scaler = new ParallelModifier(progressmod);
							
							arrow_scaler.setLoop(true);
							my_sprites.get(0).addModifier(arrow_scaler);
							
							break;
						case 2:
							clicks = 0;
							if(cb.working==false && cb.splash==false){

								my_sprites.get(0).clearModifier();
								double angle = my_sprites.get(0).getAngle();
								double length = my_sprites.get(0).getWidthScaled();
								Log.i("arrow", "angle: " + angle + "length: "+ length + " maybe: "+ my_sprites.get(0).getScaleX());
								for(int l=0;l<map.map_layers.size();l++){
									if(l==0){
										map.map_layers.get(l).removeChild(my_sprites.get(0));
									}
								}
								this.getEngine().getScene().getTopLayer().remove(my_sprites.get(0));
								my_sprites.clear();
								this.getEngine().getScene().getTopLayer().remove(cb);
								cb = new Cannonball(map, texture2, clickable_rectangle.x+clickable_rectangle.width, clickable_rectangle.y+clickable_rectangle.height/2-cb.getHeight()+16, 10, 1,0);
								this.getEngine().getScene().getTopLayer().add(cb);
								cb.working = true;
								cb.action_timer = 0;
								cb.next_action = cb.getAction(SessionData.FLY);
								cb.doSomething();
							}
							break;
						}
						
					}
				}
			}
		}	
	}
	
	private void mapChange(Map next_map, TMXObject where_to_start){
		map = next_map;
		resume(); 
		int c;
		int r;
		for(int l=0;l<map.map_layers.size();l++){
			map.map_layers.get(l).setPosition(0,0);
			c = map.map_layers.get(l).getColumnAtPosition(map.clickable_rectangles.get(0).x);
			r = map.map_layers.get(l).getRowAtPosition(map.clickable_rectangles.get(0).y);
			TMXTile tile_of_rectangle = map.map_layers.get(l).getTileAt(c, r);
			if(tile_of_rectangle.on_highest_surface==true){
				sprite.current_surface = tile_of_rectangle.surface;
				break;
			}
		}
		
		for(int l=0;l<map.map_layers.size();l++){
			map.map_layers.get(l).scroll(where_to_start.x-getWidth()/2, where_to_start.y-getHeight()/2);
		}
		for(TMXLayer vorlayer : map.in_front_layers){
			vorlayer.setPosition(0,0);
			vorlayer.scroll(where_to_start.x-getWidth()/2, where_to_start.y-getHeight()/2);
		}
		sprite.move(where_to_start.x - map.map_layers.get(0).getX(), where_to_start.y - map.map_layers.get(0).getY());
	} 
	
	
	
	private boolean sailorDragAndDrop(E3Scene scene, MotionEvent motion_event){
		if (motion_event.getAction() == MotionEvent.ACTION_DOWN) {
			if(sailors_symbol.contains(getTouchEventX(scene, motion_event), getTouchEventY(scene, motion_event))){
				sailor_drag_drop = true;
				if(sailors_symbol2==null){
					Texture sailors_symbol_texture  =  new AssetTexture("HUD_sailor.png", this);
					sailors_symbol2 = new Sprite( sailors_symbol_texture, 0, 0);
					scene.getTopLayer().add(sailors_symbol2);
				}
				return true;
			}
		}
		if(motion_event.getAction() == MotionEvent.ACTION_MOVE && sailor_drag_drop){
			sailors_symbol2.move(getTouchEventX(scene, motion_event), getTouchEventY(scene, motion_event));
			return true;
		}
		if(motion_event.getAction() == MotionEvent.ACTION_UP && sailor_drag_drop){	
			scene.getTopLayer().remove(sailors_symbol2);
			sailors_symbol2=null;
			sailor_drag_drop = false; 
			return true;
		}			
		return false;
	}
	
	private void turnAndMove(int toX, int toY){
		diff_x = toX - sprite.getRealX();
		diff_y = toY - sprite.getRealY();
		sprite.getDirection(diff_x, diff_y);
	}

	  
	private void moveTheScene(final Sprite sprite, final int xstep, final int ystep) {
		boolean scroll = false;
		int confirmed_x = 0;
		int confirmed_y = 0;
		if(	(xstep >  0 && sprite.getRealX() + getWidth() / 2 + sprite.getWidth() / 2 > getWidth()) || 
			(xstep <= 0 && sprite.getRealX() - getWidth() / 2 + sprite.getWidth() / 2 < 0)){
			scroll = true;
			confirmed_x = xstep;
		}
		
		if(	(ystep >  0 && sprite.getRealY() + getHeight() / 2 + sprite.getHeight() / 2 > getHeight()) || 
			(ystep <= 0 && sprite.getRealY() - getHeight() / 2 + sprite.getHeight() / 2 < 0)){
			scroll=true;
			confirmed_y=ystep;
		}
		
		if(scroll==true){ 
			for (TMXLayer layer : map.map_layers) {
				layer.scroll(layer.getX() + confirmed_x, layer.getY()+confirmed_y);
			}
			for (TMXLayer layer : map.in_front_layers) {
				layer.scroll(layer.getX() + confirmed_x, layer.getY()+confirmed_y);
			}
		}	
	}

	@Override
	public void onUpdateScene(E3Scene scene, final long elapsedMsec) {
		runOnUiThread(new Runnable() { 
			@Override
			public void run() {
				postUpdate(new Runnable() {
					@Override
					public void run() {
		            	if(loaded==true){
							int small_x = (int) Math.round(diff_x/60);
							int small_y = (int) Math.round(diff_y/40);
							surface_changed = sprite.check(null, small_x, small_y, diff_x, diff_y);
							if(sprite.working){
								if(works_on!=null){
									for(Event event : works_on.events){
										event.startEvent(sprite.action_timer);
									}
								}
								if(sprite.action_timer > sprite.actual_action.getDisruptionTime()){
					  				sprite.working = false;
					  				sprite.stop();
					  				sprite.action_timer=0;
					  			}
							}
							cb.fly();  
							moveTheScene(sprite, small_x, small_y);
							if(surface_changed){
								changeEbenen();
							}
							elapsed_time++;
							cb.action_timer++;
							sprite.action_timer++;
		            	}
              }}); 
		}});
	}
	
	public void changeEbenen(){
		ArrayList<Drawable> newscene = new ArrayList<Drawable>();
		
		boolean done=false;
		for(int l = 0; l<map.map_layers.size();l++){
			loop:
			for(Surface inner_surface : sprite.current_surface.inner_surfaces){
				for(Surface inner_inner_surface : inner_surface.inner_surfaces){
					if(map.map_layers.get(l).equals(inner_inner_surface.layer)){
						done=true;
						newscene.add(sprite); 
						break loop;
					}
				}
			}
			if(done==false && l == map.map_layers.size() - 1){
				newscene.add(sprite);
			}
			newscene.add(map.map_layers.get(l));
		}
		
		//TODO
		newscene.add(cb);
		for(MySprite sprite : my_sprites){
			newscene.add(sprite);
		}
		for(TMXLayer vorlayer : map.in_front_layers){
			newscene.add(vorlayer);
		}
		engine.getScene().getTopLayer().setDrawables(newscene);
	}
	
	public void finishedToast(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getContext(), "finished", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	public void downloadFile() {
	    FTPClient ftpClient = new FTPClient();
	    try {
	    	ftpClient.connect(SessionData.server, SessionData.port);
	        ftpClient.login(SessionData.user, SessionData.pass);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        String remoteFile = "map.txt";
	        File downloadFile = new File(getFilesDir(), "map1.txt");
	        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
	        boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
	        outputStream.close();
	        if (success) {
	        	Log.i("StandardGameScreenActivity","File has been downloaded successfully.");
	        }
	    } catch (IOException ex) {
	    	System.out.println("Error: " + ex.getMessage());
	        ex.printStackTrace();
	    } finally {
	        try {
	            if (ftpClient.isConnected()) {
	                ftpClient.logout();
	                ftpClient.disconnect();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

	@Override
	public void onModifierStart(ShapeModifier modifer, Shape shape) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifierFinished(ShapeModifier modifier, Shape shape) {
		// TODO Auto-generated method stub
		
	}
}

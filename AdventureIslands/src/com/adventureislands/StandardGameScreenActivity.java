package com.adventureislands;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.datastructure.Ebene;
import com.datastructure.objects.Event;
import com.datastructure.objects.Exit;
import com.datastructure.objects.Objekt;
import com.datastructure.objects.Rank;
import com.datastructure.objects.Shovel;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXTile;
import com.datastructure.tmx.TMXTiledMap;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Drawable;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.modifier.AxisMoveModifier;
import com.e3roid.drawable.modifier.MoveModifier;
import com.e3roid.drawable.modifier.ProgressModifier;
import com.e3roid.drawable.modifier.ShapeModifier;
import com.e3roid.drawable.modifier.function.AcceleroIn;
import com.e3roid.drawable.modifier.function.BackIn;
import com.e3roid.drawable.modifier.function.BounceIn;
import com.e3roid.drawable.modifier.function.BounceInOut;
import com.e3roid.drawable.modifier.function.BounceOut;
import com.e3roid.drawable.modifier.function.ElasticIn;
import com.e3roid.drawable.modifier.function.ElasticInOut;
import com.e3roid.drawable.modifier.function.Linear;
import com.e3roid.drawable.modifier.function.Progressive;
import com.e3roid.drawable.modifier.function.SineIn;
import com.e3roid.drawable.modifier.function.SineInOut;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.ModifierEventListener;
import com.e3roid.event.SceneUpdateListener;



public class StandardGameScreenActivity extends E3Activity  implements  SceneUpdateListener{
	private int width;
	private int height;
	private int difX = 0;
	private int difY = 0;
	private Map map;
	private boolean ebeneChanged=false;
	private ArrayList<AutomatedSprite> automated_sprites = new ArrayList<AutomatedSprite>();
	private TiledTexture texture;
	private TiledTexture texture2;
	private Sailor sprite;
	private Cannonball cb;
	private int elapsedTime=0;
	private int actionTimer=0;
	private int clickTime=0;
	private Objekt isworkingOn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {    
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.height=320;
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
	public E3Engine onLoadEngine() {

		E3Engine engine = new E3Engine(this, width, height);
		engine.requestFullScreen();
		engine.requestLandscape();
		
		return engine;
	} 

	@Override
	public E3Scene onLoadScene() {
		E3Scene scene = new E3Scene();
		map = SessionData.instance().map; 
		if(map!=null){
			scene.addEventListener(this);
			scene.registerUpdateListener(20, this);
			texture = new TiledTexture("pirate.png", 32, 32, 0, 0, 0, 0, this);
			sprite = new Sailor(texture, 0, 200);
			
			texture2 = new TiledTexture("cannonball.png", 20, 20, 0, 0, 0, 0, this);
			cb = new Cannonball(texture2, sprite.getRealX(), sprite.getRealY());
			//automated_sprites.add(cb);
			
			for(int l=0;l<map.getMapLayers().size();l++){
				map.getMapLayers().get(l).setSceneSize(getWidth(), getHeight());
			}	
			
			for(int l=0;l<map.getMapLayers().size();l++){
				if(l==0){
					map.getMapLayers().get(l).addChild(sprite);
					map.getMapLayers().get(l).addChild(cb);
				}
				scene.getTopLayer().add(map.getMapLayers().get(l)); //adds to loadable Drawables
			}
			scene.getTopLayer().add(sprite);
			scene.getTopLayer().add(cb);
			cb.setOnLayer(true);
			for(TMXLayer vorlayer : map.getObjektvorLayers()){
				vorlayer.setSceneSize(getWidth(), getHeight());
				scene.getTopLayer().add(vorlayer);
			}
			sprite.setParameter(map.getFirstEbene(), map.getCollisionRectangles(), map.getMapLayers());
			
			
		}
		return scene;
	}

	@Override
	public void onLoadResources() {
	}
	
	@Override
	public boolean onSceneTouchEvent(E3Scene scene, MotionEvent motionEvent) {
		
		if(loaded==true){
			if(!sprite.isWorking()){
				int toX = getTouchEventX(scene, motionEvent) - (texture.getTileWidth()  / 2);
				int toY = getTouchEventY(scene, motionEvent) - (texture.getTileHeight() / 2);
				if (motionEvent.getAction() == MotionEvent.ACTION_MOVE || motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					if (sprite != null) {
						difX = toX - sprite.getRealX();
						difY = toY - sprite.getRealY();
						sprite.getDirection(difX, difY);
						
					}
				}
				else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
					sprite.stop();
					sprite.resetDirection();
					difX=0;
					difY=0;
				}
				
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
					if(clickTime+25>elapsedTime){
						for(TMXObject clickableRectangle : map.getClickableRectangles().getObjects()){
							Rect rectOfClickableObject = clickableRectangle.getRectangle(map.mapLayers.get(0));
							Log.i("test1", "rectOfClickableObject" + rectOfClickableObject.bottom +" "+ rectOfClickableObject.left+" " + rectOfClickableObject.right + " " +rectOfClickableObject.top);
							Log.i("test1", "tox " + toX + "toy "+toY);
							if(rectOfClickableObject.contains(toX, toY)){
								Log.i("test2", "test123");
								if(sprite.intersects(rectOfClickableObject)){
									Log.i("test3", "test123");
									if(clickableRectangle.getZugehoerigesObjekt().isPickable()){
										Log.i("test4", "test123");
										for(TMXLayer layer : clickableRectangle.getZugehoerigesObjekt().getFragment().getLayers()){
											layer.getTileAt(0, 0).setEmpty();
										}
										sprite.setEquipped(clickableRectangle.getZugehoerigesObjekt());
									}
									else if(clickableRectangle.getZugehoerigesObjekt() instanceof Exit){
										pause();
										this.loaded=false;
										mapChange(map.getInnerMap(),map.getInnerMap().getClickableRectangles().getObjects().get(0));
									} 
									else if(clickableRectangle.getZugehoerigesObjekt() instanceof Rank){
										pause();
										this.loaded=false;
										mapChange(map.getOuterMap(), map.getOuterMap().getClickableRectangles().getObjects().get(map.getOuterMap().getClickableRectangles().getObjects().size()-1));
										Log.i("AdventureLog tmppath of exit",""+map.getClickableRectangles().getObjects().size());
									} 
									else if (clickableRectangle.getZugehoerigesObjekt().isDoable() && (sprite.getEquipped() instanceof Shovel)){
										isworkingOn = clickableRectangle.getZugehoerigesObjekt();
										isworkingOn.setupEvents();
										sprite.setWorking(true);
										actionTimer=0;
										sprite.setNextAction(sprite.getAction(SessionData.DIG));
										sprite.doSomething();
									}
								}
							}
						}	
					}
					clickTime=elapsedTime;
				}
			}
		}
		return true;
	}
	
	private void mapChange(Map nextMap, TMXObject whereToStart){
		SessionData.instance().map = nextMap;
		resume(); 
		int c;
		int r;
		for(int l=0;l<map.getMapLayers().size();l++){
			map.getMapLayers().get(l).setPosition(0,0);
			c = map.getMapLayers().get(l).getColumnAtPosition(map.getClickableRectangles().getObjects().get(0).getX());
			r = map.getMapLayers().get(l).getRowAtPosition(map.getClickableRectangles().getObjects().get(0).getY());
			TMXTile tile_of_rectangle = map.getMapLayers().get(l).getTileAt(c, r);
			if(tile_of_rectangle.on_highest_ebene==true){
				sprite.setAktuelleEbene(tile_of_rectangle.ebene);
				break;
			}
		}
		
		for(int l=0;l<map.getMapLayers().size();l++){
			map.getMapLayers().get(l).scroll(whereToStart.getX()-getWidth()/2, whereToStart.getY()-getHeight()/2);
		}
		for(TMXLayer vorlayer : map.getObjektvorLayers()){
			vorlayer.setPosition(0,0);
			vorlayer.scroll(whereToStart.getX()-getWidth()/2, whereToStart.getY()-getHeight()/2);
		}
		sprite.move(whereToStart.getX() - map.getMapLayers().get(0).getX(), whereToStart.getY() - map.getMapLayers().get(0).getY());
	} 

	  
	private void moveTheScene(final Sprite sprite, final int xstep, final int ystep) {

		boolean scroll=false;
		int acceptedx=0;
		int acceptedy=0;
		if((xstep>0 && sprite.getRealX() + getWidth()/2 + sprite.getWidth()/2 > getWidth()) || (xstep<=0 && sprite.getRealX() - getWidth()/2 + sprite.getWidth()/2 < 0)){
			scroll=true;
			acceptedx=xstep;
		}
		
		if((ystep>0 && sprite.getRealY() + getHeight()/2 + sprite.getHeight()/2  > getHeight()) || (ystep<=0 && sprite.getRealY() - getHeight()/2 + sprite.getHeight()/2 < 0)){
			scroll=true;
			acceptedy=ystep;
		}
		if(scroll==true){ 
			
			
			for (TMXLayer layer : map.getMapLayers()) {
				layer.scroll(layer.getX() + acceptedx, layer.getY()+acceptedy);
			}
			for (TMXLayer layer : map.getObjektvorLayers()) {
				layer.scroll(layer.getX() + acceptedx, layer.getY()+acceptedy);
			}
			
			
        		
		}	
		
	}
	
	/*@Override
	public void onResume(){
		loaded=false;
		map = null;
		sprite = null;
		texture = null;
		texture2 = null; 
		isworkingOn=null; 
		surfaceView.destroyDrawingCache();
		surfaceView=null;
		engine = null;
		engine = onLoadEngine();
		onSetContentView();
		System.gc(); 
		this.resume(); 
		super.onResume();
	}*/
	

	@Override
	public void onUpdateScene(E3Scene scene, final long elapsedMsec) {
		runOnUiThread(new Runnable() { 
			@Override
			public void run() {
				postUpdate(new Runnable() {
					@Override
					public void run() {
		            	if(loaded==true){
							int smallerX = (int) Math.round(difX/60);
							int smallerY = (int) Math.round(difY/40);
							ebeneChanged = sprite.check(null,smallerX,smallerY,difX,difY);
							
							if(sprite.isWorking()){
								if(isworkingOn!=null){
									for(Event event : isworkingOn.getTileEvents()){
										event.startEvent(actionTimer);
									}
								}
								if((actionTimer>sprite.getActualAction().getDisruptionTime())){
					  				sprite.setWorking(false);
					  				sprite.stop();
					  				actionTimer=0;
					  			}
							}
							
							moveTheScene(sprite, smallerX,smallerY);
							if(ebeneChanged){
								changeEbenen();
							}
							elapsedTime++;
							actionTimer++;
		            	}
              }}); 
		}});
	}
	
	public void changeEbenen(){
		Log.i("ebene","changed");
		ArrayList<Drawable> newscene = new ArrayList<Drawable>();
		
		boolean done=false;
		for(int l = 0; l<map.getMapLayers().size();l++){
			if(map.getMapLayers().get(l).getName().equals("" + (Integer.parseInt(sprite.getAktuelleEbene().getLayer().getName())+2))){
				done=true;
				newscene.add(sprite); 
			}
			if(done==false && l == map.getMapLayers().size()-1){
				newscene.add(sprite);
			}
			newscene.add(map.getMapLayers().get(l));
		}
		for(TMXLayer vorlayer : map.getObjektvorLayers()){
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

}

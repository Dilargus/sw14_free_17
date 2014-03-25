package com.adventureislands;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

import com.datastructure.Ebene;
import com.datastructure.objects.Objekt;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXObjectGroup;
import com.datastructure.tmx.TMXTile;
import com.e3roid.drawable.Drawable;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;

public class Sailor extends AnimatedSprite {

	private int direction;
	private Action actualAction;
	private Action nextAction;
	private ArrayList<Action> actions;
	private Ebene aktuelleEbene;
	private TMXObjectGroup collisionObjects;
	private ArrayList<TMXLayer> mapLayers;
	private boolean ebeneChanged=false;
	private Objekt equipped;
	private boolean working = false;
	
	
	


	public boolean isWorking() {
		return working;
	}


	public void setWorking(boolean working) {
		this.working = working;
	}


	public Objekt getEquipped() {
		return equipped;
	}
	

	public void setEquipped(Objekt equipped) {
		this.equipped = equipped; 
	}
	
	public Sailor(TiledTexture texture, int x, int y) {
		super(texture, x, y);
		actions = new ArrayList<Action>();
		
		for(int i = SessionData.DIG;i<=SessionData.DO_NOTHING;i++){
			actions.add(new Action(i));
		}
		actualAction = getAction(SessionData.DO_NOTHING);
		nextAction =  getAction(SessionData.DO_NOTHING);
	}
	
	public Action getAction(int identifier){
		Log.i("AdventureLog size",""+actions.size());
		for(Action action : actions){
  			if(action.getIdentifier()==identifier){
  				return action;
  			}
  		}
		return null;
	}

	public TMXObjectGroup getCollisionObjects() {
		return collisionObjects;
	}

	public void setCollisionObjects(TMXObjectGroup collisionObjects) {
		this.collisionObjects = collisionObjects;
	}

	public ArrayList<TMXLayer> getMapLayers() {
		return mapLayers;
	}

	public void setMapLayers(ArrayList<TMXLayer> mapLayers) {
		this.mapLayers = mapLayers;
	}

	public void setAktuelleEbene(Ebene aktuelleEbene) {
		this.aktuelleEbene = aktuelleEbene;
	}

	@Override
	public Rect getCollisionRect() {
		// king's collision rectangle is just around his body.
		Rect rect = this.getRect();
		rect.inset(7, 0);
		rect.top = rect.top + 20;
		return rect;
	}
	
	public Rect getActionRect() {
		// king's collision rectangle is just around his body.
		Rect rect = this.getRect();
		//rect.inset(7, 0);
		//rect.top = rect.top + 20;
		return rect;
	}
	
	public void collision(AnimatedSprite sprite2){
		moveIntoScene();
		float angle = this.getAngle();
		if(angle<22.5 || angle>=337.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(0, -5);
			}
		}
		else if(angle>=22.5 && angle<67.5){
			if(collidesWithSprite(this,sprite2)){	
				this.moveRelative(5, -5);
			}
		}
		else if(angle>=67.5 && angle<112.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(5, 0);
			}
		}
		else if(angle>=112.5 && angle<157.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(5, 5);
			}
		}
		else if(angle>=157.5 && angle<202.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(0, 5);
			}
		}
		else if(angle>=202.5 && angle<247.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(-5, 5);
			}
		}
		else if(angle>=247.5 && angle<292.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(-5, 0);
			}
		}
		else if(angle>=292.5 && angle<337.5){
			if(collidesWithSprite(this,sprite2)){
				this.moveRelative(-5, -5);
			}
		}
	}
	
	public void setParameter(Ebene aktuelleEbene,
			TMXObjectGroup collisionObjects, ArrayList<TMXLayer> mapLayers){
		this.collisionObjects=collisionObjects;
		this.aktuelleEbene=aktuelleEbene;
		this.mapLayers=mapLayers;
	}
	
	public Action getActualAction() {
		return actualAction;
	}


	public Action getNextAction() {
		return nextAction;
	}


	public void setNextAction(Action nextAction) {
		this.nextAction = nextAction;
	}


	public void setActualAction(Action actualAction) {
		this.actualAction = actualAction;
	}


	public ArrayList<Action> getActions() {
		return actions;
	}


	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}


	public void getDirection(final int difx, final int dify){
		if(Math.abs(dify)>=Math.abs(difx)){
          	if(dify>0 && direction!=1){
          		nextAction = getAction(SessionData.GO_DOWN);
          		doSomething();
          		direction=1;
          	}
          	else if(dify<=0 && direction!=2){
          		nextAction = getAction(SessionData.GO_UP);
          		doSomething();
          		direction=2;
          	}
        }
		else{
          	if(difx>0 && direction!=3){
          		nextAction = getAction(SessionData.GO_RIGHT);
          		doSomething();
          		direction=3;
          	}
          	else if(difx<0 && direction!=4){
          		nextAction = getAction(SessionData.GO_LEFT);
          		doSomething();
          		direction=4;
          	}
        }
		
	}
	
	public void resetDirection(){
		direction = 0;
	}
	
	private boolean isInTheScene(int xstep, int ystep) {
		int x = this.getRealX() + xstep;
		int y = this.getRealY() + ystep;
		return x > 0 && y > 0 && x < getWidth()  - this.getWidth() &&
				y < getHeight() - this.getHeight();
	}
	
	private boolean collidesWithObject(int xstep, int ystep){
		if (collisionObjects == null) return false;
		for(TMXObject object : collisionObjects.getObjects()){
			Rect objectrect = object.getRectangle(mapLayers.get(0));
			if(colliding(objectrect,xstep,ystep)) return true;
		}
		return false;
	} 
	
	public void doSomething(){
		actualAction = nextAction;

		if(actualAction.getLoops()==0){
			this.animate(actualAction.getDuration(), actualAction.getFrames());
		}
		else{
			this.animate(actualAction.getDuration(),actualAction.getLoops(), actualAction.getFrames());
		}
	}

	public boolean colliding(Rect rectB,int xstep,int ystep) {
		Rect rectA = this.getCollisionRect();
		return (rectA.left + xstep < rectB.right && rectB.left < rectA.right + xstep
				&& rectA.top + ystep < rectB.bottom && rectB.top < rectA.bottom+ ystep);
	}

	private boolean collidesWithSprite(AnimatedSprite sprite, AnimatedSprite sprite2) {
		if(sprite2.collidesWith(sprite.getCollisionRect())){
			return true;
		}
		else{
			return false;
		}
	}
	
	private void moveIntoScene() {
		if(this.getRealX()<0){
			this.moveRelativeX(-this.getRealX());
		}
		else if(this.getRealX()+this.getWidth()/2>getWidth()){
			this.moveRelativeX(getWidth()-(this.getRealX()+this.getWidth()));
		}
		if(this.getRealY()<0){
			this.moveRelativeY(-this.getRealY());
		}
		else if(this.getRealY()+this.getHeight()/2>getHeight()){
			this.moveRelativeY(getHeight()-(this.getRealY()+this.getHeight()));
		}
	}
	
	private boolean collidesWithTile(int xstep, int ystep) {
		//ArrayList<Ebene> ebenen = map.getAllEbenen();
		Ebene nextEbene=null;
		boolean gleicheEbene=false;
		boolean höhereEbene=false;
		boolean niedrigereEbene=false;
		boolean isblocked=false; 
		loop:
		for(Ebene innereEbene : aktuelleEbene.getInnerEbenen()){
			for(TMXTile tile : innereEbene.getLayer().getTileFromRect(this.getCollisionRect(), xstep, ystep)){ 				
				if(innereEbene.getFeinerRahmen().contains(tile) && tile.joints.contains(aktuelleEbene.getEbenenindex())){
					Log.i("auf höhere","aktuell: " + aktuelleEbene.ebenenindex + "joints " + tile.joints + "höhereEbene :"+innereEbene.ebenenindex);
					höhereEbene=true;
					nextEbene=innereEbene; 
					break loop;
				}
				else if(innereEbene.getFeinerRahmen().contains(tile)){
					Log.i("blocked","aktuell: " + aktuelleEbene.ebenenindex + "joints " + tile.joints + "höhereEbene :"+innereEbene.ebenenindex);
					isblocked=true;
				}  
			}
		}
		
		 
		if(höhereEbene){
			ändereAktuelleEbene(nextEbene);
			return false;
		}
		else if(isblocked){
			return true;
		}
		else{  
			
			for(TMXTile tile : aktuelleEbene.getLayer().getTileFromRect(this.getCollisionRect(), xstep, ystep)){
				if(tile.ebene==aktuelleEbene){
					gleicheEbene=true;
					break;
				}
			}
		}
		 
		if(gleicheEbene){
			return false;
		}
		else{
			loop:
			for(TMXTile testtile : aktuelleEbene.getLayer().getTileFromRect(this.getCollisionRect(), -xstep, -ystep)){
				if(testtile.joints.contains(aktuelleEbene.getÄußereEbene().getEbenenindex())){
					for(TMXTile tile : aktuelleEbene.getÄußereEbene().getLayer().getTileFromRect(this.getCollisionRect(), xstep, ystep)){
						if(tile.ebene==aktuelleEbene.getÄußereEbene()){
							Log.i("auf niedriegere","aktuell: " + aktuelleEbene.ebenenindex + "joints " + tile.joints + "niedrigereEbene :"+aktuelleEbene.getÄußereEbene().ebenenindex);
							niedrigereEbene=true;
							nextEbene= aktuelleEbene.getÄußereEbene();
							break loop;
						}
					}
				}
			}
		}
		if(niedrigereEbene){
			ändereAktuelleEbene(nextEbene);
			return false;
		}
		else{
			Log.i("totally","imfucked");
			return true;
		}
	}
	


	public Ebene getAktuelleEbene() {
		return aktuelleEbene;
	}


	private void ändereAktuelleEbene(Ebene neueEbene){
		ebeneChanged=true;
		aktuelleEbene = neueEbene;
	}
	
	public boolean check(ArrayList<AnimatedSprite> otherSprites,int smallerX, int smallerY, int difX,int difY){
		if(!collidesWithObject(smallerX, smallerY) && !collidesWithTile(smallerX,smallerY) && difX!=0 && difY!=0){
			this.moveRelative(smallerX,smallerY);
			
			//collision(otherSprites.get(0));	
		}
		if(ebeneChanged==true){
			ebeneChanged=false;
			return true;
		}
		else{
			return false;
		} 
	}
	
	public boolean intersects(Rect rectB) {
		Rect rectA = this.getActionRect();
		
		return (rectA.left < rectB.right && rectB.left < rectA.right 
				&& rectA.top < rectB.bottom && rectB.top < rectA.bottom) || 
				(rectB.left < rectA.right && rectA.left < rectB.right 
				&& rectB.top < rectA.bottom && rectA.top < rectB.bottom);
	}
}

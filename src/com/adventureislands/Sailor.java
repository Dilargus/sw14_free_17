package com.adventureislands;

import java.util.ArrayList;
import android.graphics.Rect;
import com.datastructure.Surface;
import com.datastructure.objects.Object;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXTile;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;

public class Sailor extends MySprite {

	public int direction;
	public Object equipped_object;
	
	
	public Sailor(Map map,TiledTexture texture, int x, int y) {
		super(map, texture, x, y);
		actions = new ArrayList<Action>();
		for(int i = SessionData.DIG; i <= SessionData.DO_NOTHING; i++){
			actions.add(new Action(i));
		}
		actual_action = getAction(SessionData.DO_NOTHING);
		next_action =  getAction(SessionData.DO_NOTHING);
	}
	
	@Override
	public Rect getCollisionRect() {
		// king's collision rectangle is just around his body.
		Rect rect = this.getRect();
		rect.inset(7, 0);
		rect.top = rect.top + 20;
		return rect;
	}
	
	@Override
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

	public void getDirection(final int dif_x, final int dif_y){
		if(Math.abs(dif_y) >= Math.abs(dif_x)){
          	if(dif_y > 0 && direction != 1){
          		next_action = getAction(SessionData.GO_DOWN);
          		doSomething();
          		direction=1;
          	}
          	else if(dif_y <= 0 && direction != 2){
          		next_action = getAction(SessionData.GO_UP);
          		doSomething();
          		direction=2;
          	}
        }
		else{
          	if(dif_x > 0 && direction != 3){
          		next_action = getAction(SessionData.GO_RIGHT);
          		doSomething();
          		direction=3;
          	}
          	else if(dif_x < 0 && direction != 4){
          		next_action = getAction(SessionData.GO_LEFT);
          		doSomething();
          		direction=4;
          	}
        }
		
	}
	
	public void resetDirection(){
		direction = 0;
	}
	
}

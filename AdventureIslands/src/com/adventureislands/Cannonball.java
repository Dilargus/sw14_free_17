package com.adventureislands;

import android.graphics.Rect;
import android.widget.Toast;

import com.e3roid.drawable.Shape;
import com.e3roid.drawable.modifier.AxisMoveModifier;
import com.e3roid.drawable.modifier.ProgressModifier;
import com.e3roid.drawable.modifier.ShapeModifier;
import com.e3roid.drawable.modifier.function.Progressive;
import com.e3roid.drawable.modifier.function.SineInOut;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.ModifierEventListener;

public class Cannonball extends AutomatedSprite {

	private int flyheight = 50;
	
	public Cannonball(TiledTexture texture, int x, int y) {
		super(texture, x, y);
	}

	public Rect getActionRect() {
		Rect rect = this.getRect();
		return rect;
	}
	
	public void fly(int fromX, int fromY,int range){
		this.addModifier(new ProgressModifier(
				new AxisMoveModifier(
						fromX, fromX, fromX+range,
						AxisMoveModifier.AXIS_X),
						20*range, SineInOut.getInstance(),Cannonball.this));
		this.addModifier(new ProgressModifier(
				new AxisMoveModifier(
						fromY, fromY-flyheight, fromY,
						AxisMoveModifier.AXIS_Y),
						20*range, CannonBallModifier.getInstance()));
	}
	
	public boolean intersects(Rect rectB) {
		Rect rectA = this.getActionRect();
		
		return (rectA.left < rectB.right && rectB.left < rectA.right 
				&& rectA.top < rectB.bottom && rectB.top < rectA.bottom) || 
				(rectB.left < rectA.right && rectA.left < rectB.right 
				&& rectB.top < rectA.bottom && rectA.top < rectB.bottom);
	}
}

package com.adventureislands;

import android.graphics.Rect;
import android.util.Log;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.modifier.ShapeModifier;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.ModifierEventListener;

public class AutomatedSprite extends AnimatedSprite implements ModifierEventListener  {

	private Boolean gets_modificated=false;
	private Boolean is_on_Layer=false;
	
	public Boolean IsOnLayer() {
		return is_on_Layer;
	}


	public void setOnLayer(Boolean is_on_Layer) {
		this.is_on_Layer = is_on_Layer;
	}


	public AutomatedSprite(TiledTexture texture, int x, int y) {
		super(texture, x, y);
	}


	public Boolean getsModificated() {
		return gets_modificated;
	}


	public void setModificated(Boolean modificated) {
		this.gets_modificated = modificated;
	}


	@Override
	public Rect getCollisionRect() {
		Rect rect = this.getRect();
		return rect;
	}
	
	@Override
	public void onModifierStart(ShapeModifier modifer, Shape shape) {
		gets_modificated=true;
		Log.i("modified","true");
	}

	@Override
	public void onModifierFinished(ShapeModifier modifier, Shape shape) {
		gets_modificated=false;
		Log.i("modified","false");
	}	
}

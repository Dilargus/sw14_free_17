package com.adventureislands;

import java.util.ArrayList;
import android.graphics.Rect;
import android.util.Log;

import com.datastructure.Surface;
import com.datastructure.objects.Object;
import com.datastructure.tmx.TMXLayer;
import com.datastructure.tmx.TMXObject;
import com.datastructure.tmx.TMXTile;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.TiledTexture;

public abstract class MySprite extends AnimatedSprite {

	public Action actual_action;
	public Action next_action;
	public ArrayList<Action> actions;
	public Surface current_surface;
	public Map map;
	public boolean surface_changed=false;
	public boolean working = false;
	public int action_timer=0;
	
	
	public MySprite(Map map, TiledTexture texture, int x, int y) {
		super(texture, 0, 0);
		this.map = map;
		moveGlobal(x, y);
	}
	
	public Action getAction(int identifier){
		for(Action action : actions){
  			if(action.getIdentifier()==identifier){
  				return action;
  			}
  		}
		return null;
	}

	@Override
	abstract public Rect getCollisionRect() ;
	
	abstract public Rect getActionRect();
	
	public boolean isInTheScene(int x_step, int y_step) {
		int x = this.getRealX() + x_step;
		int y = this.getRealY() + y_step;
		return x > 0 && y > 0 && x < getWidth()  - this.getWidth() &&
				y < getHeight() - this.getHeight();
	}
	
	public boolean collidesWithObject(int x_step, int y_step){
		if (map.collision_rectangles == null){ 
			return false;
		}
		for(TMXObject object : map.collision_rectangles){
			Rect objectrect = object.getRectangle(map.map_layers.get(0));
			if(colliding(objectrect, x_step, y_step)){ 
				return true;
			}
		}
		return false;
	} 
	
	public void doSomething(){
		actual_action = next_action;

		if(actual_action.getLoops()==0){
			this.animate(actual_action.getDuration(), actual_action.getFrames());
		}
		else{
			this.animate(actual_action.getDuration(),actual_action.getLoops(), actual_action.getFrames());
		}
	}

	public boolean colliding(Rect rectangle_b, int x_step, int y_step) {
		Rect rectangle_a = this.getCollisionRect();
		return (rectangle_a.left + x_step < rectangle_b.right && rectangle_b.left < rectangle_a.right + x_step
				&& rectangle_a.top + y_step < rectangle_b.bottom && rectangle_b.top < rectangle_a.bottom+ y_step);
	}

	public boolean collidesWithSprite(AnimatedSprite sprite_a, AnimatedSprite sprite_b) {
		if(sprite_b.collidesWith(sprite_a.getCollisionRect())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void moveIntoScene() {
		if(this.getRealX() < 0){
			this.moveRelativeX( - this.getRealX());
		}
		else if(this.getRealX() + this.getWidth() / 2 > getWidth()){
			this.moveRelativeX(getWidth() - (this.getRealX() + this.getWidth()));
		}
		if(this.getRealY() < 0){
			this.moveRelativeY( - this.getRealY());
		}
		else if(this.getRealY() + this.getHeight() / 2 > getHeight()){
			this.moveRelativeY(getHeight() - (this.getRealY() + this.getHeight()));
		}
	}
	
	public boolean collidesWithTile(int x_step, int y_step) {  
		Surface next_surface = null;
		boolean same_surface = false;
		boolean higher_surface = false;
		boolean lower_surface = false;
		boolean is_blocked = false; 
		
		loop:
		for(Surface inner_surface : current_surface.inner_surfaces){
			for(TMXTile tile : inner_surface.layer.getTileFromRect(this.getCollisionRect(), x_step, y_step)){ 				
				if(inner_surface.border.contains(tile) && tile.joints.contains(current_surface.surface_index)){
					higher_surface=true;
					next_surface=inner_surface; 
					break loop;
				}
				else if(inner_surface.border.contains(tile)){
					is_blocked=true;
				}  
			}
		}

		if(higher_surface){
			changeCurrentSurface(next_surface);
			return false;
		}
		else if(is_blocked){
			return true;
		}
		else{  
			
			for(TMXTile tile : current_surface.layer.getTileFromRect(this.getCollisionRect(), x_step, y_step)){
				if(tile.surface==current_surface){
					same_surface=true;
					break;
				}
			}
		}
		 
		if(same_surface){
			return false;
		}
		else{
			loop:
			for(TMXTile tile_to_check : current_surface.layer.getTileFromRect(this.getCollisionRect(), -x_step, -y_step)){
				if(tile_to_check.joints.contains(current_surface.outer_surface.surface_index)){
					for(TMXTile tile : current_surface.outer_surface.layer.getTileFromRect(this.getCollisionRect(), x_step, y_step)){
						if(tile.surface==current_surface.outer_surface){
							lower_surface=true;
							next_surface= current_surface.outer_surface;
							break loop;
						}
					}
				}
			}
		}
		if(lower_surface){
			changeCurrentSurface(next_surface);
			return false;
		}
		else{
			return true;
		}
	}

	public void changeCurrentSurface(Surface new_surface){
		surface_changed=true;
		current_surface = new_surface;
	}
	
	public boolean check(ArrayList<AnimatedSprite> other_sprites,int small_x, int small_y, int diff_x,int diff_y){
		if(!collidesWithObject(small_x, small_y) && !collidesWithTile(small_x,small_y) && diff_x!=0 && diff_y!=0){
			this.moveRelative(small_x,small_y);
			
			//collision(otherSprites.get(0));	
		}
		if(surface_changed==true){
			surface_changed=false;
			return true;
		}
		else{
			return false;
		} 
	}
	
	public boolean intersects(Rect rectangle_b) {
		Rect rectangle_a = this.getActionRect();
		
		return (rectangle_a.left < rectangle_b.right && rectangle_b.left < rectangle_a.right 
				&& rectangle_a.top < rectangle_b.bottom && rectangle_b.top < rectangle_a.bottom) || 
				(rectangle_b.left < rectangle_a.right && rectangle_a.left < rectangle_b.right 
				&& rectangle_b.top < rectangle_a.bottom && rectangle_a.top < rectangle_b.bottom);
	}
	
	
	public void moveAndSetCurrentSurface(TMXObject where_to_start, int scene_width, int scene_height){
		int middle_point_x = where_to_start.x + where_to_start.width/2;
		int middle_point_y = where_to_start.y + where_to_start.height/2;
		int c = map.map_layers.get(0).getColumnAtPosition(middle_point_x);
		int r = map.map_layers.get(0).getRowAtPosition(middle_point_y);
		for(int l=0;l<map.map_layers.size();l++){
			map.map_layers.get(l).setPosition(0,0);
			
			TMXTile tile_of_rectangle = map.map_layers.get(l).getTileAt(c, r);
			if(tile_of_rectangle.on_highest_surface==true){
				this.current_surface = tile_of_rectangle.surface;
				break;
			}
			
		}
		for(int l=0;l<map.map_layers.size();l++){
			map.map_layers.get(l).scroll(middle_point_x - scene_width / 2, middle_point_y - scene_height / 2);
		}
		for(TMXLayer vorlayer : map.in_front_layers){
			vorlayer.setPosition(0,0);
			vorlayer.scroll(middle_point_x - scene_width / 2, middle_point_y - scene_height / 2);
		}
		
		this.moveGlobal(where_to_start.x, where_to_start.y);
	}
	
	public void moveGlobal(int x, int y){
		this.move(0,0);
		this.moveRelative(x - map.map_layers.get(0).getX(), y - map.map_layers.get(0).getY());
	}
}

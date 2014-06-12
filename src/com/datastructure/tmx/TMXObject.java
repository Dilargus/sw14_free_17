package com.datastructure.tmx;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import android.graphics.Rect;
import com.datastructure.objects.Object;
import com.e3roid.util.SAXUtil;

public class TMXObject implements
java.io.Serializable{
	public final String name;
	public final String type;
	public int x;
	public int y;
	public final int width;
	public final int height;
	public Object corresponding_object;
	public ArrayList<TMXProperty> properties = new ArrayList<TMXProperty>();

	public TMXObject(Attributes attrs) {
		this.name = SAXUtil.getString(attrs, "name");
		this.type = SAXUtil.getString(attrs, "type");
		this.x    = SAXUtil.getInt(attrs, "x", 0);
		this.y    = SAXUtil.getInt(attrs, "y", 0);
		this.width  = SAXUtil.getInt(attrs, "width", 0);
		this.height = SAXUtil.getInt(attrs, "height", 0);
	}
	
	public TMXObject(String name, String type, int x, int y, int width, int height) {
		this.name = name;
		this.type = type;
		this.x    = x;
		this.y    = y;
		this.width  = width;
		this.height = height;
	}
	
	public Rect getRectangle(TMXLayer vorlageLayer){
		return new Rect(
				this.x-vorlageLayer.getX(),
				this.y-vorlageLayer.getY(),
				this.x-vorlageLayer.getX()+this.width, 
				this.y-vorlageLayer.getY()+this.height);
	}
	
}

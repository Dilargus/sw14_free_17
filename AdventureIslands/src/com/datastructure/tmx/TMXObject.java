package com.datastructure.tmx;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import android.graphics.Rect;

import com.datastructure.objects.Objekt;
import com.e3roid.util.SAXUtil;

public class TMXObject implements
java.io.Serializable{
	private final String name;
	private final String type;
	private int x;
	private int y;
	private final int width;
	private final int height;
	private Objekt zugehoerigesObjekt;
	private ArrayList<TMXProperty> properties = new ArrayList<TMXProperty>();
	
	
	public Objekt getZugehoerigesObjekt() {
		return zugehoerigesObjekt;
	}

	public void setZugehoerigesObjekt(Objekt zugehoerigesObjekt) {
		this.zugehoerigesObjekt = zugehoerigesObjekt;
	}

	public TMXObject(Attributes attrs) {
		this.name = SAXUtil.getString(attrs, "name");
		this.type = SAXUtil.getString(attrs, "type");
		this.x    = SAXUtil.getInt(attrs, "x", 0);
		this.y    = SAXUtil.getInt(attrs, "y", 0);
		this.width  = SAXUtil.getInt(attrs, "width", 0);
		this.height = SAXUtil.getInt(attrs, "height", 0);
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void addObjectProperty(TMXProperty prop) {
		this.properties.add(prop);
	}
	
	public ArrayList<TMXProperty> getObjectProperties() {
		return this.properties;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	public Rect getRectangle(TMXLayer vorlageLayer){
		return new Rect(
				this.getX()-vorlageLayer.getX(),
				this.getY()-vorlageLayer.getY(),
				this.getX()-vorlageLayer.getX()+this.getWidth(), 
				this.getY()-vorlageLayer.getY()+this.getHeight());
	}
	
}

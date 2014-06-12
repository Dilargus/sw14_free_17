package com.datastructure.tmx;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.e3roid.util.SAXUtil;

public class TMXObjectGroup implements
java.io.Serializable{
	private final String name;
	private final int width;
	private final int height;
	private ArrayList<TMXObject> tmxobjects = new ArrayList<TMXObject>();
	
	
	public TMXObjectGroup(Attributes attrs) {
		this.name = SAXUtil.getString(attrs, "name");
		this.width  = SAXUtil.getInt(attrs, "width", 0);
		this.height = SAXUtil.getInt(attrs, "height", 0);
	}
	public TMXObjectGroup(String name, int width,int height) {
		this.name = name;
		this.width  = width;
		this.height = height;
	}
	
	public ArrayList<TMXObject> getObjects() {
		return tmxobjects;
	}
	
	public void addObject(TMXObject object) {
		tmxobjects.add(object);
	}
	
	public String getName() {
		return name;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}

package com.datastructure.objects;

import com.datastructure.tmx.TMXTile;


public class Event implements  
java.io.Serializable{
	private int start;
	private TMXTile tileToChange;
	private int atlascolumn;
	private int atlasrow;
	private int GID;
	public Event(int start, TMXTile tileToChange, int atlascolumn, int atlasrow, int GID) {
		super();
		this.start = start;
		this.tileToChange = tileToChange;
		this.atlascolumn = atlascolumn;
		this.atlasrow = atlasrow;
		this.GID = GID;
	}
	public void setNull()
	{
		tileToChange=null;
	}
	
	
	
	public int getGID() {
		return GID;
	}
	
	public void startEvent(int time){
		if(start==time){
			tileToChange.setAtlas(atlascolumn, atlasrow);
			tileToChange.setGID(GID);
		}
	}



	public void setGID(int gID) {
		GID = gID;
	}



	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public TMXTile getTileToChange() {
		return tileToChange;
	}
	public void setTileToChange(TMXTile tileToChange) {
		this.tileToChange = tileToChange;
	}
	public int getAtlascolumn() {
		return atlascolumn;
	}
	public void setAtlascolumn(int atlascolumn) {
		this.atlascolumn = atlascolumn;
	}
	public int getAtlasrow() {
		return atlasrow;
	}
	public void setAtlasrow(int atlasrow) {
		this.atlasrow = atlasrow;
	}
	

	
	
	
}

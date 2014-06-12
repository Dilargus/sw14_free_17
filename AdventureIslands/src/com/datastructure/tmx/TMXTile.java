/*
 * Copyright (c) 2010-2011 e3roid project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of the project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.datastructure.tmx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.datastructure.Ebene;

import android.graphics.Point;
import android.graphics.Rect;

public class TMXTile implements
java.io.Serializable{
	private int gid;
	private int atlasColumn;
	private int atlasRow;
	public boolean free=false;
	public boolean on_highest_ebene=false;
	
	private transient final int column;
	private transient final int row;
	private transient final int width;
	private transient final int height;
	
	public transient int index;
	public transient int direction;
	
	public transient TMXLayer layer;
	public transient Ebene ebene;
	
	public transient ArrayList<Integer> ebenen_difference;
	public transient ArrayList<Integer> layer_difference; 
	public transient ArrayList<Integer> boden_arten;
	
	public ArrayList<Integer> joints = new ArrayList<Integer>();

	


	public TMXTile(int gid, int column, int row, 
			int atlasColumn, int atlasRow, int width, int height) {
		this.gid = gid;
		this.column = column;
		this.row    = row;
		this.atlasColumn = atlasColumn;
		this.atlasRow    = atlasRow;
		this.width = width;
		this.height = height;
	}
	
	public TMXTile(TMXTile tile) {
		this.gid = tile.gid;
		this.column = tile.column;
		this.row    = tile.row;
		this.atlasColumn = tile.atlasColumn;
		this.atlasRow    = tile.atlasRow;
		this.width = tile.width;
		this.height = tile.height;
		this.ebenen_difference=new ArrayList<Integer>();
		this.layer_difference=new ArrayList<Integer>();
		this.boden_arten=new ArrayList<Integer>();
		this.joints=new ArrayList<Integer>();
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public static TMXTile getEmptyTile(int column, int row) {
		return new TMXTile(0, column, row, -1, -1, 0, 0);
	}
	
	public static boolean isEmpty(TMXTile tile) {
		if (tile == null) return true;
		return tile.getGID() == 0;
	}
	
	public int getGID() {
		return this.gid;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getAtlasColumn() {
		return this.atlasColumn;
	}
	
	public int getAtlasRow() {
		return this.atlasRow;
	}
	
	public Rect getRect() {
		int left   = getColumn() * getWidth();
		int right  = left + getWidth();
		int top    = getRow() * getHeight();
		int bottom = top + getHeight();
		
		return new Rect(left, right, top, bottom);
	}
	
	public void setGID(int gid) {
		this.gid = gid;
	}
	
	public void setAtlas(int column, int row) {
		this.atlasColumn = column;
		this.atlasRow = row;
	}
	
	public void setEmpty() {
		this.gid = 0;
		this.setAtlas(0,0);
		ebenen_difference=null;
		layer_difference=null;
		boden_arten=null;
		joints=new ArrayList<Integer>();
	}
	
	public void copyProperties(TMXTile fromTile){
		
		if(fromTile.ebenen_difference!=null){
			this.ebenen_difference=fromTile.ebenen_difference;
		}
		if(fromTile.layer_difference!=null){
			this.layer_difference=fromTile.layer_difference;
		}
		if(fromTile.boden_arten!=null){
			this.boden_arten=fromTile.boden_arten;
		}
		if(fromTile.joints!=null){
			this.joints=fromTile.joints;
		}
	}
	
	public void resetProperties(){
		this.ebenen_difference=new ArrayList<Integer>();
		this.layer_difference=new ArrayList<Integer>();
		this.boden_arten=new ArrayList<Integer>();
		this.joints=new ArrayList<Integer>();
	}
}

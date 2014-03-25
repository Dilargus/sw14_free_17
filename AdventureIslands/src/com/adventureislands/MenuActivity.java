package com.adventureislands;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import com.datastructure.tmx.TMXTiledMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;  
	}

	public void loadMap(View v){
		Downloader downloader = new Downloader(this.getBaseContext());
		Map downloadedMap = null;
		try {
			downloadedMap = downloader.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		downloader=null;
		
		
		createMap creator = new createMap(downloadedMap,null,this.getBaseContext(),SessionData.SANDSTRAND,SessionData.INSEL,"large");
		downloadedMap=null;
		
		try {
			SessionData.instance().map = creator.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		creator=null;
		
		Intent intent = new Intent(this, StandardGameScreenActivity.class);
		//setting the resolution
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "320");
		startActivity(intent);
	}
	

	public void createMap(View v){
		
		createMap creator;
		creator = new createMap(null,this.getBaseContext(),SessionData.SANDSTRAND,SessionData.INSEL,"large");
		try {
			SessionData.instance().map = creator.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		creator = null;
		
		FileOutputStream fOut=null;
		try {
			fOut = openFileOutput("map.txt", MODE_WORLD_READABLE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Uploader uploader = new Uploader(this.getBaseContext(),SessionData.instance().map,fOut);
		TMXTiledMap nullifier = null;
		try {
			nullifier = uploader.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		fOut = null;
		nullifier = null;
		uploader = null;
		
		Intent intent = new Intent(this, StandardGameScreenActivity.class);  
		//setting the resolution
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "320");
		startActivity(intent);
	}
}

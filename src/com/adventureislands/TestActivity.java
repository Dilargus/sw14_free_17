package com.adventureislands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity {

	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		SessionData.instance().desired_map = SessionData.TESTISLAND;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;  
	}

	public void startTestMap(View v){
		SessionData.instance().state = SessionData.PLAYING;
		Intent intent = new Intent(getBaseContext(), StandardGameScreenActivity.class);  
		startActivity(intent);
	}
	public void smallIslandClicked(View v){
		SessionData.instance().desired_map = SessionData.TESTISLAND;
	}
	public void shipClicked(View v){
		SessionData.instance().desired_map = SessionData.TESTSHIP;
	}
	
}
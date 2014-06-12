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
		SessionData.instance().desired_map = SessionData.ISLAND;
		SessionData.instance().state = SessionData.PLAYING;
		Intent intent = new Intent(getBaseContext(), StandardGameScreenActivity.class);  
		startActivity(intent);
	}
	
	public void testing(View v){
		Intent intent = new Intent(getBaseContext(), TestActivity.class);  
		startActivity(intent);
	}

	public void goToCity(View v){
		SessionData.instance().desired_map = SessionData.CITY;
		SessionData.instance().state = SessionData.PLAYING;
		Intent intent = new Intent(getBaseContext(), StandardGameScreenActivity.class);  
		startActivity(intent);
	}
	
	public void playMusic(View v){			
		if(SessionData.instance().musicOn==true){
			SessionData.instance().musicPlayer.stop();
			SessionData.instance().musicOn=false;
			Log.i("MenuActivity", "Music turned off");
		}
		else{
			try {
				SessionData.instance().musicPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SessionData.instance().musicPlayer.start();
			SessionData.instance().musicOn=true;
			Log.i("MenuActivity", "Music turned on");
		}
	}

	public void createMap(View v){    
		Button p1_button = (Button)findViewById(R.id.createMapButton);
		p1_button.setText("..GENERATING MAP..");
		createMapTask();
	}
	
	public void createMapTask(){
		new AsyncTask<Void,Integer,Map>() {
			@Override
			protected Map doInBackground(Void... params) {
				Map map = new Map(null,getBaseContext(), SessionData.ISLAND,"large");
				return map;
			}
			@Override
			protected void onPostExecute(Map map) {
				SessionData.instance().map = map;
				Button p1_button = (Button)findViewById(R.id.createMapButton);
				p1_button.setText("..UPLOADING MAP..");
				uploadTask();
			}

		}.execute();
	}
	
	
	public void uploadTask(){
		FileOutputStream file_output_stream = null;
		try {
			file_output_stream = openFileOutput("map.txt", MODE_WORLD_READABLE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream[] fos_params = {file_output_stream};
		
		new AsyncTask<FileOutputStream,Integer,Boolean>() {
			@Override
			protected Boolean doInBackground(FileOutputStream... params) {
				FileOutputStream file_output_stream = params[0];
			  ObjectOutputStream object_output_stream = null;
			   	try
			    {
			        object_output_stream = new ObjectOutputStream(file_output_stream);
			        object_output_stream.writeObject(SessionData.instance().map);
			        object_output_stream.close();
			        file_output_stream.close();
			        SessionData.instance().map = null;
			    }
			    catch (IOException ex)
			    {
			        ex.printStackTrace();
			        return false;
			    }
				File file = new File(getFilesDir(), "map.txt");
				uploadFile(file);
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean finished) {
				if(finished){
					Button create_map_button = (Button)findViewById(R.id.createMapButton);
					create_map_button.setText("..MAP GOT UPLOADED..");
				}
			}

			public void uploadFile(File file){
				
		        FTPClient ftp_client = new FTPClient();
		        try {
		            ftp_client.connect(SessionData.server, SessionData.port);
		            showServerReply(ftp_client);
		            ftp_client.login(SessionData.user, SessionData.pass);
		            showServerReply(ftp_client);
		            ftp_client.enterLocalPassiveMode();
		            showServerReply(ftp_client);
		            ftp_client.setFileType(FTP.BINARY_FILE_TYPE);

		            String remote_file = "map.txt";
		            InputStream input_stream = new FileInputStream(file);
		            
		            Log.i("MenuActivity","Start uploading file");
		            boolean done = ftp_client.storeFile(remote_file, input_stream);
		            input_stream.close();
		            if (done) {
		            	System.out.println("The file is uploaded successfully.");
		            	 Log.i("MenuActivity","The file is uploaded successfully.");
		            }
		        } catch (IOException ex) {
		        	 Log.i("MenuActivity","Error: " + ex.getMessage());
		            ex.printStackTrace();
		        } finally {
		            try {
		                if (ftp_client.isConnected()) {
		                    ftp_client.logout();
		                    ftp_client.disconnect();
		                }
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		        }
			}
			 
			private void showServerReply(FTPClient ftp_client) {
		        String[] replies = ftp_client.getReplyStrings();
		        if (replies != null && replies.length > 0) {
		            for (String aReply : replies) {
		                Log.i("MenuActivity","" + aReply);
		            }
		        }
			}	
		}.execute(fos_params);
	}
	
	
	@Override
	protected void onStop(){
		if(SessionData.instance().state == SessionData.EXITING){
			SessionData.instance().musicPlayer.release();
		}
		
		super.onStop();
		
	}
	@Override
	protected void onStart(){
		if(SessionData.instance().musicPlayer==null){
			SessionData.instance().musicPlayer = MediaPlayer.create(this, R.raw.menu_intro);
			SessionData.instance().musicPlayer.setLooping(true);
			this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	
			if(SessionData.instance().musicOn==true){
				SessionData.instance().musicPlayer.start();
			}
		}
		super.onStart();
	}
}

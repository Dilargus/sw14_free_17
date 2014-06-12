package com.adventureislands;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;


import com.datastructure.tmx.TMXTiledMap;
import com.e3roid.drawable.texture.TiledTexture;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

 

public class Uploader extends AsyncTask<Void,Integer,TMXTiledMap> {
	private Context context;
	static private String server = "adventureislands.bplaced.net";
	static private int port = 21;
	static private String user = "adventureislands";
	static private String pass = "PW2long*";
	static private Object object;
	private FileOutputStream fOut;
	
	public Uploader(Context context, Object object, FileOutputStream fOut) {
		this.fOut = fOut;
		this.context=context;
		this.object = object;
	}
	
	public void uploadFile(File file){
		
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            showServerReply(ftpClient);
            ftpClient.login(user, pass);
            showServerReply(ftpClient);
            ftpClient.enterLocalPassiveMode();
            showServerReply(ftpClient);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            //File firstLocalFile = new File(Environment.getExternalStorageDirectory() + File.separator + "map.txt");
            //firstLocalFile.createNewFile(); 

            String firstRemoteFile = "map.txt";
            InputStream inputStream = new FileInputStream(file);
            
            Log.i("AdventureLog upload","Start uploading first file");
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
            if (done) {
            	 Log.i("AdventureLog upload","The first file is uploaded successfully.");
               
            }
        } catch (IOException ex) {
        	 Log.i("AdventureLog upload","Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
	
	  private static void showServerReply(FTPClient ftpClient) {
	        String[] replies = ftpClient.getReplyStrings();
	        if (replies != null && replies.length > 0) {
	            for (String aReply : replies) {
	                Log.i("AdventureLog SERVER: ","" + aReply);
	            }
	        }
	    }

	@Override
	protected TMXTiledMap doInBackground(Void... arg0) {
       ObjectOutputStream oos = null;
       try
        {
            oos = new ObjectOutputStream(fOut);
            oos.writeObject(object);
            oos.close();
            fOut.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
		File file = new File(context.getFilesDir(), "map.txt");
		uploadFile(file);
		return null;
	}
}

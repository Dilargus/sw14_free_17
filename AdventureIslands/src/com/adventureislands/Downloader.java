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
import java.io.ObjectInputStream;
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

 

public class Downloader extends AsyncTask<Void,Integer,Map> {
	private static Context context;
	static private String server = "adventureislands.bplaced.net";
	static private int port = 21;
	static private String user = "adventureislands";
	static private String pass = "PW2long*";
	
	public Downloader(Context context) {
		this.context=context;
	}
	
	public static void downloadFile() {
	    FTPClient ftpClient = new FTPClient();
	    try {
	    	ftpClient.connect(server, port);
	        ftpClient.login(user, pass);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        String remoteFile = "map.txt";
	        File downloadFile = new File(context.getFilesDir(), "map.txt");
	        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
	        boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
	        outputStream.close();
	        if (success) {
	        	System.out.println("File has been downloaded successfully.");
	        }
	    } catch (IOException ex) {
	    	System.out.println("Error: " + ex.getMessage());
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
	protected Map doInBackground(Void... arg0) {
		Map map=null;
		downloadFile();
		
		FileInputStream fis = null;
		try
		{
		   File downloadFile = new File(context.getFilesDir(), "map.txt");
           fis = new FileInputStream(downloadFile);
           ObjectInputStream ois = null;
           try
           {
        	   ois = new ObjectInputStream(fis);
               map = (Map) ois.readObject();
            
               // Escape early if cancel() is called
               if (isCancelled()) return null;
           }
           catch (IOException ex)
           {
               ex.printStackTrace();
           }
           catch(ClassNotFoundException ex) 
           {
               ex.printStackTrace();
           }
           finally
           {
               try
               {
                        if(ois!=null)
                        { 
                           ois.close();
                        }
               }
               catch (IOException ex)
               {
                   ex.printStackTrace();
               }
           }
       }
       catch (FileNotFoundException ex)
       {
           ex.printStackTrace();
       }
       finally
       {
           try{if(fis!=null)
           {
                   fis.close();
           }}
           catch (IOException ex)
           {
               ex.printStackTrace();
           }
       }
		return map;
	}
}

package com.datastructure;

import java.util.ArrayList;
import java.util.Arrays;

import com.adventureislands.SessionData;

import android.util.Log;

public class Boden implements
java.io.Serializable{
	private ArrayList<myPoint> rand;
	private myPoint grund;
	private int art;
	private ArrayList<Integer> nextB�den ;
	private boolean mehrst�ckig=false;
	private boolean zufallsgroesse=false;
	private boolean betretbar = true;
	private int gapsgroesse=5;
	private double maxgroesse=0.5;
	
	public boolean isBetretbar() {
		return betretbar;
	}



	public void setBetretbar(boolean betretbar) {
		this.betretbar = betretbar;
	}



	public Boden(int art) {
		nextB�den = new ArrayList<Integer>();
		this.art = art;
		switch(art){

			case SessionData.MINENBODEN:
				grund = new myPoint(11, 2); 
				maxgroesse=0.3;
				zufallsgroesse=false;
				betretbar=false;
			break;
			case SessionData.DUNKLESCHLUCHT:
				grund = new myPoint(1, 11);
				maxgroesse=0.3;
				mehrst�ckig=true;
				betretbar=false;
				zufallsgroesse=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.MINENBODEN)));
			break;
			case SessionData.DUNKELHEIT: 
				grund = new myPoint(6, 2);
				zufallsgroesse=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.DUNKLESCHLUCHT)));
			break;
			case SessionData.SANDSTRAND:
				maxgroesse=0.7;
				zufallsgroesse=false;
				grund = new myPoint(1, 14); 
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.SANDKLIPPE),new Integer(SessionData.BROCKENSAND), new Integer(SessionData.HELLERSAND)));
			break;
			case SessionData.UNTERWASSERSAND:
				grund = new myPoint(11, 11); 
				maxgroesse=0.4;
				zufallsgroesse=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.SANDSTRAND)));
			break;
			case SessionData.TIEFESWASSER:
				grund = new myPoint(1, 11);
				betretbar=false;
				zufallsgroesse=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.WASSER)));
			break;
			case SessionData.WASSER: 
				grund = new myPoint(11, 14);
				zufallsgroesse=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.UNTERWASSERSAND)));
			break;
			case SessionData.STEINMAUER:
				betretbar=false;
				zufallsgroesse=true;
				grund = new myPoint(11, 6);
			break;
			case SessionData.BRACHLAND:
				grund = new myPoint(1, 20);
				zufallsgroesse=true;
				gapsgroesse = 100;
			break;
			case SessionData.ERH�HUNG:
				betretbar=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENLAND)));
				grund = new myPoint(6, 17);
				zufallsgroesse=true;
			break;
			case SessionData.HELLERSAND:
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.PFLASTER),new Integer(SessionData.EISENKLIPPE),new Integer(SessionData.BRACHLAND)));
				grund = new myPoint(6, 20);
				zufallsgroesse=true;
			break;
			case SessionData.PFLASTER:
				grund = new myPoint(11, 20);
				zufallsgroesse=true;
			break;
			case SessionData.SANDKLIPPE:
				mehrst�ckig=true;
				betretbar=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.KLIPPENSAND)));
				grund = new myPoint(6, 8);
				maxgroesse = 0.1;
				zufallsgroesse=true;
			break;
			case SessionData.KLIPPENSAND:
				betretbar=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENSAND)));
				grund = new myPoint(6, 6);
				zufallsgroesse=true;
			break;
			case SessionData.EISENKLIPPE:
				mehrst�ckig=true;
				betretbar=false;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.EISENKLIPPENSAND)));
				grund = new myPoint(1, 8);
				zufallsgroesse=true;
			break;
			case SessionData.EISENKLIPPENSAND:
				betretbar=false;
				grund = new myPoint(1, 6);
				zufallsgroesse=true;
			break;
			case SessionData.BROCKENSAND:
				grund = new myPoint(1, 23);
				zufallsgroesse=true;
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENLAND),new Integer(SessionData.ERH�HUNG)));
			break;
			case SessionData.BROCKENLAND:
				zufallsgroesse=true;
				gapsgroesse = 50;
				grund = new myPoint(6, 23);
				nextB�den = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.STEINMAUER),new Integer(SessionData.VERTIEFUNG)));
			break;
			case SessionData.VERTIEFUNG:
				betretbar=false;
				zufallsgroesse=true;
				grund = new myPoint(1, 17);
			break;
			case SessionData.SCHIFFSHUELLE:
				betretbar=false;
				nextB�den = null;
				grund = null;
				maxgroesse = 0;
				zufallsgroesse=false;
			break;
			case SessionData.SCHIFFSDECK:
				betretbar=false;
				nextB�den = null;
				grund = null;
				maxgroesse = 0;
				zufallsgroesse=false;
			break;
		}
		if(grund!=null){
			if(!mehrst�ckig){
				rand = new ArrayList<myPoint>(Arrays.asList(
						//links oben, 					 	oben waagrecht,                			rechts oben,                     	rechts senkrecht,
						new myPoint(grund.x-1, grund.y-1), 	new myPoint(grund.x, grund.y-1), 		new myPoint(grund.x+1, grund.y-1), 	new myPoint(grund.x+1, grund.y),  
						//rechts unten,                  	unten waagrecht,               			links unten,                     	links senkrecht,
						new myPoint(grund.x+1, grund.y+1), 	new myPoint(grund.x, grund.y+1), 		new myPoint(grund.x-1, grund.y+1), 	new myPoint(grund.x-1, grund.y),   
						//links nach oben,               	oben nach rechts,                		rechts nach unten,             		unten nach links
						new myPoint(grund.x+2, grund.y-1), 	new myPoint(grund.x+3, grund.y-1), 		new myPoint(grund.x+3, grund.y), 	new myPoint(grund.x+2, grund.y)));
			}
			else{
				rand = new ArrayList<myPoint>(Arrays.asList(
						//links oben, 					 	oben waagrecht,                			rechts oben,                     	rechts senkrecht,
						new myPoint(16, 0), 				new myPoint(16, 0), 					new myPoint(16, 0), 				new myPoint(16, 0),  
						//rechts unten,                  	unten waagrecht,               			links unten,                     	links senkrecht,
						new myPoint(grund.x+1, grund.y+1), 	new myPoint(grund.x, grund.y+1), 		new myPoint(grund.x-1, grund.y+1), 	new myPoint(16, 0),   
						//links nach oben,               	oben nach rechts,                		rechts nach unten,             		unten nach links
						new myPoint(16, 0), 				new myPoint(16, 0), 					new myPoint(16, 0), 				new myPoint(16, 0)));
			}
		}
		
	}

	public boolean isZufallsgroesse() {
		return zufallsgroesse;
	}

	public void setZufallsgroesse(boolean zufallsgroesse) {
		this.zufallsgroesse = zufallsgroesse;
	}

	public int getGapsgroesse() {
		return gapsgroesse;
	}



	public void setGapsgroesse(int gapsgroesse) {
		this.gapsgroesse = gapsgroesse;
	}

	public double getMaxgroesse() {
		return maxgroesse;
	}

	public void setMaxgroesse(double maxgroesse) {
		this.maxgroesse = maxgroesse;
	}

	public myPoint getRandst�ck(int stelle){
		return rand.get(stelle);
	}
	public ArrayList<myPoint> getRand(){
		return rand;
	}
	
	public int getArt(){
		return art;
	}
	
	public boolean isMehrst�ckig() {
		return mehrst�ckig;
	}

	public void setMehrst�ckig(boolean mehrst�ckig) {
		this.mehrst�ckig = mehrst�ckig;
	}

	public myPoint getGrund(){
		return grund;
	}
	
	public ArrayList<Integer> getnextB�den(){
		return nextB�den;
	}
}

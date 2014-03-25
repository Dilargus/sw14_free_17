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
	private ArrayList<Integer> nextBöden ;
	private boolean mehrstöckig=false;
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
		nextBöden = new ArrayList<Integer>();
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
				mehrstöckig=true;
				betretbar=false;
				zufallsgroesse=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.MINENBODEN)));
			break;
			case SessionData.DUNKELHEIT: 
				grund = new myPoint(6, 2);
				zufallsgroesse=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.DUNKLESCHLUCHT)));
			break;
			case SessionData.SANDSTRAND:
				maxgroesse=0.7;
				zufallsgroesse=false;
				grund = new myPoint(1, 14); 
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.SANDKLIPPE),new Integer(SessionData.BROCKENSAND), new Integer(SessionData.HELLERSAND)));
			break;
			case SessionData.UNTERWASSERSAND:
				grund = new myPoint(11, 11); 
				maxgroesse=0.4;
				zufallsgroesse=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.SANDSTRAND)));
			break;
			case SessionData.TIEFESWASSER:
				grund = new myPoint(1, 11);
				betretbar=false;
				zufallsgroesse=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.WASSER)));
			break;
			case SessionData.WASSER: 
				grund = new myPoint(11, 14);
				zufallsgroesse=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.UNTERWASSERSAND)));
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
			case SessionData.ERHÖHUNG:
				betretbar=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENLAND)));
				grund = new myPoint(6, 17);
				zufallsgroesse=true;
			break;
			case SessionData.HELLERSAND:
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.PFLASTER),new Integer(SessionData.EISENKLIPPE),new Integer(SessionData.BRACHLAND)));
				grund = new myPoint(6, 20);
				zufallsgroesse=true;
			break;
			case SessionData.PFLASTER:
				grund = new myPoint(11, 20);
				zufallsgroesse=true;
			break;
			case SessionData.SANDKLIPPE:
				mehrstöckig=true;
				betretbar=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.KLIPPENSAND)));
				grund = new myPoint(6, 8);
				maxgroesse = 0.1;
				zufallsgroesse=true;
			break;
			case SessionData.KLIPPENSAND:
				betretbar=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENSAND)));
				grund = new myPoint(6, 6);
				zufallsgroesse=true;
			break;
			case SessionData.EISENKLIPPE:
				mehrstöckig=true;
				betretbar=false;
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.EISENKLIPPENSAND)));
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
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.BROCKENLAND),new Integer(SessionData.ERHÖHUNG)));
			break;
			case SessionData.BROCKENLAND:
				zufallsgroesse=true;
				gapsgroesse = 50;
				grund = new myPoint(6, 23);
				nextBöden = new ArrayList<Integer>(Arrays.asList(new Integer(SessionData.STEINMAUER),new Integer(SessionData.VERTIEFUNG)));
			break;
			case SessionData.VERTIEFUNG:
				betretbar=false;
				zufallsgroesse=true;
				grund = new myPoint(1, 17);
			break;
			case SessionData.SCHIFFSHUELLE:
				betretbar=false;
				nextBöden = null;
				grund = null;
				maxgroesse = 0;
				zufallsgroesse=false;
			break;
			case SessionData.SCHIFFSDECK:
				betretbar=false;
				nextBöden = null;
				grund = null;
				maxgroesse = 0;
				zufallsgroesse=false;
			break;
		}
		if(grund!=null){
			if(!mehrstöckig){
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

	public myPoint getRandstück(int stelle){
		return rand.get(stelle);
	}
	public ArrayList<myPoint> getRand(){
		return rand;
	}
	
	public int getArt(){
		return art;
	}
	
	public boolean isMehrstöckig() {
		return mehrstöckig;
	}

	public void setMehrstöckig(boolean mehrstöckig) {
		this.mehrstöckig = mehrstöckig;
	}

	public myPoint getGrund(){
		return grund;
	}
	
	public ArrayList<Integer> getnextBöden(){
		return nextBöden;
	}
}

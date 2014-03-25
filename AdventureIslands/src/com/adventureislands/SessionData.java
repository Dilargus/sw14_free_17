package com.adventureislands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import android.graphics.Point;

import com.datastructure.Player;
import com.datastructure.tmx.TMXTile;


public class SessionData {
	private static SessionData instance;
	private Player self;
	public Random randomInt = new Random();
	//gerichtete Tiles
	public static final int DOWN = 1;
	public static final int DOWNLEFT=5;
	public static final int UP = 3;
	public static final int RIGHT = 4;
	public static final int LEFT = 2;
	public static final int LEFTUP=6;
	public static final int UPRIGHT=7;
	public static final int RIGHTDOWN=8;
	public static final int SCHIFFSLAENGE=19;
	public static final int SCHIFFSBREITE=10;
	
	//Rahmenrichtungen
	public static final int LINKSOBEN = 0;
	public static final int OBEN = 1;
	public static final int OBENRECHTS = 2;
	public static final int RECHTS = 3;
	public static final int RECHTSUNTEN = 4;
	public static final int UNTEN = 5;
	public static final int UNTENLINKS = 6;
	public static final int LINKS = 7;
	public static final int LINKSNACHOBEN = 8;
	public static final int OBENNACHRECHTS = 9;
	public static final int RECHTSNACHUNTEN = 10;
	public static final int UNTENNACHLINKS = 11;
	

	public static final int INSEL = 1;
	public static final int GANZ = 2; 
	public static final int HALBINSEL = 3;  
	public static final int ZUFALL = -1;

	public static final int EBENE=1;
	public static final int OBJEKT=2;
	public static final int OBJEKTVOR=3;
	
	public static final String SMALL = "small";
	public static final String MEDIUM = "medium";
	public static final String LARGE = "large";
	public static final String XLARGE = "xlarge";
	public static final String LARGETRI = "largetri";
	public static final int SICHTBAR = 1;
	public static final int UNSICHTBAR = 0;
	
	public static final int SANDINSEL = 0;
	public static final int SANDSTRAND = 1;
	public static final int UNTERWASSERSAND = 2;
	public static final int TIEFESWASSER = 3;
	public static final int WASSER = 4;
	public static final int STEINMAUER = 5;
	public static final int STEINMAUERSOCKEL = 6;
	public static final int BRACHLAND = 7;
	public static final int ERHÖHUNG = 8;
	public static final int HELLERSAND = 9;
	public static final int PFLASTER = 10;
	public static final int SANDKLIPPE = 11;
	public static final int KLIPPENSAND = 12;
	public static final int EISENKLIPPE = 13;
	public static final int EISENKLIPPENSAND = 14;
	public static final int BROCKENSAND = 15;
	public static final int BROCKENLAND = 16;
	public static final int VERTIEFUNG = 17;

	public static final int HÖHLE = 30;
	public static final int DUNKELHEIT = 31;
	public static final int DUNKLESCHLUCHT = 32;
	public static final int MINENBODEN = 33;
	
	
	public static final int SCHIFFSHUELLE = 40;
	public static final int SCHIFFSDECK = 41;
	
	//actions
	public static final int DIG = 20;
	public static final int GO_UP = 21;
	public static final int GO_DOWN = 22;
	public static final int GO_RIGHT = 23;
	public static final int GO_LEFT = 24;
	public static final int DO_NOTHING = 25;
	
	//events
	
	public static final int RIGHT_SIDE = 1;
	public static final int LEFT_SIDE = 2;
	
	
	
	
	public ArrayList<Point> ultramini = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(5,0),new Point(5,5), new Point(0,5)));
	public ArrayList<Point> mini = new ArrayList<Point>(Arrays.asList(new Point(10,10), new Point(20,10),new Point(20,20), new Point(10,20)));
	public ArrayList<Point> small = new ArrayList<Point>(Arrays.asList(new Point(5,5), new Point(20,5),new Point(20,20), new Point(5,20)));
	public ArrayList<Point> medium = new ArrayList<Point>(Arrays.asList(new Point(20,20), new Point(50,20),new Point(50,50), new Point(20,50)));
	public ArrayList<Point> large = new ArrayList<Point>(Arrays.asList(new Point(20,20), new Point(70,20),new Point(70,70), new Point(20,70)));
	public ArrayList<Point> xlarge = new ArrayList<Point>(Arrays.asList(new Point(20,20), new Point(100,20),new Point(100,100), new Point(20,100)));
	public ArrayList<Point> trianglelarge = new ArrayList<Point>(Arrays.asList(new Point(20,20), new Point(80,20),new Point(50,80)));
	
	
	public ArrayList<Point> neuner = new ArrayList<Point>(Arrays.asList(new Point(0,+1), new Point(-1,0),new Point(0,-1), new Point(+1,0),new Point(-1,+1),new Point(-1,-1),new Point(+1,-1),new Point(+1,+1)));
	public ArrayList<Point> fünfundzwanziger = new ArrayList<Point>(Arrays.asList(new Point(-2,-2), new Point(-1,-2),new Point(0,-2), new Point(1,-2),new Point(2,-2),new Point(-2,-1),new Point(-1,-1),new Point(0,-1),new Point(1,-1), new Point(2,-1),new Point(-2,0), new Point(-1,0),new Point(0,0),new Point(1,0),new Point(2,0),new Point(-2,1),new Point(-1,1), new Point(0,1),new Point(1,1), new Point(2,1),new Point(-2,2),new Point(-1,2),new Point(0,2),new Point(1,2),new Point(2,2)));
	public ArrayList<Point> vierer = new ArrayList<Point>(Arrays.asList(new Point(0,+1), new Point(-1,0),new Point(0,-1), new Point(+1,0)));
	public ArrayList<Point> neunerinorder = new ArrayList<Point>(Arrays.asList(new Point(0,0),new Point(-1,-1), new Point(0,-1),new Point(1,-1), new Point(+1,0),new Point(+1,+1),new Point(0,+1),new Point(-1,+1),new Point(-1,0)));
	//public ArrayList<Point> dreieckenblock = new ArrayList<Point>(Arrays.asList(new Point(1,0),new Point(1,1), new Point(0,1),new Point(1,0),new Point(0,1),new Point(-1,1),new Point(-1,0),new Point(-1,0),new Point(-1,-1),new Point(0,-1),new Point(0,-1),new Point(1,-1),new Point(1,0)));
	
	
	
	
	public ArrayList<Point> zweixdrei = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(1,0),new Point(0,1), new Point(1,1),new Point(0,2),new Point(1,2)));
	public ArrayList<Point> dreixdrei = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(1,0),new Point(2,0),new Point(0,1), new Point(1,1),new Point(2,1),new Point(0,2),new Point(1,2),new Point(2,2)));
	public ArrayList<Point> zweixzwei = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(1,0),new Point(0,1),new Point(1,1)));
	public ArrayList<Point> einsxeins = new ArrayList<Point>(Arrays.asList(new Point(0,0)));
	

	public Map map;
	public Player player1;
	public int ebenencounter=0;
	public int objektlayercounter=0;
	public int objektvorlayercounter=0;
	public int ebenenlayercounter=0;

    public static SessionData instance()
    {
        if(instance == null)
        {
            instance = new SessionData();
        }
        return instance;
    }
}

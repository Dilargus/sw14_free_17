package com.adventureislands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import android.graphics.Point;
import android.media.MediaPlayer;
import com.datastructure.objects.Cannon;
import com.datastructure.objects.Empty;
import com.datastructure.objects.Object;


public class SessionData {
	private static SessionData instance;

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

	//Rahmenrichtungen
	public static final int NINE_LEFTTOP = 0;
	public static final int NINE_TOP = 1;
	public static final int NINE_TOPRIGHT = 2;
	public static final int NINE_RIGHT = 3;
	public static final int NINE_RIGHTBOTTOM = 4;
	public static final int NINE_BOTTOM = 5;
	public static final int NINE_BOTTOMLEFT = 6;
	public static final int NINE_LEFT = 7;
	public static final int FOUR_LEFTTOP = 8;
	public static final int FOUR_TOPRIGHT = 9;
	public static final int FOUR_RIGHTBOTTOM = 10;
	public static final int FOUR_BOTTOMLEFT = 11;
	
	
	public static final int CANNON = 1;
	public static final int EMPTY = 2;
	public static final int CROSS = 3;
	public static final int EXIT = 4;
	public static final int PALM = 5;
	public static final int RANK = 6;
	public static final int SHIP = 7;
	public static final int SHOVEL = 8;
	public ArrayList<Object> SMALL_WEAPONS = new ArrayList<Object>(Arrays.asList(new Cannon(),new Empty()));
	

	public static final int ISLAND = 1;
	public static final int CITY = 50;

	public static final int TESTISLAND = 3;
	public static final int TESTSHIP = 4;
	
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
	public static final int FLY = 26;
	public static final int CLASHINTOWATER = 27;
	public static final int LIE = 28;
	
	//events
	
	public static final int RIGHT_SIDE = 1;
	public static final int LEFT_SIDE = 2;
	
	static public String server = "adventureislands.bplaced.net";
	static public int port = 21;
	static public String user = "adventureislands";
	static public String pass = "PW2long*";
	
	public ArrayList<Point> NINE_SURROUNDINGS = new ArrayList<Point>(Arrays.asList(new Point(0,+1), new Point(-1,0),new Point(0,-1), new Point(+1,0),new Point(-1,+1),new Point(-1,-1),new Point(+1,-1),new Point(+1,+1)));
	public ArrayList<Point> TWENTYFIVE_SURROUNDINGS = new ArrayList<Point>(Arrays.asList(new Point(-2,-2), new Point(-1,-2),new Point(0,-2), new Point(1,-2),new Point(2,-2),new Point(-2,-1),new Point(-1,-1),new Point(0,-1),new Point(1,-1), new Point(2,-1),new Point(-2,0), new Point(-1,0),new Point(0,0),new Point(1,0),new Point(2,0),new Point(-2,1),new Point(-1,1), new Point(0,1),new Point(1,1), new Point(2,1),new Point(-2,2),new Point(-1,2),new Point(0,2),new Point(1,2),new Point(2,2)));
	public ArrayList<Point> FOUR_SURROUNDINGS = new ArrayList<Point>(Arrays.asList(new Point(0,+1), new Point(-1,0),new Point(0,-1), new Point(+1,0)));
	public ArrayList<Point> NINE_SURROUNDINGS_ORDERED = new ArrayList<Point>(Arrays.asList(new Point(0,0),new Point(-1,-1), new Point(0,-1),new Point(1,-1), new Point(+1,0),new Point(+1,+1),new Point(0,+1),new Point(-1,+1),new Point(-1,0)));
	
	//actions
	public static final int MENU = 1;
	public static final int PLAYING = 2;
	public static final int EXITING = 0;
	
	
	public ArrayList<Point> TWOxTHREE = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(1,0),new Point(0,1), new Point(1,1),new Point(0,2),new Point(1,2)));
	public ArrayList<Point> THREExTHREE = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(1,0),new Point(2,0),new Point(0,1), new Point(1,1),new Point(2,1),new Point(0,2),new Point(1,2),new Point(2,2)));
	public ArrayList<Point> TWOxTWO = new ArrayList<Point>(Arrays.asList(new Point(0,0), new Point(1,0),new Point(0,1),new Point(1,1)));
	public ArrayList<Point> ONExONE = new ArrayList<Point>(Arrays.asList(new Point(0,0)));
	
	public ArrayList<Point> weapon_atlas = new ArrayList<Point>(Arrays.asList(new Point(63,0), new Point(63,2)));
	
	public boolean musicOn=true;
	public MediaPlayer musicPlayer;
	public int desired_map = -1;
	public int state = SessionData.MENU;

	//für Upload der Map
	public Map map;
	
    public static SessionData instance()
    {
        if(instance == null)
        {
            instance = new SessionData();
        }
        return instance;
    }
}
